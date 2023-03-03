package com.spotify.playlists.utils.errorhandling.customexceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("Forbidden");
    }
}