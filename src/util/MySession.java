package util;

import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Session de l'utilisateur vue par le developpeur.
 * Le developpeur ne voit jamais HttpSession : seulement cette classe.
 */
public class MySession {

    private final String id;
    private final long creationTime;
    private final long lastAccessTime;

    // Variables de session : nom -> valeur (ce que le developpeur manipule)
    private final Map<String, Object> attributes;

    // Noms des variables presentes AU DEPART, avant l'appel de la methode
    private final Set<String> initialKeys;

    // Constructeur prive : on ne peut pas faire "new MySession(...)" de l'exterieur.
    // On est oblige de passer par from(), donc elle est toujours remplie depuis une vraie session.
    private MySession(String id, long creationTime, long lastAccessTime) {
        this.id = id;
        this.creationTime = creationTime;
        this.lastAccessTime = lastAccessTime;
        this.attributes = new LinkedHashMap<>();
        this.initialKeys = new LinkedHashSet<>();
    }

    /**
     * HttpSession -> MySession : cree l'objet et le remplit avec une boucle.
     */
    public static MySession from(HttpSession session) {
        MySession mySession = new MySession(
            session.getId(),
            session.getCreationTime(),
            session.getLastAccessedTime()
        );

        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            mySession.attributes.put(name, session.getAttribute(name));
            mySession.initialKeys.add(name);   // on garde le nom du depart
        }
        return mySession;
    }

    /**
     * MySession -> HttpSession : a appeler apres l'invoke.
     */
    public void applyTo(HttpSession session) {
        // 1. Variables presentes au depart mais retirees par le developpeur
        for (String name : initialKeys) {
            if (!attributes.containsKey(name)) {
                session.removeAttribute(name);
            }
        }
        // 2. Variables nouvelles ou modifiees (on n'ecrit que ce qui change)
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (session.getAttribute(entry.getKey()) != entry.getValue()) {
                session.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }

    // ===== Methodes utilitaires pour le developpeur =====

    public String getId() { return id; }
    public long getCreationTime() { return creationTime; }
    public long getLastAccessTime() { return lastAccessTime; }

    public Object get(String key) {
        return attributes.get(key);
    }

    // Lecture au bon type, sans cast : String nom = session.get("nom", String.class);
    public <T> T get(String key, Class<T> type) {
        Object value = attributes.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    public void set(String key, Object value) { attributes.put(key, value); }
    public void remove(String key) { attributes.remove(key); }
    public boolean contains(String key) { return attributes.containsKey(key); }
    public void clear() { attributes.clear(); }
}