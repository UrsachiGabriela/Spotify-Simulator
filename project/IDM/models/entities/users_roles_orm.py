from sqlalchemy import Column, String, Integer, Date, Table, ForeignKey

from base.sql_base import Base

user_roles_relationship = Table(
    'users_roles', Base.metadata,
    Column('uid', Integer, ForeignKey('users.uid')),
    Column('rid', Integer, ForeignKey('roles.rid')),
    extend_existing=True
)
