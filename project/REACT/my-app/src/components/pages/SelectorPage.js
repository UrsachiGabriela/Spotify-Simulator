import React, { Component, Fragment } from 'react';
import { Card } from 'primereact/card';
import { Button } from 'primereact/button';
import '../../index.css'
import "primereact/resources/themes/lara-light-indigo/theme.css";
import { withRouter } from '../../common/with-router';

class SelectorPage extends React.Component{

    constructor(props)
    {
        super(props)

        this.setPage=this.setPage.bind(this);

        this.state = {
            currentPage : null
        };
    }

    setPage(page){
        this.props.router.navigate("/"+page);  
    }



    render() {
        const isContentManager = this.props.isContentManager;
        const isAdmin = this.props.isAdmin;
        const isClient = this.props.isClient;
        const isArtist = this.props.isArtist;

        return (
        <Fragment>
             <h1>PLEASE SELECT YOUR ROLE:</h1>
            {isAdmin && (<Card title="ADMIN" style={{ width: '25rem', marginBottom: '2em' }}>
                <button className="m-0" style={{lineHeight: '1.5'}} onClick= {() => this.setPage('admin')}>ADMIN</button>
            </Card>)}

            {isContentManager && (<Card title="MANAGER" style={{ width: '25rem', marginBottom: '2em' }}>
                <button className="m-0" style={{lineHeight: '1.5'}} onClick= {() => this.setPage('manager')} >MANAGER</button>
            </Card>)}

            {isClient && (<Card title="CLIENT" style={{ width: '25rem', marginBottom: '2em' }}>
                <button className="m-0" style={{lineHeight: '1.5'}} onClick= {() => this.setPage('client')} >CLIENT</button>
            </Card>)}


            {isArtist && (<Card title="ARTIST" style={{ width: '25rem', marginBottom: '2em' }}>
                <button className="m-0" style={{lineHeight: '1.5'}} onClick= {() => this.setPage('artist')} >ARTIST</button>
            </Card>)}
        </Fragment>

        )
    }
}

export default withRouter(SelectorPage);