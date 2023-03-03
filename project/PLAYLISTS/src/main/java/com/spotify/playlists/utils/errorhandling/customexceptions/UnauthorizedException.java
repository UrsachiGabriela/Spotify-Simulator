package com.spotify.playlists.utils.errorhandling.customexceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Unauthorized");
    }
}