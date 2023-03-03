package spotify.view.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewArtistRequest {
    @NotBlank(message = "Artist name should not be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+", message = "Invalid name format")
    private String name;

    @Pattern(regexp = "true|false", message = "active field should be either true or false")
    private String active;
}