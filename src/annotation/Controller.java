package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Cette annotation marque une classe comme etant un controleur
// reconnu par notre mini framework Spring MVC
@Retention(RetentionPolicy.RUNTIME)// Signifie : visible quand l'application tourne
@Target(ElementType.TYPE)// Signifie : utilisable uniquement sur des Classes
public @interface Controller {
}