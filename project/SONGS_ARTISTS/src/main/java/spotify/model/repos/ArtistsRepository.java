package spotify.model.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spotify.model.entities.ArtistEntity;

import java.util.List;
import java.util.UUID;

public interface ArtistsRepository extends JpaRepository<ArtistEntity, String> {

    ArtistEntity findByName(String name);

    @Query(value = "SELECT * FROM artists a, artists_songsAlbums asa\n" +
            "WHERE a.UUID = asa.artist_ID\n" +
            "AND asa.song_ID =?1", nativeQuery = true)
    List<ArtistEntity> artistsForGivenSong(int songId);

    Page<ArtistEntity> findAllByName(String name, Pageable pageable);

    Page<ArtistEntity> findAllByNameContaining(String name, Pageable pageable);

}

