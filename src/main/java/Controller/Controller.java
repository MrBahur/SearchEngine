package Controller;

import Model.Model;
import Model.Search.Searcher;
import View.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class Controller {
    @FXML
    public Button browseCorpusDirBTN;
    @FXML
    public Button browseDicDirBTN;
    @FXML
    public TextField corpusDir;
    @FXML
    public TextField dicDir;
    @FXML
    public CheckBox toStem;
    @FXML
    public Button showDicBTN;
    @FXML
    public Button resetBTN;
    @FXML
    public Button loadDicBTN;
    @FXML
    public Button runBTN;
    @FXML
    public TextField query;
    @FXML
    public Button runQuery;
    @FXML
    public TextField queryFile;
    @FXML
    public Button browseQueryFile;
    @FXML
    public TextField docID;
    @FXML
    public Button searchDocID;
    @FXML
    public CheckBox toSemantic;
    @FXML
    public Button runQueryFile;


    private boolean clickedCorpusBrowse = false;
    private boolean clickedDicBrowse = false;
    private boolean clickedQueryBrowse = false;
    private boolean loaded = false;

    @FXML
    public void handleBrowseClick(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(Main.primaryStage);
        String s = selectedDirectory.getPath();
        if (actionEvent.getSource().equals(browseCorpusDirBTN)) {
            clickedCorpusBrowse = true;
            corpusDir.setText(s);
        } else if (actionEvent.getSource().equals(browseDicDirBTN)) {
            clickedDicBrowse = true;
            dicDir.setText(s);
        } else if (actionEvent.getSource().equals(browseQueryFile)) {
            clickedQueryBrowse = true;
            queryFile.setText(s);
        }
    }

    public void loadDictionaryFromPostingFile(ActionEvent actionEvent) {
        if (clickedDicBrowse && clickedCorpusBrowse) {
            try {
                BufferedReader f = new BufferedReader(new FileReader(dicDir.getText() + "\\Dictionary.txt"));
                Main.model.setDictionary(new HashMap<>());
                String line;
                while ((line = f.readLine()) != null) {
                    Main.model.getDictionary().put(line.substring(0, line.indexOf("->")),
                            new Pair<>(Integer.parseInt(line.substring(line.indexOf("->") + 2, line.indexOf("="))),
                                    Integer.parseInt(line.substring(line.indexOf("=") + 1))));
                }
                Main.model.setSearcher(new Searcher(toStem.isSelected(), corpusDir.getText(), Main.model.getDictionary()));
                loaded = true;
            } catch (IOException e) {
                clickedCorpusBrowse = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Wrong Path Message");
                alert.setHeaderText("You entered Wrong path");
                alert.setContentText("Please click the browse button and insert path that contains\n" +
                        "the Posting Files and corpus for this project" +
                        "thanks!");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("didn't entered path");
            alert.setHeaderText("You didn't entered path");
            alert.showAndWait();
        }
    }

    public void showDictionary(ActionEvent actionEvent) {
        if (loaded) {
            TableView tableView = new TableView();

            TableColumn<String, DictionaryTableEntry> column1 = new TableColumn<>("Term");
            column1.setCellValueFactory(new PropertyValueFactory<>("term"));
            TableColumn<String, DictionaryTableEntry> column2 = new TableColumn<>("Amount");
            column2.setCellValueFactory(new PropertyValueFactory<>("amount"));
            //TableColumn<String, DictionaryTableEntry> column3 = new TableColumn<>("Pointer");
            //column3.setCellValueFactory(new PropertyValueFactory<>("pointer"));


            tableView.getColumns().add(column1);
            tableView.getColumns().add(column2);
            //tableView.getColumns().add(column3);


            for (Map.Entry<String, Pair<Integer, Integer>> entry : Main.model.getDictionary().entrySet()) {
                tableView.getItems().add(new DictionaryTableEntry(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue()));
            }
            tableView.getSortOrder().add(column1);
            VBox vBox = new VBox(tableView);

            Scene scene = new Scene(vBox);

            Stage secondaryStage = new Stage();

            secondaryStage.setScene(scene);
            secondaryStage.setAlwaysOnTop(true);
            secondaryStage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("no dictionary loaded");
            alert.setHeaderText("Please click Load to load dictionary from Posting files\n" +
                    "or run to run the Parser on the corpus\n" +
                    "no need to load the dictionary if you run it on the corpus");
            alert.showAndWait();
        }
    }

    public void showQuery(ArrayList<Pair<String, Double>> queryResult) {
        TableView tableView = new TableView();

        TableColumn<String, SingleQueryTableEntry> column1 = new TableColumn<>("DocID");
        column1.setCellValueFactory(new PropertyValueFactory<>("docID"));
        TableColumn<String, SingleQueryTableEntry> column2 = new TableColumn<>("Rank");
        column2.setCellValueFactory(new PropertyValueFactory<>("rank"));

        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);

        for (Pair<String, Double> p : queryResult) {
            tableView.getItems().add(new SingleQueryTableEntry(p.getKey(), p.getValue()));
        }

        VBox vBox = new VBox(tableView);

        Scene scene = new Scene(vBox);

        Stage secondaryStage = new Stage();

        secondaryStage.setScene(scene);
        secondaryStage.setAlwaysOnTop(true);
        secondaryStage.show();
    }

    public void reset(ActionEvent actionEvent) {
        Main.model = new Model();
        System.gc();
        System.gc();
        loaded = false;
        clickedCorpusBrowse = false;
        clickedDicBrowse = false;
        clickedQueryBrowse = false;
        deleteFilesInDir("PostingFile");
        deleteFilesInDir("PostingFiles");
        deleteFilesInDir("SPostingFile");
        deleteFilesInDir("SPostingFiles");
        corpusDir.setText("Corpus Directory Path");
        dicDir.setText("Dictionary Directory Path");

    }

    private void deleteFilesInDir(String path) {
        File f1 = new File(path);
        if (f1.exists() && f1.isDirectory()) {
            String[] entries = f1.list();
            for (String s : entries) {
                File currentFile = new File(f1.getPath(), s);
                currentFile.delete();
            }
            while (!f1.delete()) ;
        }
    }

    public void run(ActionEvent actionEvent) {
        if (clickedCorpusBrowse) {
            File corpus = new File(corpusDir.getText() + "\\corpus");
            File stopWords = new File(corpusDir.getText() + "\\05 stop_words.txt");
            if (corpus.exists() && corpus.isDirectory() && stopWords.exists()) {
                Main.model.setParser(corpusDir.getText(), toStem.isSelected());
                Main.model.getParser().parse();
                Main.model.setDictionary(Main.model.getParser().getDictionary());
                Main.model.setDocuments(Main.model.getParser().getDocuments());
                Main.model.setSearcher(new Searcher(toStem.isSelected(), corpusDir.getText(), Main.model.getDictionary()));
                loaded = true;
            } else {
                clickedCorpusBrowse = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Wrong Path Message");
                alert.setHeaderText("You entered Wrong path");
                alert.setContentText("Please click the browse button and insert path that contains\n" +
                        "corpus folder named 'corpus'\n" +
                        "and stop words file named '05 stop_words.txt'\n" +
                        "thanks!");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("didn't entered path");
            alert.setHeaderText("You didn't entered path");
            alert.showAndWait();
        }
    }

    public void handleRunQueryClick(ActionEvent actionEvent) {
        if (loaded) {
            showQuery(Main.model.getSearcher().search(query.getText()));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("no dictionary loaded");
            alert.setHeaderText("Please click Load to load dictionary from Posting files\n" +
                    "or run to run the Parser on the corpus\n" +
                    "no need to load the dictionary if you run it on the corpus");
            alert.showAndWait();
        }
    }

    public void handleSearchDocIDClick(ActionEvent actionEvent) {
        if (loaded) {
            LinkedList<String> result = Main.model.getSearcher().searchForPhrases(docID.getText().toUpperCase());
            if (result == null) {
                System.out.println("no doc id in the name " + docID.getText() + "exist");
            } else {
                for (String s : result) {
                    System.out.println(s);
                }
            }
        } else {
            System.out.println("raise exception");
        }
    }

    public void handleRunQueryFileButton(ActionEvent actionEvent) {
        if (loaded) {
            if (clickedQueryBrowse) {
                File f = new File(queryFile.getText() + "\\03 queries.txt");
                if (f.exists()) {
                    showMultiQuery(Main.model.getSearcher().search(f));
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("no query file loaded, please insert query file in the name '03 queries.txt'");
                    alert.setHeaderText("Please click Browse to load query file");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("no query file loaded");
                alert.setHeaderText("Please click Browse to load query file");
                alert.showAndWait();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("no dictionary loaded");
            alert.setHeaderText("Please click Load to load dictionary from Posting files\n" +
                    "or run to run the Parser on the corpus\n" +
                    "no need to load the dictionary if you run it on the corpus");
            alert.showAndWait();
        }
    }

    private void showMultiQuery(ArrayList<Pair<Integer, ArrayList<Pair<String, Double>>>> queriesResults) {
        TableView tableView = new TableView();

        TableColumn<String, MultiQueryTableEntry> column1 = new TableColumn<>("Query Number");
        column1.setCellValueFactory(new PropertyValueFactory<>("queryID"));
        TableColumn<String, MultiQueryTableEntry> column2 = new TableColumn<>("DocID");
        column2.setCellValueFactory(new PropertyValueFactory<>("docID"));
        TableColumn<String, MultiQueryTableEntry> column3 = new TableColumn<>("Rank");
        column3.setCellValueFactory(new PropertyValueFactory<>("rank"));

        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);


        for (Pair<Integer, ArrayList<Pair<String, Double>>> p1 : queriesResults) {
            for (Pair<String, Double> p2 : p1.getValue()) {
                tableView.getItems().add(new MultiQueryTableEntry(p1.getKey(), p2.getKey(), p2.getValue()));
            }
        }

        VBox vBox = new VBox(tableView);

        Scene scene = new Scene(vBox);

        Stage secondaryStage = new Stage();

        secondaryStage.setScene(scene);
        secondaryStage.setAlwaysOnTop(true);
        secondaryStage.showAndWait();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Query File");
        alert.setHeaderText("Saving File Dialog");
        alert.setContentText("do you want to save the results to a file?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            int iter = 1;
            double sim = 0.0;
            String runID = "ab";
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(Main.primaryStage);
            String s = selectedDirectory.getPath();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(s + "\\results.txt"));
                for (Pair<Integer, ArrayList<Pair<String, Double>>> p1 : queriesResults) {
                    for (Pair<String, Double> p2 : p1.getValue()) {
                        String line = "" + p1.getKey() + " " + iter + " " + p2.getKey() + " " + p2.getValue().intValue() + " " + sim + " " + runID + '\n';
                        writer.write(line);
                    }
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("pressed cancel");
        }
    }

    public class DictionaryTableEntry {
        private String term;
        private Integer amount;
        //private Integer pointer;

//        public Integer getPointer() {
//            return pointer;
//        }

        public DictionaryTableEntry() {

        }

        public DictionaryTableEntry(String term, Integer amount, Integer pointer) {
            this.term = term;
            this.amount = amount;
            //this.pointer = pointer;
        }

//        public void setPointer(Integer pointer) {
//            this.pointer = pointer;
//        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }

    public class MultiQueryTableEntry {
        private Integer queryID;
        private String docID;
        private Double rank;

        public MultiQueryTableEntry(Integer queryID, String docID, Double rank) {
            this.queryID = queryID;
            this.docID = docID;
            this.rank = rank;
        }

        public Integer getQueryID() {
            return queryID;
        }

        public void setQueryID(Integer queryID) {
            this.queryID = queryID;
        }

        public String getDocID() {
            return docID;
        }

        public void setDocID(String docID) {
            this.docID = docID;
        }

        public Double getRank() {
            return rank;
        }

        public void setRank(Double rank) {
            this.rank = rank;
        }
    }

    public class SingleQueryTableEntry {
        private String docID;
        private Double rank;

        public SingleQueryTableEntry(String docID, Double rank) {
            this.docID = docID;
            this.rank = rank;
        }

        public String getDocID() {
            return docID;
        }

        public void setDocID(String docID) {
            this.docID = docID;
        }

        public double getRank() {
            return rank;
        }

        public void setRank(double rank) {
            this.rank = rank;
        }
    }
}
