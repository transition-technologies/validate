import models.*;
import org.junit.*;
import play.data.validation.Error;
import play.data.validation.Required;
import play.data.validation.Validation;
import services.*;
import play.modules.validation.ValidationException;
import play.test.UnitTest;

import java.lang.System;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ExceptionTest extends UnitTest {

    private Service service;

    @Before
    public void setUp() {
        service = new Service();
    }

    @Test(expected = ValidationException.class)
    public void testFillCurrentValidation() {
        try {
            service.validateMultiple("", "some@email.com", new Date(11));
        } catch (ValidationException ex) {
            ex.fillValidation();
            assertTrue(Validation.hasErrors());
            assertFalse(Validation.errors().isEmpty());
            assertNotNull(Validation.error("notEmpty"));
            throw ex;
        }
    }
}
