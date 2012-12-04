package services;

import play.data.validation.*;
import play.data.validation.CheckWith;
import play.data.validation.Email;
import play.data.validation.InPast;
import play.data.validation.Required;

import java.lang.String;
import java.util.Date;

public class Service {

    public void validateRequired(@Required String notEmpty) {
        System.out.println("Required");
        String a = "asd";
        System.out.println(a);
    }

    public void validateMultiple(@Required String notEmpty, @Email String email, @InPast Date date) {
        System.out.println("Multiple");
    }

    public void validateCheckWith(@CheckWith(SampleCheck.class) String valid) {
        System.out.println("CheckWith");
    }

    public void validateAnnotation(@CheckMe String check) {
        System.out.println("Annotation");
    }
}

