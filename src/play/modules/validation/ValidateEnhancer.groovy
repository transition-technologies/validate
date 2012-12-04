/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package play.modules.validation

import javassist.*
import javassist.bytecode.CodeAttribute
import javassist.bytecode.LocalVariableAttribute
import javassist.bytecode.ParameterAnnotationsAttribute
import javassist.bytecode.annotation.Annotation
import org.h2.constraint.Constraint
import play.Logger
import play.Play
import play.classloading.ApplicationClasses.ApplicationClass
import play.classloading.enhancers.Enhancer

import java.lang.annotation.Annotation

/**
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
public class ValidateEnhancer extends Enhancer {

    static final String PARAMETER_NAMES_FIELD = '$$validation_method_parameters$$'

    @Override
    public void enhanceThisClass(ApplicationClass ac)
    throws Exception {
        CtClass clazz = makeClass(ac)

        if (shouldEnhance(clazz)) {
            def methods = clazz.getDeclaredMethods()
            methods.grep(this.&shouldEnhance).each { method ->
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

    private def collectParameterNames(CtClass clazz, CtMethod method) {
        Logger.debug "Collecting parameter names for validated methods in ${clazz.name}"

        def initializer = clazz.classInitializer ?: clazz.makeClassInitializer()
        def values = []

        def codeAtt = method.methodInfo.getAttribute(CodeAttribute.tag)
        def localVars = codeAtt?.getAttribute(LocalVariableAttribute.tag)

        if (localVars) {
            for (i in (1..localVars.tableLength() - 1)) {
                def varName = localVars.variableName(i)
                if (varName != ("__stackRecorder")) {
                    values << varName
                }
            }
            def code = "${PARAMETER_NAMES_FIELD}.put(\"$method.name\", new String[]{\"${values.join("\", \"")}\"});"
            Logger.debug "Injecting in static block: $code"
            initializer.insertAfter(code)
        }
    }

    private def initializeMethodParamsField(CtClass clazz) {
        try {
            clazz.getDeclaredField(PARAMETER_NAMES_FIELD)
        } catch (NotFoundException e) {
            CtField f = CtField.make("java.util.Map $PARAMETER_NAMES_FIELD = new java.util.HashMap();", clazz)
            f.setModifiers(Modifier.PUBLIC | Modifier.STATIC)
            clazz.addField(f)
        }
    }

    private def enhanceMethod(CtClass clazz, CtMethod method) {
        Logger.debug "Enhancing: ${method.longName}"
        boolean isStatic = (method.modifiers & Modifier.STATIC) == Modifier.STATIC
        if (isStatic) {
            method.insertBefore("play.modules.validation.ValidatePlugin.validateMethod(${clazz.name}.class, null, \"${method.name}\", \$sig, \$args);")
        } else {
            method.insertBefore("play.modules.validation.ValidatePlugin.validateMethod(${clazz.name}.class, this, \"${method.name}\", \$sig, \$args);")
        }
    }

    private boolean shouldEnhance(CtClass clazz) {
        try {
            clazz.getDeclaredField(PARAMETER_NAMES_FIELD)
        } catch (NotFoundException e) {
            return true
        }

        return false
    }

    private boolean shouldEnhance(CtMethod method) {
        def allAnnotations = method.methodInfo?.getAttribute(ParameterAnnotationsAttribute.visibleTag)?.annotations
        for (paramAnnotations in allAnnotations) {
            for (annotation in paramAnnotations) {
                if (isValid(annotation)) {
                    return true
                }
            }
        }

        return false
    }

    private boolean isValid(annotation) {
        def annotationClass = classPool.get(annotation.typeName)
        def annotationTypes = annotationClass.annotations*.annotationType()
        boolean valid = annotationTypes.find({ it.name == net.sf.oval.configuration.annotation.Constraint.class.name }) != null
        return valid
    }

}
