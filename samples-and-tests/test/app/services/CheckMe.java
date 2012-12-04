package services;

import net.sf.oval.configuration.annotation.Constraint;
import play.data.validation.CheckWithCheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(checkWith = CheckMeCheck.class)
public @interface CheckMe {

    String message() default "validation.invalid";
}
