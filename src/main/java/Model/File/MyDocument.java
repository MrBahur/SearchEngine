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
        boolean foundText = false;
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            line = bufferedReader.readLine();
            docNumber = line.substring(line.indexOf("<DOCNO>") + 7, line.indexOf("</DOCNO>"));
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("<TEXT>")) {
                    foundText = true;
                    while (!(line = bufferedReader.readLine()).equals("</TEXT>")) {
                        stringBuilder.append(line);
                    }
                }
            }
            if (!foundText || stringBuilder.length() == 0) {
                bufferedReader = new BufferedReader(new StringReader(plainText));
                while ((line = bufferedReader.readLine()) != null) {
                    switch (line) {
                        case "<DATELINE>":
                            while (!(line = bufferedReader.readLine()).equals("</DATELINE>")) {
                                stringBuilder.append(line);
                            }
                            break;
                        case "<GRAPHIC>":
                            while (!(line = bufferedReader.readLine()).equals("</GRAPHIC>")) {
                                stringBuilder.append(line);
                            }
                            break;
                        case "<CORRECTION>":
                            while (!(line = bufferedReader.readLine()).equals("</CORRECTION>")) {
                                stringBuilder.append(line);
                            }
                            break;
                    }
                }

            }
            this.text = new Text(stringBuilder.toString());
            bufferedReader = new BufferedReader(new StringReader(plainText));
            stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("<HEADLINE>")) {
                    while (!(line = bufferedReader.readLine()).equals("</HEADLINE>")) {
                        stringBuilder.append(line);
                    }
                }
            }
            this.title = new Text(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDocNumber() {
        return docNumber;
    }

    public Text getText() {
        return this.text;
    }

    public Text getTitle() {
        return title;
    }

    public void printDoc() {
        System.out.println("Number: " + this.docNumber);
        System.out.println("Title:");
        this.title.printText();
        System.out.println("Text:");
        this.text.printText();
        System.out.println();
    }
}
