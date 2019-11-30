package Model.Parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static void main(String args[]) {
        // String to be scanned to find the pattern.
        String line = "This order was placed for QT3000! OK3000?";
        String pattern = " ";
        String[] arr = line.split(pattern);
        for (String asd : arr) {
            System.out.println(asd);
        }

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);
        if (m.find()) {
            System.out.println("Found value: " + m.group(0));


        } else {
            System.out.println("NO MATCH");
        }
    }
}
