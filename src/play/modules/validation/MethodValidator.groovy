package play.modules.validation

import net.sf.oval.ConstraintViolation
import net.sf.oval.guard.Guard

import java.lang.reflect.Method

/**
 * Oval validator used to validate methods
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
class MethodValidator extends Guard {

    /**
     * Check if method parameters and pre-conditions are valid
     *
     * @param object object for method (null if static method)
     * @param method method to validate
     * @param params passed in parameters
     *
     * @return list of violations. Will be empty if method is valid.
     */
    public List<ConstraintViolation> vaidateMethod(Object object, Method method, Object[] params) {
        def violations = new ArrayList()
        validateMethodParameters(object, method, params, violations)
        validateMethodPre(object, method, params, violations)
        return violations
    }
}
