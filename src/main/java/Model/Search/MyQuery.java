package Model.Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class MyQuery {
    private int queryNum;
    private String query;

    public MyQuery(String plainText) {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(plainText));
        String line;
        String queryNumber;
        boolean inQuery = false;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            queryNumber = line.substring(line.indexOf("<num> Number: ") + 14).trim();
            queryNum = Integer.parseInt(queryNumber);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("<desc> Description: ")) {
                    inQuery = true;
                } else if (line.equals("<narr> Narrative: ")) {
                    inQuery = false;
                } else if (inQuery) {
                   stringBuilder.append(line);
                   stringBuilder.append('\n');
                }
            }
            query = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * debug function that helps us
     */
    public void printQuery() {
        System.out.println("Number: " + this.queryNum);
        System.out.println(query);
    }

    /**
     * getter for query number
     *
     * @return queryNumber
     */
    public int getQueryNum() {
        return queryNum;
    }

    /**
     * getter for query
     *
     * @return query
     */
    public String getQuery() {
        return query;
    }
}

