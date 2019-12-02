package Model.Parser;

import java.util.Arrays;
import java.util.List;

/**
 * took from: https://stackoverflow.com/questions/26948858/converting-words-to-numbers-in-java
 */
public class WordsToNumber {
    private final static List<String> allowedStrings = Arrays.asList
            (
                    "zero", "one", "two", "three", "four", "five", "six", "seven",
                    "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen",
                    "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty",
                    "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
                    "hundred", "thousand", "million", "millions", "billion", "trillion"
            );

    public static List<String> getAllowedStrings() {
        return allowedStrings;
    }

    public long execute(String toConvert) {
        boolean isValidInput = true;
        long result = 0;
        long finalResult = 0;
        if (toConvert != null && toConvert.length() > 0) {
            toConvert = toConvert.replaceAll("-", " ");
            toConvert = toConvert.toLowerCase().replaceAll(" and", " ");
            String[] splittedParts = toConvert.trim().split("\\s+");
            for (String str : splittedParts) {
                if (!(isValidInput = allowedStrings.contains(str))) {
                    System.out.println("fix: " + str);
                    break;
                }
            }
            if (isValidInput) {
                for (String str : splittedParts) {
                    if (str.equalsIgnoreCase("zero")) {
                        result += 0;
                    } else if (str.equalsIgnoreCase("one")) {
                        result += 1;
                    } else if (str.equalsIgnoreCase("two")) {
                        result += 2;
                    } else if (str.equalsIgnoreCase("three")) {
                        result += 3;
                    } else if (str.equalsIgnoreCase("four")) {
                        result += 4;
                    } else if (str.equalsIgnoreCase("five")) {
                        result += 5;
                    } else if (str.equalsIgnoreCase("six")) {
                        result += 6;
                    } else if (str.equalsIgnoreCase("seven")) {
                        result += 7;
                    } else if (str.equalsIgnoreCase("eight")) {
                        result += 8;
                    } else if (str.equalsIgnoreCase("nine")) {
                        result += 9;
                    } else if (str.equalsIgnoreCase("ten")) {
                        result += 10;
                    } else if (str.equalsIgnoreCase("eleven")) {
                        result += 11;
                    } else if (str.equalsIgnoreCase("twelve")) {
                        result += 12;
                    } else if (str.equalsIgnoreCase("thirteen")) {
                        result += 13;
                    } else if (str.equalsIgnoreCase("fourteen")) {
                        result += 14;
                    } else if (str.equalsIgnoreCase("fifteen")) {
                        result += 15;
                    } else if (str.equalsIgnoreCase("sixteen")) {
                        result += 16;
                    } else if (str.equalsIgnoreCase("seventeen")) {
                        result += 17;
                    } else if (str.equalsIgnoreCase("eighteen")) {
                        result += 18;
                    } else if (str.equalsIgnoreCase("nineteen")) {
                        result += 19;
                    } else if (str.equalsIgnoreCase("twenty")) {
                        result += 20;
                    } else if (str.equalsIgnoreCase("thirty")) {
                        result += 30;
                    } else if (str.equalsIgnoreCase("forty")) {
                        result += 40;
                    } else if (str.equalsIgnoreCase("fifty")) {
                        result += 50;
                    } else if (str.equalsIgnoreCase("sixty")) {
                        result += 60;
                    } else if (str.equalsIgnoreCase("seventy")) {
                        result += 70;
                    } else if (str.equalsIgnoreCase("eighty")) {
                        result += 80;
                    } else if (str.equalsIgnoreCase("ninety")) {
                        result += 90;
                    } else if (str.equalsIgnoreCase("hundred")) {
                        if(result==0){
                            result=1;
                        }
                        result *= 100;
                    } else if (str.equalsIgnoreCase("thousand")) {
                        if(result==0){
                            result=1;
                        }
                        result *= 1000;
                        finalResult += result;
                        result = 0;
                    } else if (str.equalsIgnoreCase("million")) {
                        if(result==0){
                            result=1;
                        }
                        result *= 1000000;
                        finalResult += result;
                        result = 0;
                    } else if (str.equalsIgnoreCase("millions")) {
                        result *= 1000000;
                        finalResult += result;
                        result = 0;
                    } else if (str.equalsIgnoreCase("billion")) {
                        if(result==0){
                            result=1;
                        }
                        result *= 1000000000;
                        finalResult += result;
                        result = 0;
                    } else if (str.equalsIgnoreCase("trillion")) {
                        if(result==0){
                            result=1;
                        }
                        result *= 1000000000000L;
                        finalResult += result;
                        result = 0;
                    }

                }

            }
        }
        finalResult += result;
        return finalResult;
    }

    public static void main(String[] args) {
        WordsToNumber test = new WordsToNumber();
        System.out.println(test.execute("One hundred two thousand and thirty four"));
        System.out.println(test.execute("zero"));
        System.out.println(test.execute("one"));
        System.out.println(test.execute("sixteen"));
        System.out.println(test.execute("one hundred"));
        System.out.println(test.execute("one hundred eighteen"));
        System.out.println(test.execute("two hundred"));
        System.out.println(test.execute("two hundred nineteen"));
        System.out.println(test.execute("eight hundred"));
        System.out.println(test.execute("eight hundred one"));
        System.out.println(test.execute("one thousand three hundred sixteen"));
        System.out.println(test.execute("one million"));
        System.out.println(test.execute("two millions"));
        System.out.println(test.execute("three millions two hundred"));
        System.out.println(test.execute("seven hundred thousand"));
        System.out.println(test.execute("nine millions"));
        System.out.println(test.execute("nine millions one thousand"));
        System.out.println(test.execute("one hundred twenty three millions four hundred"));
        System.out.println(test.execute("fifty six thousand seven hundred eighty nine"));
        System.out.println(test.execute("two billion one hundred forty seven millions"));
        System.out.println(test.execute("four hundred eighty three thousand six hundred forty seven"));
        System.out.println(test.execute("three billion ten"));
        System.out.println(test.execute("million"));
    }
}