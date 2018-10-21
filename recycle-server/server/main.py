import json
import os

import redis
import requests
from flask import Flask, request, jsonify

redis_url = os.getenv('REDIS_URL', 'redis://localhost:6379/0')
r = redis.from_url(redis_url, decode_responses=True)

app = Flask(__name__)

GOOGLE_PROJECT_ID = 'parkrecycle-220001'
GOOGLE_MODEL_ID = 'plastic_bottles_v20181020165932'


classification_categories = {
    'food': 'compost',
    'plastic': 'plastic',
    'plates': 'compost',
    'soda_can': 'aluminum',
    'trash': 'trash'
}
disposal_categories = ['compost', 'trash', 'aluminum', 'plastic']


def fail(message, code=500):
    return jsonify({'success': False, 'message': message}), code


def set_kiosk_lights(kiosk_id, disposal_category, valid_for=5):
    try:
        category_idx = disposal_categories.index(disposal_category)
        r.set('kiosk_lights/{}'.format(kiosk_id), json.dumps([category_idx]), ex=valid_for)
    except ValueError:
        pass


@app.route('/')
def route_index():
    return '''There be dragons''', 500


@app.route('/test')
def route_test():
    return 'Hello, world!'


@app.route('/user_stats')
def route_stats():
    user_id = request.args.get('user', None)

    if user_id is None:
        return fail("'user_id' is required", code=400)

    response = requests.get('https://stats.dev.hack.gt/api/user/{}'.format(user_id))
    if response.status_code < 200 or response.status_code >= 300:
        return fail('Error from StatGT: {}'.format(response.content))

    data = r.get('user_stat/{}'.format(user_id))
    if data is not None:
        data = json.loads(data)

        data['total_points'] = data.get('total_points', 0)
        data['disposal_types'] = data.get('disposal_types', [])
    else:
        data = {
            'total_points': 0,
            'disposal_types': []
        }

    data.update(response.json())

    r.set('user_stat/{}'.format(user_id), json.dumps(data))

    # ensure an entry for every disposal category in the response
    disposal_types = data['disposal_types']
    disposal_type_names = [x['name'] for x in disposal_types]
    for category in disposal_categories:
        if category not in disposal_type_names:
            disposal_types.append({'name': category, 'points': 0})

    return jsonify(data)


@app.route('/kiosk/<int:kiosk_id>')
def route_kiosk(kiosk_id):
    lit_list = r.get('kiosk_lights/{}'.format(kiosk_id))
    if lit_list is not None:
        lit_list = json.loads(lit_list)
    else:
        lit_list = []

    return jsonify({'lit': lit_list})


@app.route('/leaderboard')
def route_leaderboard():
    users = [json.loads(r.get(u)) for u in r.scan_iter(match='user_stat/*')]

    users_by_points = sorted(users, key=lambda u: u.get('total_points', 0))[:10]

    return jsonify({
        'leaderboard': [{'name': u['name'], 'points': u.get('total_points', 0)}
                        for u in users_by_points]
    })
