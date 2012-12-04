import models.*;
import org.junit.*;
import services.*;
import play.modules.validation.ValidationException;
import play.test.UnitTest;

import java.util.Date;

public class AnnotationTest extends UnitTest {

    private Service service;

    @Before
    public void setUp() {
        service = new Service();
    }

    @Test
    public void testValid() {
        service.validateAnnotation("check");
    }

    @Test(expected = ValidationException.class)
    public void testInvalidNull() {
        service.validateAnnotation(null);
    }
}
