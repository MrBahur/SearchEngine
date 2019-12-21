package Model.InvertFile;

import Model.File.Name;
import Model.File.Phrase;
import Model.File.Term;
import Model.File.Word;


import Model.File.Number;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Indexer {
    public static final int NUM_OF_DOCS = 472525;
    private static final int numOfDocsPerPosting = 5000;

    private Map<String, Pair<Integer, Integer>> documents;// doc ID to <max_tf,Number of unique words>
    private Integer currentDoc;
    private String currentDocID;
    private Map<String, Pair<Integer, Integer>> dictionary;//words to amount in corpus Map first Integer is amount in corpus, second Integer is pointer to posting file
    private Map<Term, LinkedList<Pair<String, Integer>>> postingFiles;
    private Map<String, Integer> phrasesDocs;
    private boolean toStem;
    public static int numberCounter = 0;

    public Indexer(boolean toStem) {
        this(16777216, 4096, toStem);
    }

    public Indexer(int initialWordSize, int numOfDocsInMemory, boolean toStem) {
        documents = new HashMap<>(NUM_OF_DOCS, 1);
        currentDoc = 0;
        dictionary = new HashMap<>(initialWordSize, 4);
        postingFiles = new HashMap<>(numOfDocsInMemory);
        phrasesDocs = new HashMap<>();
        this.toStem = toStem;
    }

    public void removeSinglePhrases() {
        for (Map.Entry<String, Integer> entry : phrasesDocs.entrySet()) {
            dictionary.remove(entry.getKey());
        }
        Set<String> set = new HashSet<>();
        for (Map.Entry<String, Pair<Integer, Integer>> entry : dictionary.entrySet()) {
            if (entry.getValue().getKey() == 1) {
                set.add(entry.getKey());
            }
        }
        for (String s : set) {
            dictionary.remove(s);
        }
    }

    public void addWord(Term term) {
        if (term instanceof Phrase) {
            if (dictionary.containsKey(term.toString())) {
                if (phrasesDocs.containsKey(term.toString())) {
                    if (!phrasesDocs.get(term.toString()).equals(currentDoc)) {
                        phrasesDocs.remove(term.toString());
                    }
                }
            } else {
                phrasesDocs.put(term.toString(), currentDoc);
            }
        }
        if (term instanceof Word) {
            if (dictionary.containsKey(term.toString().toUpperCase())) {
                Integer num = dictionary.get(term.toString().toUpperCase()).getKey();
                dictionary.remove(term.toString().toUpperCase());
                dictionary.put(term.toString(), new Pair<>(num, 0));
            }
        }
        if (term instanceof Name) {
            if (dictionary.containsKey(term.toString().toLowerCase())) {
                term = new Word(term.toString(), toStem);
            }
        }
        if (dictionary.containsKey(term.toString())) {
            Integer amount = dictionary.get(term.toString()).getKey();
            dictionary.replace(term.toString(), new Pair<>(amount + 1, 0));
        } else {
            dictionary.put(term.toString(), new Pair<>(1, 0));
            if (term instanceof Number) {
                numberCounter++;
            }
        }
        if (!postingFiles.containsKey(term)) {
            postingFiles.put(term, new LinkedList<>());
            postingFiles.get(term).addLast(new Pair<>(currentDocID, 1));
        }
        if (!postingFiles.get(term).getLast().getKey().equals(currentDocID)) {
            postingFiles.get(term).addLast(new Pair<>(currentDocID, 1));
        } else {
            Pair<String, Integer> pair = postingFiles.get(term).getLast();
            postingFiles.get(term).removeLast();
            postingFiles.get(term).addLast(new Pair<>(pair.getKey(), pair.getValue() + 1));

        }
        Pair<Integer, Integer> pair = documents.get(currentDocID);
        if (postingFiles.get(term).getLast().getValue() == 1) {
            documents.replace(currentDocID, new Pair<>(pair.getKey(), pair.getValue() + 1));
        } else if (postingFiles.get(term).getLast().getValue() > pair.getKey()) {
            documents.replace(currentDocID, new Pair<>(pair.getKey() + 1, pair.getValue()));
        }
    }

    public void addDoc(String doc) {

        documents.put(doc, new Pair<>(0, 0));
        currentDocID = doc;
        currentDoc++;
        if (currentDoc % numOfDocsPerPosting == 0) {
            writePostingFileToDisc(postingFiles, currentDoc / numOfDocsPerPosting);
            System.out.println("Wrote to disc, doc number:" + currentDoc);
            postingFiles = new HashMap<>(postingFiles.size());
//            System.gc();
//            System.gc();
        }
    }

    private void mergePostingFiles() {
        try {
            new File("PostingFile").mkdir();
            BufferedWriter[] writers = getBufferedWriters(0, "PostingFile");
            Stream<Path> walk = Files.walk(Paths.get("PostingFiles"));
            List<String> files = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            int counter = 0;
            for (char character = 'a'; character <= 'z'; character++, counter++) {
                mergePostingFile(character, files, writers[counter]);
                writers[counter].flush();
            }
            for (char character = 'A'; character <= 'Z'; character++, counter++) {
                mergePostingFile(character, files, writers[counter]);
                writers[counter].flush();
            }
            mergePostingFile('0', files, writers[counter]);
            writers[counter].flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mergePostingFile(char character, List<String> files, BufferedWriter writer) throws IOException {
        HashMap<String, LinkedList<Pair<String, Integer>>> postingFile = new HashMap<>();
        for (String file : files) {
            if (file.charAt(13) != character) {
                continue;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String term = line.substring(0, line.indexOf("->"));
                String[] pairs = line.substring(line.indexOf("->") + 2).replaceAll(" ", "").split(";");
                if (dictionary.containsKey(term)) {
                    if (!postingFile.containsKey(term)) {
                        postingFile.put(term, new LinkedList<>());
                    }
                    for (String s : pairs) {
                        postingFile.get(term).addLast(new Pair<>(s.substring(0, s.indexOf('=')), Integer.parseInt(s.substring(s.indexOf('=') + 1))));
                    }
                }
            }
        }
        int counter = 0;
        for (Map.Entry<String, LinkedList<Pair<String, Integer>>> entry : postingFile.entrySet()) {
            writer.write(entry.getKey());
            writer.write("->");
            for (Pair<String, Integer> p : entry.getValue()) {
                writer.write(p.toString());
                writer.write(";");
            }
            writer.write('\n');
            int amount = dictionary.get(entry.getKey()).getKey();
            dictionary.replace(entry.getKey(), new Pair<>(amount, counter));
            counter++;
        }
    }

    private void writePostingFileToDisc(Map<Term, LinkedList<Pair<String, Integer>>> postingFiles, int iteration) {
        try {
            new File("PostingFiles").mkdir();
            BufferedWriter[] writers = getBufferedWriters(iteration, "PostingFiles");
            for (Map.Entry<Term, LinkedList<Pair<String, Integer>>> entry : postingFiles.entrySet()) {
                if (entry.getKey().toString().charAt(0) >= 'a' && entry.getKey().toString().charAt(0) <= 'z') {
                    writeToFile(entry, writers[entry.getKey().toString().charAt(0) - 'a']);
                } else if (entry.getKey().toString().charAt(0) >= 'A' && entry.getKey().toString().charAt(0) <= 'Z') {
                    writeToFile(entry, writers[entry.getKey().toString().charAt(0) - 'A' + 26]);
                } else {
                    writeToFile(entry, writers[52]);
                }
            }
            for (int j = 0; j < writers.length; j++) {
                writers[j].flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedWriter[] getBufferedWriters(int iteration, String path) throws IOException {
        BufferedWriter[] writers = new BufferedWriter[53];
        int i = 0;
        for (char letter = 'a'; letter <= 'z'; letter++, i++) {
            writers[i] = new BufferedWriter(new FileWriter(path + "\\" + letter + (iteration == 0 ? "" : iteration) + ".txt"));
        }
        for (char letter = 'A'; letter <= 'Z'; letter++, i++) {
            writers[i] = new BufferedWriter(new FileWriter(path + "\\" + letter + "@" + (iteration == 0 ? "" : iteration) + ".txt"));
        }
        writers[i] = new BufferedWriter(new FileWriter(path + "\\" + "0" + (iteration == 0 ? "" : iteration) + ".txt"));
        return writers;
    }

    private void writeToFile(Map.Entry<Term, LinkedList<Pair<String, Integer>>> entry, BufferedWriter writer) throws IOException {
        writer.write(entry.getKey().toString());
        writer.write("->");
        for (Pair<String, Integer> p : entry.getValue()) {
            writer.write(p.toString());
            writer.write(';');
        }
        writer.write('\n');
    }

    public Map getDictionary() {
        return dictionary;
    }

    public void markEnd() {
        writePostingFileToDisc(postingFiles, (currentDoc / numOfDocsPerPosting) + 1);
        mergePostingFiles();
        writeDictionaryToDisc();
        System.out.println("Wrote to disc, doc number:" + currentDoc);
    }

    private void writeDictionaryToDisc() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("PostingFile\\Dictionary.txt"));
            writeMapToFile(writer, dictionary);
            writer = new BufferedWriter(new FileWriter("PostingFile\\DocumentsInfo.txt"));
            writeMapToFile(writer, documents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeMapToFile(BufferedWriter writer, Map<String, Pair<Integer, Integer>> map) throws IOException {
        for (Map.Entry<String, Pair<Integer, Integer>> entry : map.entrySet()) {
            writer.write(entry.getKey());
            writer.write("->");
            writer.write(entry.getValue().toString());
            writer.write('\n');
        }
        writer.flush();
    }

    public static void main(String[] args) {

    }
}
