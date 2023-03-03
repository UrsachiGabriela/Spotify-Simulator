import {v4 as uuidv4} from 'uuid';

class UUIDService{
    generateUUID(){
        return uuidv4();
    }

}

export default new UUIDService()