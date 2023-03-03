import { Card } from "primereact/card";
import React from "react"
import dataService from "../../services/data.service";

class SimpleSong extends React.Component{
    constructor(props){
        super(props);


        this.state = {
            id:null,
            name:"" ,
            genre:"",
            year:null,
            type:"",
            artists:[],
            innerSongs:[] ,

            song:null
        };

    }

    componentDidMount(){
        console.log("in simple song: "+this.props.songUrl)
        this.getSongByUrl(this.props.songUrl)
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevProps.songUrl !== this.props.songUrl) {
            this.getSongByUrl(this.props.songUrl)
        }
      }
    // componentDidUpdate(){
    //     console.log("in update")
    //     this.getSongByUrl(this.props.songUrl)
    // }

    getSongByUrl(url){
        dataService.getSongByUrl(url)
        .then((response)=>{
            this.setState({song:response.data})
        })
        .catch((error)=>{
            this.props.handleError(error)
        })
    }
    render(){
        const {song} = this.state;

        return(
            <div>
                {song && (
                    <Card>
                        <h3>Selected song</h3>
                        <p><b>ID:</b> {song.id}</p>
                        <p><b>NAME:</b> {song.name}</p>
                        <p><b>TYPE:</b> {song.type}</p>
                        <p><b>YEAR:</b> {song.year}</p>
                    </Card>

                )}
            
            </div>
         
            
        )
    }
}

export default SimpleSong