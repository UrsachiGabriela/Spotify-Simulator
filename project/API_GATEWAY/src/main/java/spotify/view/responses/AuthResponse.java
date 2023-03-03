package spotify.view.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spotify.idmclient.wsdl.StringArray;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    Integer sub;

    List<String> roles;
}
