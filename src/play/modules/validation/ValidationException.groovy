package play.modules.validation

import play.data.validation.Error
import play.data.validation.Validation

/**
 * Exception showing that data validation has failed.
 * <p/>
 * Use {@link ValidationException#getErrors} to get more information
 * about objects that failed validation.
 *
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

    /**
     * Add validation error to current validation errors list.
     *
     * @param error
     */
    public void addError(Error error) {
        validationErrors << error
    }

    /**
     * Get unmodifiable list of validation errors that caused this
     * exception to be thrown.
     *
     * @return Unmodifiable list of errors
     */
    public List<Error> getErrors() {
        return Collections.unmodifiableList(validationErrors)
    }

    /**
     * Fill current thread validation object (from stock Play validation
     * mechanism) with errors collected by this exception.
     */
    public void fillValidation() {
        Validation validation = Validation.current()
        errors.each {error ->
            //FIXME: Using package private fields, be more elegant please :)
            validation.addError(error.key, error.message, error.variables)
        }
    }

    /**
     * Build readable message for constructor with only list of validation errors.
     *
     * @param validationErrors
     * @return
     */
    private static String buildMessage(List<Error> validationErrors) {
        def sb = new StringBuilder("Validation failed:")
        validationErrors.each {
            sb << "\n" << it.message()
        }
    }
}
