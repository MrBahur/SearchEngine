package Model.File;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * they way we represent a doc in the memory
 */
public class MyDocument {
    private String docNumber;
    private Text title;
    private Text text;

    /**
     * constructor for docs that gets the text from <Doc> to</Doc>
     * and creates Text and title
     * @param plainText
     */
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
            stringBuilder.delete(0, stringBuilder.length());
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

    /**
     * getter for doc number
     * @return docNumber
     */
    public String getDocNumber() {
        return docNumber;
    }

    /**
     * getter for text
     * @return text as Text object
     */
    public Text getText() {
        return this.text;
    }
    /**
     * getter for title
     * @return title as Text object
     */
    public Text getTitle() {
        return title;
    }

    /**
     * debug function that helps us dind
     */
    public void printDoc() {
        System.out.println("Number: " + this.docNumber);
        System.out.println("Title:");
        this.title.printText();
        System.out.println("Text:");
        this.text.printText();
        System.out.println();
    }
}
