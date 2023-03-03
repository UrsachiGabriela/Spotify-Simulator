package spotify.services.validators;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import spotify.utils.errorhandling.customexceptions.UnprocessableContentException;
import spotify.model.entities.ArtistEntity;
import spotify.model.entities.SongEntity;
import spotify.utils.enums.MusicType;

@Log4j2
@Component
public class CreateValidator implements Validator {
    @Override
    public void validate(Object target, String... dependency) {
        log.info("[{}] -> validate entity before insertion: {}", this.getClass().getSimpleName(),target.getClass().getSimpleName());

        if (target.getClass().equals(SongEntity.class)) {
            SongEntity songEntity = (SongEntity) target;
            validateSong(songEntity);
        } else if (target.getClass().equals(ArtistEntity.class)) {
            ArtistEntity artistEntity = (ArtistEntity) target;
            validateArtist(artistEntity);
        }
    }

    private void validateArtist(ArtistEntity artistEntity) {

    }

    private void validateSong(SongEntity songEntity) {
        if (songEntity.getType().equals(MusicType.ALBUM) && songEntity.getParent() != null) {
            throw new UnprocessableContentException("An album cannot be part of another album");
        }

        if (songEntity.getParent() != null && songEntity.getParent().getType().equals(MusicType.SONG)) {
            throw new UnprocessableContentException("A song cannot contain another songs");
        }
    }
}
