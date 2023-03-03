package spotify.services.dataprocessors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import spotify.model.entities.ArtistEntity;
import spotify.model.repos.ArtistsRepository;
import spotify.utils.errorhandling.customexceptions.ConflictException;
import spotify.utils.errorhandling.customexceptions.EntityNotFoundException;
import spotify.model.entities.SongEntity;
import spotify.model.repos.SongsRepository;
import spotify.services.validators.CreateValidator;
import spotify.services.validators.FilterValidator;
import spotify.utils.errorhandling.ErrorMessages;
import spotify.utils.enums.MusicGenre;
import spotify.utils.enums.MusicType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SongsService {

    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private ArtistsRepository artistsRepository;
    @Autowired
    private CreateValidator createValidator;
    @Autowired
    private FilterValidator filterValidator;

    public Set<SongEntity> getAllSongs() {
        return new HashSet<>(songsRepository.findAll());
    }

    public Page<SongEntity> getPageableSongs(Integer page, Integer pageSize, String searchBy, String searchedValue, String match) {
        Pageable paging = getPageable(page, pageSize);

        if (searchBy == null || searchedValue == null) {
            // display all without conditions
            return getPageableSongsWithoutCondition(paging);

        } else {
            // validate type of searched value for given searchedBy param
            filterValidator.validate(searchedValue, searchBy);

            return searchBy.equals("title") ? (getPageableSongsByTitle(paging, searchedValue, match))
                    : (searchBy.equals("year") ? getPageableSongsByYear(paging, Integer.parseInt(searchedValue))
                    : getPageableSongsByGenre(paging, MusicGenre.valueOf(searchedValue.toUpperCase())));

        }
    }

    private Page<SongEntity> getPageableSongsWithoutCondition(Pageable paging) {
        return songsRepository.findAll(paging);
    }

    private Page<SongEntity> getPageableSongsByTitle(Pageable paging, String title, String match) {
        return match != null ? songsRepository.findAllByName(title, paging) : songsRepository.findAllByNameContaining(title, paging);
    }

    private Page<SongEntity> getPageableSongsByYear(Pageable paging, Integer year) {
        return songsRepository.findAllByYear(year, paging);

    }

    private Page<SongEntity> getPageableSongsByGenre(Pageable paging, MusicGenre musicGenre) {
        return songsRepository.findAllByGenre(musicGenre, paging);
    }

    public SongEntity getSongById(int sid) {
        Optional<SongEntity> songEntity = songsRepository.findById(sid);

        if (!songEntity.isPresent()) {
            throw new EntityNotFoundException(ErrorMessages.SONG_NOT_FOUND + sid);
        }
        return songEntity.get();
    }

    public SongEntity getAlbumOfSong(int albumId) {
        try{
            return getSongById(albumId);
        }
        catch (EntityNotFoundException ex){
            throw new ConflictException(ErrorMessages.INEXISTENT_ALBUM + albumId);
        }
    }

    public void updateSong(SongEntity oldEntity,SongEntity newEntity){
        newEntity.setId(oldEntity.getId());
        newEntity.setType(oldEntity.getType());
        newEntity.setParent(oldEntity.getParent());
        newEntity.setSongEntities(oldEntity.getSongEntities());

        createValidator.validate(newEntity);
        songsRepository.save(newEntity);
    }

    public SongEntity createNewSong(SongEntity songEntity) {
        createValidator.validate(songEntity);
        return songsRepository.save(songEntity);
    }

    public SongEntity deleteSong(Integer id) {
        SongEntity songEntity = getSongById(id);

        if (songEntity.getType().equals(MusicType.ALBUM) && !songEntity.getSongEntities().isEmpty()) {
            throw new ConflictException("You are not able to remove this album until you remove all its songs");
        }

        //artistsService.removeSongFromArtists(songEntity.getId());
        songsRepository.deleteById(songEntity.getId());

        return songEntity;
    }


    private Pageable getPageable(Integer page, Integer pageSize) {
        Pageable paging;

        if (page == null) {
            paging = PageRequest.ofSize(pageSize);
        } else {
            paging = PageRequest.of(page, pageSize);
        }

        return paging;
    }

}
