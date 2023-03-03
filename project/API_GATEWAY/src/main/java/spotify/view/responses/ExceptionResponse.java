package spotify.view.responses;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class ExceptionResponse implements Serializable {
    String error;
    Integer status;
    String details;

}
