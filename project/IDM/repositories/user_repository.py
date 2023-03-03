from models.entities.role_orm import Role
from models.entities.user_orm import User
from base.sql_base import Session

from repositories.repository import Repository

session = Repository.get_session()


def get_users():
    users = session.query(User).all()
    return users


def get_user_by_name(username):
    user = session.query(User).filter(User.uname == username)
    return user.first()

def get_user_by_id(uid):
    user = session.query(User).filter(User.uid == uid)
    return user.first()


def create_user(username, password, role):
    session = Session().object_session(role)

    user = User(username, password)
    user.roles.append(role)

    try:
        session.add(user)
        session.commit()
    except Exception as exc:
        session.rollback()
        return f"Failed to add user - {exc}"

    return True


def update_password(user, new_password):
    user.upass = new_password

    try:
        session.add(user)
        session.commit()
    except Exception as exc:
        session.rollback()
        return f"Failed to update user password - {exc}"

    return True


def update_user_roles(user: User, new_role: Role):
    session.expunge(user)

    new_session = Session().object_session(new_role)
    new_session.add(user)

    user.roles.append(new_role)

    try:
        new_session.add(user)
        new_session.commit()
    except Exception as exc:
        new_session.rollback()
        return f"Failed to update user roles - {exc}"

    return True


def delete_user_role(user: User, removed_role: Role):
    session.expunge(user)

    new_session = Session().object_session(removed_role)
    new_session.add(user)

    try:
        user.roles.remove(removed_role)
        new_session.add(user)
        new_session.commit()
    except Exception as exc:
        new_session.rollback()
        return f"Failed to update user roles - {exc}"

    return True


def delete_user(user: User):
    session.query(User).filter(User.uname == user.uname).delete()

    try:
        session.commit()
    except Exception as exc:
        session.rollback()
        return f"Failed to remove user - {exc}"

    return True
