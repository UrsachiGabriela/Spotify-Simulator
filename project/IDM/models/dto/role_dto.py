from spyne import ComplexModel, Integer, String


class RoleDTO(ComplexModel):
    rid = Integer()
    rname = String()

    def __init__(self,rid,rname):
        super(RoleDTO,self).__init__()
        self.rid =rid
        self.rname=rname


