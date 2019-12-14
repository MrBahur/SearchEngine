package Model.Parser;

import Model.File.*;
import Model.File.Number;
import Model.InvertFile.Indexer;
import Model.ReadFile.ReadFile;

import java.util.ArrayList;
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
            if (tempNumOfDocs >= 3000) {
                //break;
            }
        }
    }

    private void parse(MyDocument d) {
        if (d.getTitle().getPlainText().length() > 0) {
            String[] splitted = d.getTitle().getPlainText().replaceAll("(-+ *)+", " - ")
                    .replaceAll("[,\"\\[\\]:();<>~*&{}|]", " ")
                    .replaceAll("%", " % ").trim().split(" +");
            parse(splitted);
        }
        if (d.getText().getPlainText().length() > 0) {
            String[] splitted = d.getText().getPlainText().replaceAll("(-+ *)+", " - ")
                    .replaceAll("[,\"\\[\\]:();<>~*&{}|]", " ")
                    .replaceAll("%", " % ").trim().split(" +");
            for (int i = 0; i < splitted.length; i++) {
                if (splitted[i].length() == 0) {
                    System.out.println(d.getDocNumber());
                }
            }
            parse(splitted);
        }
    }

    //next thing to parse - > Numbers (regular numbers)
    private boolean isNumeric(String s) {
        boolean point = false;
        for (int i = 0; i < s.length(); i++) {
            if (!(s.charAt(i) <= '9' && s.charAt(i) >= '0')) {
                if (i < s.length() - 1 && s.charAt(i) == '.' && !point) {
                    point = true;
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    private boolean isInteger(String s) {
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
            } else if ((j = isDate(splitted, i)) != 0) {
                i += j;
                numberOfParsePhrases++;
            } else if ((j = isNumber(splitted, i)) != 0) {
                i += j;
                numberOfParsePhrases++;
            } else if ((j = isPhrase(splitted, i)) != 0) {
                i += j;
                numberOfParsePhrases++;
            } else if ((j = isName(splitted, i)) != 0) {
                i += j;
                numberOfParsePhrases++;
            } else {
                numberOfNotParsePhrases++;
                i++;
            }
        }
    }

    private int isPhrase(String[] splitted, int i) {
        int toReturn = 0;
        if (isPhrase(splitted[i])) {
            if (i + 1 < splitted.length) {
                if (splitted[i + 1].equals("-")) {
                    if (i + 2 < splitted.length) {
                        if (isPhrase(splitted[i + 2])) {
                            if (i + 3 < splitted.length) {
                                if (splitted[i + 3].equals("-")) {
                                    if (i + 4 < splitted.length) {
                                        if (isPhrase(splitted[i + 4])) {
                                            indexer.addWord(new Phrase(splitted[i], splitted[i + 2], splitted[i + 4]));//A-A-A
                                            toReturn = 5;
                                        }
                                    }
                                } else if (isPhrase(splitted[i + 3])) {
                                    indexer.addWord(new Phrase(splitted[i], splitted[i + 2], splitted[i + 3]));//A-AA
                                    toReturn = 4;
                                } else {
                                    indexer.addWord(new Phrase(splitted[i], splitted[i + 2]));
                                    toReturn = 3;//A-A
                                }
                            }
                        }
                    }
                } else {
                    if (isPhrase(splitted[i + 1])) {
                        if (i + 2 < splitted.length) {
                            if (splitted[i + 2].equals("-")) {
                                if (i + 3 < splitted.length) {
                                    if (isPhrase(splitted[i + 3])) {
                                        indexer.addWord(new Phrase(splitted[i], splitted[i + 1], splitted[i + 3]));
                                        toReturn = 4;//AA-A
                                    }
                                }
                            } else if (isPhrase(splitted[i + 2])) {
                                indexer.addWord(new Phrase(splitted[i], splitted[i + 1], splitted[i + 2]));
                                toReturn = 3;//AAA
                            } else {
                                indexer.addWord(new Phrase(splitted[i], splitted[i + 1]));
                                toReturn = 2;//AA
                            }
                        }
                    } else {
                        indexer.addWord(new Name(splitted[i]));
                        toReturn = 1;
                    }
                }
            } else {
                indexer.addWord(new Name(splitted[i]));
                toReturn = 1;
            }
        }

        return toReturn;
    }

    private boolean isPhrase(String s) {
        return s.charAt(0) >= 'A' && s.charAt(0) <= 'Z';
    }

    private int isNumber(String[] splitted, int i) {//TBD handle fractions
        int toReturn = 0;
        if (isNumeric(splitted[i])) {
            double value = Double.parseDouble(splitted[i]);
            long multiplyValue = 1;
            int numberAfterPoint = 0;
            String sign = "#";
            if (i + 1 < splitted.length) {
                if (splitted[i + 1].equalsIgnoreCase("point")) {
                    if (i + 2 < splitted.length) {
                        if (isNumeric(splitted[i + 2])) {
                            numberAfterPoint = Integer.parseInt(splitted[i + 2]);
                            if (i + 3 < splitted.length) {
                                if (WordsToNumber.getAllowedStrings().contains(splitted[i + 3].toLowerCase())) {
                                    multiplyValue = wordsToNumber.execute(splitted[i + 3]);
                                    if (i + 4 < splitted.length) {
                                        if (splitted[i + 4].equalsIgnoreCase("%") ||
                                                splitted[i + 4].equalsIgnoreCase("%.") ||
                                                splitted[i + 4].equalsIgnoreCase("percent") ||
                                                splitted[i + 4].equalsIgnoreCase("percent.") ||
                                                splitted[i + 4].equalsIgnoreCase("percentage") ||
                                                splitted[i + 4].equalsIgnoreCase("percentage.")) {
                                            sign = "%";
                                            toReturn = 5;
                                        } else if (splitted[i + 4].equalsIgnoreCase("dollars") ||
                                                splitted[i + 4].equalsIgnoreCase("$")) {
                                            sign = "$";
                                            toReturn = 5;
                                        } else if (i + 5 < splitted.length && splitted[i + 4].equalsIgnoreCase("U.S.") && splitted[i + 5].equalsIgnoreCase("dollars")) {
                                            sign = "$";
                                            toReturn = 6;
                                        } else {
                                            toReturn = 4;
                                        }
                                    } else {
                                        toReturn = 4;
                                    }
                                } else if (splitted[i + 3].equalsIgnoreCase("%") ||
                                        splitted[i + 3].equalsIgnoreCase("%.") ||
                                        splitted[i + 3].equalsIgnoreCase("percent") ||
                                        splitted[i + 3].equalsIgnoreCase("percent.") ||
                                        splitted[i + 3].equalsIgnoreCase("percentage") ||
                                        splitted[i + 3].equalsIgnoreCase("percentage.")) {
                                    sign = "%";
                                    toReturn = 4;
                                } else if (splitted[i + 3].equalsIgnoreCase("dollars") ||
                                        splitted[i + 3].equalsIgnoreCase("$")) {
                                    sign = "$";
                                    toReturn = 4;
                                } else if (i + 4 < splitted.length && splitted[i + 3].equalsIgnoreCase("U.S.") && splitted[i + 4].equalsIgnoreCase("dollars")) {
                                    sign = "$";
                                    toReturn = 5;
                                } else {
                                    toReturn = 3;
                                }
                            } else {
                                toReturn = 3;
                            }
                        } else {
                            toReturn = 1;
                        }
                    } else {
                        toReturn = 1;
                    }
                } else if (WordsToNumber.getAllowedStrings().contains(splitted[i + 1].toLowerCase())) {
                    multiplyValue = wordsToNumber.execute(splitted[i + 1]);
                    if (i + 2 < splitted.length) {
                        if (splitted[i + 2].equalsIgnoreCase("%") ||
                                splitted[i + 2].equalsIgnoreCase("%.") ||
                                splitted[i + 2].equalsIgnoreCase("percent") ||
                                splitted[i + 2].equalsIgnoreCase("percent.") ||
                                splitted[i + 2].equalsIgnoreCase("percentage") ||
                                splitted[i + 2].equalsIgnoreCase("percentage.")) {
                            sign = "%";
                            toReturn = 3;
                        } else if (splitted[i + 2].equalsIgnoreCase("dollars") ||
                                splitted[i + 2].equalsIgnoreCase("$")) {
                            sign = "$";
                            toReturn = 3;
                        } else if (i + 3 < splitted.length && splitted[i + 2].equalsIgnoreCase("U.S.") && splitted[i + 3].equalsIgnoreCase("dollars")) {
                            sign = "$";
                            toReturn = 4;
                        } else {
                            toReturn = 2;
                        }
                    } else {
                        toReturn = 1;
                    }
                } else if (splitted[i + 1].equalsIgnoreCase("%") ||
                        splitted[i + 1].equalsIgnoreCase("%.") ||
                        splitted[i + 1].equalsIgnoreCase("percent") ||
                        splitted[i + 1].equalsIgnoreCase("percent.") ||
                        splitted[i + 1].equalsIgnoreCase("percentage") ||
                        splitted[i + 1].equalsIgnoreCase("percentage.")) {
                    sign = "%";
                    toReturn = 2;
                } else if (splitted[i + 1].equalsIgnoreCase("dollars") ||
                        splitted[i + 1].equalsIgnoreCase("$")) {
                    sign = "$";
                    toReturn = 2;
                } else if (i + 2 < splitted.length && splitted[i + 1].equalsIgnoreCase("U.S.") && splitted[i + 2].equalsIgnoreCase("dollars")) {
                    sign = "$";
                    toReturn = 3;
                } else {
                    toReturn = 1;
                }
                double valueAfterPoint = 0;
                if (numberAfterPoint > 0) {
                    String s = "0." + numberAfterPoint;
                    valueAfterPoint = Double.parseDouble(s);
                }
                indexer.addWord(new Number((value + valueAfterPoint) * multiplyValue, sign));
                return toReturn;
            }
        } else if (splitted[i].charAt(0) == '$') {
            toReturn = 0;
            double value = 0;
            long multiplyValue = 1;
            int numberAfterPoint = 0;
            if (splitted[i].length() == 1) {
                if (i + 1 < splitted.length) {
                    if (isNumeric(splitted[i + 1])) {
                        value = Double.parseDouble(splitted[i + 1]);
                        if (i + 2 < splitted.length) {
                            if (splitted[i + 2].equalsIgnoreCase("point")) {
                                if (i + 3 < splitted.length) {
                                    if (isNumeric(splitted[i + 3])) {
                                        numberAfterPoint = Integer.parseInt(splitted[i + 3]);
                                        if (i + 4 < splitted.length) {
                                            if (WordsToNumber.getAllowedStrings().contains(splitted[i + 4].toLowerCase())) {
                                                multiplyValue = wordsToNumber.execute(splitted[i + 4]);
                                                toReturn = 5;
                                            }
                                        } else {
                                            toReturn = 3;
                                        }
                                    } else {
                                        toReturn = 3;
                                    }
                                } else {
                                    toReturn = 2;
                                }
                            } else if (WordsToNumber.getAllowedStrings().contains(splitted[i + 2].toLowerCase())) {
                                multiplyValue = wordsToNumber.execute(splitted[i + 2]);
                                toReturn = 3;
                            } else {
                                toReturn = 2;
                            }
                        } else {
                            toReturn = 2;
                        }
                        double valueAfterPoint = 0;
                        if (numberAfterPoint > 0) {
                            String s = "0." + numberAfterPoint;
                            valueAfterPoint = Double.parseDouble(s);
                        }
                        indexer.addWord(new Number((value + valueAfterPoint) * multiplyValue, "$"));
                        return toReturn;
                    } else if (WordsToNumber.getAllowedStrings().contains(splitted[i + 1].toLowerCase())) {
                        //TBD - handle very little cases
                    } else {
                        toReturn = 1;
                    }
                } else {
                    toReturn = 1;
                }
            } else {
                String splittedI = splitted[i].substring(1);
                if (isNumeric(splittedI)) {
                    value = Double.parseDouble(splittedI);
                    if (i + 1 < splitted.length) {
                        if (splitted[i + 1].equalsIgnoreCase("point")) {
                            if (i + 2 > splitted.length) {
                                if (isInteger(splitted[i + 2])) {
                                    numberAfterPoint = Integer.parseInt(splitted[i + 2]);
                                    if (i + 3 < splitted.length) {
                                        if (WordsToNumber.getAllowedStrings().contains(splitted[i + 3].toLowerCase())) {
                                            multiplyValue = wordsToNumber.execute(splitted[i + 3]);
                                            toReturn = 4;
                                        } else {
                                            toReturn = 3;
                                        }
                                    } else {
                                        toReturn = 3;
                                    }
                                } else {
                                    toReturn = 1;
                                }
                            } else {
                                toReturn = 1;
                            }
                        } else {
                            toReturn = 1;
                        }
                    } else {
                        toReturn = 1;
                    }
                    double afterPointValue = 0;
                    if (numberAfterPoint > 0) {
                        String s = "0." + numberAfterPoint;
                        afterPointValue = Double.parseDouble(s);
                    }
                    indexer.addWord(new Number((value + afterPointValue) * multiplyValue, "$"));
                    return toReturn;
                } else if (WordsToNumber.getAllowedStrings().contains(splittedI.toLowerCase())) {
                    //TBD - handle very little cases
                } else {
                    return 0;
                }
            }

        } else if (WordsToNumber.getAllowedStrings().contains(splitted[i].toLowerCase())) {
            double value = wordsToNumber.execute(splitted[i]);
            long multiplyValue = 1;
            long numberAfterPoint = 0;
            String sign = "#";
            if (i + 1 < splitted.length) {
                if (splitted[i + 1].equalsIgnoreCase("point")) {
                    if (i + 2 < splitted.length) {
                        if (WordsToNumber.getAllowedStrings().contains(splitted[i + 2].toLowerCase())) {
                            numberAfterPoint = wordsToNumber.execute(splitted[i + 2]);
                            if (i + 3 < splitted.length) {
                                if (WordsToNumber.getAllowedStrings().contains(splitted[i + 3].toLowerCase())) {
                                    multiplyValue = wordsToNumber.execute(splitted[i + 3]);
                                    if (i + 4 < splitted.length) {
                                        if (splitted[i + 4].equalsIgnoreCase("%") ||
                                                splitted[i + 4].equalsIgnoreCase("%.") ||
                                                splitted[i + 4].equalsIgnoreCase("percent") ||
                                                splitted[i + 4].equalsIgnoreCase("percent.") ||
                                                splitted[i + 4].equalsIgnoreCase("percentage") ||
                                                splitted[i + 4].equalsIgnoreCase("percentage.")) {
                                            sign = "%";
                                            toReturn = 5;
                                        } else if (splitted[i + 4].equalsIgnoreCase("dollars") ||
                                                splitted[i + 4].equalsIgnoreCase("$")) {
                                            sign = "$";
                                            toReturn = 5;
                                        } else if (i + 5 < splitted.length && splitted[i + 4].equalsIgnoreCase("U.S.") && splitted[i + 5].equalsIgnoreCase("dollars")) {
                                            sign = "$";
                                            toReturn = 6;
                                        } else {
                                            toReturn = 3;
                                        }
                                    } else {
                                        toReturn = 3;
                                    }
                                } else {
                                    toReturn = 2;
                                }
                            } else {
                                toReturn = 2;
                            }
                        } else {
                            toReturn = 1;
                        }
                    } else {
                        toReturn = 1;
                    }
                } else if (WordsToNumber.getAllowedStrings().contains(splitted[i + 1].toLowerCase())) {
                    multiplyValue = wordsToNumber.execute(splitted[i + 1]);
                    if (i + 2 < splitted.length) {
                        if (splitted[i + 2].equalsIgnoreCase("%") ||
                                splitted[i + 2].equalsIgnoreCase("%.") ||
                                splitted[i + 2].equalsIgnoreCase("percent") ||
                                splitted[i + 2].equalsIgnoreCase("percent.") ||
                                splitted[i + 2].equalsIgnoreCase("percentage") ||
                                splitted[i + 2].equalsIgnoreCase("percentage.")) {
                            sign = "%";
                            toReturn = 3;
                        } else if (splitted[i + 2].equalsIgnoreCase("dollars") ||
                                splitted[i + 2].equalsIgnoreCase("$")) {
                            sign = "$";
                            toReturn = 3;
                        } else if (i + 3 < splitted.length && splitted[i + 2].equalsIgnoreCase("U.S.") && splitted[i + 3].equalsIgnoreCase("dollars")) {
                            sign = "$";
                            toReturn = 4;
                        } else {
                            toReturn = 2;
                        }
                    } else {
                        toReturn = 1;
                    }
                } else {
                    toReturn = 1;
                }
            } else {
                toReturn = 1;
            }
            double afterPointValue = 0;
            if (numberAfterPoint > 0) {
                String s = "0." + numberAfterPoint;
                afterPointValue = Double.parseDouble(s);
            }
            indexer.addWord(new Number((value + afterPointValue) * multiplyValue, "$"));
            return toReturn;
        }
        return toReturn;
    }

    private int isRange(String[] splitted, int j) {//TBD = Repair
        String first = null;
        String second = null;
        String third = null;
        int toReturn = 0;
        if (j + 1 < splitted.length) {
            if (splitted[j].charAt(0) == '-' && splitted[j - 1].length() > 0 && splitted[j + 1].length() > 0) {
                first = splitted[j - 1];
                second = splitted[j + 1];
                toReturn = 3;
                if (j + 3 < splitted.length) {
                    if (splitted[j + 2].charAt(0) == '-' && splitted[j + 3].length() > 0) {
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
            if (isInteger(splitted[i])) {
                if (splitted.length > i + 1 && isMonth(splitted[i + 1])) {
                    int day = Integer.parseInt(splitted[i]);
                    if (day <= 31 && day >= 1) {
                        indexer.addWord(new Date(day, MONTHS.indexOf(splitted[i + 1].toUpperCase()) % 12 + 1, -1));
                        return 2;
                    }
                }
            }
        } else if (splitted.length > i + 1 && isMonth(splitted[i])) {//MM-DD OR MM-YYY
            if (isInteger(splitted[i + 1])) {
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
