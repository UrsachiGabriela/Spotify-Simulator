import axios from "axios";
import React, { Fragment } from "react";
import dataService from "../../services/data.service";
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import '../../index.css'


const API_URL = "http://localhost:8080/api/songcollection/artists";

class ArtistCollection extends React.Component{
    constructor(props){
        super(props);

        this.onChangeSearchName = this.onChangeSearchName.bind(this);
        this.retrieveArtists= this.retrieveArtists.bind(this);
        //this.onRowSelected=this.onRowSelected.bind(this);
        this.updateLinks=this.updateLinks.bind(this);
        this.onFirstPageSelected=this.onFirstPageSelected.bind(this);
        this.onNextPageSelected=this.onNextPageSelected.bind(this);
        this.onPrevPageSelected=this.onPrevPageSelected.bind(this);
        this.onLastPageSelected=this.onLastPageSelected.bind(this);

        this.state = {
            artists: [],
            searchedName: "",
            pageValue: 0,
            pageSize: 9,

            firstPage:null,
            prevPage:null,
            nextPage:null,
            lastPage:null,  
        };

    }


    componentDidMount(){
        this.retrieveArtists();
    }


    updateLinks(links){
        if(links.prev){
            this.setState({
                prevPage:links.prev.href               
            });
        }
        else{
            this.setState({
                prevPage:null               
              });
        }

        if(links.next){
            this.setState({
                nextPage:links.next.href              
              });
        }
        else{
            this.setState({
                nextPage:null          
              });
        }

        if(links.first){
            this.setState({
                firstPage:links.first.href              
              });
        }
        else{
            this.setState({
                firstPage:null          
              });
        }

        if(links.last){
            this.setState({
                lastPage:links.last.href              
              });
        }
        else{
            this.setState({
                lastPage:null          
              });
        }

    }

    retrieveArtists() {
        const { searchedName, pageValue, pageSize } = this.state;
        const params = this.getRequestParams(searchedName, pageValue, pageSize);


        dataService.getAllArtists(params)
          .then((response) => {
            if(response.data && response.data._embedded){
                const artists = response.data._embedded.artistResponseList;

                this.setState({
                    artists: artists
                });

                if(response.data._links){
                    const links = response.data._links;
                    this.updateLinks(links);
                }

               
            }
            else{
                this.setState({
                    artists: []
                });
            }

          })
          .catch((e) => {
            console.log(e);
          });
    }

    getRequestParams(searchedName, pageValue, pageSize) {
        let params = {};
    
        if (searchedName) {
          params["name"]=searchedName;
        }
    
        if (pageValue>=0) {
          params["page"] = pageValue;
        }
    
        if (pageSize) {
          params["size"] = pageSize;
        }
    
        return params;
      }


    onChangeSearchName(e) {
        const searchedName = e.target.value;
        this.setState({
            searchedName:searchedName,
            pageValue:0
        })
      }



    // onRowSelected(e){
    //     const song = e.data;
    //     console.log(e)
    //     this.setState({
    //         selectedSong: song._links.self.href
    //     }
    //     )
    // }

    onFirstPageSelected(e){
        const firstPage = this.state.firstPage;
        this.setPageProperties(firstPage)
    }

    onLastPageSelected(e){
        const lastPage = this.state.lastPage;
        this.setPageProperties(lastPage)
    }

    onPrevPageSelected(e){
        const prevPage = this.state.prevPage;
        this.setPageProperties(prevPage)
    }


    onNextPageSelected(e){
        const nextPage = this.state.nextPage;
        this.setPageProperties(nextPage)
    }

    setPageProperties(page){
        const pageValue = new URLSearchParams(new URL(page).search).get("page")
        const pageSize = new URLSearchParams(new URL(page).search).get("size")

        this.setState({
            pageValue:pageValue,
            pageSize:pageSize,
        })
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevState.pageValue !== this.state.pageValue) {
            this.retrieveArtists()
        }
      }

    render(){
        const {
            searchedName,
            artists,
            firstPage,
            prevPage,
            currentPage,
            nextPage,
            lastPage,
            pageValue,
            pageSize
          } = this.state;


        const header = (
            <div className="table-header">
                <h5 className="mx-0 my-1">All artists</h5>
                <span className="p-input-icon-left">
                    <i className="pi pi-search" />
                    <div className="input-group mb-3">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Search by name"
                                value={searchedName}
                                onChange={this.onChangeSearchName}
                            />
                            <div className="input-group-append">
                                <button
                                    className="btn btn-outline-secondary"
                                    type="button"
                                    onClick={this.retrieveArtists}
                                >
                                    Search
                                </button>
                            </div>
                        </div>
                </span>
            </div>
        );

        const footer = (
            <div className="table-footer">

                <Button label="First" className="p-button-link" disabled={!firstPage} onClick = {this.onFirstPageSelected}/>
                <Button label="Prev" className="p-button-link" disabled={!prevPage} onClick = {this.onPrevPageSelected}/>
                <Button className="p-button-rounded p-button-info p-button-outlined">{parseInt(pageValue)+1}</Button>
                <Button label="Next" className="p-button-link" disabled={!nextPage} onClick = {this.onNextPageSelected}/>
                <Button label="Last" className="p-button-link" disabled={!lastPage} onClick = {this.onLastPageSelected}/>

            </div>
        );
       
        return(
            <Fragment>
            
                    {/* <Toast ref={(el) => this.toast = el} /> */}

           
                        
                        <DataTable ref={(el) => this.dt = el} value={artists} selectionMode="single" onSelectionChange={(this.props.onArtistSelected)&& (e => this.props.onArtistSelected(e.value))}
                            dataKey="id" 
                            globalFilter={this.state.globalFilter} 
                            header={header}
                            footer={footer} 
                            responsiveLayout="scroll">
                            {/* <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} exportable={false}></Column> */}
                            
                            <Column field="name" header="Name" sortable style={{ minWidth: '8rem' }}></Column>
                            <Column field="active" header="Active" sortable style={{ minWidth: '16rem' }}></Column>
      
                        </DataTable>
                 

            </Fragment>
            
        )
    }
}

export default ArtistCollection;