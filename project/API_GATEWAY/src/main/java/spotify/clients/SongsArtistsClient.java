package spotify.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import spotify.utils.Urls;
import spotify.view.requests.NewArtistRequest;
import spotify.view.requests.NewSongRequest;
import spotify.view.responses.ArtistResponse;
import spotify.view.responses.SongResponse;

import java.util.Optional;
import java.util.UUID;

@Component
public class SongsArtistsClient {

    private final RestTemplate restTemplate;

    @Autowired
    public SongsArtistsClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        //this.restTemplate = restTemplateBuilder.requestFactory((Supplier<ClientHttpRequestFactory>) new HttpComponentsClientHttpRequestFactory()).build();
    }

    public ResponseEntity<SongResponse> getSongById(Integer songId) {
        UriComponents url = UriComponentsBuilder.fromHttpUrl(Urls.SONG_REQUEST)
                .pathSegment(String.valueOf(songId))
                .build();

        return restTemplate.exchange(url.toString(), HttpMethod.GET, null, SongResponse.class);
    }

    public ResponseEntity<PagedModel<ArtistResponse>> getAllArtists(Integer page, Integer size, String name, String match) {
        ParameterizedTypeReference<PagedModel<ArtistResponse>> responseType = new ParameterizedTypeReference<>() {
        };

        UriComponents url = UriComponentsBuilder.fromHttpUrl(Urls.ARTIST_REQUEST)
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("size", Optional.ofNullable(size))
                .queryParamIfPresent("name", Optional.ofNullable(name))
                .queryParamIfPresent("match", Optional.ofNullable(match))
                .build();

        return restTemplate.exchange(url.toString(), HttpMethod.GET, null, responseType);
        //return this.restTemplate.getForObject(url.toString(), responseType.getType());
    }

    public ResponseEntity<PagedModel<SongResponse>> getAllSongs(Integer page, Integer size, String searchBy, String searchedValue, String match) {
        ParameterizedTypeReference<PagedModel<SongResponse>> responseType = new ParameterizedTypeReference<>() {
        };

        UriComponents url = UriComponentsBuilder.fromHttpUrl(Urls.SONG_REQUEST)
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("size", Optional.ofNullable(size))
                .queryParamIfPresent("searchBy", Optional.ofNullable(searchBy))
                .queryParamIfPresent("searchedValue", Optional.ofNullable(searchedValue))
                .queryParamIfPresent("match", Optional.ofNullable(match))
                .build();

        return restTemplate.exchange(url.toString(), HttpMethod.GET, null, responseType);
    }

    public ResponseEntity<ArtistResponse> createArtist(UUID uuid, NewArtistRequest newArtistRequest, String authorizationHeader) {


        UriComponents url = UriComponentsBuilder.fromHttpUrl(Urls.ARTIST_REQUEST)
                .pathSegment(String.valueOf(uuid))
                .build();

        HttpHeaders headers = new HttpHeaders();

        if (authorizationHeader != null) {
            headers.add("Authorization", authorizationHeader);
        }

        HttpEntity<NewArtistRequest> requestUpdate = new HttpEntity<>(newArtistRequest, headers);
        return restTemplate.exchange(url.toString(), HttpMethod.PUT, requestUpdate, ArtistResponse.class);
    }

    public ResponseEntity<SongResponse> createSong(NewSongRequest newSongRequest, String authorizationHeader) {
        UriComponents url = UriComponentsBuilder.fromHttpUrl(Urls.SONG_REQUEST)
                .build();

        HttpHeaders headers = new HttpHeaders();

        if (authorizationHeader != null) {
            headers.add("Authorization", authorizationHeader);
        }

        HttpEntity<NewSongRequest> request = new HttpEntity<>(newSongRequest, headers);
        return restTemplate.exchange(url.toString(), HttpMethod.POST, request, SongResponse.class);
    }



}
