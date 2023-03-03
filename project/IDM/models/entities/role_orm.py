from sqlalchemy import Column, String, Integer, Table, ForeignKey
from base.sql_base import Base

# movies_actors_association = Table(
#     'users_roles', Base.metadata,
#     Column('user_id', Integer, ForeignKey('users.id')),
#     Column('role_id', Integer, ForeignKey('roles.id'))
# )


class Role(Base):
    __tablename__ = 'roles'

    rid = Column(Integer, primary_key=True)
    rname = Column(String)

    def __init__(self, value):
        self.rname = value

    # def __eq__(self, other):
    #     return (self.rname == other or
    #             self.rname == getattr(other, 'name', None))
    #
    # def __ne__(self, other):
    #     return not self.__eq__(other)
    #
    # def __hash__(self):
    #     return hash(self.rname)