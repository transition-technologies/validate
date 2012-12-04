package play.modules.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure validation engine for method differently then defaults
 * set-up in application.conf
 * <p/>
 * Default behavior (if not configured in application.conf nor with this annotation)
 * is to throw {@link ValidationException} when validation failed.
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Validate {

    /**
     * If method should throw exception if validation failed
     * or fill Playframework {@link play.data.validation.Validation} object for current thread
     * instead. You can retrieve validation errors with standard Playframework
     * mechanisms then.
     */
    public boolean throwException() default true;
}
