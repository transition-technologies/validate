package play.modules.validation

import play.data.validation.Error

/**
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
public class ValidationException extends RuntimeException {
    private List<Error> validationErrors = []

    ValidationException(String message) {
        super(message)
    }

    ValidationException(List<Error> validationErrors) {
        this(buildMessage(validationErrors), validationErrors)
    }

    ValidationException(String message, List<Error> validationErrors) {
        this(message)
        this.validationErrors = validationErrors
    }

    public void addError(Error error) {
        validationErrors << error
    }

    public List<Error> getErrors() {
        return Collections.unmodifiableList(validationErrors)
    }

    private static String buildMessage(List<Error> validationErrors) {
        def sb = new StringBuilder("Validation failed:")
        validationErrors.each {
            sb << "\n" << it.message()
        }
    }
}
