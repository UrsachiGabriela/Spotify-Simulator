package com.spotify.playlists.controller;

import com.spotify.playlists.model.collections.Playlist;
import com.spotify.playlists.model.collections.Resource;
import com.spotify.playlists.services.PlaylistService;
import com.spotify.playlists.services.authorization.AuthService;
import com.spotify.playlists.services.clients.RestClient;
import com.spotify.playlists.services.dtoassemblers.PlaylistModelAssembler;
import com.spotify.playlists.services.mappers.PlaylistMapper;
import com.spotify.playlists.services.mappers.SongMapper;
import com.spotify.playlists.utils.enums.UserRoles;
import com.spotify.playlists.view.SongRequestResponse;
import com.spotify.playlists.view.requests.PlaylistRequest;
import com.spotify.playlists.view.requests.SimpleSongRequest;
import com.spotify.playlists.view.responses.ExceptionResponse;
import com.spotify.playlists.view.responses.PlaylistResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.Set;



@Log4j2
@RestController
@Validated
@CrossOrigin(origins = "http://localhost:3000/")
@RequestMapping("/api/playlistscollection")
public class WebController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private RestClient restClient;

    private final PlaylistMapper playlistMapper = PlaylistMapper.INSTANCE;
    private final SongMapper songMapper = SongMapper.INSTANCE;

    @Autowired
    private PlaylistModelAssembler playlistModelAssembler;

    @Operation(summary = "Get all playlists")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found playlists", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlaylistResponse.class)))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for query params", content = @Content),
                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
            })
    @GetMapping(value = "/playlists")
    public ResponseEntity<CollectionModel<PlaylistResponse>> getAllPlaylists(
            @RequestParam(required = false)
            @Pattern(regexp = "^[-,a-zA-Z0-9\\s]*", message = "Invalid name format")
            String name,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> GET, getAllPlaylists", this.getClass().getSimpleName());


        // authorize
        Integer userID = authService.authorize(authorizationHeader, UserRoles.CLIENT);

        // get documents
        Set<Playlist> playlists = playlistService.getAllPlaylists(userID, name);

        // map to dto
        Set<PlaylistResponse> playlistResponses = playlistMapper.toPlaylistDTOSet(playlists);

        // add links
        CollectionModel<PlaylistResponse> playlistModels = playlistModelAssembler.toCollectionModel(playlistResponses);

        return ResponseEntity.ok().body(playlistModels);
    }

    @Operation(summary = "Get playlist by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched playlist", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PlaylistResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Searched playlist not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for path variables", content = @Content)

            })
    @GetMapping(value = "/playlists/{id}")
    public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable String id,
                                                            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> GET, getPlaylistWithID {}", this.getClass().getSimpleName(),id);


        // authorize
        Integer userID = authService.authorize(authorizationHeader, UserRoles.CLIENT);

        // get document
        Playlist playlist = playlistService.getPlaylistById(userID, id);

        // map to dto
        PlaylistResponse playlistResponse = playlistMapper.toPlaylistDTO(playlist);

        // add links
        playlistModelAssembler.toModel(playlistResponse);

        return ResponseEntity.ok().body(playlistResponse);
    }

    @Operation(summary = "Add new playlist to playlist collection")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "201", description = "Successfully added  new playlist resource", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PlaylistResponse.class))}),
                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
                    @ApiResponse(responseCode = "409", description = "Conflict: unique name constraint unsatisfied", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            })
    @PostMapping("/playlists")
    public ResponseEntity<PlaylistResponse> createPlaylist(@Valid @RequestBody PlaylistRequest playlistRequest,
                                                           @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        log.info("[{}] -> POST, createPlaylist: {}", this.getClass().getSimpleName(),playlistRequest);

        // authorize
        Integer userID = authService.authorize(authorizationHeader, UserRoles.CLIENT);

        // map dto to document
        Playlist playlist = playlistMapper.toPlaylist(playlistRequest);

        // insert in db
        playlist = playlistService.createPlaylist(userID, playlist);

        // map to dto
        PlaylistResponse playlistResponse = playlistMapper.toPlaylistDTO(playlist);

        // add links
        playlistModelAssembler.toModel(playlistResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(playlistResponse);
    }


    @Operation(summary = "Add songs to an existing playlist")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "204", description = "Successfully inserted new song in playlist", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PlaylistResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Searched playlist not found or given song not existent", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
            })
    @PatchMapping("/playlists/{playlistId}/songs")
    public ResponseEntity<PlaylistResponse> addSongToPlaylist(@PathVariable String playlistId,
                                                              @Validated @RequestBody SimpleSongRequest songRequest,
                                                              @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> PATCH, add song {} to playlist {}", this.getClass().getSimpleName(),songRequest,playlistId);

        // authorize
        Integer userID = authService.authorize(authorizationHeader, UserRoles.CLIENT);

        // send request
        SongRequestResponse songRequestResponse = restClient.getSongById(songRequest.getSongId());

        // map to resource
        Resource song = songMapper.toSongResource(songRequestResponse);

        // update db
        Playlist updatedPlaylist = playlistService.addSongToPlaylist(userID, playlistId, song);

        // map to dto
        PlaylistResponse playlistResponse = playlistMapper.toPlaylistDTO(updatedPlaylist);

        // add links
        playlistModelAssembler.toModel(playlistResponse);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete playlist")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted playlist", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PlaylistResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Searched playlist not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            })
    @DeleteMapping("/playlists/{playlistId}")
    public ResponseEntity<PlaylistResponse> deletePlaylist(@PathVariable String playlistId,
                                                           @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> DELETE, deletePlaylist: {}", this.getClass().getSimpleName(),playlistId);

        // authorize
        Integer userID = authService.authorize(authorizationHeader, UserRoles.CLIENT);

        Playlist removedPlaylist = playlistService.deletePlaylist(userID, playlistId);

        // map to dto
        PlaylistResponse playlistResponse = playlistMapper.toPlaylistDTO(removedPlaylist);

        // add links
        playlistModelAssembler.toModel(playlistResponse);

        return ResponseEntity.ok().body(playlistResponse);
    }
}
