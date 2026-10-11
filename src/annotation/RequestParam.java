package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Cette annotation se colle sur un PARAMETRE de methode controleur.
// Elle indique le nom de la variable a chercher dans la requete HTTP.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    // Nom de la variable dans la requete (ex: "nom").
    // Si vide, on utilise le nom du parametre de la methode (flag -parameters).
    String value() default "";

    // true = si la variable est absente de la requete, on leve BindingException.
    // false = si absente, on met une valeur par defaut (0, false, null...).
    boolean required() default false;
}