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
import spotify.view.requests.NewArtistRequest;
import spotify.view.responses.ArtistResponse;

import java.util.UUID;

@Log4j2
@RestController
@Validated
@RequestMapping(value = "/api/spotify/artists", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistController {
    @Autowired
    private SongsArtistsClient songsArtistsClient;

    @GetMapping()
    public ResponseEntity<PagedModel<ArtistResponse>> getAllArtists(
            @RequestParam(required = false)
            Integer page,

            @RequestParam(required = false)
            Integer size,

            @RequestParam(required = false)
            String name,

            @RequestParam(required = false)
            String match
    ) {
        log.info("[{}] -> GET, getAllArtists: page:{}, size:{}, name:{}, match:{}", this.getClass().getSimpleName(), page, size, name, match);
        return songsArtistsClient.getAllArtists(page, size, name, match);
    }


    @PutMapping("/{uuid}")
    public ResponseEntity<ArtistResponse> createNewArtist(@PathVariable UUID uuid,
                                                          @RequestBody NewArtistRequest newArtist,
                                                          @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        log.info("[{}] -> PUT, createNewArtist: uuid:{}, artist:{}, authorizationHeader:{}", this.getClass().getSimpleName(), uuid, newArtist, authorizationHeader);
        return songsArtistsClient.createArtist(uuid, newArtist, authorizationHeader);
    }


}
