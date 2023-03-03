package com.spotify.playlists.model.repos;

// https://stackoverflow.com/questions/12274019/how-to-configure-mongodb-collection-name-for-a-class-in-spring-data


public interface CustomRepository {
    String getCollectionName();

    void setCollectionName(String newCollectionName);
}
