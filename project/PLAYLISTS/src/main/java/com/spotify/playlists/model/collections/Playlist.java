package com.spotify.playlists.model.collections;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Document(collection = "#{@playlistRepository.getCollectionName()}")
public class Playlist {
    @Id
    private String id;

    @NonNull
    private String name;
    private Set<Resource> favSongs = new HashSet<>();

    public Playlist(@NotNull String name) {
        super();
        this.name = name;
    }
}
