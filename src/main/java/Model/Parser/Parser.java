package Model.Parser;

import Model.File.MyDocument;
import Model.File.MyFile;
import Model.File.Number;
import Model.File.Phrase;
import Model.InvertFile.InvertFile;
import Model.ReadFile.ReadFile;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private ReadFile<String> readFile;
    private InvertFile invertFile;
    private WordsToNumber wordsToNumber;
    private static long numberOfParsePhrases = 0;
    private static long numberOfNotParsePhrases = 0;

    public Parser(String path) {
        readFile = new ReadFile<>(path);
        invertFile = new InvertFile();
        wordsToNumber = new WordsToNumber();
    }

    public void parse() {
        for (String filePath : readFile) {
            MyFile myFile = new MyFile(readFile.getPath() + "\\" + filePath + "\\" + filePath);
            for (MyDocument doc : myFile) {
                invertFile.addDoc(doc.getDocNumber());
                parse(doc);
            }
        }
    }


    private void parse(MyDocument d) {
        String[] splitted = d.getTitle().getPlainText().split(" ");
        parse(splitted);
        splitted = d.getText().getPlainText().split(" ");
        parse(splitted);
    }

    //next thing to parse - > Numbers (regular numbers)
    private void parse(String[] splitted) {
        for (int i = 0; i < splitted.length; i++) {
             if (WordsToNumber.getAllowedStrings().contains(splitted[i].toLowerCase())) {
                double value = 0;
                StringBuilder number = new StringBuilder();
                while (i < splitted.length && WordsToNumber.getAllowedStrings().contains(splitted[i])) {
                    number.append(splitted[i]).append(" ");
                    i++;
                }
                if (i < splitted.length && splitted[i].equalsIgnoreCase("point")) {
                    i++;
                    StringBuilder afterPointNumber = new StringBuilder();
                    while (i < splitted.length && WordsToNumber.getAllowedStrings().contains(splitted[i])) {
                        afterPointNumber.append(splitted[i]).append(" ");
                        i++;
                        if (splitted[i].equalsIgnoreCase("and")) {
                            i++;
                        }
                    }
                    value = wordsToNumber.execute(afterPointNumber.toString());
                    if (value <= 9) {
                        value /= 10;
                    } else if (value <= 99) {
                        value /= 100;
                    }
                }
                value += wordsToNumber.execute(number.toString());
                String sign = "#";
                if (i < splitted.length && (splitted[i].equalsIgnoreCase("%") ||
                        splitted[i].equalsIgnoreCase("percent") ||
                        splitted[i].equalsIgnoreCase("percentage"))) {
                    sign = "%";
                    i++;
                }
                if (i < splitted.length && (splitted[i].equalsIgnoreCase("$") ||
                        splitted[i].equalsIgnoreCase("dollars"))) {
                    sign = "$";
                    i++;
                }

                invertFile.addWord(new Number(value, sign));
                numberOfParsePhrases++;
            } else {
                numberOfNotParsePhrases++;
            }
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Parser p = new Parser("F:\\Study\\SearchEngine\\corpus");
        p.parse();
        long finish = System.currentTimeMillis();
        System.out.println("Time Elapsed =" + ((finish - start) / 1000.0) + "seconds");
        Map<Integer, String> documents = p.invertFile.getDocuments();
        System.out.println("Number of documents in the corpus:" + documents.size());
        System.out.println("Phrases already parsed: " + numberOfParsePhrases);
        System.out.println("Phrases left to parse: " + numberOfNotParsePhrases);
        System.out.println("finished " + (100.0 * ((double) numberOfParsePhrases / (numberOfNotParsePhrases + numberOfParsePhrases)) + "%"));
    }
}
