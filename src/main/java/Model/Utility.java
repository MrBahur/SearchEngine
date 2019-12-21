package Model;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Utility {

    private static void printTopTenAndLowestTen(String pathToDic) throws IOException {
        BufferedReader f = new BufferedReader(new FileReader(pathToDic));
        ArrayList<Pair<String, Integer>> map = new ArrayList<>();
        String line;
        while ((line = f.readLine()) != null) {
            map.add(new Pair<>(line.substring(0, line.indexOf("->")), Integer.parseInt(line.substring(line.indexOf("->") + 2, line.indexOf("=")))));
        }
        Pair[] arr = new Pair[map.size()];
        for (int i = 0; i < map.size(); i++) {
            arr[i] = map.get(i);
        }
        Arrays.sort(arr, new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return Integer.compare((int) o1.getValue(), (int) o2.getValue());
            }
        });
        System.out.println("Lowest freq terms:");
        for (int i = 0; i < 10; i++) {
            System.out.println((i + 1) + "." + arr[i]);
        }
        System.out.println("Highest freq terms:");
        for (int i = arr.length - 1, j = 0; j < 10; i--, j++) {
            System.out.println((j + 1) + "." + arr[i]);
        }

    }

    private static void printDocOrderedByDocNumber(String pathToDic, String pathToDoc) throws IOException {
        BufferedReader f = new BufferedReader(new FileReader(pathToDic));
        HashMap<String, Pair<Integer, Integer>> map = new HashMap();
        String line;
        while ((line = f.readLine()) != null) {
            map.put(line.substring(0, line.indexOf("->")), new Pair<>(Integer.parseInt(line.substring(line.indexOf("->") + 2,
                    line.indexOf("="))), Integer.parseInt(line.substring(line.indexOf("=") + 1))));
        }
        f.close();
        f = new BufferedReader(new FileReader(pathToDoc));
        Set<String> words = new HashSet<>();
        while ((line = f.readLine()) != null) {
            for (String s : line.split(" ")) {
                words.add(s);
            }
        }
        String[] orderedWords = (String[]) words.toArray();
        Arrays.sort(orderedWords);
        int i=1;
        System.out.println("the words in the document ordered and the amount of times ");
        for (String s : orderedWords) {
            System.out.println();
        }

    }

    public static void main(String[] args) {
        try {
            printTopTenAndLowestTen("PostingFile\\Dictionary.txt");
            printDocOrderedByDocNumber("PostingFile\\Dictionary.txt", "d:\\documents\\users\\matanana\\Downloads\\FBIS3-3366.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
