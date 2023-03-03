package com.spotify.playlists.utils.errorhandling.customexceptions;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
