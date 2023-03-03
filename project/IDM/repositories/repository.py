from base.sql_base import Session


class Repository:
    __session = None

    @staticmethod
    def get_session():
        if Repository.__session is None:
            __session = Session()
        return __session
