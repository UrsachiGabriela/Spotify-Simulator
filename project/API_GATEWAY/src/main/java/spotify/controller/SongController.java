package spotify.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spotify.clients.SongsArtistsClient;
import spotify.view.requests.NewSongRequest;
import spotify.view.responses.SongResponse;

@Log4j2
@RestController
@Validated
@RequestMapping(value = "/api/spotify/songs", produces = MediaType.APPLICATION_JSON_VALUE)
public class SongController {
    @Autowired
    private SongsArtistsClient songsArtistsClient;

    @GetMapping()
    public ResponseEntity<PagedModel<SongResponse>> getAllSongs(
            @RequestParam(required = false)
            Integer page,

            @RequestParam(required = false)
            Integer size,

            @RequestParam(required = false)
            String searchBy,

            @RequestParam(required = false)
            String searchedValue,

            @RequestParam(required = false)
            String match
    ) {
        log.info("[{}] -> GET, getAllSongs: page:{}, size:{}, searchBy:{}, searchedValue:{}, match:{}", this.getClass().getSimpleName(), page, size, searchBy, searchedValue, match);
        return songsArtistsClient.getAllSongs(page, size, searchBy, searchedValue, match);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSong(@PathVariable int id) {

        log.info("[{}] -> GET, getSong: id:{}", this.getClass().getSimpleName(), id);
        return songsArtistsClient.getSongById(id);
    }

    @PostMapping()
    public ResponseEntity<SongResponse> createNewSong(@RequestBody NewSongRequest newSongRequest,
                                                      @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        log.info("[{}] -> POST, createNewSong: song{}, authorizationHeader:{}", this.getClass().getSimpleName(), newSongRequest, authorizationHeader);
        return songsArtistsClient.createSong(newSongRequest, authorizationHeader);
    }

}
