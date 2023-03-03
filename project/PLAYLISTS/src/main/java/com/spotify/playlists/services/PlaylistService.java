package com.spotify.playlists.services;

import com.spotify.playlists.utils.errorhandling.customexceptions.ConflictException;
import com.spotify.playlists.utils.errorhandling.customexceptions.ResourceNotFoundException;
import com.spotify.playlists.model.collections.Playlist;
import com.spotify.playlists.model.collections.Resource;
import com.spotify.playlists.model.repos.PlaylistRepository;
import com.spotify.playlists.utils.errorhandling.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Set<Playlist> getAllPlaylists(Integer userId, String playlistName) {
        String collectionName = String.format("user.%d.playlists", userId);


        playlistRepository.setCollectionName(collectionName);

        if (playlistName == null) {
            return new HashSet<>(playlistRepository.findAll());
        }

        List<Playlist> playlists = playlistRepository.findAllByNameContaining(playlistName);

        return new HashSet<>(playlists);
    }

    public Playlist getPlaylistById(Integer userId, String id) {
        String collectionName = String.format("user.%d.playlists", userId);

        if (!mongoTemplate.collectionExists(collectionName)) {
            throw new ResourceNotFoundException(ErrorMessages.COLLECTION_NOT_FOUND);
        }

        playlistRepository.setCollectionName(collectionName);

        Optional<Playlist> playlist = playlistRepository.findById(id);

        if (!playlist.isPresent()) {
            throw new ResourceNotFoundException(ErrorMessages.PLAYLIST_NOT_FOUND + id);
        }

        return playlist.get();
    }

    public Playlist createPlaylist(Integer userId, Playlist playlist) {
        String collectionName = String.format("user.%d.playlists", userId);

        playlistRepository.setCollectionName(collectionName);

        // daca exista deja un playlist cu acelasi nume (UK pe nume playlist)
        if (mongoTemplate.collectionExists(collectionName) && playlistRepository.existsByName(playlist.getName())) {
            throw new ConflictException(ErrorMessages.PLAYLIST_ALREADY_EXISTS);
        }

        return playlistRepository.save(playlist);
    }

    public Playlist addSongToPlaylist(Integer userId, String id, Resource resource) {
        String collectionName = String.format("user.%d.playlists", userId);
        playlistRepository.setCollectionName(collectionName);

        if (!mongoTemplate.collectionExists(collectionName)) {
            throw new ResourceNotFoundException(ErrorMessages.COLLECTION_NOT_FOUND);
        }

        Optional<Playlist> playlist = playlistRepository.findById(id);

        if (!playlist.isPresent()) {
            throw new ResourceNotFoundException(ErrorMessages.PLAYLIST_NOT_FOUND + id);
        }

        playlist.get().getFavSongs().add(resource);
        return playlistRepository.save(playlist.get());
    }

    public Playlist deletePlaylist(Integer userId, String id) {
        String collectionName = String.format("user.%d.playlists", userId);

        if (!mongoTemplate.collectionExists(collectionName)) {
            throw new ResourceNotFoundException(ErrorMessages.COLLECTION_NOT_FOUND);
        }

        playlistRepository.setCollectionName(collectionName);

        Optional<Playlist> playlist = playlistRepository.findById(id);

        if (!playlist.isPresent()) {
            throw new ResourceNotFoundException(ErrorMessages.PLAYLIST_NOT_FOUND + id);
        }

        playlistRepository.deleteById(id);
        return playlist.get();
    }

//    public Playlist deleteSongFromPlaylist(String userId, String playlistId, Resource resource){
//
//    }

//    public Playlist getPlaylistByName(String userId, String playlistName) {
//        String collectionName = String.format("user.%s.playlists", userId);
//        playlistRepository.setCollectionName(collectionName);
//
//        Optional<Playlist> playlist = playlistRepository.findByName(playlistName);
//
//        if (playlist.isEmpty()) {
//            throw new EntityNotFoundException(ErrorMessages.PLAYLIST_NOT_FOUND + playlistName);
//        }
//
//        return playlist.get();
//    }

    public void savePlaylist(Playlist playlist) {
        playlistRepository.save(playlist);
    }


}
