package Model.Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class MyQuery {
    private String queryNum;

    public MyQuery(String plainText) {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(plainText));
        String line;
        try {
            line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            queryNum = line.substring(line.indexOf("<num> Number: ") + 13);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printQuery() {
        System.out.println("Number: " + this.queryNum);
    }

    public String getQueryNum() {
        return queryNum;
    }
}

