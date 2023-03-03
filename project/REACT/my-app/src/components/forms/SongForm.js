import React from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { InputTextarea } from "primereact/inputtextarea";
import { RadioButton } from "primereact/radiobutton";
import { InputNumber } from "primereact/inputnumber";
import { Fragment } from "react";
import { Button } from "primereact/button";
import { Dropdown } from 'primereact/dropdown';
import dataService from "../../services/data.service";
import { MultiSelect } from 'primereact/multiselect';
import { Checkbox } from 'primereact/checkbox';


class SongForm extends React.Component{

    constructor(props){
        super(props);

        this.onChangedName = this.onChangedName.bind(this);
        this.addSong=this.addSong.bind(this);
        this.addArtists = this.addArtists.bind(this);

        this.genres = [
            'HEAVY_METAL',
            'ROCK',
            'POP',
            'COUNTRY'
        ];

        this.state={
            name:"",
            genre:'ROCK',
            year:"0",
            type:"SONG",
            parentId:null,
            artists:[],
            albums:[]
        }
    }

    componentDidMount(){
        let params={}
        params["page"]= 0;
        params["size"] = 100;

        dataService.getAllSongs(params) 
            .then((response) => {
                if(response.data && response.data._embedded){
                    const songs = response.data._embedded.songResponseList;
                           
                    var albums = songs.filter(function (el)
                    {
                      return el.type == "ALBUM"
                    }

                    );
                    this.setState({
                        albums:albums
                    });
                }

          })
          .catch((e) => {
            console.log(e);
          });

        // dataService.getAllArtists(params)
        //     .then((response) => {
        //         if(response.data && response.data._embedded){
        //             const artists = response.data._embedded.artistResponseList;
                        
        //             var activeArtists = artists.filter(function (el)
        //             {
        //                 return el.active == true
        //             }

        //             );
        //             this.setState({
        //                 artists:artists
        //             });
        //         }

        //     })
        //     .catch((e) => {
        //         console.log(e);
        //     });

    }

    onChangedName(e){
        this.setState({
            name: e.target.value
          });     
    }

    onChangedGenre(e){
        this.setState({
            genre: e.target.value
          });  
    }



    addArtists(e){
        const artists = e.target.value

        this.setState({
            artists: artists.split(",")
        })

        console.log(artists.split(","))


    }

    addSong(e){
        e.preventDefault();
        const song ={
            name:this.state.name,
            genre:this.state.genre,
            year:this.state.year,
            type:this.state.type,
            parentId:this.state.parentId != 'None'? this.state.parentId : null,
            artists:this.state.artists
        }

        console.log(song)
        this.props.addSong(song);
    }


    render(){

        const albums = this.state.albums.map(
            album => <option key={album.id} value={album.id}>{album.name}</option>
        )



        return (
            <Fragment>
                <form onSubmit={this.addSong}>
                <div className="content-section implementation">
                    <div className="card">
                        <div className="p-fluid grid">
                            <div className="field col-12 md:col-4">
                                <span className="p-float-label">
                                    <label htmlFor="inputtext">Name</label>
                                    <br></br>
                                    <InputText id="inputtext" value={this.state.name} onChange={(e) => this.setState({ name: e.target.value })} />
                                    
                                </span>
                            </div>
                            <div className="field col-12 md:col-4">
                                <span className="p-float-label">
                                    <label htmlFor="dropdown">Genre</label>
                                    <br></br>
                                    <select name="genres" id="genres" onChange={(e) => this.setState({ genre: e.target.value })} >
                                        <option value="ROCK">ROCK</option>
                                        <option value="HEAVY_METAL">HEAVY_METAL</option>
                                        <option value="POP">POP</option>
                                        
                                        <option value="COUNTRY">COUNTRY</option>
                                    </select>
                                </span>
                            </div>
                            <br></br>
                            <div className="field col-12 md:col-4">
                                <span className="p-float-label">
                                    <label htmlFor="inputnumber">Year</label>
                                    <br></br>

                                    <InputNumber inputId="inputnumber" value={this.state.year} onChange={(e) => this.setState({ year: e.value })} />
                                </span>
                            </div>
                            <div className="field col-12 md:col-4">
                                <span className="p-float-label">
                                    <label htmlFor="types">Type</label>
                                    <br></br>
                                    <select name="types" id="types" onChange={(e) => this.setState({ type: e.target.value })} >
                                        <option value="SONG">SONG</option>
                                        <option value="ALBUM">ALBUM</option>
                                    </select>
                                </span>
                            </div>

                            {this.state.type != 'ALBUM' && 
                            (
                                <div className="field col-12 md:col-4">
                                    <span className="p-float-label">
                                        <label htmlFor="albums">Album</label>
                                        <br></br>
                                        <select name="albums" id="albums" onChange={(e) => this.setState({ parentId: e.target.value })} >
                                            {albums}
                                            <option key="a" value="a">None</option>
                                        </select>
                                        
                                    </span>
                                </div>
                            )
                            
                            } 
                            <br></br>
                            <div className="field col-12 md:col-4">
                                <span className="p-float-label">
                                    <label htmlFor="inputtext">Artists: insert them separated by comma</label>
                                    <br></br>
                                    <InputText id="inputtext" onChange={this.addArtists} />
                                </span>
                            </div>
   
                        </div>

                    </div>


                </div>

                    <input type="submit" value="Add song" data-test="submit" />
                </form>

            </Fragment>

 
        )
    }
}

export default SongForm;