from spyne import ComplexModel, Integer, String, Array

from models.dto.role_dto import RoleDTO



class UserDTO(ComplexModel):
    uid = Integer()
    uname = String()
    uroles = Array(RoleDTO)

    def __init__(self,uid,uname,uroles):
        super(UserDTO,self).__init__()
        self.uid =uid
        self.uname=uname
        self.uroles=uroles

