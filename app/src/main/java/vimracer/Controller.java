package vimracer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class Controller implements Initializable {

    TextWindow soultions;
    Vim vim;

    @FXML private Text soulutionText;
    @FXML private Text vimText;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        vim = new Vim();
    }

    @FXML
    public void onVimDoThing() {
        vim.setRandomText(10,10);
        vimText.setText(vim.toString());
    }

    @FXML
    public void handleOnKeyPressed() {
        vim.setRandomText(10,10);
        vimText.setText(vim.toString());
    }

    // public void setVimText(String text) {
    //     vimWindow.setText(text);
    // }



}
