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
    public Stopwatch stopwatch;
    KeypressCounter keypressCounter;
    TextLoader textLoader;

    Game game;

    @FXML private Text solutionText;
    @FXML private Text vimText;
    // @FXML private Pane vimPane;
    @FXML public Text stopwatchText;
    @FXML public Text keypressCounterText;

    final int lineLength = 80;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        textLoader = new TextLoader();
        vim = new Vim();
        solution = new TextWindow();
        this.populateUI();
    }

    @FXML
    public void populateUI() {
        solution.setText(textLoader.getText());
        solutionText.setText(solution.toString(lineLength));
        keypressCounterText.setText("0");
        stopwatchText.setText("00:00:000");
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) {
        vim.keyPress(event);
        vimText.setText(vim.toString(lineLength));

        if (this.game != null) {
            this.game.keypress(); //TODO: flytt denne til vim.java for å unngå registrering av ugyldige tastetrykk
            keypressCounterText.setText(this.game.getKeypressCounter());
            this.updateStopwatch(); //dette må kjøres på interval
        }
    }

    @FXML
    public void handleOnKeyReleased(KeyEvent event) {
        vim.keyRelease(event);
    }

    @FXML
    public void updateStopwatch() {
        if (this.game != null) {
            System.out.println("yeah");
            this.stopwatchText.setText(game.getStopwatch());
        }
    }

    @FXML
    public void startGame() {
        this.game = new Game(this);
        System.out.println("startGame");
        this.updateStopwatch();
    }

    public void endGame() {
        this.game = null;
    }
}