/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package play.modules.validation

import play.Play
import play.PlayPlugin
import play.data.validation.Error
import play.classloading.ApplicationClasses.ApplicationClass
import play.data.validation.Validation

import java.lang.reflect.Method
import javassist.runtime.Desc
import play.Logger

/**
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
public class ValidatePlugin extends PlayPlugin {

    private final ValidateEnhancer enhancer = new ValidateEnhancer()

    @Override
    public void enhance(ApplicationClass applicationClass)
    throws Exception {
        enhancer.enhanceThisClass(applicationClass)
    }

    @Override
    void onLoad() {
        Logger.debug("Configuring Javassist to use context classloader");
        Desc.useContextClassLoader = true
    }

    /**
     * Execute method parameters validation for passed in method.
     * Validation should be configured using standard Playframework validation
     * annotation mechanisms.
     *
     * @param clazz Class validated method is declared in
     * @param object Object validation should be executed for (instance for non-static method). Will be null for static methods.
     * @param methodName name of validated method
     * @param paramTypes validated method parameter types
     * @param params validated method parameters (objects that will actually be validated
     *
     * @throws ValidationException thrown if validation failed and validation engine was configured for exception throwing
     */
    public static void validateMethod(Class clazz, Object object, String methodName, Class[] paramTypes, Object[] params)
    throws ValidationException {
        def validator = new MethodValidator()
        def method = clazz.getDeclaredMethod(methodName, paramTypes)
        def violations = validator.vaidateMethod(object, method, params)

        def errors = []

        violations.each { violation ->
            def allNames = clazz.getDeclaredField(ValidateEnhancer.PARAMETER_NAMES_FIELD).get(null)
            def names = allNames[methodName]
            def messageVariables = violation.messageVariables ? violation.messageVariables.values() as String[] : new String[0]
            errors << [names[violation.context.parameterIndex], violation.message, messageVariables]
        }
        if (errors && shouldThrowException(method)) {
            throw new ValidationException(errors.collect({ new Error(it[0], it[1], it[2]) }))
        } else {
            errors.each { error ->
                Validation.current().addError(error)
            }
        }
    }


    private static boolean shouldThrowException(Method method) {
        def annotation = method.getAnnotation(Validate.class)
        if (annotation) {
            return annotation.throwException()
        } else {
            return Play.configuration.getProperty("validation.throwException", "true") as boolean
        }
    }
}
