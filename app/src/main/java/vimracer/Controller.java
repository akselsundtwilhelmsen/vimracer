package vimracer;

import java.net.URL;
import java.util.ResourceBundle;
// import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class Controller implements Initializable {

    TextWindow solution;
    Vim vim;
    Stopwatch stopwatch;
    KeypressCounter keypressCounter;

    @FXML private Text solutionText;
    @FXML private Text vimText;
    // @FXML private Pane vimPane;
    @FXML private Text stopwatchText;
    //TODO: legg til keypresscounter i UI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111111

    final int lineLength = 80;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        TextLoader textLoader = new TextLoader();
        vim = new Vim();
        solution = new TextWindow();
        stopwatch = new Stopwatch(); // burde gjøres når spillet startes
        keypressCounter = new KeypressCounter(); //burde gjøres når spillet startes
        solution.setText(textLoader.getText());
        solutionText.setText(solution.toString(lineLength));

        stopwatchText.setFill(Color.RED);
        stopwatchText.setText(stopwatch.toString());
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) {
        vim.keyPress(event);
        keypressCounter.keypress();
        vimText.setText(vim.toString(lineLength));
        if (isTextsEqual()) {
            // midlertidig
            solution.setText("Congratulations! You won");
            solutionText.setText(solution.toString(lineLength));
        }
    }

    @FXML
    public void handleOnKeyReleased(KeyEvent event) {
        vim.keyRelease(event);
    }

    @FXML
    public void updateStopwatch() {
        stopwatchText.setText(stopwatch.toString());
    }

    private boolean isTextsEqual() {
        return solution.equals(vim);
    }
}