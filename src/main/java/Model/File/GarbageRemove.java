package Model.File;

public class GarbageRemove {
    public static String remove(String s) {
        if (s != null) {
            return s.replaceAll("[^a-zA-Z]", "");
        } else return null;
    }

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
