import React from "react";
import { InputText } from "primereact/inputtext";

import { Fragment } from "react";



class ArtistForm extends React.Component{

    constructor(props){
        super(props);

        console.log(props)


        this.onChangedArtistName = this.onChangedArtistName.bind(this);
        this.onChangedActive = this.onChangedActive.bind(this);
        this.addNewArtist=this.addNewArtist.bind(this);


        this.state = {
            name:"",
            active:true
        }
    }


    onChangedArtistName(e){
        this.setState({
            name: e.target.value
          });     
    }

    onChangedActive(e){
        this.setState({
            genre: e.target.value
          });  
    }


    addNewArtist(e){
        e.preventDefault();
        const {name,active} = this.state
        const artist ={
            name:name,
            active:active
        }

        console.log("ARTIST "+artist)


        this.props.addArtist(artist);
    }


    render(){


        return (
            <Fragment>
                <form onSubmit={this.addNewArtist}>
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
                                    
                                    <br></br>
                                    <select name="active" id="active" onChange={(e) => this.setState({ active: e.target.value })} >
                                        <option value="true">ACTIVE</option>
                                        <option value="false">INACTIVE</option>
                                        
                                    </select>
                                </span>
                            </div>
                            <br></br>
                        
                        </div>
                    </div>

                </div>

                    <input type="submit" value="Add artist" data-test="submit" />
                </form>

            </Fragment>

 
        )
    }
}

export default ArtistForm;