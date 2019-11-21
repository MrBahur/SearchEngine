package Model.File;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

public class MyDocument {
    private String docNumber;
    private ArrayList<Node> children;
    private Text title;
    private Text text;

    public MyDocument(String plainText) {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(plainText));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            boolean inText = false;
            line = bufferedReader.readLine();
            docNumber = line.substring(line.indexOf("<DOCNO>") + 7, line.indexOf("</DOCNO>"));
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("<TEXT>")) {
                    inText = true;
                } else if (line.equals("</TEXT>")) {
                    this.text = new Text(stringBuilder.toString());
                    break;
                } else if (inText) {
                    stringBuilder.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void printDoc() {
        System.out.println("Number: " + this.docNumber);
        System.out.println("Text: ");
        this.text.printText();
        System.out.println();
    }
}
