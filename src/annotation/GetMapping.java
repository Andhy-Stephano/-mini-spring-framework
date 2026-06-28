package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Cette annotation se colle sur une METHODE (pas une classe)
// Elle indique quelle URL cette methode doit traiter
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
    String value(); // l'URL a stocker, ex: "/emp/list"
     String[] method() default {"GET"};
}
