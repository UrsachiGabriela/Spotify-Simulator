from spyne import ComplexModel, Integer, String,Array


class AuthorizeResp(ComplexModel):
    sub = Integer()
    roles = Array(String)

    def __init__(self,sub,roles):
        super(AuthorizeResp, self).__init__()
        self.sub =sub
        self.roles=roles
