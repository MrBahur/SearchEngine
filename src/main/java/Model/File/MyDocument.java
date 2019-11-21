package Model.File;


import java.util.ArrayList;

public class MyDocument {
    private String docNumber;
    private ArrayList<Node> children;
    private Text title;
    private Text text;

    public MyDocument(String plainText) {
        docNumber = plainText;//just for testing, need to semi parse it
    }

    public String getDocNumber() {
        return docNumber;
    }
}
