package spotify.services.validators;

/**
 * Validations based on resource representation/properties
 */
public interface Validator {
    void validate(Object target, String... dependency);
}
