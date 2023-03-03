package com.spotify.playlists.services.clients;

import com.spotify.playlists.utils.Urls;
import com.spotify.playlists.view.SongRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClient {
    private RestTemplateBuilder restTemplateBuilder;

    private final RestTemplate restTemplate;

    @Autowired
    public RestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.restTemplate = restTemplateBuilder.build();
    }

    public SongRequestResponse getSongById(Integer songId) {

        return this.restTemplate.getForObject(Urls.SONG_REQUEST + songId, SongRequestResponse.class);

    }
}
