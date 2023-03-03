import axios from "axios";
import { redirect } from "react-router-dom";
import ErrorPage from "../components/pages/ErrorPage";


const API_URL = "http://localhost:8082/api/spotify/";


class AuthService {


    logout() {
        const user = JSON.parse(localStorage.getItem('user'));
        //console.log("In logout: "+user.accessToken)
        const headers = { 
            'Authorization': `Bearer ${user.accessToken}`
        };
        return axios.post('http://localhost:8082/api/spotify/logout', null,{headers})
        .then(response => {
            // remove user from local storage to log user out
            localStorage.removeItem('user');
            console.log("Logout response: "+response.data)
        })
        
      }

    
      getCurrentUser() {
        return JSON.parse(localStorage.getItem('user'));
      }
}

export default new AuthService()