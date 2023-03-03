package spotify.model.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import spotify.model.entities.SongEntity;
import spotify.utils.enums.MusicGenre;

public interface SongsRepository extends JpaRepository<SongEntity, Integer> {
    Page<SongEntity> findAllByName(String name, Pageable pageable);

    Page<SongEntity> findAllByNameContaining(@Param("name") String name, Pageable pageable);

    Page<SongEntity> findAllByGenre(MusicGenre genre, Pageable pageable);

    Page<SongEntity> findAllByYear(Integer year, Pageable pageable);

}
