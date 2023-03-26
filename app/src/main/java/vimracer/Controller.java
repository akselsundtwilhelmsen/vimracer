package vimracer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
// import javafx.scene.layout.AnchorPane;
// import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
// import javafx.stage.PopupWindow.AnchorLocation;

public class Controller implements Initializable {

    TextWindow solution;
    Vim vim;

    @FXML private Text solutionText;
    @FXML private Text vimText;
    // @FXML private Pane vimPane;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        TextLoader textLoader = new TextLoader();
        vim = new Vim();
        solution = new TextWindow();
        solution.setText(textLoader.getText());
        solutionText.setText(solution.toString());
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) {
        vim.keyPress(event);
        vimText.setText(vim.toString());
        if (isTextsEqual()) {
            // midlertidig
            solution.setText("Congratulations! You won");
            solutionText.setText(solution.toString());
        }
    }

    @FXML
    public void handleOnKeyReleased(KeyEvent event) {
        vim.keyRelease(event);
    }

    private boolean isTextsEqual() {
        return solution.equals(vim);
    }

    // public void setVimText(String text) {
    //     vimWindow.setText(text);
    // }
}