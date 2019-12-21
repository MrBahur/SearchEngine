package Model.File;

/**
 * Wrapper class for String, if we will want to extend the simple way we
 * present text in memory
 */
public class Text {
    private String plainText;

    /**
     * Constructor
     *
     * @param plainText the text/ title of a doc
     */
    public Text(String plainText) {
        this.plainText = plainText;
    }

    /**
     * return the text of the docs
     *
     * @return ...
     */
    public String getPlainText() {
        return plainText;
    }

    /**
     * printer function for debug
     */
    public void printText() {
        System.out.println(plainText);
    }
}
