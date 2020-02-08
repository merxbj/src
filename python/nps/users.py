from webargs import fields
from webargs.flaskparser import use_kwargs, parser
from flask_restful import Resource, abort

users = [
    {
        "name": "Jarda",
        "age": 32,
        "occupation": "SSE"
    },
    {
        "name": "Psenda",
        "age": 42,
        "occupation": "Manager"
    },
    {
        "name": "Filip",
        "age": 22,
        "occupation": "JSE"
    },
]


class User(Resource):
    get_args = {
        'name': fields.Str(
            required=True,
        ),
    }

    @use_kwargs(get_args)
    def get(self, name):
        for user in users:
            if user["name"] == name:
                return user, 200
        return "User not found", 404

    post_args = {
        'name': fields.Str(
            required=True,
        ),
        'age': fields.Int(
            required=True,
        ),
        'occupation': fields.Str(
            required=True,
        ),
    }

    @use_kwargs(post_args)
    def post(self, name, age, occupation):
        for user in users:
            if user["name"] == name:
                return "User with name {} already exists".format(name), 400

        user = {
            "name": name,
            "occupation": occupation,
            "age": age
        }

        users.append(user)
        return user, 201

    put_args = post_args

    @use_kwargs(put_args)
    def put(self, name, age, occupation):
        for user in users:
            if user["name"] == name:
                user["age"] = age
                user["occupation"] = occupation
                return user, 200

        user = {
            "name": name,
            "occupation": occupation,
            "age": age
        }

        users.append(user)
        return user, 201

    delete_args = get_args

    @use_kwargs(delete_args)
    def delete(self, name):
        global users
        users = [user for user in users if user["name"] != name]
        return "{} is deleted.".format(name), 200


# This error handler is necessary for usage with Flask-RESTful.
@parser.error_handler
def handle_request_parsing_error(err):
    abort(422, errors=err.messages)
