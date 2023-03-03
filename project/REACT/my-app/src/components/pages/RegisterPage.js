import React, { Fragment, Component } from "react"
import { InputText } from 'primereact/inputtext';
import axios from "axios";
import { Toast } from 'primereact/toast';
import { withRouter } from "../../common/with-router";
import { Navigate } from "react-router-dom";

const API_URL = "http://localhost:8082/api/spotify/register";


class RegisterPage extends React.Component {
    constructor(props){
        super(props);
        this.handleRegister = this.handleRegister.bind(this);
        this.onChangeUsername = this.onChangeUsername.bind(this);
        this.onChangePassword = this.onChangePassword.bind(this);

        this.state = {
            username: "",
            password:"",
            message:"",
            token: null,
            redirect:false
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
   
      handleRegister(e){
        e.preventDefault();
        const name = this.state.username;
        const password = this.state.password;


        axios.post(API_URL , {
          name,
          password
        })
        .then(authResponse => {
            this.setState({redirect:true})})
        .catch(error=>{
          this.toast.show({severity:"error", summary:"ERROR",detail:error.response.data.details,life:2000})
        })  
      }


    render() {
        const {redirect} = this.state;
        return (
          <Fragment>
             <h1>REGISTER</h1>
            <div className="col-md-12">
                <div className="card card-container">
                    <img
                        src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
                        width={200}
                        alt="profile-img"
                        className="profile-img-card"
                    />

                    <form onSubmit={this.handleRegister}
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

                        <Toast className="p-dialog" ref={(el) => this.toast = el} />
                        {redirect && < Navigate to="/login" replace={true} />}

                    </form>
                </div>
            </div>
                        
          </Fragment>

        )
  }

}
export default withRouter(RegisterPage)