package vimracer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class Controller implements Initializable {

    Vim vim;

    @FXML private Text fasit;
    @FXML private Text vimWindow;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        vim = new Vim();
        
    }

    @FXML
    public void onVimDoThing() {
        vim.setText("yea");
        fasit.setText(vim.toString());
    }

    // public void setVimText(String text) {
    //     vimWindow.setText(text);
    // }



}
