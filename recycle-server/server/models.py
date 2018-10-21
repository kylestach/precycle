import flask.db
class User(db.Model):
    id = db.Column(db.Integer, primary_key = True)
    hackgt_id = db.Column(db.Integer
