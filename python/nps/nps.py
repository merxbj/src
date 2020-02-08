import random
import string

from flask import Flask, request
from functools import wraps
from flask_restful import Api, Resource, abort
from webargs import fields, validate
from webargs.flaskparser import use_kwargs, parser
from datetime import datetime

app = Flask(__name__)
api = Api(app)

# Some static header data for testing
apikeys = {
    "123",
    "ABC",
    "XYZ"
}

sites = {
    "901",
    "111",
    "001"
}


# Decorator function that ensures that appropriate HTTP Headers are present (Authorization and siteid)
def require_headers(view_function):
    @wraps(view_function)
    # the new, post-decoration function. Note *args and **kwargs here.
    def decorated_function(*args, **kwargs):
        if request.headers.get('Authorization') and request.headers.get('Authorization') in apikeys:
            if request.headers.get('siteid') and request.headers.get('siteid') in sites:
                return view_function(*args, **kwargs)
            else:
                abort(422)
        else:
            abort(401)

    return decorated_function


# This error handler is necessary for webargs usage with Flask-RESTful.
@parser.error_handler
def handle_request_parsing_error(error, req, schema, status_code, headers):
    abort(422, errors=error.messages)


# Some static washcode data for testing
washcodes = [
    {
        "Code": "ABCD1234",
        "Credit": 2000.0,
        "LimitCount": 0,
        "ProductRestriction": 0,
        "Expiry": "2019-03-31",
        "CreatedTime": "2018-03-31:00:00:00"
    },
    {
        "Code": "EFGH5678",
        "Credit": 0.0,
        "LimitCount": 50,
        "ProductRestriction": 1,
        "Expiry": "2019-05-31",
        "CreatedTime": "2010-03-31:00:00:00"
    },
]


# /washcodes API
class Washcodes(Resource):
    get_args = {
        "code": fields.String(required=False, missing=None),
        "fromDateTime": fields.DateTime(required=False, missing=None)
    }

    @use_kwargs(get_args)
    @require_headers
    def get(self, code, fromDateTime):
        if fromDateTime:
            result = [washcode for washcode in washcodes
                      if datetime.fromisoformat(washcode["CreatedTime"]) > fromDateTime]
        else:
            result = [washcode for washcode in washcodes if not code or washcode["Code"] == code]
        return result, 200

    post_args = {
        "Washcode": fields.Nested(
            {
                "credit": fields.Float(required=False, missing=0.0),
                "productRestriction": fields.Integer(required=False, missing=0),
                "limitCount": fields.Integer(required=False, missing=0),
                "expiry": fields.DateTime(required=False, missing=None)
            },
            location="json", required=True)
    }

    @use_kwargs(post_args)
    @require_headers
    def post(self, Washcode):

        washcode = {
            "Code": ''.join(random.choices(string.ascii_uppercase + string.digits, k=10)),
            "Credit": Washcode["credit"],
            "LimitCount": Washcode["limitCount"],
            "ProductRestriction": Washcode["productRestriction"],
            "Expiry": Washcode["expiry"].isoformat(),
            "CreatedTime": datetime.utcnow().isoformat()
        }

        washcodes.append(washcode)

        return washcode, 200

    del_args = {
        "code": fields.String(required=True)
    }

    @use_kwargs(del_args)
    @require_headers
    def delete(self, code):
        for washcode in washcodes:
            if washcode["Code"] == code:
                washcodes.remove(washcode)
                return "{}", 200
        return "{ error : {errorCode : 40401, errorMessage : ""code not found!""} }", 404


api.add_resource(Washcodes, "/washcodes")


# Some static washkey data for testing
washkeys = [
    {
        "Id": "00001A6AE124",
        "Name": "Name?",
        "Credit": 1000.0,
        "ProductRestriction": 0,
        "Active": True,
        "Expiry": "2019-03-31",
        "CreatedTime": "2018-03-31:00:00:00"
    },
    {
        "Id": "00001B6AF135",
        "Name": "Name Two?",
        "Credit": 2000.0,
        "ProductRestriction": 0,
        "Active": True,
        "Expiry": "2019-05-31",
        "CreatedTime": "2018-01-31:00:00:00"
    },
    {
        "Id": "000009699111",
        "Name": "Name Three?",
        "Credit": 0.0,
        "ProductRestriction": 0,
        "Active": False,
        "Expiry": "2018-03-31",
        "CreatedTime": "2017-03-31:00:00:00"
    }
]


# /washkeys API
class Waskeys(Resource):
    get_args = {
        "id": fields.String(required=False, missing=None),
        "fromDateTime": fields.DateTime(required=False, missing=None),
        "active": fields.Boolean(required=False, missing=None),
        "name": fields.String(required=False, missing=None)
    }

    @use_kwargs(get_args)
    @require_headers
    def get(self, id, fromDateTime, active, name):
        if fromDateTime:
            result = [washkey for washkey in washkeys
                      if datetime.fromisoformat(washkey["CreatedTime"]) > fromDateTime]
        elif active:
            result = [washkey for washkey in washkeys if washkey["Active"] == active]
        elif name:
            result = [washkey for washkey in washkeys if name.lower() in washkey["Name"].lower()]
        else:
            result = [washkey for washkey in washkeys if not id or washkey["Id"] == id]
        return result, 200

    post_args = {
        "washKey": fields.Nested(
            {
                "id": fields.String(required=True),
                "credit": fields.Integer(required=True),
                "productrestriction": fields.Integer(required=False, missing=0),
                "name": fields.String(required=False, missing=""),
                "active": fields.Boolean(required=False, missing=True),
                "expiry": fields.Date(required=False, missing=None)
            },
            location="json", required=True),
        "paymentType": fields.String(required=False, missing="", location="json")
    }

    @use_kwargs(post_args)
    @require_headers
    def post(self, washKey, paymentType):

        for washkey in washkeys:
            if washkey["Id"] == washKey["id"]:
                return "{ error : {errorCode : 40901, errorMessage : ""washkey already exists""}", 409

        washkey = {
            "Id": washKey["id"],
            "Name": washKey["name"],
            "Credit": washKey["credit"],
            "ProductRestriction": washKey["productrestriction"],
            "Active": washKey["active"],
            "Expiry": washKey["expiry"].isoformat(),
            "CreatedTime": datetime.utcnow().isoformat()
        }

        washkeys.append(washkey)

        return "{}", 200


api.add_resource(Waskeys, "/washkeys")


# And here we go!
app.run(debug=True, port=5000, host="localhost")
