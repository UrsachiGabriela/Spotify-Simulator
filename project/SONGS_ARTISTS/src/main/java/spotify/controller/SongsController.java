package spotify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spotify.model.entities.ArtistEntity;
import spotify.model.entities.SongEntity;
import spotify.services.authorization.AuthService;
import spotify.services.dataprocessors.ArtistsService;
import spotify.services.dataprocessors.SongsService;
import spotify.services.dtoassemblers.ArtistModelAssembler;
import spotify.services.dtoassemblers.SongModelAssembler;
import spotify.services.mappers.ArtistMapper;
import spotify.services.mappers.SongMapper;
import spotify.utils.enums.UserRoles;
import spotify.view.requests.NewSongRequest;
import spotify.view.requests.SongForUpdateRequest;
import spotify.view.responses.ArtistResponse;
import spotify.view.responses.ExceptionResponse;
import spotify.view.responses.SongResponse;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Set;


@Log4j2
@RestController
@RequestMapping(value = "/api/songcollection/songs", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000/")
@Validated
public class SongsController {

    @Autowired
    private AuthService authService;
    @Autowired
    private ArtistsService artistsService;
    @Autowired
    private SongsService songsService;
    @Autowired
    private SongModelAssembler songModelAssembler;
    @Autowired
    private ArtistModelAssembler artistModelAssembler;
    private final SongMapper songMapper = SongMapper.INSTANCE;
    private final ArtistMapper artistMapper = ArtistMapper.INSTANCE;
    @Autowired
    private PagedResourcesAssembler<SongResponse> songDTOPagedResourcesAssembler;


    @Operation(summary = "Get all songs")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched songs", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SongResponse.class)))}),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for query params", content = @Content),
                    @ApiResponse(responseCode = "422", description = "Invalid values for query params", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),

            })
    @GetMapping()
    public ResponseEntity<PagedModel<SongResponse>> getAllSongs(
            @RequestParam(required = false)
            @Min(0) Integer page,

            @RequestParam(defaultValue = "9", required = false)
            @Min(value = 1, message = "Page size should not be less than one ")
            Integer size,

            @RequestParam(required = false)
            @Pattern(regexp = "title|year|genre", message = "Invalid criteria")
            String searchBy,

            @RequestParam(required = false)
            String searchedValue,

            @Pattern(regexp = "exact", message = "Invalid match")
            @RequestParam(required = false)
            String match

    ) {
        log.info("[{}] -> GET, getAllSongs: page:{}, size:{}, searchBy:{}, searchedValue:{}, match:{}", this.getClass().getSimpleName(), page, size, searchBy, searchedValue, match);

        // query database
        Page<SongEntity> songEntities = songsService.getPageableSongs(page, size, searchBy, searchedValue, match);

        // map entities to dtos
        Page<SongResponse> songDTOPage = songEntities.map(songMapper::toCompleteSongDto);

        // add links
        for (SongResponse songResponse : songDTOPage) {
            songResponse.getSongs().forEach(s -> songModelAssembler.toSimpleModel(s));
        }
        PagedModel<SongResponse> songModels = songDTOPagedResourcesAssembler.toModel(songDTOPage, songModelAssembler);

        return ResponseEntity.ok().body(songModels);
    }

    @Operation(summary = "Get song by its identifier")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched song", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SongResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Searched song not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for path variables", content = @Content)

            })
    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSongById(@PathVariable int id) {
        log.info("[{}] -> GET, getSongById, songId:{}", this.getClass().getSimpleName(), id);

        // query database
        SongEntity songEntity = songsService.getSongById(id);

        // map entity to dto
        SongResponse songResponse = songMapper.toCompleteSongDto(songEntity);

        // add links
        songModelAssembler.toModel(songResponse);
        for (SongResponse innerSong : songResponse.getSongs()) {
            songModelAssembler.toSimpleModel(innerSong);
        }

        return ResponseEntity.ok().body(songResponse);
    }

    @Operation(summary = "Add new song to song resources")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "201", description = "Successfully added  new song resource", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SongResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Malformed request syntax", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Invalid role", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Mentioned album or artists don't exist yet", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Semantically erroneous request body fields", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),

            })
    @PostMapping()
    public ResponseEntity<SongResponse> createSong(@Valid @RequestBody NewSongRequest newSong,
                                                   @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> POST, addNewSong, song:{}", this.getClass().getSimpleName(), newSong);

        // authorize
        authService.authorize(authorizationHeader, UserRoles.CONTENT_MANAGER);

        // query db for album and artists (not continue if either album or artists do not exist)
        SongEntity album = newSong.getParentId() != null ? songsService.getAlbumOfSong(newSong.getParentId()) : null;
        Set<ArtistEntity> artistEntities = artistsService.getArtistsByNameIfActive(newSong.getArtists()); // if artists don't exist, or they are inactive, the song will not be created

        // map dto to entity
        SongEntity songEntity = songMapper.toSongEntity(newSong, album);

        // create new song
        songsService.createNewSong(songEntity);
        artistsService.assignSongToMultipleArtists(artistEntities, songEntity);

        // map entity to dto
        SongResponse songResponse = songMapper.toCompleteSongDto(songEntity);

        // add links
        songModelAssembler.toComplexModel(songResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(songResponse);
    }

    @Operation(summary = "Update an existing song")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "204", description = "Successfully updated", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Invalid role", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Song not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
                    @ApiResponse(responseCode = "422", description = "Invalid music genre", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
            })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateSong(@PathVariable int id,
                                           @Valid @RequestBody SongForUpdateRequest songForUpdateRequest,
                                           @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        log.info("[{}] -> PATCH, updateSong, id:{}, song:{}", this.getClass().getSimpleName(), id, songForUpdateRequest);

        // authorize
        authService.authorize(authorizationHeader, UserRoles.CONTENT_MANAGER);

        // query db for album and artists (not continue if either album or artists do not exist)
        SongEntity oldSongEntity = songsService.getSongById(id);

        // map dto to new entity
        SongEntity updatedEntity = songMapper.toSongEntity(songForUpdateRequest);

        // update song
        songsService.updateSong(oldSongEntity, updatedEntity);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Delete song resource identified by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted song resource", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SongResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Invalid role", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Searched song not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict: cannot remove album without removing all its songs", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<SongResponse> deleteSong(@PathVariable int id,
                                                   @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> DELETE, deleteSong, songId:{}", this.getClass().getSimpleName(), id);

        // authorize
        authService.authorize(authorizationHeader, UserRoles.CONTENT_MANAGER);

        // delete song
        SongEntity removedSong = songsService.deleteSong(id);

        // map entity to dto
        SongResponse songResponse = songMapper.toCompleteSongDto(removedSong);

        // add links
        songModelAssembler.toModel(songResponse);

        return ResponseEntity.status(HttpStatus.OK).body(songResponse);
    }

    @Operation(summary = "Get artists for song identified by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found song by id", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ArtistResponse.class)))}),
                    @ApiResponse(responseCode = "404", description = "Searched song not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for path variables", content = @Content)

            })
    @GetMapping("/{id}/artists")
    public ResponseEntity<Set<ArtistResponse>> getAllArtistsForGivenSong(@PathVariable int id) {
        log.info("[{}] -> GET, getAllArtistsForGivenSong, songId:{}", this.getClass().getSimpleName(), id);

        // query db
        Set<ArtistEntity> artistEntities = artistsService.getArtistForGivenSong(id);

        // map to dto
        Set<ArtistResponse> artistResponses = artistMapper.toArtistWithName(artistEntities);

        // add links
        artistResponses.forEach(artistDTO -> artistModelAssembler.toSimpleModel(artistDTO));

        return ResponseEntity.ok().body(artistResponses);
    }
}
