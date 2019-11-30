package Model.File;

import java.util.ArrayList;

public class Text extends Node {
    private ArrayList<Phrase> phrases;
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
