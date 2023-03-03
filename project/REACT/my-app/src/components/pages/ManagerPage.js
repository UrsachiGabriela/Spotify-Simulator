import React, { Fragment } from "react";
import SongCollection from "../collections/SongCollection";
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { Toolbar } from "primereact/toolbar";
import { Dialog } from "primereact/dialog";
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { Navigate } from "react-router-dom";
import axios from "axios";
import '../../index.css'
import SongForm from "../forms/SongForm";
import ArtistCollection from "../collections/ArtistCollection";
import ArtistForm from "../forms/ArtistForm.js";
import uuidService from "../../services/uuid.service";



class ManagerPage extends React.Component{
    constructor(props){
        super(props);

        this.onChangePage = this.onChangePage.bind(this);
        this.onItemSelected=this.onItemSelected.bind(this);
        this.confirmDeleteSelected = this.confirmDeleteSelected.bind(this);
        this.hideDeleteItemDialog = this.hideDeleteItemDialog.bind(this);
        this.openNew = this.openNew.bind(this);
        this.handleError=this.handleError.bind(this);
        this.addSong=this.addSong.bind(this);
        this.addArtist=this.addArtist.bind(this);

        this.state = {
           
             
            selectedPage:"songs",
            selectedItemType:"",
            selectedItem:null,

            deleteItemDialog: false,

            redirect:null,
            redirectMessge:null,
            redirectBack:null,

            // submitted: false,
            itemDialog:false
        };

    }

    onChangePage(page){
        this.setState({
            selectedPage:page
        })
    }

    onItemSelected(e){
        const item=e;
        this.setState({   
            selectedItem: item
        })
    }



    confirmDeleteSelected() {
        this.setState({ deleteItemDialog: true });
    }

    hideDeleteItemDialog() {
        this.setState({ deleteItemDialog: false });
    }

    deleteItem = () => {
        const {selectedItem} = this.state
        const deleteUrl = selectedItem._links.self.href;
        const headers = { 
            'Authorization': `Bearer ${this.props.token}`
          };


          axios.delete(deleteUrl,{headers})
          .then((response)=>{
              console.log(JSON.stringify(response))
              this.toast.show({ severity: 'success', summary: 'Successful', detail: 'Item Deleted', life: 1000 });
          })
          .catch((error)=>{
              this.handleError(error)
          })
        
        this.setState({ deleteItemDialog: false });

    }

    handleError(error){
        if(error.response){
            if(error.response.status == 401){
                this.setState({
                    redirect:true,
                    redirectMessge:"Please sign in to continue",
                    redirectBack:window.location.href
                })
            }
            else{
                if(error.response.data)
                {
                    const errorDetails = error.response.data.details
                    this.toast.show({severity:"error", summary:"ERROR",detail:errorDetails,life:2000})
                }
                else{
                    this.toast.show({severity:"error", summary:"ERROR",detail:error.response.status,life:2000})
                }
            }
        }
    }

    openNew() {
        this.setState({
            itemDialog: true
        });
    }


    hideDialog() {
        this.setState({
            itemDialog: false
        });
    }

    addSong(song){
        const addSongUrl = "http://localhost:8080/api/songcollection/songs"
        const headers = { 
            'Authorization': `Bearer ${this.props.token}`
          };

        axios.post(addSongUrl,song,{headers})
        .then((response) => {
            console.log(response.data)
            this.toast.show({ severity: 'success', summary: 'Successful', detail: 'Song created', life: 1000 });
            this.setState({
                itemDialog:false
            })
        })
        .catch((error)=>{
            this.handleError(error)
        })
    }

    addArtist(artist){
        const uuid = uuidService.generateUUID()
        const addArtistUrl = "http://localhost:8080/api/songcollection/artists"+"/"+uuid
        const headers = { 
            'Authorization': `Bearer ${this.props.token}`
        };

        axios.put(addArtistUrl,artist,{headers})
        .then((response) => {
            console.log(response.data)
            this.toast.show({ severity: 'success', summary: 'Successful', detail: 'Artist created', life: 1000 });
            this.setState({
                itemDialog:false
            })
        })
        .catch((error)=>{
            this.handleError(error)
        })
    }

    render(){
        const {selectedPage,songs,selectedItem} = this.state
        const {redirect,redirectBack,redirectMessge,itemDialog} = this.state;

        const leftToolbarTemplate = (
            <React.Fragment>
            <Button label="New" icon="pi pi-plus" className="p-button-success mr-2" onClick={this.openNew} />
            <Button label="Delete" icon="pi pi-trash" className="p-button-danger" onClick={this.confirmDeleteSelected} disabled={!selectedItem} />
        </React.Fragment>
        )


        const deleteItemDialogFooter = (
            <React.Fragment>
                <Button label="No" icon="pi pi-times" className="p-button-text" onClick={this.hideDeleteItemDialog} />
                <Button label="Yes" icon="pi pi-check" className="p-button-text" onClick={this.deleteItem} />
            </React.Fragment>
        );



        return (
            <Fragment>
                <div className="block">
                    <div className="sidenav navbar-dark bg-dark">
                        <a href="#songs" onClick={()=>{this.onChangePage("songs")}} >Songs</a>
                        <a href="#artists" onClick={()=>{this.onChangePage("artists")}}>Artists</a>
                    </div>

                    <div className="datatable-crud-demo">
                        <Toast className="p-dialog" ref={(el) => this.toast = el} />

                        <div className="card">
                            <Toolbar className="mb-4" left={leftToolbarTemplate}></Toolbar>

                            {selectedPage == "songs" && <SongCollection onSongSelected={this.onItemSelected}></SongCollection>}
                            {selectedPage == "artists" && <ArtistCollection onArtistSelected={this.onItemSelected}></ArtistCollection>}
                            
                        </div>

                        <Dialog className="p-dialog" visible={this.state.deleteItemDialog} style={{ width: '450px' }} header="Confirm"  modal footer={deleteItemDialogFooter} onHide={this.hideDeleteItemDialog} breakpoints={{ "960px": "75vw" }} >
                            <div className="confirmation-content">
                                <i className="pi pi-exclamation-triangle mr-3" style={{ fontSize: '2rem'}} ></i>
                                {selectedItem && <span>Are you sure you want to delete <b>{selectedItem.name}</b>?</span>}
                            </div>
                        </Dialog>

                    </div>
                   
                   
                   {selectedPage == "songs" && itemDialog && (<SongForm addSong={this.addSong}></SongForm>)}
                   {selectedPage == "artists" && itemDialog && (<ArtistForm addArtist={this.addArtist}></ArtistForm>)}
                </div>

                {redirect && < Navigate to="/login" state={redirectMessge} replace={true} />}

                {/* {selectedItem && selectedItem._links.self.href} */}
            </Fragment>
        )
    }
}

export default ManagerPage;