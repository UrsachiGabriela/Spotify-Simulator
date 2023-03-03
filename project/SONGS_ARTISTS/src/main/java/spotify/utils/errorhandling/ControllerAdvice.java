package spotify.utils.errorhandling;

import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.ws.soap.client.SoapFaultClientException;
import spotify.utils.Urls;
import spotify.utils.errorhandling.customexceptions.*;
import spotify.view.responses.ExceptionResponse;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

@Log4j2
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(javax.validation.ConstraintViolationException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), ConstraintViolationException.class.getSimpleName() ,ex.getMessage());

        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }


    @ExceptionHandler({ConflictException.class, SQLIntegrityConstraintViolationException.class, ConstraintViolationException.class})
    public ResponseEntity<ExceptionResponse> handleConflictExceptions(RuntimeException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), RuntimeException.class.getSimpleName() ,ex.getMessage());

        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    // trying to get resource that doesn't exist
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundExceptions(EntityNotFoundException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), EntityNotFoundException.class.getSimpleName() ,ex.getMessage());

        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({UnprocessableContentException.class, IllegalArgumentException.class})
    public ResponseEntity<ExceptionResponse> handleUnprocessableContentException(RuntimeException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), RuntimeException.class.getSimpleName() ,ex.getMessage());

        String details = ex.getMessage();
        if (details.contains("Invalid UUID string") || details.contains("UUID string too large")) {
            details = "Invalid UUID";
        }
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    // for invalid dto request properties
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), MethodArgumentNotValidException.class.getSimpleName() ,ex.getMessage());

        String details = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ExceptionResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), HttpClientErrorException.class.getSimpleName() ,ex.getMessage());

        String details;
        if (Objects.requireNonNull(ex.getMessage()).contains("details")) {
            details = ex.getLocalizedMessage().split("details")[1].replaceAll("[^a-zA-Z0-9 -]", "");
        } else {
            details = ex.getLocalizedMessage().split("error")[1].replaceAll("[^a-zA-Z0-9 -]", "");
        }

        HttpStatus status = ex.getStatusCode();
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(SoapFaultClientException.class)
    public ResponseEntity<String> handleSOAPClientErrorException(SoapFaultClientException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), SoapFaultClientException.class.getSimpleName() ,ex.getMessage());

        if (Objects.equals(ex.getMessage(), "Invalid token")) {
            return new ResponseEntity<>(Urls.LOGIN_REQUEST_URL, HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
        log.info("[{}] -> handle {}", this.getClass().getSimpleName(), UnauthorizedException.class.getSimpleName());

        return new ResponseEntity<>(Urls.LOGIN_REQUEST_URL, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException ex) {
        log.info("[{}] -> handle {}", this.getClass().getSimpleName(), ForbiddenException.class.getSimpleName());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
