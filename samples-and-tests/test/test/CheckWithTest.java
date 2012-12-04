import models.*;
import org.junit.*;
import services.*;
import play.modules.validation.ValidationException;
import play.test.UnitTest;

import java.util.Date;

public class CheckWithTest extends UnitTest {

    private Service service;

    @Before
    public void setUp() {
        service = new Service();
    }

    @Test(expected = ValidationException.class)
    public void testInvalidNullValue() {
        service.validateCheckWith(null);
    }

    @Test(expected = ValidationException.class)
    public void testInvalidEmptyValue() {
        service.validateCheckWith("");
    }

    @Test(expected = ValidationException.class)
    public void testInvalidValue() {
        service.validateCheckWith("some test invalid value");
    }

    @Test
    public void testValidValue() {
        service.validateCheckWith("valid");
    }
}
