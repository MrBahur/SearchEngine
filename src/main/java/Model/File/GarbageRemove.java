package Model.File;

/**
 * class to remove garbage letters from words when no needed
 */
public class GarbageRemove {
    /**
     * remove all chars that aren't a-z,A-Z
     * @param s the string to remove chars from
     * @return string without the chars that above
     */
    public static String remove(String s) {
        if (s != null) {
            return s.replaceAll("[^a-zA-Z]", "");
        } else return null;
    }

    /**
     * remove all chars that aren't a-z,A-Z - and space
     * @param s the string to remove chars from
     * @return string without the chars that above
     */
    public static String removeWithoutFew(String s) {
        if (s != null) {
            return s.replaceAll("[^a-zA-Z\\- ]", "");
        } else return null;
    }
    public static String removeWithoutNumbers(String s) {
        if (s != null) {
            return s.replaceAll("[^a-zA-Z1-9]", "");
        } else return null;
    }
}
