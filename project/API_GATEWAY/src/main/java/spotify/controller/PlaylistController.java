package spotify.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spotify.clients.PlaylistsClient;
import spotify.view.requests.PlaylistRequest;
import spotify.view.requests.SimpleSongRequest;
import spotify.view.responses.PlaylistResponse;

@Log4j2
@RestController
@Validated
@RequestMapping(value = "/api/spotify/playlists", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlaylistController {
    @Autowired
    private PlaylistsClient playlistsClient;


    @GetMapping()
    public ResponseEntity<CollectionModel<PlaylistResponse>> getPlaylists(
            @RequestParam(required = false)
            String name,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {


        log.info("[{}] -> GET, getAllPlaylists: name:{}, authorizationHeader:{}", this.getClass().getSimpleName(), name, authorizationHeader);
        return playlistsClient.getAllPlaylists(name, authorizationHeader);
    }

    @PostMapping()
    public ResponseEntity<PlaylistResponse> createPlaylist(@RequestBody PlaylistRequest playlistRequest,
                                                           @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        log.info("[{}] -> POST, createPlaylist: playlist:{}, authorizationHeader:{}", this.getClass().getSimpleName(), playlistRequest, authorizationHeader);
        return playlistsClient.createPlaylist(playlistRequest, authorizationHeader);
    }

    @PatchMapping("/{playlistId}")
    public ResponseEntity<PlaylistResponse> addSongToPlaylist(@PathVariable String playlistId,
                                                              @RequestBody SimpleSongRequest songRequest,
                                                              @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> PATCH, addSongToPlaylist: playlist:{}, song:{} authorizationHeader:{}", this.getClass().getSimpleName(), playlistId, songRequest, authorizationHeader);
        return playlistsClient.addSongToPlaylist(playlistId, songRequest, authorizationHeader);
    }
}
