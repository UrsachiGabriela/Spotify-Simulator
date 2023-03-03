package com.spotify.playlists.model.repos;

import com.spotify.playlists.model.collections.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface PlaylistRepository extends MongoRepository<Playlist, String>, CustomRepository {
    boolean existsByName(String name);

    Optional<Playlist> findByName(String name);

    List<Playlist> findAllByNameContaining(String name);
}
