import controllers.*;
import org.junit.Test;
import play.modules.validation.ValidationException;
import play.mvc.results.Redirect;
import play.test.UnitTest;

import java.lang.NoSuchMethodException;
import java.lang.String;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ControllerTest extends UnitTest {

    @Test
    public void testValidPrivate() throws Exception {
        Method method = Application.class.getDeclaredMethod("privateMethod", String.class);
        method.setAccessible(true);
        method.invoke(null, "something");
    }

    @Test(expected = ValidationException.class)
    public void testInvalidPrivate() throws Throwable {
        Method method = Application.class.getDeclaredMethod("privateMethod", String.class);
        method.setAccessible(true);
        try {
            method.invoke(null, "");
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    public void testUtilValid() {
        Application.util("something");
    }

    @Test(expected = ValidationException.class)
    public void testUtilInvalid() {
        Application.util("");
    }

    @Test(expected = Redirect.class)
    public void testControllerMethodInvalid() {
        Application.controllerMethod(null);
    }

    @Test(expected = Redirect.class)
    public void testControllerMethodValid() {
        Application.controllerMethod("something");
    }
}
