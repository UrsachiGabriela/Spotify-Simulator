import React, { Fragment, useState } from "react"

import { BrowserRouter, Route, Routes, Link, Router } from 'react-router-dom';
import "bootstrap/dist/css/bootstrap.min.css";

import { withRouter } from "./common/with-router";
import LoginPage from "./components/pages/LoginPage";
import authService from "./services/auth.service";
import ErrorPage from "./components/pages/ErrorPage";
import SelectorPage from "./components/pages/SelectorPage";
import AdminPage from "./components/pages/AdminPage";
import ManagerPage from "./components/pages/ManagerPage";
import ClientPage from "./components/pages/ClientPage";
import ArtistPage from "./components/pages/ArtistPage";
import RegisterPage from "./components/pages/RegisterPage";
import SongCollection from "./components/collections/SongCollection";

class App extends React.PureComponent {

constructor(props) {
    super(props);

    this.logout=this.logout.bind(this);


    this.state = {
      user: null
    };
  }

  componentDidMount(){
    
    const currentUser = authService.getCurrentUser()

    if(currentUser){
      const myUser ={
        sub:currentUser.sub,
        token: currentUser.accessToken,
        isAdmin: currentUser.roles.includes('APP_ADMIN'),
        isContentManager: currentUser.roles.includes('CONTENT_MANAGER'),
        isClient: currentUser.roles.includes('CLIENT'),
        isArtist: currentUser.roles.includes('ARTIST')
      };

      this.setState({
        user:myUser
      })
    }
  }


  logout() {
    authService.logout().then(()=>
    {
      window.location.href = '/';
    })
  }



  render(){
    const {user,currentPage} = this.state;
    //const {token,sub,isAdmin,isContentManager,isClient,isArtist} = this.state;

    const navbar = (
        <nav className="navbar navbar-expand navbar-dark bg-dark">
          <div className="navbar-nav">
              <Link to="/" className="nav-item nav-link">Home</Link>
          </div>

          {(user && user.token) ? (
              <div className="navbar-nav ml-auto">
                <li className="nav-item">
                  <a onClick={this.logout} className="nav-link">Logout</a>
                </li>
              </div>
            ) : (
              <div className="navbar-nav ml-auto">
                <li className="nav-item">
                  <Link to={"/login"} className="nav-link">
                    Login
                  </Link>
                </li>

                <li className="nav-item">
                  <Link to={"/register"} className="nav-link">
                    Sign Up
                  </Link>
                </li>
              </div>
            )}               
        </nav>
    )
    
    return(
      <Fragment>
              <BrowserRouter>
                {navbar}
                {this.getCurrentPage}
                <Routes>
                  <Route exact path="/" element={(user && user.token)?
                                      (
                                          <SelectorPage 
                                              isContentManager={user.isContentManager} 
                                              isArtist={user.isArtist} 
                                              isAdmin={user.isAdmin} 
                                              isClient={user.isClient}
                                          ></SelectorPage>
                                      ) 
                                      :<SongCollection />} />
                  <Route path="/login" element={<LoginPage></LoginPage>} />
                  <Route path="/register" element={<RegisterPage></RegisterPage>} />
                  <Route path="/client" element={(user && user.token && user.isClient) ? <ClientPage token={user.token}></ClientPage> : <ErrorPage/>} />
                  <Route path="/manager" element={(user && user.token && user.isContentManager) ? <ManagerPage token={user.token}></ManagerPage> : <ErrorPage/>} />
                  <Route path="/admin" element={(user && user.token && user.isAdmin) ? <AdminPage token={user.token}></AdminPage> :  <ErrorPage/>} />
                  <Route path="/artist" element={(user && user.token && user.isArtist) ? <ArtistPage token={user.token}></ArtistPage> : <ErrorPage/>} />
                  {/* <Route path="/error" element={<Error />} /> */}
                </Routes>
              </BrowserRouter>             
      </Fragment>
    )
  }
}

export default App;