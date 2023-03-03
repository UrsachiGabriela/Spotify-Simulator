import React, { Fragment, Component } from "react"
import { InputText } from 'primereact/inputtext';
import axios from "axios";
import { withRouter } from "../../common/with-router";

const API_URL = "http://localhost:8082/api/spotify/";

class LoginPage extends React.Component {
    constructor(props){
        super(props);
        this.handleLogin = this.handleLogin.bind(this);
        this.onChangeUsername = this.onChangeUsername.bind(this);
        this.onChangePassword = this.onChangePassword.bind(this);

        this.state = {
            username: "",
            password:"",
            message:"",
            token: null
        };
    }

    onChangeUsername(e) {
        this.setState({
          username: e.target.value
        });
       
      }
    
      onChangePassword(e) {
        this.setState({
          password: e.target.value
        });
      }
   
      handleLogin(e){
        e.preventDefault();
        const name = this.state.username;
        const password = this.state.password;


        axios.post(API_URL + "login", {
          name,
          password
        })
        .then(response => {
              const headers = { 
                'Authorization': `Bearer ${response.data.jwsToken}`
              };
      
              axios.post(API_URL+ "authorize","",{headers})    
                  .then(authResponse => {
                      console.log("In authorize")
      
                      const user = {
                          'sub':`${authResponse.data.sub}`,
                          'roles': `${authResponse.data.roles}`,
                          'accessToken': `${response.data.jwsToken}`
                      }
      
                      localStorage.setItem("user", JSON.stringify(user));
                  

                      //this.props.router.navigate("/");                  
                      //window.location.reload();

                      

                     window.location.href = '/';
                    
                  })
                  .catch(error=>{
                    this.setState({
                      message: "Invalid username or password"
                    });
                  })               

        })
        .catch(error=>{
            this.setState({
              message: "Invalid username or password"
            });

        })
      }


    render() {
            
        return (
          <Fragment>
            <h1>LOGIN</h1>
            <div className="col-md-12">
                <div className="card card-container">
                    <img
                        src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
                        width={200}
                        alt="profile-img"
                        className="profile-img-card"
                    />

                    <form onSubmit={this.handleLogin}
                        ref={c => {
                            this.form = c;
                        }}>
                                <div className="form-group">
                                    <div className="p-inputgroup">

                                        <InputText 
                                            className="form-control"
                                            placeholder="Username" 
                                            value={this.state.username} 
                                            onChange={this.onChangeUsername} 
                                        />
                                    </div>

                                    <div className="p-inputgroup">
                                        <InputText
                                          type="password"
                                          placeholder="Password"
                                          className="form-control"
                                          value={this.state.password} 
                                          onChange={this.onChangePassword} 
                                        />
                                    </div>
                                </div>

                        <input type="submit" value="Submit" data-test="submit" />

                        {this.state.message && (
                            <div className="form-group">
                                <div className="alert alert-danger" role="alert">
                                {this.state.message}
                                </div>
                            </div>
                            )}
                    </form>
                </div>
            </div>
                        
          </Fragment>

        )
  }

}
export default withRouter(LoginPage)