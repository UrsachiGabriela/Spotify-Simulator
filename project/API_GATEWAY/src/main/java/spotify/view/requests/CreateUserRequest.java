package spotify.view.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateUserRequest {

    @NotBlank(message = "Username should not be empty")
    @Pattern(regexp = "^[- a-zA-Z\\s]+", message = "Invalid name format")
    private String username;
    private String password;
    private String role;
}
