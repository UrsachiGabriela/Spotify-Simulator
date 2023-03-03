package spotify.clients;

import com.spotify.idmclient.wsdl.*;
import org.springframework.http.HttpStatus;
import org.springframework.util.SerializationUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import spotify.utils.Urls;
import spotify.utils.enums.UserRoles;
import spotify.utils.errorhandling.InvalidEnumException;
import spotify.view.requests.CreateUserRequest;
import spotify.view.requests.LoginRequest;
import spotify.view.requests.RegisterRequest;
import spotify.view.responses.AuthResponse;
import spotify.view.responses.ExceptionResponse;
import spotify.view.responses.LoginResponse;

import javax.xml.bind.JAXBElement;
import java.nio.charset.Charset;

public class IDMClient  extends WebServiceGatewaySupport {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public AuthResponse authorizeUser(String jwsToken){
        Authorize authorize = new Authorize();
        authorize.setAccessToken(OBJECT_FACTORY.createAuthorizeAccessToken(jwsToken));

        JAXBElement<Authorize> request = OBJECT_FACTORY.createAuthorize(authorize);
        System.out.println(request);

        getWebServiceTemplate().marshalSendAndReceive(Urls.IDM_REQUEST_URL,request);

        JAXBElement<AuthorizeResponse> responseJAXBElement = (JAXBElement<AuthorizeResponse>) getWebServiceTemplate().marshalSendAndReceive(Urls.IDM_REQUEST_URL,request);
        AuthorizeResponse response = responseJAXBElement.getValue();

        return new AuthResponse(response.getAuthorizeResult().getValue().getSub().getValue().intValue(),
                response.getAuthorizeResult().getValue().getRoles().getValue().getString());
    }

    public String createUser(String token, CreateUserRequest createUserRequest){
        CreateUser createUser = new CreateUser();
        createUser.setAccessToken(OBJECT_FACTORY.createCreateUserAccessToken(token));
        createUser.setUname(OBJECT_FACTORY.createCreateUserUname(createUserRequest.getUsername()));
        createUser.setUpass(OBJECT_FACTORY.createCreateUserUpass(createUserRequest.getPassword()));

        try{
            createUser.setUrole(OBJECT_FACTORY.createCreateUserUrole(UserRoles.valueOf(createUserRequest.getRole()).name()));
        }
        catch (Exception e){
            throw new InvalidEnumException("Invalid role");
        }
        JAXBElement<CreateUser> request = OBJECT_FACTORY.createCreateUser(createUser);
        JAXBElement<CreateUserResponse> responseJAXBElement = (JAXBElement<CreateUserResponse>) getWebServiceTemplate().marshalSendAndReceive(Urls.IDM_REQUEST_URL,request);

        CreateUserResponse response = responseJAXBElement.getValue();

        return response.getCreateUserResult().getValue();
    }

    public void register(RegisterRequest registerRequest){
        RegisterUser registerUser = new RegisterUser();
        registerUser.setUname(OBJECT_FACTORY.createRegisterUserUname(registerRequest.getName()));
        registerUser.setUpass(OBJECT_FACTORY.createRegisterUserUpass(registerRequest.getPassword()));

        JAXBElement<RegisterUser> request = OBJECT_FACTORY.createRegisterUser(registerUser);
        JAXBElement<RegisterUserResponse> responseJAXBElement = (JAXBElement<RegisterUserResponse>) getWebServiceTemplate().marshalSendAndReceive(Urls.IDM_REQUEST_URL,request);

        RegisterUserResponse response = responseJAXBElement.getValue();
    }

    public LoginResponse login(LoginRequest loginRequest){
        Login login =new Login();
        login.setUname(OBJECT_FACTORY.createLoginUname(loginRequest.getName()));
        login.setUpass(OBJECT_FACTORY.createLoginUpass(loginRequest.getPassword()));

        JAXBElement<Login> request = OBJECT_FACTORY.createLogin(login);

        JAXBElement<com.spotify.idmclient.wsdl.LoginResponse> responseJAXBElement =
                (JAXBElement<com.spotify.idmclient.wsdl.LoginResponse>) getWebServiceTemplate().marshalSendAndReceive(Urls.IDM_REQUEST_URL,request);

        com.spotify.idmclient.wsdl.LoginResponse response = responseJAXBElement.getValue();

        return new LoginResponse(response.getLoginResult().getValue());
    }

    public boolean logout(String token){
        Logout logout = new Logout();
        logout.setAccessToken(OBJECT_FACTORY.createLogoutAccessToken(token));

        JAXBElement<Logout> request = OBJECT_FACTORY.createLogout(logout);

        JAXBElement<LogoutResponse> responseJAXBElement = (JAXBElement<LogoutResponse>) getWebServiceTemplate().marshalSendAndReceive(Urls.IDM_REQUEST_URL,request);

        LogoutResponse response = responseJAXBElement.getValue();

        return response.getLogoutResult().getValue();
    }


}
