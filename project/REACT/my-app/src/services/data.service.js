import axios from "axios"
const SONGS_API_URL = "http://localhost:8080/api/songcollection/songs";
const ARTISTS_API_URL = "http://localhost:8080/api/songcollection/artists";
const PLAYLISTS_API_URL = "http://localhost:8081/api/playlistscollection/playlists";

class DataService{
    getAllSongs(params){
        return axios.get(SONGS_API_URL,{params});
    }

    getAllSongsByUrlAndParams(url,params){
        // if(params)
        //     console.log("SERVICE params: "+JSON.stringify(params))
        // console.log("SERVICE url: "+url)
        return axios.get(url,{params});
    }

    deleteSong(url){
        axios.delete(url)
    }

    getSongByUrl(url){
        return axios.get(url)
    }
    
    getAllArtists(params){
        return axios.get(ARTISTS_API_URL,{params});
    }

    getMyPlaylists(token){
        const headers = { 
            'Authorization': `Bearer ${token}`
          };
        return axios.get(PLAYLISTS_API_URL,{headers})
    }
}

export default new DataService()