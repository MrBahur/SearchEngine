package Model.Parser;

import Model.File.MyDocument;
import Model.File.MyFile;
import Model.File.Number;
import Model.InvertFile.Indexer;
import Model.ReadFile.ReadFile;

import java.util.Map;

public class Parser {
    private ReadFile<String> readFile;
    private Indexer indexer;
    private WordsToNumber wordsToNumber;
    private static long numberOfParsePhrases = 0;
    private static long numberOfNotParsePhrases = 0;
    private static int tempNumOfDocs = 0;

    public Parser(String path) {
        readFile = new ReadFile<>(path);
        indexer = new Indexer();
        wordsToNumber = new WordsToNumber();
    }

    public void parse() {
        int counter = 0;
        for (String filePath : readFile) {
            MyFile myFile = new MyFile(readFile.getPath() + "\\" + filePath + "\\" + filePath);
            for (MyDocument doc : myFile) {
                if (counter < 20000) {
                    indexer.addDoc(doc.getDocNumber());
                    parse(doc);
                    tempNumOfDocs += 1;
                    counter++;
                }
            }
            if (tempNumOfDocs >= 20000) {
                //break;
            }
        }
        //System.out.println(indexer.getCurrentSizeColumns() + " " + indexer.getCurrentSizeRows());
        //System.out.println(counter);
    }


    private void parse(MyDocument d) {
        String[] splitted = d.getTitle().getPlainText().trim().replaceAll(",","").replaceAll(" +"," ").split(" ");
        parse(splitted);
        splitted = d.getText().getPlainText().trim().replaceAll(",","").replaceAll(" +"," ").split(" ");
        parse(splitted);
    }

    //next thing to parse - > Numbers (regular numbers)
    private boolean isNumeric(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!(s.charAt(i) <= '9' && s.charAt(i) >= '0')) {
                return false;
            }
        }
        return true;
    }

    private void parse(String[] splitted) {
        for (Integer i = 0; i < splitted.length; i++) {
            if (splitted[i].length() == 0) {//empty string, nothing to parse.
                continue;

            } else if (isRange(splitted, i)) {
                //parse range
            } else if (isName(splitted, i)) {
                //parse name
            } else if (isDate(splitted, i)) {
                //parse date
            } else if (isNumeric(splitted[i])) {
                //here we need to parse Numeric value, same flow as WordsToNumber parsing, need to usr this code somehow, extract methods and shit.
                double value = 0;
                value = Double.parseDouble(splitted[i]);
                i++;
                if (i < splitted.length && (splitted[i].equalsIgnoreCase("point") || splitted[i].equals("."))) {
                    i++;
                    StringBuilder afterPointNum = new StringBuilder();
                    while (i < splitted.length && (isNumeric(splitted[i]) || splitted[i].length() == 0)) {
                        afterPointNum.append(splitted[i]);
                        i++;
                    }
                    if (afterPointNum.length() > 0) {
                        int afterPointValue = Integer.parseInt(afterPointNum.toString());
                        if (afterPointValue <= 9) {
                            value += afterPointValue / 10.0;
                        } else if (value <= 99) {
                            value += afterPointValue / 100.0;
                        }
                    }
                }
                StringBuilder millionValue = new StringBuilder();
                while (i < splitted.length && (splitted[i].length() == 0 || splitted[i].equals(" "))) {
                    if (WordsToNumber.getAllowedStrings().contains(splitted[i])) {
                        millionValue.append(splitted[i]);
                    }
                    i++;
                }
                if (millionValue.length() >= 0) {
                    value *= wordsToNumber.execute(millionValue.toString());
                }

                String sign = "#";
                if (i < splitted.length && (splitted[i].equalsIgnoreCase("%") ||
                        splitted[i].equalsIgnoreCase("percent") ||
                        splitted[i].equalsIgnoreCase("percentage"))) {
                    sign = "%";
                    i++;
                }
                if (i < splitted.length && (splitted[i].equalsIgnoreCase("$") ||
                        splitted[i].equalsIgnoreCase("dollars") ||
                        (splitted[i].equalsIgnoreCase("U.S.") && i + 1 < splitted.length && splitted[i + 1].equalsIgnoreCase("dollars")))) {
                    if (splitted[i].equalsIgnoreCase("U.S.") && i + 1 < splitted.length && splitted[i + 1].equalsIgnoreCase("dollars")) {
                        i += 2;
                        sign = "$";
                    } else {
                        sign = "$";
                        i++;
                    }

                }
                indexer.addWord(new Number(value, sign));
                numberOfParsePhrases++;
            } else if (WordsToNumber.getAllowedStrings().contains(splitted[i].toLowerCase())) {
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
                        splitted[i].equalsIgnoreCase("dollars") ||
                        (splitted[i].equalsIgnoreCase("U.S.") && i + 1 < splitted.length && splitted[i + 1].equalsIgnoreCase("dollars")))) {
                    if (splitted[i].equalsIgnoreCase("U.S.") && i + 1 < splitted.length && splitted[i + 1].equalsIgnoreCase("dollars")) {
                        i += 2;
                        sign = "$";
                    } else {
                        sign = "$";
                        i++;
                    }
                }

                indexer.addWord(new Number(value, sign));
                numberOfParsePhrases++;
            } else {
                numberOfNotParsePhrases++;
            }
        }
    }

    private boolean isRange(String[] splitted, int i) {
        return false;
    }

    private boolean isName(String[] splitted, int i) {
        return false;
    }

    private boolean isDate(String[] splitted, int i) {
        return false;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Parser p = new Parser("C:\\corpus");
        p.parse();
        long finish = System.currentTimeMillis();
        System.out.println("Time Elapsed =" + ((finish - start) / 1000.0) + "seconds");
        Map<Integer, String> documents = p.indexer.getDocuments();
        System.out.println("Number of documents in the corpus:" + documents.size());
        System.out.println("Phrases already parsed: " + numberOfParsePhrases);
        System.out.println("Phrases left to parse: " + numberOfNotParsePhrases);
        System.out.println("finished " + (100.0 * ((double) numberOfParsePhrases / (numberOfNotParsePhrases + numberOfParsePhrases)) + "%"));
    }
}
