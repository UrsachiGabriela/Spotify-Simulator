package spotify.view.requests;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NewSongRequest {
    @NotBlank(message = "Song name should not be empty")
    @Pattern(regexp = "^[- 'a-zA-Z\\s]+", message = "Invalid song name format")
    private String name;

    @NotNull(message = "Music genre should not be null")
    private String genre;

    @Min(value = 0, message = "Invalid year")
    @Max(value = 9999, message = "Invalid year")
    private Integer year;

    @NotNull(message = "Music type should not be null")
    private String type;

    private @Valid Integer parentId;

    @NotNull(message = "Artists list should not be empty")
    private Set<String> artists;

}
