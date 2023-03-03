import React from "react";
import { InputText } from "primereact/inputtext";

import { Fragment } from "react";
import SimpleSong from "../elements/SimpleSong";



class PlaylistForm extends React.Component{

    constructor(props){
        super(props);

        console.log(props)



        this.addNewPlaylist=this.addNewPlaylist.bind(this);


        this.state = {
            name:"",
            active:true
        }
    }




    addNewPlaylist(e){
        e.preventDefault();
        const {name} = this.state
        const playlist ={
            name:name
        }

        console.log("playlist "+playlist)


        this.props.addPlaylist(playlist);
    }


    render(){


        return (
            <Fragment>
                <form onSubmit={this.addNewPlaylist}>
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
                            <br></br>
                        
                        </div>
                    </div>

                </div>

                    <input type="submit" value="Create playlist" data-test="submit" />
                </form>

 
            </Fragment>

 
        )
    }
}

export default PlaylistForm;