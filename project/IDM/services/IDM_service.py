
import spyne
from spyne import Application, rpc, ServiceBase, String, Boolean, Array, Iterable

from models.dto.authorize_response import AuthorizeResp
from models.dto.role_dto import RoleDTO
from models.dto.user_dto import UserDTO
from repositories.role_repository import *
from repositories.user_repository import *
from utils import security
from utils.password_ops import encode, match, is_valid
from utils.roles_code import Roles


class IDMService(ServiceBase):

    @rpc(String, String, _returns=String)
    def register_user(ctx, uname, upass):
        role = get_role_by_name(Roles.CLIENT.name)

        if not is_valid(upass):
            raise spyne.Fault(faultcode='Client', faultstring="Too weak password")

        encoded_password = encode(upass)
        create = create_user(uname, encoded_password, role)

        if create is True:
            return "Successfully created"

        if "users_UK" in create:
            raise spyne.Fault(faultcode='Client', faultstring="This username already exists")

        raise spyne.Fault(faultcode='Server', faultstring="Could not save this user")

    # admin can create CONTENT MANAGERS
    # content manager can create ARTISTS
    @rpc(String, String, String, String, _returns=String)
    def create_user(ctx, access_token, uname, upass, urole):
        auth_response = security.auth(access_token)

        role = get_role_by_name(urole)
        if role is None:
            raise spyne.Fault(faultcode='Client', faultstring="Invalid role")

        if role.rname not in [Roles.CLIENT.name, Roles.ARTIST.name, Roles.CONTENT_MANAGER.name]:
            raise spyne.Fault(faultcode='Client', faultstring="Invalid role")

        if (role.rname == Roles.CONTENT_MANAGER.name) and (Roles.APP_ADMIN.name not in auth_response.roles):
            raise spyne.Fault(faultcode='Client', faultstring="Forbidden")

        if (role.rname == Roles.ARTIST.name) and (Roles.CONTENT_MANAGER.name not in auth_response.roles):
            raise spyne.Fault(faultcode='Client', faultstring="Forbidden")

        if not is_valid(upass):
            raise spyne.Fault(faultcode='Client', faultstring="Too weak password")

        encoded_password = encode(upass)
        # role = get_role_by_name(Roles.CONTENT_MANAGER.name)
        create = create_user(uname, encoded_password, role)

        if create is True:
            return str(get_user_by_name(uname).uid) + ": "+uname

        if "users_UK" in create:
            raise spyne.Fault(faultcode='Client', faultstring="This username already exists")

        raise spyne.Fault(faultcode='Server', faultstring="Could not save this user")

    # this action is permitted to account owner or to app admin(in case of forgotten pass)
    @rpc(String, String, String, String, _returns=String)
    def change_upass(ctx, access_token, uname, old_pass, new_pass):
        auth_response: AuthorizeResp = security.auth(access_token)

        # get user identified by uname
        user = get_user_by_name(uname)

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        if (user.uid != auth_response.sub) and (Roles.APP_ADMIN.name not in auth_response.roles):
            raise spyne.Fault(faultcode='Client', faultstring="Forbidden")

        # if password is changed by app admin, he is not required to know the old password for that user
        if (user.uid == auth_response.sub) and (not match(old_pass, user.upass)):
            raise spyne.Fault(faultcode='Client', faultstring="Incorrect old password")

        if old_pass == new_pass:
            raise spyne.Fault(faultcode='Client', faultstring="New password cannot be the same as old password")

        if update_password(user, encode(new_pass)) is True:
            return "Password successfully updated"

        raise spyne.Fault(faultcode='Server', faultstring="Could not change the password for given user")

    @rpc(String, String, String, _returns=String)
    def add_user_role(ctx, access_token, uname, new_role):
        auth_response: AuthorizeResp = security.auth(access_token)

        if Roles.APP_ADMIN.name not in auth_response.roles:
            raise spyne.Fault(faultcode='Client', faultstring="Forbidden")

        user = get_user_by_name(uname)
        role = get_role_by_name(new_role)  # to verify if new role name is valid

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        if role is None:
            raise spyne.Fault(faultcode='Client', faultstring="This role doesn't exist")

        if update_user_roles(user, role) is True:
            return "Successfully updated"

        raise spyne.Fault(faultcode='Server', faultstring="Could not update roles for given user")

    @rpc(String, String, String, _returns=String)
    def remove_user_role(ctx, access_token, uname, removed_role):
        auth_response: AuthorizeResp = security.auth(access_token)

        if Roles.APP_ADMIN.name not in auth_response.roles:
            raise spyne.Fault(faultcode='Client', faultstring="Forbidden")

        user = get_user_by_name(uname)
        role = get_role_by_name(removed_role)  # to verify if new role name is valid

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        if role is None:
            raise spyne.Fault(faultcode='Client', faultstring="This role doesn't exist")

        delete_status = delete_user_role(user, role)
        if delete_status is True:
            return "Successfully deleted"

        if "not in list" in delete_status:
            raise spyne.Fault(faultcode='Client', faultstring="The role has not been assigned to this user")

        raise spyne.Fault(faultcode='Server', faultstring="Could not remove roles for given user")

    @rpc(String, String, _returns=String)
    def remove_user(ctx, access_token, uname):
        auth_response: AuthorizeResp = security.auth(access_token)

        if Roles.APP_ADMIN.name not in auth_response.roles:
            raise spyne.Fault(faultcode='Client', faultstring="Forbidden")

        user = get_user_by_name(uname)

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        if delete_user(user) is True:
            return "Successfully deleted"

        raise spyne.Fault(faultcode='Server', faultstring="Could not remove roles for given user")

    @rpc(String, String, _returns=UserDTO)
    def get_user_info(ctx, access_token, uname):
        auth_response: AuthorizeResp = security.auth(access_token)

        # get user identified by uname
        user = get_user_by_name(uname)

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        # only the administrator and the user himself can see the details related to the account
        if (user.uid != auth_response.sub) and (Roles.APP_ADMIN.name not in auth_response.roles):
            raise spyne.Fault(faultcode='Client', faultstring="Forbidden")

        roles = []
        for role in user.roles:
            dto = RoleDTO(role.rid, role.rname)
            roles.append(dto)

        result = UserDTO(user.uid, user.uname, roles)
        return result

    @rpc(String, _returns=Array(UserDTO))
    def list_users(ctx, access_token):
        auth_response: AuthorizeResp = security.auth(access_token)

        if Roles.APP_ADMIN.name not in auth_response.roles:
            raise spyne.Fault(faultcode='Client', faultstring="Forbidden")

        users = get_users()
        result = []

        for user in users:
            roles = []
            for role in user.roles:
                dto = RoleDTO(role.rid, role.rname)
                roles.append(dto)

            result.append(UserDTO(user.uid, user.uname, roles))

        return result

    @rpc(String, _returns=Iterable(RoleDTO))
    def list_roles(ctx, access_token):
        # this method only needs authorized user, no matter what role he has (just not GUEST)
        security.auth(access_token)

        roles = get_roles()
        result = []

        for role in roles:
            dto = RoleDTO(role.rid, role.rname)
            result.append(dto)

        return result

    @rpc(String, String, _returns=String)
    def login(ctx, uname, upass):
        user = get_user_by_name(uname)

        if user is not None:
            password = user.upass
            if match(upass, password):
                roles_name = list(map(lambda role: role.rname, user.roles))
                return security.create_access_token(user.uid, roles_name)
        return "False"

    @rpc(String, _returns=AuthorizeResp)
    def authorize(ctx, access_token):
        return security.auth(access_token)

    @rpc(String, _returns=Boolean)
    def logout(ctx, access_token):
        security.add_to_blacklist(access_token)
        return True

