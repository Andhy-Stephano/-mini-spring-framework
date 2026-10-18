package util;

/**
 * Represente un fichier uploade depuis un formulaire HTML.
 *
 * Quand un utilisateur envoie un fichier via <input type="file">,
 * le framework cree automatiquement une instance de MapFile avec :
 * - fileName : le nom original du fichier (ex: "photo_cin.jpg")
 * - contentType : le type MIME du fichier (ex: "image/jpeg")
 * - bytes : le contenu binaire complet du fichier
 *
 * Exemple d'utilisation dans un controleur :
 *   public String save(MapFile rectoCIN) {
 *       // rectoCIN.getFileName() → "photo_cin.jpg"
 *       // rectoCIN.getBytes() → contenu binaire du fichier
 *   }
 */
public class MapFile {

    private String fileName;      // nom du fichier original
    private String contentType;   // type MIME (image/jpeg, application/pdf...)
    private byte[] bytes;         // contenu binaire du fichier

    // Constructeur principal
    public MapFile(String fileName, String contentType, byte[] bytes) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    // Getters
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public byte[] getBytes() { return bytes; }
    public long getSize() { return bytes != null ? bytes.length : 0; }

    @Override
    public String toString() {
        return "MapFile{fileName='" + fileName + "'"
             + ", contentType='" + contentType + "'"
             + ", size=" + getSize() + " octets}";
    }
}