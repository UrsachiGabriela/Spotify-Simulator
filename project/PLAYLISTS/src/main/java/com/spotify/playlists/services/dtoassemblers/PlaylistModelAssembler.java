package com.spotify.playlists.services.dtoassemblers;

import com.spotify.playlists.controller.WebController;
import com.spotify.playlists.view.responses.PlaylistResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PlaylistModelAssembler extends RepresentationModelAssemblerSupport<PlaylistResponse, PlaylistResponse> {


    public PlaylistModelAssembler() {
        super(WebController.class, PlaylistResponse.class);
    }

    @Override
    public PlaylistResponse toModel(PlaylistResponse playlistResponse) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(
                methodOn(WebController.class).getPlaylistById(playlistResponse.getId(),null)
        )
                .withRel("self"));
        links.add(linkTo(
                methodOn(WebController.class).getAllPlaylists(null,null))
                .withRel("parent"));

        playlistResponse.add(links);

        return playlistResponse;
    }

    @Override
    public CollectionModel<PlaylistResponse> toCollectionModel(Iterable<? extends PlaylistResponse> playlistDTOS) {
        CollectionModel<PlaylistResponse> newPlaylistDTOS = super.toCollectionModel(playlistDTOS);
        newPlaylistDTOS.add(linkTo(methodOn(WebController.class).getAllPlaylists(null,null)).withSelfRel());

        return newPlaylistDTOS;
    }
}
