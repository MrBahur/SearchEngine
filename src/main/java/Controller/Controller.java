package Controller;

import Model.Model;
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

import javax.jws.WebParam;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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


    private boolean clickedCorpusBrowse = false;
    private boolean clickedDicBrowse = false;
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
        }
    }

    public void loadDictionaryFromPostingFile(ActionEvent actionEvent) {
        if (clickedDicBrowse) {
            try {
                BufferedReader f = new BufferedReader(new FileReader(dicDir.getText() + "\\Dictionary.txt"));
                Main.model.setDictionary(new HashMap<>());
                String line;
                while ((line = f.readLine()) != null) {
                    Main.model.getDictionary().put(line.substring(0, line.indexOf("->")),
                            new Pair<>(Integer.parseInt(line.substring(line.indexOf("->") + 2, line.indexOf("="))),
                                    Integer.parseInt(line.substring(line.indexOf("=") + 1))));
                }
                loaded = true;
            } catch (IOException e) {
                clickedCorpusBrowse = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Wrong Path Message");
                alert.setHeaderText("You entered Wrong path");
                alert.setContentText("Please click the browse button and insert path that contains\n" +
                        "the Posting Files for this project" +
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

            TableColumn<String, MyTableEntry> column1 = new TableColumn<>("Term");
            column1.setCellValueFactory(new PropertyValueFactory<>("term"));
            TableColumn<String, MyTableEntry> column2 = new TableColumn<>("Amount");
            column2.setCellValueFactory(new PropertyValueFactory<>("amount"));
            //TableColumn<String, MyTableEntry> column3 = new TableColumn<>("Pointer");
            //column3.setCellValueFactory(new PropertyValueFactory<>("pointer"));


            tableView.getColumns().add(column1);
            tableView.getColumns().add(column2);
            //tableView.getColumns().add(column3);


            for (Map.Entry<String, Pair<Integer, Integer>> entry : Main.model.getDictionary().entrySet()) {
                tableView.getItems().add(new MyTableEntry(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue()));
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

    public void reset(ActionEvent actionEvent) {
        Main.model = new Model();
        System.gc();
        System.gc();
        loaded = false;
        clickedCorpusBrowse = false;
        clickedDicBrowse = false;
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

    public class MyTableEntry {
        private String term;
        private Integer amount;
        //private Integer pointer;

//        public Integer getPointer() {
//            return pointer;
//        }

        public MyTableEntry() {

        }

        public MyTableEntry(String term, Integer amount, Integer pointer) {
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
}
