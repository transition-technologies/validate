package services;

import net.sf.oval.*;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

public class CheckMeCheck extends AbstractAnnotationCheck<CheckMe> {

    @Override
    public boolean isSatisfied(Object validatedObject, Object value,
            OValContext context, Validator validator) throws OValException {

        return "check".equalsIgnoreCase((String) value);
    }
}
