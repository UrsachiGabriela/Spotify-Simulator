package spotify.services.validators;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import spotify.utils.errorhandling.customexceptions.UnprocessableContentException;
import spotify.utils.errorhandling.ErrorMessages;
import spotify.utils.enums.MusicGenre;

@Log4j2
@Component
public class FilterValidator implements Validator {
    @Override
    public void validate(Object target, String... dependency) {
        log.info("[{}] -> validate query params values ", this.getClass().getSimpleName());
        switch (dependency[0]) {
            case "title":
                validateTitle(target);
                break;
            case "year":
                validateYear(target);
                break;
            case "genre":
                validateGenre(target);
                break;
        }
    }

    private void validateTitle(Object title) {

    }

    private void validateYear(Object year) {
        try {
            int y = Integer.parseInt(year.toString());
            if (y < 0 || y >= 4000) {
                throw new UnprocessableContentException(ErrorMessages.INVALID_YEAR);
            }
        } catch (IllegalArgumentException ex) {
            throw new UnprocessableContentException(ErrorMessages.INVALID_YEAR);
        }
    }

    private void validateGenre(Object genre) {
        try {
            MusicGenre mg = MusicGenre.valueOf(genre.toString().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new UnprocessableContentException(ErrorMessages.INVALID_MUSIC_GENRE);
        }
    }
}
