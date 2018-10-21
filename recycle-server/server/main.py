import base64
import json
import logging
import math
import os

import redis
import requests
from flask import Flask, request, jsonify

from server.predict import get_prediction


if not os.getenv('GOOGLE_APPLICATION_CREDENTIALS', ''):
    raise RuntimeError('GOOGLE_APPLICATION_CREDENTIALS must be set')

if not os.path.exists(os.getenv('GOOGLE_APPLICATION_CREDENTIALS', '')):
    raise RuntimeError('GOOGLE_APPLICATION_CREDENTIALS ({}) does not exist'
                       .format(os.getenv('GOOGLE_APPLICATION_CREDENTIALS', '')))


app = Flask(__name__)

gunicorn_logger = logging.getLogger('gunicorn.error')
app.logger.propagate = False
app.logger.setLevel(gunicorn_logger.level)


redis_url = os.getenv('REDIS_URL', 'redis://localhost:6379/0')
r = redis.from_url(redis_url, decode_responses=True)

GOOGLE_PROJECT_ID = 'parkrecycle-220001'
GOOGLE_MODEL_ID = 'ICN8112082385550945410'

classification_categories = {
    'food': 'compost',
    'plastic': 'plastic',
    'plates': 'compost',
    'soda_can': 'aluminum',
    'trash': 'trash'
}
disposal_categories = ['compost', 'trash', 'aluminum', 'plastic']
disposal_category_points = {'compost': 3, 'trash': 1, 'aluminum': 2, 'plastic': 2}
disposal_category_names = {'compost': 'Compost', 'trash': 'Trash', 'aluminum': 'Aluminum', 'plastic': 'Plastic'}


app.logger.info('Starting...')


def category_to_name(cat):
    return disposal_category_names.get(cat, 'Miscellaneous')


def required_experience_for_level(level):
    return math.ceil(2.5 * 2 ** (level - 1))


def fail(message, code=500):
    return jsonify({'success': False, 'message': message}), code


def set_kiosk_lights(kiosk_id, disposal_category, valid_for=5):
    try:
        category_idx = disposal_categories.index(disposal_category)
    except ValueError:
        return

    payload = json.dumps([category_idx])

    app.logger.info('Settings lights to {} for kiosk_id={}'.format(payload, kiosk_id))

    r.set('kiosk_lights/{}'.format(kiosk_id), payload, ex=valid_for)


def increment_user_points(user_id, points):
    data = r.get('user_stat/{}'.format(user_id))
    if data is None:
        return

    data = json.loads(data)

    points = data.get('total_points') + points
    level = data.get('level', 1)
    while True:
        points_required = required_experience_for_level(level)
        if points < points_required:
            break

        level += 1
        points -= points_required
        app.logger.info('Level Up: {} has leveled up to {} which requires {} points'
                        .format(data['name'], level, points_required))

    data['total_points'] = points
    data['level'] = level

    r.set('user_stat/{}'.format(user_id), json.dumps(data))

    return data


@app.route('/')
def route_index():
    return '''There be dragons''', 500


@app.route('/test')
def route_test():
    return 'Hello, world!'


@app.route('/report', methods=['POST'])
def route_report():
    r.lpush('user_reports', json.dumps(request.json))
    app.logger.info('Reported: {}'.format(request.json))

    return 'OK'


@app.route('/user_stats')
def route_stats():
    user_id = request.args.get('user', None)

    if user_id is None:
        return fail("'user_id' is required", code=400)

    response = requests.get('https://stats.dev.hack.gt/api/user/{}'.format(user_id))
    if response.status_code < 200 or response.status_code >= 300:
        return fail('Error from StatGT: {}'.format(response.content))

    data = {
        'total_points': 0,
        'disposal_types': [],
        'level': 1,
    }

    existing_data = r.get('user_stat/{}'.format(user_id))
    if existing_data is not None:
        data.update(json.loads(existing_data))

    data.update(response.json())

    r.set('user_stat/{}'.format(user_id), json.dumps(data))

    # ensure an entry for every disposal category in the response
    disposal_types = data['disposal_types']
    disposal_type_names = [x['name'] for x in disposal_types]
    for category in disposal_categories:
        if category not in disposal_type_names:
            disposal_types.append({'name': category, 'points': 0})

    for entry in disposal_types:
        entry['name'] = category_to_name(entry['name'])

    data['disposal_types'] = disposal_types

    if 'level' in data:
        data[''] = required_experience_for_level(data['level'])

    return jsonify(data)


@app.route('/kiosk/<int:kiosk_id>')
def route_kiosk(kiosk_id):
    lit_list = r.get('kiosk_lights/{}'.format(kiosk_id))
    if lit_list is not None:
        lit_list = json.loads(lit_list)
    else:
        lit_list = []

    return jsonify({'lit': lit_list})


@app.route('/image_detect', methods=['POST'])
def detect_image():
    data = request.json
    image_bytes = base64.b64decode(data['image_data'])
    kiosk_id = int(data.get('kiosk_id', 1))
    user_id = data['user']
    prediction = get_prediction(image_bytes, GOOGLE_PROJECT_ID, GOOGLE_MODEL_ID)

    fields = []
    for field in prediction.payload:
        fields.append((field.classification.score, field.display_name))

    fields.sort()

    category = classification_categories[fields[-1][1]]

    set_kiosk_lights(kiosk_id, category)

    points_gained = disposal_category_points.get(category, 0)

    # updates Redis (other user data will become invalid)
    increment_user_points(user_id, points_gained)

    return jsonify({
        'type': category_to_name(category),
        'points': points_gained,
        'description': 'Lorem ipsum dolor iset ' * 5
    })


@app.route('/leaderboard')
def route_leaderboard():
    users = [json.loads(r.get(u)) for u in r.scan_iter(match='user_stat/*')]

    users_by_points = sorted(users, key=lambda u: u.get('total_points', 0))[:10]

    return jsonify({
        'leaderboard': [{'name': u['name'], 'points': u.get('total_points', 0)}
                        for u in users_by_points]
    })
