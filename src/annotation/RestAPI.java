package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Cette annotation se colle sur une methode pour dire
// "cette methode retourne du JSON, pas une vue HTML"
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RestAPI {
    // false = l'objet retourne est un objet Java a convertir en JSON
    // true  = le retour est deja une chaine JSON, ne pas reconvertir
    boolean alreadyJson() default false;
    
}
