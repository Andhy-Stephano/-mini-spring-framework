package exception;

// Erreur levee quand le data binding echoue :
// - parametre obligatoire absent de la requete
// - valeur envoyee invalide pour le type attendu
// - parametre de methode de type objet complexe (reserve au Sprint 7 bis)
public class BindingException extends Exception {

    public BindingException(String message) {
        super(message);
    }
}