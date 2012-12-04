/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package play.modules.validation

import javassist.*
import javassist.bytecode.CodeAttribute
import javassist.bytecode.LocalVariableAttribute
import javassist.bytecode.ParameterAnnotationsAttribute
import net.sf.oval.configuration.annotation.Constraint
import play.Logger
import play.classloading.ApplicationClasses.ApplicationClass
import play.classloading.enhancers.ControllersEnhancer
import play.classloading.enhancers.Enhancer
import play.mvc.Util
import static javassist.Modifier.*

/**
 * Playframework class enhancer adding method parameters validation to
 * all classes that require it.
 * <p/>
 * Conditions for method to have validation injected:
 * <ul>
 *     <li>Method is not Play controller method (not a public static method in controller class) or has {@link Util} annotation</li>
 *     <li>Method is not abstract</li>
 *     <li>Method has at least one parameter annotated with annotation that is a {@link Constraint}</li>
 * </ul>
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
public class ValidateEnhancer extends Enhancer {

    /**
     * Name of auto-generated static field that will keep map
     * of original parameter names so they can be retrieved in runtime
     * (there's no way to retrieve parameter names via reflection)
     */
    static final String PARAMETER_NAMES_FIELD = '$$validation_method_parameters$$'

    /**
     * Check if class should be enhanced
     * and add validation as first step to all methods
     * that have parameters annotated with validation annotations.
     *
     * @param ac class to enhance
     * @throws Exception
     */
    @Override
    public void enhanceThisClass(ApplicationClass ac)
    throws Exception {
        CtClass clazz = makeClass(ac)

        if (shouldEnhance(clazz)) {
            def methods = clazz.getDeclaredMethods()
            methods.grep({ shouldEnhance(clazz, it) }).each { method ->
                Logger.debug "Injecting validation code in method: ${method.longName}"
                initializeMethodParamsField(clazz)
                collectParameterNames(clazz, method)
                enhanceMethod(clazz, method)
            }

            clazz.rebuildClassFile()

            ac.enhancedByteCode = clazz.toBytecode();
            clazz.defrost();
        }
    }

    /**
     * Collect original parameter names and store them in generated static field
     * for reference when generating validation error messages (which parameter failed validation)
     *
     * @param clazz
     * @param method
     */
    private def collectParameterNames(CtClass clazz, CtMethod method) {
        Logger.debug "Collecting parameter names for validated methods in ${clazz.name}"

        def initializer = clazz.classInitializer ?: clazz.makeClassInitializer()
        def values = []

        def codeAtt = method.methodInfo.getAttribute(CodeAttribute.tag)
        def localVars = codeAtt?.getAttribute(LocalVariableAttribute.tag)

        use(ModifiersCategory) {
            boolean isStatic = method.hasModifier(STATIC)

            for (i in (0..<method.parameterTypes.length)) {
                int j = i
                if (!isStatic) {
                    //Non static methods have 1st implicit parameter - this. We don't need it.
                    ++j
                }
                def varName = localVars.variableName(j)
                values << varName
            }

        }
        def code = "${PARAMETER_NAMES_FIELD}.put(\"$method.name\", new String[]{\"${values.join("\", \"")}\"});"
        Logger.debug "Injecting in static block:  $code "
        initializer.insertAfter(code)
    }

    /**
     * Create parameter names map field in processed class if needed
     * @param clazz
     * @return
     */
    private def initializeMethodParamsField(CtClass clazz) {
        try {
            clazz.getDeclaredField(PARAMETER_NAMES_FIELD)
        } catch (NotFoundException e) {
            CtField f = CtField.make("java.util.Map $PARAMETER_NAMES_FIELD = new java.util.HashMap();", clazz)
            f.setModifiers(PUBLIC | STATIC)
            clazz.addField(f)
        }
    }

    /**
     * Insert ValidatePlugin.validateMethod call in the beginning of a method
     * @param clazz
     * @param method
     * @return
     */
    private def enhanceMethod(CtClass clazz, CtMethod method) {
        use(ModifiersCategory) {
            Logger.debug "Enhancing: ${method.longName}"
            boolean isStatic = method.hasModifier(STATIC)
            if (isStatic) {
                method.insertBefore("play.modules.validation.ValidatePlugin.validateMethod(${clazz.name}.class, null, \"${method.name}\", \$sig, \$args);")
            } else {
                method.insertBefore("play.modules.validation.ValidatePlugin.validateMethod(${clazz.name}.class, this, \"${method.name}\", \$sig, \$args);")
            }
        }
    }

    /**
     * Check if class should be nehanced
     * @param clazz
     * @return
     */
    private boolean shouldEnhance(CtClass clazz) {
        use(ModifiersCategory) {
            return !clazz.hasModifier(ABSTRACT)
        }
    }

    /**
     * Check if method should be enhanced
     *
     * @param clazz
     * @param method
     * @return
     */
    private boolean shouldEnhance(CtClass clazz, CtMethod method) {
        //Ignore controller methods that are not utilities, they have OOTB play validation
        use(ModifiersCategory) {
            return ((!clazz.subtypeOf(classPool.get(ControllersEnhancer.ControllerSupport.class.name))
                    || !method.hasExactModifier(PUBLIC, STATIC)
                    || hasAnnotation(method, Util.class))
                    && !method.hasModifier(ABSTRACT)
                    && hasValidatedParameter(method))

        }
    }

    private boolean hasAnnotation(annotated, annotationClass) {
        def annotations = getAnnotations(annotated)
        for (ann in annotations.annotations) {
            if (ann.typeName == annotationClass.name) {
                return true
            }
        }

        return false
    }

    /**
     * Check if method has at least one parameter that has to be validated
     * @param method
     * @return
     */
    private boolean hasValidatedParameter(method) {
        def allAnnotations = method.methodInfo?.getAttribute(ParameterAnnotationsAttribute.visibleTag)?.annotations
        for (paramAnnotations in allAnnotations) {
            for (annotation in paramAnnotations) {
                def annotationClass = classPool.get(annotation.typeName)
                if (hasAnnotation(annotationClass, Constraint.class)) {
                    return true
                }
            }
        }

        return false
    }
}
