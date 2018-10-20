import json
import os

import requests
from flask import Flask, request, jsonify

import redis

redis_url = os.getenv('REDIS_URL', 'redis://localhost:6379/0')
r = redis.from_url(redis_url, decode_responses=True)

app = Flask(__name__)

print('Hello world')


def fail(message, code=500):
    return jsonify({'success': False, 'message': message}), code


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

    data = response.json()

    r.set('user_stat/{}'.format(user_id), json.dumps(data))

    return jsonify(data)
