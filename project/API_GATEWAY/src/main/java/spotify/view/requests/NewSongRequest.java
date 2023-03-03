package spotify.view.requests;

import lombok.*;
import spotify.utils.enums.MusicGenre;
import spotify.utils.enums.MusicType;

import javax.validation.Valid;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NewSongRequest {

    private String name;


    private String genre;


    private Integer year;


    private String type;

    private Integer parentId;


    private Set<String> artists;

}
