package vimracer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class Controller implements Initializable {

    @FXML
    private Text fasit;
    @FXML
    private Text vimWindow;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        Vim vim = new Vim();
        vim.setText("yesplesa");

    }

    public void setVimText(String text) {
        vimWindow.setText(text);
    }



}
