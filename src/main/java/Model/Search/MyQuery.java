package Model.Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class MyQuery {
    private int queryNum;
    private String desc;
    private String query;

    /**
     * constructor for queries that gets the text in <title> and from <desc> to<narr>
     *
     * @param plainText
     */
    public MyQuery(String plainText) {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(plainText));
        String line;
        String queryNumber;
        boolean inDesc = false;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            queryNumber = line.substring(line.indexOf("<num> Number: ") + 14).trim();
            queryNum = Integer.parseInt(queryNumber);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("<title>")) {
                   query = line.substring(8);
                }else if (line.equals("<desc> Description: ")) {
                    inDesc = true;
                } else if (line.equals("<narr> Narrative: ")) {
                    inDesc = false;
                } else if (inDesc) {
                   stringBuilder.append(line);
                   stringBuilder.append('\n');
                }
            }
            desc = stringBuilder.toString();
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
        System.out.println(desc);
    }

    /**
     * getter for desc number
     *
     * @return queryNumber
     */
    public int getQueryNum() {
        return queryNum;
    }

    /**
     * getter for desc
     *
     * @return desc
     */
    public String getDesc() {
        return desc;
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

