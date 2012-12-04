package services;

import play.data.validation.Check;

public class SampleCheck extends Check {

    public boolean isSatisfied(Object validatedObject, Object value) {
        return "valid".equals(value);
    }
}
