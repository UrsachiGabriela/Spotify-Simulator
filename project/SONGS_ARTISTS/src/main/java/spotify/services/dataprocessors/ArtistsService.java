package spotify.services.dataprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import spotify.utils.errorhandling.customexceptions.ConflictException;
import spotify.utils.errorhandling.customexceptions.EntityNotFoundException;
import spotify.model.entities.ArtistEntity;
import spotify.model.entities.SongEntity;
import spotify.model.repos.ArtistsRepository;
import spotify.services.validators.CreateValidator;
import spotify.utils.errorhandling.ErrorMessages;

import java.util.*;
import java.util.stream.Collectors;

// SQL constraints validation is part of this service
@Component
public class ArtistsService {
    @Autowired
    private ArtistsRepository artistsRepository;

    @Autowired
    private CreateValidator createValidator;

    public Set<ArtistEntity> getAllArtists() {
        return new HashSet<>(artistsRepository.findAll());
    }

    public Page<ArtistEntity> getPageableArtists(Integer page, Integer pageSize, String name, String match) {
        Pageable paging;

        if (page == null) {
            paging = PageRequest.ofSize(pageSize);
        } else {
            paging = PageRequest.of(page, pageSize);
        }

        if (name == null) {
            return artistsRepository.findAll(paging);
        } else {
            if (match == null) {
                return artistsRepository.findAllByNameContaining(name, paging);
            } else {
                return artistsRepository.findAllByName(name, paging);
            }
        }
    }

    public ArtistEntity getArtistByUUID(UUID uuid) {
        Optional<ArtistEntity> artistEntity = artistsRepository.findById(uuid.toString());

        if (!artistEntity.isPresent()) {
            throw new EntityNotFoundException(ErrorMessages.ARTIST_NOT_FOUND + uuid);
        }

        return artistEntity.get();
    }

    public ArtistEntity getArtistByName(String name) {
        ArtistEntity artistEntity = artistsRepository.findByName(name);

        if (artistEntity == null) {
            throw new ConflictException(ErrorMessages.ARTIST_NOT_FOUND + name);
        }

        return artistEntity;
    }

    public boolean itExistsArtist(UUID uuid) {
        return artistsRepository.findById(uuid.toString()).isPresent();
    }

    public ArtistEntity createOrReplaceArtist(ArtistEntity artistEntity) {
        // verificare suplimentara doar pt a avea mesaj custom la exceptie; altfel, e returnat in response mesajul exceptiei din bd
        ArtistEntity artistEntityWithSameName = artistsRepository.findByName(artistEntity.getName());
        if ((artistEntityWithSameName != null) && (!Objects.equals(artistEntityWithSameName.getUuid(), artistEntity.getUuid()))) {
            throw new ConflictException(artistEntity.getName() + ErrorMessages.NAME_ALREADY_EXISTENT);
        }

        return artistsRepository.save(artistEntity);
    }

    public ArtistEntity deleteArtist(UUID uuid) {
        ArtistEntity artistEntity = getArtistByUUID(uuid);
        artistsRepository.delete(artistEntity);

        return artistEntity;
    }

    public ArtistEntity addSongsToArtist(ArtistEntity artistEntity, Set<SongEntity> songEntities) {
        verifyArtistActivity(artistEntity);

        Set<SongEntity> newSongsSet = artistEntity.getSongs();
        newSongsSet.addAll(songEntities);

        artistEntity.setSongs(newSongsSet);
        return artistsRepository.save(artistEntity);
    }

    public void assignSongToMultipleArtists(Set<ArtistEntity> artistEntities, SongEntity songEntity){
        artistEntities.forEach(artistEntity -> addSongsToArtist(artistEntity, new HashSet<>() {{
            add(songEntity);
        }}));
    }

    public Set<ArtistEntity> getArtistsByNameIfActive(Set<String> artistNames){
        Set<ArtistEntity> artistEntities = new HashSet<>();

        for (String artistName : artistNames) {
            artistEntities.add(getArtistByName(artistName));
        }
        artistEntities.forEach(this::verifyArtistActivity);

        return artistEntities;
    }

    public void removeSongFromArtists(int songId) {
        List<ArtistEntity> artistEntities = artistsRepository.artistsForGivenSong(songId);

        for (ArtistEntity a : artistEntities) {
            Set<SongEntity> remainedSongs = a.getSongs().stream().filter(s -> s.getId() != songId).collect(Collectors.toSet());
            a.setSongs(remainedSongs);
            artistsRepository.save(a);
        }
    }
    public Set<ArtistEntity> getArtistForGivenSong(int songId) {
        List<ArtistEntity> artistEntities = artistsRepository.artistsForGivenSong(songId);
        return new HashSet<>(artistEntities);
    }

    private void verifyArtistActivity(ArtistEntity artistEntity){
        if(!artistEntity.isActive())
            throw new ConflictException(artistEntity.getName()+": "+ErrorMessages.INACTIVE_ARTIST);
    }
}
