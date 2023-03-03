package com.spotify.playlists.view.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "song")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleSongRequest {
    @NonNull
    private Integer songId;
}
