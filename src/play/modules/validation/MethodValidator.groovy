package play.modules.validation

import net.sf.oval.ConstraintViolation
import net.sf.oval.guard.Guard

import java.lang.reflect.Method

/**
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
class MethodValidator extends Guard {

    public List<ConstraintViolation> vaidateMethod(Object object, Method method, Object[] params) {
        def violations = new ArrayList()
        validateMethodParameters(object, method, params, violations)
        validateMethodPre(object, method, params, violations)
        return violations
    }
}
