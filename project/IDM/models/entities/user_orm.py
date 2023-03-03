from sqlalchemy import Column, String, Integer
from base.sql_base import Base
from sqlalchemy.orm import relationship
from models.entities.users_roles_orm import user_roles_relationship


class User(Base):
    __tablename__ = 'users'

    uid = Column(Integer, primary_key=True)
    uname = Column(String)
    upass = Column(String)
    roles = relationship("Role", secondary=user_roles_relationship)

    def __init__(self, username, password):
        self.uname = username
        self.upass = password

    def __hash__(self):
        return hash(self.roles)