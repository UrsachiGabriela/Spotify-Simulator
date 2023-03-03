package spotify.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import spotify.utils.Urls;
import spotify.view.requests.PlaylistRequest;
import spotify.view.requests.SimpleSongRequest;
import spotify.view.responses.PlaylistResponse;

import java.util.Optional;

@Component
public class PlaylistsClient {

    private final RestTemplate restTemplate;

    @Autowired
    public PlaylistsClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity<CollectionModel<PlaylistResponse>> getAllPlaylists(String name,String authorizationHeader) {
        ParameterizedTypeReference<CollectionModel<PlaylistResponse>> responseType = new ParameterizedTypeReference<>() {
        };

        UriComponents url = UriComponentsBuilder.fromHttpUrl(Urls.PLAYLIST_REQUEST)
                .queryParamIfPresent("name", Optional.ofNullable(name))
                .build();

        HttpHeaders headers = new HttpHeaders();
        if (authorizationHeader != null) {
            headers.add("Authorization", authorizationHeader);
        }
        return restTemplate.exchange(url.toString(), HttpMethod.GET, new HttpEntity<>(null, headers), responseType);
    }

    public ResponseEntity<PlaylistResponse> createPlaylist(PlaylistRequest playlistRequest, String authorizationHeader) {
        UriComponents url = UriComponentsBuilder.fromHttpUrl(Urls.PLAYLIST_REQUEST)
                .build();

        HttpHeaders headers = new HttpHeaders();

        if (authorizationHeader != null) {
            headers.add("Authorization", authorizationHeader);
        }

        HttpEntity<PlaylistRequest> request = new HttpEntity<>(playlistRequest, headers);
        return restTemplate.exchange(url.toString(), HttpMethod.POST, request, PlaylistResponse.class);
    }

    public ResponseEntity<PlaylistResponse> addSongToPlaylist(String playlistId, SimpleSongRequest songRequest, String authorizationHeader) {
        UriComponents url = UriComponentsBuilder.fromHttpUrl(Urls.PLAYLIST_REQUEST)
                .pathSegment(String.valueOf(playlistId))
                .path("/songs")
                .build();

        HttpHeaders headers = new HttpHeaders();

        if (authorizationHeader != null) {
            headers.add("Authorization", authorizationHeader);
        }

        HttpEntity<SimpleSongRequest> request = new HttpEntity<>(songRequest, headers);
        return restTemplate.exchange(url.toString(), HttpMethod.PATCH, request, PlaylistResponse.class);
    }

}
