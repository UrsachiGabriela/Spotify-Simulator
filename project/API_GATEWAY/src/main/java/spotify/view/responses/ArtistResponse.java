package spotify.view.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtistResponse extends RepresentationModel<ArtistResponse> {
    private UUID uuid;

    private String name;

    private Boolean active;

    private Set<SongResponse> songs;

    private Boolean hasSongs = Boolean.FALSE;
}
