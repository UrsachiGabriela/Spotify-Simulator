package com.spotify.playlists.model.repos;


public class CustomRepositoryImpl implements CustomRepository {
    private static String collectionName;

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public void setCollectionName(String newCollectionName) {
        collectionName = newCollectionName;
    }
}
