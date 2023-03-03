import collections
import json
import uuid
from datetime import datetime, timedelta

import spyne
from jwcrypto import jwk, jws
from jwcrypto.common import json_encode
from munch import DefaultMunch

from models.dto.authorize_response import AuthorizeResp
from repositories.user_repository import get_user_by_id

ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30
SECRET_KEY = jwk.JWK.generate(kty='oct', size=256)

blacklist=[]

# created this method for both local and remote call possibility of authorize functionality
def auth(access_token):
    # validate integrity + expiry date
    response = validate_token(access_token)
    if not response:
        raise spyne.Fault(faultcode='Client', faultstring="Invalid token")

    # validate roles
    obj = DefaultMunch.fromDict(response)
    if not verify_roles(obj.sub, obj.roles):
        raise spyne.Fault(faultcode='Client', faultstring="Invalid token")

    return AuthorizeResp(obj.sub, obj.roles)



def verify_roles(uid, uroles):
    user = get_user_by_id(uid)

    if user is None:
        return False

    roles = list(map(lambda role: role.rname, user.roles))

    uroles.sort()
    roles.sort()

    if collections.Counter(uroles) == collections.Counter(roles):
        return True

    return False

def get_expiration_time(expires_delta: timedelta):
    if expires_delta:
        expire = datetime.now() + expires_delta
    else:
        expire = datetime.now() + timedelta(minutes=15)

    return expire


def create_access_token(userId: int, userRoles: [],
                        expires_delta: timedelta | None = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)):
    claims = {}
    claims["iss"] = "http://127.0.0.1:8000"
    claims["sub"] = userId
    claims["exp"] = get_expiration_time(expires_delta).isoformat()
    claims["jti"] = str(uuid.uuid1())
    claims["roles"] = userRoles

    jwt = json.dumps(claims)
    header = {"alg": ALGORITHM, "typ": "JWT"}
    # header=json.dumps(header)

    jws_token = jws.JWS(payload=jwt.encode('utf-8'))
    jws_token.add_signature(SECRET_KEY, protected=json_encode(header))

    return jws_token.serialize(compact=True)


def validate_token(access_token: str):

    try:
        j = jws.JWS()
        j.deserialize(access_token)
    except:
        return False

    # verify if token is already blacklisted
    if is_in_blacklist(access_token):
        return False

    # validate integrity
    try:
        j.verify(SECRET_KEY, ALGORITHM)
    except:
        add_to_blacklist(access_token)
        return False


    # verify expiry_date
    payload = json.loads(j.payload)
    if (datetime.fromisoformat(payload['exp']) < datetime.now()):
        add_to_blacklist(access_token)
        return False

    user_roles= {'sub':payload["sub"],'roles': payload["roles"]}
    return user_roles



def add_to_blacklist(token:str):
    blacklist.append(token)

def is_in_blacklist(token:str):
    return token in blacklist

