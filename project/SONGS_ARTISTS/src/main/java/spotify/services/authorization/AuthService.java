package spotify.services.authorization;

import com.spotify.idmclient.wsdl.AuthorizeResp;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import spotify.configs.IDMClientConfig;
import spotify.utils.enums.UserRoles;
import spotify.utils.errorhandling.customexceptions.ForbiddenException;
import spotify.utils.errorhandling.customexceptions.UnauthorizedException;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class AuthService {
    private final IDMClient idmClient = new AnnotationConfigApplicationContext(IDMClientConfig.class).getBean(IDMClient.class);

    public void authorize(String authHeader, UserRoles... validRoles) {
        log.info("[{}] -> authorize: authHeader:{}", this.getClass().getSimpleName(),authHeader);

        verifyHeaderExistence(authHeader);

        // if authorization header is present, then we extract the token and call soap service for its validation
        String jwsToken = authHeader.split(" ")[1];
        AuthorizeResp authorizeResp = idmClient.authorizeUser(jwsToken);

        // extract roles and user based on the validated token
        Pair<Integer, List<UserRoles>> userRoles = decodeSOAPResponse(authorizeResp);

        // check that the user has the appropriate rights
        verifyRoles(userRoles, validRoles);
    }

    private void verifyHeaderExistence(String authHeader) {
        log.info("[{}] -> verify header existence ", this.getClass().getSimpleName());

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
        log.info("[{}] -> verify roles", this.getClass().getSimpleName());

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
        log.info("[{}] -> decode SOAP response for authorize", this.getClass().getSimpleName());

        Integer userId = authorizeResp.getSub().getValue().intValue();
        List<UserRoles> userRoles = authorizeResp.getRoles().getValue().getString()
                .stream()
                .map(UserRoles::valueOf)
                .collect(Collectors.toList());

        return Pair.of(userId, userRoles);
    }
}
