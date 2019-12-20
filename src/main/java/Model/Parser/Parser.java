package Model.Parser;

import Model.File.*;
import Model.File.Date;
import Model.File.Number;
import Model.InvertFile.Indexer;
import Model.ReadFile.ReadFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Parser {
    private static final List<String> MONTHS = Arrays.asList
            (
                    "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC",
                    "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER",
                    "OCTOBER", "NOVEMBER", "DECEMBER"
            );
    private ReadFile<String> readFile;
    private Indexer indexer;
    private boolean toStem;
    private WordsToNumber wordsToNumber;
    private static long numberOfParsePhrases = 0;
    private static long numberOfNotParsePhrases = 0;
    private static int tempNumOfDocs = 0;
    private static Set<String> stopWords = new HashSet<>();

    public Parser(String path, boolean toStem) {
        readFile = new ReadFile<>(path + "\\corpus");
        indexer = new Indexer(toStem);
        wordsToNumber = new WordsToNumber();
        this.toStem = toStem;
        BufferedReader reader;
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(path + "\\05 stop_words.txt"));
            do {
                line = reader.readLine();
                stopWords.add(line);
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse() {
        for (String filePath : readFile) {
            MyFile myFile = new MyFile(readFile.getPath() + "\\" + filePath + "\\" + filePath);
            for (MyDocument doc : myFile) {
                indexer.addDoc(doc.getDocNumber());
                parse(doc);
                tempNumOfDocs += 1;
            }
            if (tempNumOfDocs >= 10000) {
                //break;
            }
        }
        this.indexer.markEnd();
    }

    private void parse(MyDocument d) {
        if (d.getTitle().getPlainText().length() > 0) {
            String[] splitted = d.getTitle().getPlainText().replaceAll("(-+ *)+", " - ")
                    .replaceAll("[,\"\\[\\]:();<>~*&{}|]", " ").replaceAll("[/\\\\]", " / ")
                    .replaceAll("%", " % ").trim().split(" +");
            parse(splitted);
        }
        if (d.getText().getPlainText().length() > 0) {
            String[] splitted = d.getText().getPlainText().replaceAll("(-+ *)+", " - ")
                    .replaceAll("[,\"\\[\\]:();<>~*&{}|]", " ").replaceAll("[/\\\\]", " / ")
                    .replaceAll("%", " % ").trim().split(" +");
            for (int i = 0; i < splitted.length; i++) {
                if (splitted[i].length() == 0) {
                    System.out.println(d.getDocNumber());
                }
            }
            parse(splitted);
        }
    }

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

    private boolean isIntegerEndWithPoint(String s) {
        int i = 0;
        for (; i < s.length() - 1; i++) {
            if (!(s.charAt(i) <= '9' && s.charAt(i) >= '0')) {
                return false;
            }
        }
        return s.charAt(i) == '.' && i != 0;

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
            } else if ((j = isSelection(splitted, i + 1)) != 0) {
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
                if (!stopWords.contains(splitted[i].toLowerCase())) {
                    Word w;
                    if (toStem) {
                        w = new Word(splitted[i], true);
                    } else {
                        w = new Word(splitted[i], false);
                    }
                    if (w.isGood()) {
                        indexer.addWord(w);
                        numberOfParsePhrases++;
                    } else {
                        numberOfNotParsePhrases++;
                    }
                }
                i++;
            }
        }
    }

    private int isSelection(String[] splitted, int j) {
        int toReturn = 0;
        String first = null;
        String second = null;
        if (j + 1 < splitted.length) {
            if (splitted[j].equals("/")) {
                if ((isInteger(splitted[j - 1]) && isInteger(splitted[j + 1]))) {
                    first = splitted[j - 1];
                    second = splitted[j + 1];
                    toReturn = 3;
                    indexer.addWord(new Number(0, "#", Integer.parseInt(first), Integer.parseInt(second)));
                } else {
                    first = splitted[j - 1];
                    second = splitted[j + 1];
                    Selection s = new Selection(first, second);
                    if (s.isGood()) {
                        toReturn = 3;
                        indexer.addWord(s);
                    }
                }
            }
        }
        return toReturn;
    }

    private int isPhrase(String[] splitted, int i) {
        int length = splitted.length;
        String[] toAdd = {null, null, null, null};
        int j;
        int counter = 0;
        for (j = i; j < length && counter < 4; j++) {
            if (isPhrase(splitted[j])) {
                toAdd[counter] = splitted[j];
                counter++;
            } else if (splitted[j].equals("-")) {
                continue;
            } else {
                break;
            }
        }
        if (toAdd[1] != null) {
            Phrase p = new Phrase(toAdd[0], toAdd[1], toAdd[2], toAdd[3]);
            if (p.isGood()) {
                indexer.addWord(p);
                return j - i;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
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
            int mone = 0;
            int mechane = 1;
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
                } else if (isInteger(splitted[i + 1])) {
                    if (i + 2 < splitted.length) {
                        if (splitted[i + 2].equals("/")) {
                            if (i + 3 < splitted.length) {
                                if (isInteger(splitted[i + 3])) {
                                    mone = Integer.parseInt(splitted[i + 1]);
                                    mechane = Integer.parseInt(splitted[i + 3]);
                                    if (i + 4 < splitted.length) {
                                        if (WordsToNumber.getAllowedStrings().contains(splitted[i + 4].toLowerCase())) {
                                            multiplyValue = wordsToNumber.execute(splitted[i + 4]);
                                            if (i + 5 < splitted.length) {
                                                if (splitted[i + 5].equalsIgnoreCase("%") ||
                                                        splitted[i + 5].equalsIgnoreCase("%.") ||
                                                        splitted[i + 5].equalsIgnoreCase("percent") ||
                                                        splitted[i + 5].equalsIgnoreCase("percent.") ||
                                                        splitted[i + 5].equalsIgnoreCase("percentage") ||
                                                        splitted[i + 5].equalsIgnoreCase("percentage.")) {
                                                    sign = "%";
                                                    toReturn = 6;
                                                } else if (splitted[i + 5].equalsIgnoreCase("dollars") ||
                                                        splitted[i + 5].equalsIgnoreCase("$")) {
                                                    sign = "$";
                                                    toReturn = 6;
                                                } else if (i + 6 < splitted.length && splitted[i + 5].equalsIgnoreCase("U.S.") && splitted[i + 6].equalsIgnoreCase("dollars")) {
                                                    sign = "$";
                                                    toReturn = 7;
                                                } else {
                                                    toReturn = 5;
                                                }
                                            } else {
                                                toReturn = 5;
                                            }

                                        } else if (splitted[i + 4].equalsIgnoreCase("%") ||
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
                    //the indexer was here but i changed
                } else {
                    toReturn = 1;
                }
            }
            double valueAfterPoint = 0;
            if (numberAfterPoint > 0) {
                String s = "0." + numberAfterPoint;
                valueAfterPoint = Double.parseDouble(s);
            }
            indexer.addWord(new Number((value + valueAfterPoint) * multiplyValue, sign, mone, mechane));
            return toReturn;
        } else if (splitted[i].charAt(0) == '$') {
            toReturn = 0;
            double value = 0;
            long multiplyValue = 1;
            int numberAfterPoint = 0;
            int mone = 0;
            int mechane = 0;
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
                            } else if (isInteger(splitted[i + 2])) {
                                if (i + 3 < splitted.length) {
                                    if (splitted[i + 3].equals("/")) {
                                        if (i + 4 < splitted.length) {
                                            if (isInteger(splitted[i + 4])) {
                                                mone = Integer.parseInt(splitted[i + 2]);
                                                mechane = Integer.parseInt(splitted[i + 4]);
                                                if (i + 5 < splitted.length) {
                                                    if (WordsToNumber.getAllowedStrings().contains(splitted[i + 5].toLowerCase())) {
                                                        multiplyValue = wordsToNumber.execute(splitted[i + 5]);
                                                        toReturn = 6;
                                                    } else {
                                                        toReturn = 5;
                                                    }
                                                } else {
                                                    toReturn = 5;
                                                }
                                            } else {
                                                toReturn = 2;
                                            }
                                        } else {
                                            toReturn = 2;
                                        }
                                    } else {
                                        toReturn = 2;
                                    }
                                } else {
                                    toReturn = 2;
                                }
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
                        indexer.addWord(new Number((value + valueAfterPoint) * multiplyValue, "$", mone, mechane));
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
                        } else if (isInteger(splitted[i + 1])) {
                            if (i + 2 < splitted.length) {
                                if (splitted[i + 2].equals("/")) {
                                    if (i + 3 < splitted.length) {
                                        if (isInteger(splitted[i + 3])) {
                                            mone = Integer.parseInt(splitted[i + 1]);
                                            mechane = Integer.parseInt(splitted[i + 3]);
                                            if (i + 4 < splitted.length) {
                                                if (WordsToNumber.getAllowedStrings().contains(splitted[i + 4].toLowerCase())) {
                                                    multiplyValue = wordsToNumber.execute(splitted[i + 4]);
                                                    toReturn = 5;
                                                } else {
                                                    toReturn = 4;
                                                }
                                            } else {
                                                toReturn = 4;
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
                    indexer.addWord(new Number((value + afterPointValue) * multiplyValue, "$", mone, mechane));
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
            int mone = 0;
            int mechane = 0;
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
                } else if (isInteger(splitted[i + 1])) {
                    if (i + 2 < splitted.length) {
                        if (splitted[i + 2].equals("/")) {
                            if (i + 3 < splitted.length) {
                                if (isInteger(splitted[i + 3])) {
                                    mone = Integer.parseInt(splitted[i + 1]);
                                    mechane = Integer.parseInt(splitted[i + 3]);
                                    if (i + 4 < splitted.length) {
                                        if (WordsToNumber.getAllowedStrings().contains(splitted[i + 4].toLowerCase())) {
                                            multiplyValue = wordsToNumber.execute(splitted[i + 4]);
                                            toReturn = 5;
                                        } else {
                                            toReturn = 4;
                                        }
                                    } else {
                                        toReturn = 4;
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
                Range r = new Range(first, second, third);
                if (r.isGood()) {
                    indexer.addWord(r);
                    return toReturn;
                } else {
                    return 0;
                }
            } else if (splitted[j - 1].equalsIgnoreCase("Between")) {
                if (j + 2 < splitted.length && splitted[j + 1].equalsIgnoreCase("and")) {
                    first = splitted[j];
                    second = splitted[j + 2];
                    Range r = new Range(first, second, third);
                    if (r.isGood()) {
                        indexer.addWord(r);
                        return 4;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private int isName(String[] splitted, int i) {
        if (stopWords.contains(splitted[i].toLowerCase())) {
            return 0;
        }
        if (splitted[i].charAt(0) >= 'A' && splitted[i].charAt(0) <= 'Z') {
            Name n = new Name(splitted[i], toStem);
            if (n.isGood()) {
                indexer.addWord(new Name(splitted[i], toStem));
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private int isDate(String[] splitted, int i) {
        if (splitted[i].length() < 3) {//DD-MM
            if (isInteger(splitted[i]) || isIntegerEndWithPoint(splitted[i])) {
                if (splitted.length > i + 1 && isMonth(splitted[i + 1])) {
                    int day = (int) Double.parseDouble(splitted[i]);
                    if (day <= 31 && day >= 1) {
                        indexer.addWord(new Date(day, MONTHS.indexOf(splitted[i + 1].toUpperCase()) % 12 + 1, -1));
                        return 2;
                    }
                }
            }
        } else if (splitted.length > i + 1 && isMonth(splitted[i])) {//MM-DD OR MM-YYYY
            if (isInteger(splitted[i + 1]) || isIntegerEndWithPoint(splitted[i + 1])) {
                if (splitted[i + 1].length() < 3) {
                    int yearOrDay = (int) Double.parseDouble(splitted[i + 1]);
                    if (yearOrDay > 31) {
                        indexer.addWord(new Date(-1, MONTHS.indexOf(splitted[i].toUpperCase()) % 12 + 1, yearOrDay));
                        return 2;
                    } else if (yearOrDay >= 1) {
                        indexer.addWord(new Date(yearOrDay, MONTHS.indexOf(splitted[i].toUpperCase()) % 12 + 1, -1));
                        return 2;
                    }
                } else if (splitted[i + 1].length() == 4 || splitted[i + 1].length() == 5) {
                    int year = (int) Double.parseDouble(splitted[i + 1]);
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
        Parser p = new Parser("F:\\Study\\SearchEngine", false);
        p.parse();
        p.indexer.removeSinglePhrases();
        long finish = System.currentTimeMillis();
        System.out.println("Time Elapsed =" + ((finish - start) / 1000.0) + "seconds");
        System.out.println("Number of documents in the corpus:" + Indexer.NUM_OF_DOCS);
        System.out.println("Number of types of word in the corpus:" + p.indexer.getDictionary().size());
        System.out.println("Phrases already parsed: " + numberOfParsePhrases);
        System.out.println("Phrases left to parse: " + numberOfNotParsePhrases);
        System.out.println("finished " + (100.0 * ((double) numberOfParsePhrases / (numberOfNotParsePhrases + numberOfParsePhrases)) + "%"));
        System.out.println("Number:" + Indexer.numberCounter);

    }
}
