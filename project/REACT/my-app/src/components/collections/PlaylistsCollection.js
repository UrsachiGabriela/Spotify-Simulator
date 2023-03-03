import React, { Fragment } from "react"
import { TreeTable } from 'primereact/treetable';
import { Toast } from 'primereact/toast';
import { Column } from 'primereact/column';
import dataService from "../../services/data.service";

import { Card } from 'primereact/card';
import { Button } from 'primereact/button';
import '../../index.css'
import SimpleSong from "../elements/SimpleSong";



class PlaylistsCollection extends React.Component{


    constructor(props) {
        super(props);
        this.state = {
            playlists: [],
            activeSongs:[],
            detailedSong:null
        };

        this.setActiveSongs=this.setActiveSongs.bind(this);
        this.setDetailedSong=this.setDetailedSong.bind(this);
        // this.onSelect = this.onSelect.bind(this);
        // this.onUnselect = this.onUnselect.bind(this);
    }


    componentDidMount() {
        dataService.getMyPlaylists(this.props.token)
        .then((response)=>{
            // console.log(response.data)
            if(response.data){
                this.setState({
                    playlists : response.data._embedded.playlistResponses
                })
               
            }
        })
        .catch((error)=>{
            //console.log("ERROR: "+error.data)
            this.props.handleError(error)
        })
       
    }

    setActiveSongs(playlist){
        
        this.setState({
            activeSongs:playlist.favSongs,
            detailedSong:null
        })
        this.props.onPlaylistSelected(playlist)
    }

    setDetailedSong(song){
        console.log(song.name)
        this.setState({detailedSong:song})
    }

    render(){
        const {detailedSong }= this.state
        console.log("DETAILED "+JSON.stringify(detailedSong))
        const playlists = this.state.playlists.map(
            playlist => 
            <Button key={playlist.id} className="m-0" style={{lineHeight: '1.5'}} onClick= {(e) => (this.setActiveSongs(playlist)
                
            )}>
                <Card key={playlist.id} title={playlist.name} style={{ width: '25rem', marginBottom: '2em' }}>
                </Card>
            </Button>

            
        )

        const activeSongs = this.state.activeSongs.map(
            song => <ul className="block" key={song.id}>
                <h4 className="link" key={song.id} onClick={()=>this.setDetailedSong(song)} > {song.name}</h4>
            </ul>
        )

        return (
        <Fragment>
            <div className="block">
                {playlists}
            </div>
            <hr></hr>
            <div className="block2">
                <div>
                    {activeSongs}
                </div>
                {detailedSong && <SimpleSong handleError={this.props.handleError} songUrl={detailedSong.link}></SimpleSong>}
            </div>
            <hr></hr>
            

            
        </Fragment>
        )
    }
}

export default PlaylistsCollection