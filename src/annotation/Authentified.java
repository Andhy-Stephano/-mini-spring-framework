package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Cette annotation se colle sur une METHODE d'action.
// Le framework verifie que l'utilisateur est authentifie avant de l'executer.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Authentified {

    // SPRINT 10 bis : nom de la variable de session a verifier.
    // Si vide, on utilise l'init-param de web.xml (configuration).
    // Si renseigne, l'annotation impose le nom (convention).
    String value() default "";
}
