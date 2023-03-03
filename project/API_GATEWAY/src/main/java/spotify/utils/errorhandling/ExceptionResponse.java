package spotify.utils.errorhandling;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExceptionResponse implements Serializable {
    String error;
    Integer status;
    String details;

}
