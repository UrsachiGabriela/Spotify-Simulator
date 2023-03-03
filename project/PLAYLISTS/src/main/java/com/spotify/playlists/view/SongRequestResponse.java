package com.spotify.playlists.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

// request -> atunci cand SA service invoca PLAYLISTS
// response -> atunci cand PLAYLISTS service invoca SA
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "song")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SongRequestResponse extends RepresentationModel<SongRequestResponse> {
    private Integer id;
    private String name;
}