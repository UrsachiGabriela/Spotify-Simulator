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
import spotify.view.requests.NewArtistRequest;
import spotify.view.requests.NewSongsForArtistRequest;
import spotify.view.responses.ArtistResponse;
import spotify.view.responses.ExceptionResponse;
import spotify.view.responses.SongResponse;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping(value = "/api/songcollection/artists", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000/")
@Validated
public class ArtistsController {

    @Autowired
    private AuthService authService;
    @Autowired
    private ArtistsService artistsService;

    @Autowired
    private SongsService songsService;
    @Autowired
    private ArtistModelAssembler artistModelAssembler;

    @Autowired
    private SongModelAssembler songModelAssembler;
    private final ArtistMapper artistMapper = ArtistMapper.INSTANCE;
    private final SongMapper songMapper = SongMapper.INSTANCE;

    @Autowired
    private PagedResourcesAssembler<ArtistResponse> artistDTOPagedResourcesAssembler;

    @Operation(summary = "Get all artists")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched artists", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ArtistResponse.class)))}),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for query params", content = @Content),
                    @ApiResponse(responseCode = "422", description = "Invalid values for query params", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
            })
    @GetMapping()
    public ResponseEntity<PagedModel<ArtistResponse>> getAllArtists(
            @RequestParam(required = false)
            @Min(0) Integer page,

            @RequestParam(defaultValue = "1", required = false)
            @Min(value = 1, message = "Page size must not be less than one ")
            Integer size,

            @RequestParam(required = false)
            @Pattern(regexp = "^[a-zA-Z\\s]+", message = "Invalid name format")
            String name,

            @Pattern(regexp = "exact", message = "Invalid match")
            @RequestParam(required = false)
            String match
    ) {
        log.info("[{}] -> GET, getAllArtists: page:{}, size:{}, name:{}, match:{}", this.getClass().getSimpleName(), page, size, name, match);

        // query db
        Page<ArtistEntity> artistEntities = artistsService.getPageableArtists(page, size, name, match);

        // map to dto
        Page<ArtistResponse> artistDTOPage = artistEntities.map(artistMapper::toArtistWithoutSongsDto);

        // add links
        PagedModel<ArtistResponse> artistModels = artistDTOPagedResourcesAssembler.toModel(artistDTOPage, artistModelAssembler);

        return ResponseEntity.ok().body(artistModels);
    }

    @Operation(summary = "Get artist by its unique identifier")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched artist", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Searched artist not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for path variables", content = @Content)
            })
    @GetMapping("/{uuid}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable UUID uuid) {
        log.info("[{}] -> GET, getArtistById, id:{}", this.getClass().getSimpleName(), uuid);

        // query database
        ArtistEntity artistEntity = artistsService.getArtistByUUID(uuid);

        // map to dto
        ArtistResponse artistResponse = artistMapper.toArtistWithoutSongsDto(artistEntity);

        // add links
        artistModelAssembler.toModel(artistResponse);

        return ResponseEntity.ok().body(artistResponse);
    }

    @Operation(summary = "Create new artist or replace an existing one")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "201", description = "Successfully created a new artist resource", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "204", description = "Successfully replaced an existing resource with given uuid", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Invalid role", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflict: unique name constraint unsatisfied", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid values for request fields", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
            })
    @PutMapping("/{uuid}")
    public ResponseEntity<ArtistResponse> createOrReplaceArtist(@PathVariable UUID uuid,
                                                                @Valid @RequestBody NewArtistRequest newArtist,
                                                                @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        log.info("[{}] -> PUT, createOrReplaceArtist, uuid:{}, artist:{}", this.getClass().getSimpleName(), uuid, newArtist);

        // authorize
        authService.authorize(authorizationHeader, UserRoles.CONTENT_MANAGER);

        // query db to decide which of create or replace operation is needed
        boolean isAlreadyExistent = artistsService.itExistsArtist(uuid);

        // map dto to entity
        ArtistEntity artistEntity = artistMapper.toArtistEntity(newArtist, uuid);

        // update db
        ArtistEntity savedEntity = artistsService.createOrReplaceArtist(artistEntity);

        // map created/replaced entity to dto
        ArtistResponse artistResponse = artistMapper.toCompleteArtistDto(savedEntity);

        // add links
        artistModelAssembler.toComplexModel(artistResponse);

        // decide response code
        if (isAlreadyExistent)
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(artistResponse);
    }

    @Operation(summary = "Delete  artist identified by uuid")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted artist resource", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Invalid role", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Searched artist not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<ArtistResponse> deleteArtist(@PathVariable UUID uuid,
                                                       @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> DELETE, deleteArtist, uuid:{}", this.getClass().getSimpleName(), uuid);

        // authorize
        authService.authorize(authorizationHeader, UserRoles.CONTENT_MANAGER);

        // delete artist
        ArtistEntity artistEntity = artistsService.deleteArtist(uuid);

        // map deleted entity to dto
        ArtistResponse artistResponse = artistMapper.toCompleteArtistDto(artistEntity);

        // add links
        artistModelAssembler.toModel(artistResponse);

        return ResponseEntity.status(HttpStatus.OK).body(artistResponse); // reprezentarea resursei inainte de a fi stearsa
    }

    @Operation(summary = "Get songs for artist identified by uuid")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found artist", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Searched artist not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for path variables", content = @Content)
            })
    @GetMapping("/{uuid}/songs")
    public ResponseEntity<Set<SongResponse>> getAllSongsForGivenArtist(@PathVariable UUID uuid) {
        log.info("[{}] -> GET, getAllSongsForGivenArtist, artistId:{}", this.getClass().getSimpleName(), uuid);


        // query db
        ArtistEntity artistEntity = artistsService.getArtistByUUID(uuid);

        // map to dto
        Set<SongResponse> songResponseSet = songMapper.toSimpleSongDTOSet(artistEntity.getSongs());

        // add links
        for (SongResponse songResponse : songResponseSet) {
            songModelAssembler.toSimpleModel(songResponse);
        }

        return ResponseEntity.ok().body(songResponseSet);
    }


    // fie adaug songs pentru un artist dupa ce am introdus song-ul
    // fie specific la adaugarea unui song lista de artisti -> addNewSong
    @Operation(summary = "Assign songs to an artist")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully updated join table", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Invalid role", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Searched artist/song not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "409", description = "The artist is not active", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid values for request fields", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))})
            })
    @PostMapping("/{uuid}/songs")
    public ResponseEntity<ArtistResponse> assignSongsToArtist(@PathVariable UUID uuid,
                                                              @Valid @RequestBody NewSongsForArtistRequest request,
                                                              @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> POST, assignSongsToArtist, uuid:{}, songs:{}", this.getClass().getSimpleName(), uuid, request);

        // authorize
        authService.authorize(authorizationHeader, UserRoles.CONTENT_MANAGER);

        // query db for songs and artist
        ArtistEntity artistEntity = artistsService.getArtistByUUID(uuid);
        Set<SongEntity> songEntities = new HashSet<>();
        for (Integer songId : request.getSongsId()) {
            songEntities.add(songsService.getSongById(songId));
        }

        // update db
        ArtistEntity updatedArtist = artistsService.addSongsToArtist(artistEntity, songEntities);

        // map entity to dto
        ArtistResponse artistResponse = artistMapper.toCompleteArtistDto(updatedArtist);

        // add links
        artistModelAssembler.toComplexModel(artistResponse);
        for (SongResponse songResponse : artistResponse.getSongs()) {
            songModelAssembler.toSimpleModel(songResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(artistResponse);
    }
}
