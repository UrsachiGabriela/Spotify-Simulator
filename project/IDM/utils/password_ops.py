import bcrypt
import re


def encode(password):
    pass_bytes = password.encode('utf-8')
    salt = bcrypt.gensalt()

    hash_pass = bcrypt.hashpw(pass_bytes, salt)

    return hash_pass


def match(password, hash_password):
    return bcrypt.checkpw(password.encode('utf-8'), hash_password.encode('utf-8'))


#     Has minimum 8 characters in length. Adjust it by modifying {8,}
#     At least one uppercase English letter. You can remove this condition by removing (?=.*?[A-Z])
#     At least one lowercase English letter.  You can remove this condition by removing (?=.*?[a-z])
#     At least one digit. You can remove this condition by removing (?=.*?[0-9])
#     At least one special character,  You can remove this condition by removing (?=.*?[#?!@$%^&*-])
password_pattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$"


def is_valid(password):
    if re.match(password_pattern, password):
        return True

    return False
