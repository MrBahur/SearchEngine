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

/**
 * an Indexer for the Corpus.
 */
public class Indexer {
    public static final int NUM_OF_DOCS = 472525; //the number of docs in our original corpus, checked offline
    private static final int NUM_OF_DOCS_PER_POSTING = 5000; //the amount of docs we analise every time before writing to disc
    private Map<String, Pair<Integer, Integer>> documents;// doc ID -> <max_tf,Number of unique words>
    private Integer currentDoc;//current doc number
    private String currentDocID;// current doc ID
    private Map<String, Pair<Integer, Integer>> dictionary;//Term -> <amount in corpus Map , pointer to posting file>
    private Map<Term, LinkedList<Pair<String, Integer>>> postingFiles; //map of posting files for each term
    private Map<String, Integer> phrasesDocs;// DS to hold Phrases that we only saw in one Document
    private boolean toStem; //is the data we get stemmed or not
    public static int numberCounter = 0;// counter for how much unique number there are in the corpus
    //

    /**
     * the constructor we are exposing to user
     *
     * @param toStem is the data we get stemmed or not
     */
    public Indexer(boolean toStem) {
        this(16777216, NUM_OF_DOCS_PER_POSTING, toStem);
    }

    /**
     * @param initialWordSize   the init number of words we found that the engine performance was best (2^24)
     * @param numOfDocsInMemory the number of docs that we are holding in memory every time, after allot of interactions we found that 5000 is the number
     * @param toStem            is the data we get stemmed or not
     */
    public Indexer(int initialWordSize, int numOfDocsInMemory, boolean toStem) {
        documents = new HashMap<>(NUM_OF_DOCS, 1);
        currentDoc = 0;
        dictionary = new HashMap<>(initialWordSize, 4);
        postingFiles = new HashMap<>(numOfDocsInMemory);
        phrasesDocs = new HashMap<>();
        this.toStem = toStem;
    }

    /**
     * the function get Term and adding it to the dictionary and posting files,
     * it also checks all the constrains that we need to enforce
     * it also converts it to String for the dictionary (again, performance was better that way
     * probably because Java implementation of HashCode and Equals are very efficient for String)
     *
     * @param term the term to add
     */
    public void addTerm(Term term) {
        if (term instanceof Phrase) { //check if the Phrase already appeared in another doc
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
        if (term instanceof Word) { //check if there was a Name that look like this word
            if (dictionary.containsKey(term.toString().toUpperCase())) {
                Integer num = dictionary.get(term.toString().toUpperCase()).getKey();
                dictionary.remove(term.toString().toUpperCase());
                dictionary.put(term.toString(), new Pair<>(num, 0));
            }
        }
        if (term instanceof Name) { //check if there a Word that look like this Name
            if (dictionary.containsKey(term.toString().toLowerCase())) {
                term = new Word(term.toString(), toStem);
            }
        }
        if (dictionary.containsKey(term.toString())) {//check if we need to add new word to dictionary or just sum it
            Integer amount = dictionary.get(term.toString()).getKey();
            dictionary.remove(term.toString());
            dictionary.put(term.toString(), new Pair<>(amount + 1, 0));
        } else {
            dictionary.put(term.toString(), new Pair<>(1, 0));
            if (term instanceof Number) {//to answer the number question
                numberCounter++;
            }
        }
        if (!postingFiles.containsKey(term)) {// adding everything we want to save in the posting file
            postingFiles.put(term, new LinkedList<>());
            postingFiles.get(term).addLast(new Pair<>(currentDocID, 1));
        } else if (!postingFiles.get(term).getLast().getKey().equals(currentDocID)) {
            postingFiles.get(term).addLast(new Pair<>(currentDocID, 1));
        } else {
            Pair<String, Integer> pair = postingFiles.get(term).getLast();
            postingFiles.get(term).removeLast();
            postingFiles.get(term).addLast(new Pair<>(pair.getKey(), pair.getValue() + 1));

        }
        Pair<Integer, Integer> pair = documents.get(currentDocID);
        if (postingFiles.get(term).getLast().getValue() == 1) {// calculating max_tf and Number of unique words
            documents.remove(currentDocID);
            documents.put(currentDocID, new Pair<>(pair.getKey(), pair.getValue() + 1));
        } else if (postingFiles.get(term).getLast().getValue() > pair.getKey()) {
            documents.remove(currentDocID);
            documents.put(currentDocID, new Pair<>(pair.getKey() + 1, pair.getValue()));
        }
    }

    /**
     * adding a new doc to the indexer
     *
     * @param doc the doc ID of the documents we insert
     */
    public void addDoc(String doc) {
        documents.put(doc, new Pair<>(0, 0));
        currentDocID = doc;
        currentDoc++;
        if (currentDoc % NUM_OF_DOCS_PER_POSTING == 0) {
            writePostingFileToDisc(postingFiles, currentDoc / NUM_OF_DOCS_PER_POSTING);
            System.out.println("Wrote to disc, doc number:" + currentDoc);
            postingFiles = new HashMap<>(postingFiles.size());
        }
    }

    /**
     * mark the indexer that the Parser end it's work
     * and calling every thing that need to happen in the end
     */
    public void markEnd() {
        removeSinglePhrases();
        writePostingFileToDisc(postingFiles, (currentDoc / NUM_OF_DOCS_PER_POSTING) + 1);
        mergePostingFiles();
        writeDictionaryToDisc();
        System.out.println("Wrote to disc, doc number:" + currentDoc);
    }

    /**
     * return the dictionary
     *
     * @return map that is dictionary
     */
    public Map<String, Pair<Integer, Integer>> getDictionary() {
        return dictionary;
    }

    public Map<String, Pair<Integer, Integer>> getDocuments() {
        return documents;
    }

    /**
     * remove every Phrase that only was in one document
     */
    private void removeSinglePhrases() {
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

    /**
     * merge all the small posting files we create to one posting file
     */
    private void mergePostingFiles() {
        try {
            new File(((toStem) ? "S" : "") + "PostingFile").mkdir();
            BufferedWriter[] writers = getBufferedWriters(0, ((toStem) ? "S" : "") + "PostingFile");
            Stream<Path> walk = Files.walk(Paths.get(((toStem) ? "S" : "") + "PostingFiles"));
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

    /**
     * merge single posting file type to it's posting file for example: (a1,a2,...,an) -> a
     *
     * @param character the char that show what posting files we want to merge
     * @param files     the list of all the files in the PostingFiles dir
     * @param writer    a writer that write to the correct file
     * @throws IOException handled in mergePostingFiles
     */
    private void mergePostingFile(char character, List<String> files, BufferedWriter writer) throws IOException {
        HashMap<String, LinkedList<Pair<String, Integer>>> postingFile = new HashMap<>();
        for (String file : files) {
            if (file.charAt(file.indexOf('\\') + 1) != character) {
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
            dictionary.remove(entry.getKey());
            dictionary.put(entry.getKey(), new Pair<>(amount, counter));
            counter++;
        }
    }

    /**
     * write the postinfFiles to disc
     *
     * @param postingFiles the posting file we need to write
     * @param iteration    what iteration is it? (0 mark the final iteration)
     */
    private void writePostingFileToDisc(Map<Term, LinkedList<Pair<String, Integer>>> postingFiles, int iteration) {
        try {
            new File(((toStem) ? "S" : "") + "PostingFiles").mkdir();
            BufferedWriter[] writers = getBufferedWriters(iteration, ((toStem) ? "S" : "") + "PostingFiles");
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

    /**
     * init all the writers
     *
     * @param iteration what iteration is it? (0 mark the final iteration)
     * @param path      path to write to
     * @return array of writers in size of 53 (a-z, A-Z, 0)
     * @throws IOException handled in the calling function
     */
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

    /**
     * write single list to file
     *
     * @param entry  a term and he's list
     * @param writer the relevant writer
     * @throws IOException handled in the calling function
     */
    private void writeToFile(Map.Entry<Term, LinkedList<Pair<String, Integer>>> entry, BufferedWriter writer) throws IOException {
        writer.write(entry.getKey().toString());
        writer.write("->");
        for (Pair<String, Integer> p : entry.getValue()) {
            writer.write(p.toString());
            writer.write(';');
        }
        writer.write('\n');
    }

    /**
     * write the dictionary and the DocumentsInfo to the file
     */
    private void writeDictionaryToDisc() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(((toStem) ? "S" : "") + "PostingFile\\Dictionary.txt"));
            writeMapToFile(writer, dictionary);
            writer = new BufferedWriter(new FileWriter(((toStem) ? "S" : "") + "PostingFile\\DocumentsInfo.txt"));
            writeMapToFile(writer, documents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * write a map to file
     *
     * @param writer relevant writer
     * @param map    a map to write
     * @throws IOException handled in the calling function
     */
    private void writeMapToFile(BufferedWriter writer, Map<String, Pair<Integer, Integer>> map) throws IOException {
        for (Map.Entry<String, Pair<Integer, Integer>> entry : map.entrySet()) {
            writer.write(entry.getKey());
            writer.write("->");
            writer.write(entry.getValue().toString());
            writer.write('\n');
        }
        writer.flush();
    }
}
