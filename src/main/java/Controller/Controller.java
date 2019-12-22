package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

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
    public void handleBrowseClick(ActionEvent actionEvent) {
    }

    public void showDictionary(ActionEvent actionEvent) {
    }

    public void loadDictionaryFromPostingFile(ActionEvent actionEvent) {
    }

    public void reset(ActionEvent actionEvent) {
    }

    public void run(ActionEvent actionEvent) {
    }
}
