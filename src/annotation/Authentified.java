package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Cette annotation se colle sur une METHODE d'action.
// Sprint 10 : verifier que l'utilisateur est authentifie
// Sprint 10 bis : verifier le role/profil de l'utilisateur connecte
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Authentified {

    // Ancienne forme : nom de la variable de session a verifier (conserve pour compatibilite)
    String value() default "";

    // Sprint 10 bis : liste des roles autorises pour cette methode
    // Ex: @Authentified(roles = {"PROFESSEUR"}) ou @Authentified(roles = {"PROFESSEUR", "CHEF_DE_DEPARTEMENT"})
    String[] roles() default {};
}
