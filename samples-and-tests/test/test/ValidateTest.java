import models.*;
import org.junit.*;
import services.*;
import play.modules.validation.ValidationException;
import play.test.UnitTest;

import java.util.Date;

public class ValidateTest extends UnitTest {

    private Service service;

    @Before
    public void setUp() {
        service = new Service();
    }

    @Test(expected = ValidationException.class)
    public void testInvalidNullValue() {
        service.validateRequired(null);
    }

    @Test(expected = ValidationException.class)
    public void testInvalidEmptyValue() {
        service.validateRequired("");
    }

    @Test
    public void testValidValue() {
        service.validateRequired("not empty");
    }
}
