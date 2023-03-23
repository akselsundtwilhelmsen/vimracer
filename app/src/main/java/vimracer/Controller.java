package vimracer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class Controller implements Initializable {

    TextWindow soulution;
    Vim vim;

    @FXML private Text soulutionText;
    @FXML private Text vimText;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        vim = new Vim();
        soulution = new TextWindow();
        soulution.setText("Aa");
    }

    @FXML
    public void onVimDoThing() {
        vim.setRandomText();
        vimText.setText(vim.toString());
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) {
        vim.keyPress(event);
        vimText.setText(vim.toString());
        if (isTextsEqual()) {
            // midlertidig
            soulution.setText("Congratulations! You won");
            soulutionText.setText(soulution.toString());
        }
    }

    @FXML
    public void handleOnKeyReleased(KeyEvent event) {
        vim.keyRelease(event);
    }

    private boolean isTextsEqual() {
        return soulution.equals(vim);
    }
    // public void setVimText(String text) {
    //     vimWindow.setText(text);
    // }
}
