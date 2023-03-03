package com.spotify.playlists.services.mappers;


import com.spotify.playlists.model.collections.Playlist;
import com.spotify.playlists.view.requests.PlaylistRequest;
import com.spotify.playlists.view.responses.PlaylistResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;


@Mapper(uses = {SongMapper.class})
public interface PlaylistMapper {
    PlaylistMapper INSTANCE = Mappers.getMapper(PlaylistMapper.class);

    PlaylistResponse toPlaylistDTO(Playlist playlist);

    Playlist toPlaylist(PlaylistResponse playlistResponse);

    Playlist toPlaylist(PlaylistRequest playlistRequest);

    Set<PlaylistResponse> toPlaylistDTOSet(Set<Playlist> playlists);
}
