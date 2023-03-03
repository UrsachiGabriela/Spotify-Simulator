package spotify.view.requests;

import lombok.*;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SongForUpdateRequest {
    @NotBlank(message = "Song name should not be empty")
    @Pattern(regexp = "^[- 'a-zA-Z\\s]+", message = "Invalid song name format")
    private String name;

    @NotNull(message = "Music genre should not be null")
    private String genre;

    @Min(value = 0, message = "Invalid year")
    @Max(value = 9999, message = "Invalid year")
    private Integer year;

}
