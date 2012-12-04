import models.*;
import org.junit.*;
import play.data.validation.Error;
import play.data.validation.Required;
import services.*;
import play.modules.validation.ValidationException;
import play.test.UnitTest;

import java.lang.System;
import java.util.Date;
import java.util.List;

public class MultiValidateTest extends UnitTest {

    private Service service;

    @Before
    public void setUp() {
        service = new Service();
    }

    @Test(expected = ValidationException.class)
    public void testNotEmptyInvalid() {
        try {
            service.validateMultiple("", "some@email.com", new Date(11));
        } catch (ValidationException ex) {
            TestHelpers.assertValidationError(ex, "notEmpty", "Required");
            throw ex;
        }
    }

    @Test(expected = ValidationException.class)
    public void testEmailInvalid() {
        try {
            service.validateMultiple("asd", "someemail.com", new Date(11));
        } catch (ValidationException ex) {
            TestHelpers.assertValidationError(ex, "email", "Invalid email address");
            throw ex;
        }
    }

    @Test(expected = ValidationException.class)
    public void testDateInvalid() {
        try {
            service.validateMultiple("asd", "some@email.com", new Date(System.currentTimeMillis() + 1000000));
        } catch (ValidationException ex) {
            TestHelpers.assertValidationError(ex, "date", "Must be in the past");
            throw ex;
        }
    }

    @Test
    public void testValidMultipleValue() {
        service.validateMultiple("not empty", "some@email.com", new Date(11));
    }
}
