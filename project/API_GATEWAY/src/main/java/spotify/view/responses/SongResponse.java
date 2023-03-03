package spotify.view.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import spotify.utils.enums.MusicGenre;
import spotify.utils.enums.MusicType;


import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SongResponse extends RepresentationModel<SongResponse> {
    private Integer id;
    private String name;
    private MusicGenre genre;
    private Integer year;
    private MusicType type;
    private Integer parentId;
    private Set<SongResponse> songs;
}
