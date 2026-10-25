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
}
