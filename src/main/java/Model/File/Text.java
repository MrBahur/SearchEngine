package Model.File;

public class Text {
    private String plainText;

    public Text(String plainText) {
        this.plainText = plainText;
    }

    public String getPlainText() {
        return plainText;
    }


    public void printText() {
        System.out.println(plainText);
    }
}
