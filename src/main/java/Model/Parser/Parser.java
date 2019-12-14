package Model.Parser;

import Model.File.*;
import Model.File.Number;
import Model.InvertFile.Indexer;
import Model.ReadFile.ReadFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Parser {
    private static final List<String> MONTHS = Arrays.asList
            (
                    "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC",
                    "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER",
                    "OCTOBER", "NOVEMBER", "DECEMBER"
            );
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
        for (String filePath : readFile) {
            MyFile myFile = new MyFile(readFile.getPath() + "\\" + filePath + "\\" + filePath);
            for (MyDocument doc : myFile) {
                indexer.addDoc(doc.getDocNumber());
                parse(doc);
                tempNumOfDocs += 1;
            }
            if (tempNumOfDocs >= 5000) {
                //break;
            }
        }
    }

    private void parse(MyDocument d) {
        if (d.getTitle().getPlainText().length() > 0) {
            String[] splitted = d.getTitle().getPlainText().trim().replaceAll("-+", " - ").split(" +");
            parse(splitted);
        }
        if (d.getText().getPlainText().length() > 0) {
            String[] splitted = d.getText().getPlainText().trim().replaceAll("-+", " - ").split(" +");
            parse(splitted);
        }
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
        for (int i = 0; i < splitted.length; ) {
            int j = 0;
            if ((j = isRange(splitted, i + 1)) != 0) {
                i += j;
                numberOfParsePhrases++;
            } else if ((j = isName(splitted, i)) != 0) {
                i += j;
                numberOfParsePhrases++;
            } else if ((j = isDate(splitted, i)) != 0) {
                i += j;
                numberOfParsePhrases++;
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
                i++;
            }
        }
    }

    private int isRange(String[] splitted, int j) {
        String first = null;
        String second = null;
        String third = null;
        int toReturn = 0;
        if (j + 1 < splitted.length) {
            if (splitted[j].charAt(0) == '-') {
                first = splitted[j - 1];
                second = splitted[j + 1];
                toReturn = 3;
                if (j + 3 < splitted.length) {
                    if (splitted[j + 2].charAt(0) == '-') {
                        third = splitted[j + 3];
                        toReturn = 5;
                    }
                }
                indexer.addWord(new Range(first, second, third));
                return toReturn;
            } else if (splitted[j - 1].equalsIgnoreCase("Between")) {
                if (j + 2 < splitted.length && splitted[j + 1].equalsIgnoreCase("and")) {
                    first = splitted[j];
                    second = splitted[j + 2];
                    indexer.addWord(new Range(first, second, third));
                    return 4;
                }
            }
        }
        return 0;
    }

    private int isName(String[] splitted, int i) {
        if (splitted[i].charAt(0) >= 'A' && splitted[i].charAt(0) <= 'Z') {
            indexer.addWord(new Name(splitted[i]));
            return 1;
        }
        return 0;
    }

    private int isDate(String[] splitted, int i) {
        if (splitted[i].length() < 3) {//DD-MM
            if (isNumeric(splitted[i])) {
                if (splitted.length > i + 1 && isMonth(splitted[i + 1])) {
                    int day = Integer.parseInt(splitted[i]);
                    if (day <= 31 && day >= 1) {
                        indexer.addWord(new Date(day, MONTHS.indexOf(splitted[i + 1].toUpperCase()) % 12 + 1, -1));
                        return 2;
                    }
                }
            }
        } else if (splitted.length > i + 1 && isMonth(splitted[i])) {//MM-DD OR MM-YYY
            if (isNumeric(splitted[i + 1])) {
                if (splitted[i + 1].length() < 3) {
                    int yearOrDay = Integer.parseInt(splitted[i + 1]);
                    if (yearOrDay > 31) {
                        indexer.addWord(new Date(-1, MONTHS.indexOf(splitted[i].toUpperCase()) % 12 + 1, yearOrDay));
                        return 2;
                    } else if (yearOrDay >= 1) {
                        indexer.addWord(new Date(yearOrDay, MONTHS.indexOf(splitted[i].toUpperCase()) % 12 + 1, -1));
                        return 2;
                    }
                } else if (splitted[i + 1].length() == 4) {
                    int year = Integer.parseInt(splitted[i + 1]);
                    if (year < 2500) {
                        indexer.addWord(new Date(-1, MONTHS.indexOf(splitted[i].toUpperCase()) % 12 + 1, year));
                        return 2;
                    }
                }
            }

        }
        return 0;
    }

    private boolean isMonth(String s) {
        for (String m : MONTHS) {
            if (m.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Parser p = new Parser("F:\\Study\\SearchEngine\\corpus");
        p.parse();
        long finish = System.currentTimeMillis();
        System.out.println("Time Elapsed =" + ((finish - start) / 1000.0) + "seconds");
        Map<Integer, String> documents = p.indexer.getDocuments();
        System.out.println("Number of documents in the corpus:" + documents.size());
        System.out.println("Number of types of word in the corpus:" + p.indexer.getWords().size());
        System.out.println("Phrases already parsed: " + numberOfParsePhrases);
        System.out.println("Phrases left to parse: " + numberOfNotParsePhrases);
        System.out.println("finished " + (100.0 * ((double) numberOfParsePhrases / (numberOfNotParsePhrases + numberOfParsePhrases)) + "%"));
    }
}
