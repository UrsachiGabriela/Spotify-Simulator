package com.spotify.playlists.services.authorization;

import com.spotify.idmclient.wsdl.AuthorizeResp;
import com.spotify.playlists.IDMClientConfig;
import com.spotify.playlists.utils.enums.UserRoles;
import com.spotify.playlists.utils.errorhandling.customexceptions.ForbiddenException;
import com.spotify.playlists.utils.errorhandling.customexceptions.UnauthorizedException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthService {
    private final IDMClient idmClient = new AnnotationConfigApplicationContext(IDMClientConfig.class).getBean(IDMClient.class);

    public Integer authorize(String authHeader, UserRoles... validRoles) {
        verifyHeaderExistence(authHeader);

        // if authorization header is present, then we extract the token and call soap service for its validation
        String jwsToken = authHeader.split(" ")[1];
        AuthorizeResp authorizeResp = idmClient.authorizeUser(jwsToken);

        // extract roles and user based on the validated token
        Pair<Integer, List<UserRoles>> userRoles = decodeSOAPResponse(authorizeResp);

        // check that the user has the appropriate rights
        verifyRoles(userRoles, validRoles);

        // returns user id
        return userRoles.getFirst();
    }

    private void verifyHeaderExistence(String authHeader) {
        // if header des not exist
        if (authHeader == null) {
            throw new UnauthorizedException();
        }

        // if header is not of type 'Bearer'
        boolean isBearer = authHeader.split(" ")[0].equals("Bearer");
        if (!isBearer) {
            throw new UnauthorizedException();
        }
    }

    private void verifyRoles(Pair<Integer, List<UserRoles>> userRoles, UserRoles... validRoles) {
        boolean itExistsAtLeastOneValidRole = false;

        for (UserRoles validRole : validRoles) {
            if (userRoles.getSecond().contains(validRole)) {
                itExistsAtLeastOneValidRole = true;
                break;
            }
        }

        if (!itExistsAtLeastOneValidRole) {
            throw new ForbiddenException();
        }
    }

    private Pair<Integer, List<UserRoles>> decodeSOAPResponse(AuthorizeResp authorizeResp) {
        Integer userId = authorizeResp.getSub().getValue().intValue();
        List<UserRoles> userRoles = authorizeResp.getRoles().getValue().getString()
                .stream()
                .map(UserRoles::valueOf)
                .collect(Collectors.toList());

        return Pair.of(userId, userRoles);
    }
}
