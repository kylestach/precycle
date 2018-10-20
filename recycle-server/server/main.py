from flask import Flask
app = Flask(__name__)

print('Hello world')

@app.route('/test')
def route_test():
    return 'Hello, world!'




