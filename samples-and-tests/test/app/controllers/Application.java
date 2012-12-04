package controllers;

import play.*;
import play.data.validation.Required;
import play.mvc.*;

import java.lang.String;
import java.util.*;

import models.*;

import services.*;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void controllerMethod(@Required String param) {

    }

    @Util
    public static void util(@Required String param) {

    }

    private static void privateMethod(@Required String param) {

    }
}
