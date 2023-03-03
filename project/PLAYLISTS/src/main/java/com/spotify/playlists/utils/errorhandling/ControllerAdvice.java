package com.spotify.playlists.utils.errorhandling;

import com.spotify.playlists.utils.Urls;
import com.spotify.playlists.utils.errorhandling.customexceptions.ConflictException;
import com.spotify.playlists.utils.errorhandling.customexceptions.ForbiddenException;
import com.spotify.playlists.utils.errorhandling.customexceptions.ResourceNotFoundException;
import com.spotify.playlists.utils.errorhandling.customexceptions.UnauthorizedException;
import com.spotify.playlists.view.responses.ExceptionResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Log4j2
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    // for invalid query params
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), ConstraintViolationException.class.getSimpleName() ,ex.getMessage());

        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<ExceptionResponse> handleConflictExceptions(ConflictException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), ConflictException.class.getSimpleName() ,ex.getMessage());

        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundExceptions(RuntimeException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), ResourceNotFoundException.class.getSimpleName() ,ex.getMessage());

        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    // for invalid dto request fields
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
        if (ex.getMessage().contains("details")) {
            details = ex.getLocalizedMessage().split("details")[1].replaceAll("[^a-zA-Z0-9 -]", "");
        } else {
            details = "Bad request";
        }

        HttpStatus status = ex.getStatusCode();
        ExceptionResponse exceptionResponse = new ExceptionResponse(status.name(), status.value(), details);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(SoapFaultClientException.class)
    public ResponseEntity<String> handleSOAPClientErrorException(SoapFaultClientException ex) {
        log.info("[{}] -> handle {}, details:{}", this.getClass().getSimpleName(), SoapFaultClientException.class.getSimpleName() ,ex.getMessage());

        if(Objects.equals(ex.getMessage(), "Invalid token")){
            return new ResponseEntity<>(Urls.LOGIN_REQUEST_URL,HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
        log.info("[{}] -> handle {}", this.getClass().getSimpleName(), UnauthorizedException.class.getSimpleName() );

        return new ResponseEntity<>(Urls.LOGIN_REQUEST_URL,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException ex) {
        log.info("[{}] -> handle {}", this.getClass().getSimpleName(), ForbiddenException.class.getSimpleName() );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
