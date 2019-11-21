package Model.File;

import java.util.ArrayList;

public class Text extends Node {
    private ArrayList<Phrase> phrases;
    String[] tempForTests;

    public Text(String plainText) {
        tempForTests = plainText.split(" ");

    }

    public void printText() {
        for (String s : tempForTests) {
            System.out.print(s);
        }
    }
}
