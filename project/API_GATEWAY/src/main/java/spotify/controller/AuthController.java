package spotify.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spotify.clients.IDMClient;
import spotify.clients.PlaylistsClient;
import spotify.configs.IDMClientConfig;
import spotify.view.requests.CreateUserRequest;
import spotify.view.requests.LoginRequest;
import spotify.view.requests.PlaylistRequest;
import spotify.view.requests.RegisterRequest;
import spotify.view.responses.AuthResponse;
import spotify.view.responses.ExceptionResponse;
import spotify.view.responses.LoginResponse;

import javax.validation.Valid;


//TODO
// logs


@Log4j2
@RestController
@Validated
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/api/spotify", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final IDMClient idmClient = new AnnotationConfigApplicationContext(IDMClientConfig.class).getBean(IDMClient.class);
    @Autowired
    private PlaylistsClient playlistsClient;


    @PostMapping("register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest registerRequest) {

        log.info("[{}] -> POST, register: request:{}", this.getClass().getSimpleName(), registerRequest);
        idmClient.register(registerRequest);
        String token = idmClient.login(new LoginRequest(registerRequest.getName(), registerRequest.getPassword())).getJwsToken();
        playlistsClient.createPlaylist(new PlaylistRequest("Favorites"),"Bearer "+token);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("authorize")
    public ResponseEntity<AuthResponse> authorize(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        log.info("[{}] -> POST, authorize: header:{}", this.getClass().getSimpleName(), authorizationHeader);
        AuthResponse authResponse = idmClient.authorizeUser(authorizationHeader!=null?authorizationHeader.split(" ")[1]:null);

        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @PostMapping("users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserRequest createUserRequest,
                                             @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader)
    {

        log.info("[{}] -> POST, create user: request:{}", this.getClass().getSimpleName(), createUserRequest);
        String user = idmClient.createUser(authorizationHeader!=null?authorizationHeader.split(" ")[1]:null,createUserRequest);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {

        log.info("[{}] -> POST, login: request:{}", this.getClass().getSimpleName(), loginRequest);
        LoginResponse response = idmClient.login(loginRequest);

        if (response.getJwsToken().equals("False")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("logout")
    public ResponseEntity<Object> logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader)
    {

        log.info("[{}] -> POST, logout: token:{}", this.getClass().getSimpleName(), authorizationHeader);
        boolean response = idmClient.logout(authorizationHeader!=null?authorizationHeader.split(" ")[1]:null);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}






























