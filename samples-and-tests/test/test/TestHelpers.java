import org.junit.*;
import play.data.validation.Error;
import play.modules.validation.ValidationException;

import java.lang.String;
import java.lang.StringBuffer;
import java.util.List;

import static org.junit.Assert.*;

public class TestHelpers {

    public static void assertValidationError(ValidationException ex, String expectedKey, String expectedMessage) {
        List<Error> errors = ex.getErrors();
        for (Error error : errors) {
            if (expectedKey.equals(error.getKey()) && expectedMessage.equals(error.message())) {
                return;
            }
        }
        StringBuilder keys = new StringBuilder();
        StringBuilder messages = new StringBuilder();
        for (Error error : errors) {
            keys.append(' ').append(error.getKey());
            messages.append(' ').append(error.message());
        }
        fail("Didn't find proper validation error. Expected key:" + expectedKey + ", got:" + keys +
                "; message: " + expectedMessage + ", got: " + messages);
    }
}
