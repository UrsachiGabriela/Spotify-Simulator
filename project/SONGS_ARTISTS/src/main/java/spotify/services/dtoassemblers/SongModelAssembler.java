package spotify.services.dtoassemblers;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import spotify.controller.SongsController;
import spotify.view.responses.SongResponse;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SongModelAssembler extends RepresentationModelAssemblerSupport<SongResponse, SongResponse> {
    public SongModelAssembler() {
        super(SongsController.class, SongResponse.class);
    }


    public void toSimpleModel(SongResponse songResponse) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(SongsController.class)
                .getSongById(songResponse.getId()))
                .withSelfRel());

        songResponse.add(links);
    }

    @Override
    public SongResponse toModel(SongResponse songResponse) {
        List<Link> links = new ArrayList<>();

        toSimpleModel(songResponse);

        links.add(linkTo(methodOn(SongsController.class).getAllSongs(null, null, null, null, null)).withRel("parent"));
        if (songResponse.getParentId() != null) {
            links.add(linkTo(methodOn(SongsController.class)
                    .getSongById(songResponse.getParentId()))
                    .withRel("album"));
        }
        links.add(linkTo(methodOn(SongsController.class).getAllArtistsForGivenSong(songResponse.getId())).withRel("artists"));
        links.add(Link.of("http://localhost:8081/api/playlistscollection/playlists/{playlistId}/songs").withRel("add to playlist").withType("PATCH"));
        songResponse.add(links);

        return songResponse;
    }

    public void toComplexModel(SongResponse songResponse) {
        List<Link> links = new ArrayList<>();

        toModel(songResponse);
        links.add(linkTo(methodOn(SongsController.class).deleteSong(songResponse.getId(), null)).withRel("delete song").withType("DELETE"));
        songResponse.add(links);
    }

    @Override
    public CollectionModel<SongResponse> toCollectionModel(Iterable<? extends SongResponse> songDTOS) {
        CollectionModel<SongResponse> newSongDTOS = super.toCollectionModel(songDTOS);
        newSongDTOS.add(linkTo(methodOn(SongsController.class).getAllSongs(null, null, null, null, null)).withSelfRel());

        return newSongDTOS;
    }
}
