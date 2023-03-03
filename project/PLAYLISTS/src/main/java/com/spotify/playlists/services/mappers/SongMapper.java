package com.spotify.playlists.services.mappers;

import com.spotify.playlists.model.collections.Resource;
import com.spotify.playlists.view.SongRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SongMapper {
    SongMapper INSTANCE = Mappers.getMapper(SongMapper.class);

    default Resource toSongResource(SongRequestResponse songRequestResponse) {
        if (songRequestResponse == null) {
            return null;
        }

        Resource resource = new Resource();

        resource.setResourceId(songRequestResponse.getId());
        resource.setName(songRequestResponse.getName());
        resource.setLink(songRequestResponse.getLink("self").get().getHref());

        return resource;
    }
}
