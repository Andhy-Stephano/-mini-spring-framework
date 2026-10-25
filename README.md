# Mini Spring Framework

Framework Spring MVC simplifié réalisé dans le cadre du Projet S4.

## SPRINT0
- Création du FrontControllerServlet
- Gestion des requêtes via doGet/doPost → processRequest

## SPRINT10
- Annotation `@Authentified` sur les méthodes d'action à protéger
- Nom de la variable de session lu dans `web.xml` via `<init-param>` (`sessionVariableName`)
- Vérification de l'authentification avant l'invocation de la méthode
- Réponse HTTP 401 si la variable de session est absente
- La valeur authentifiée est aussi posée en attribut de requête

## SPRINT10 bis
- `@Authentified("nomVariable")` : le nom de la variable de session vient de l'annotation (convention)
- `@Authentified` sans valeur : retour à l'init-param `sessionVariableName` de `web.xml` (configuration)
- Contrôle de plusieurs variables de session différentes (ex: `utilisateur`, `admin`)
- La réponse 401 indique la variable de session manquante