package vimracer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class Controller implements Initializable {

    TextWindow solution;
    Vim vim;
    public Stopwatch stopwatch;
    KeypressCounter keypressCounter;
    TextLoader textLoader;

    Game game;

    @FXML private Text solutionText;
    @FXML private Text vimText;
    @FXML private Pane vimPane;
    @FXML public Text stopwatchText;
    @FXML public Text keypressCounterText;

    final int lineLength = 86;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        textLoader = new TextLoader();
        vim = new Vim();
        solution = new TextWindow();
        this.populateUI();

        final Controller c = this; // Oskar & Mathias hack
        Timeline updateStopwatch = new Timeline(new KeyFrame(Duration.seconds(1/60f), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                c.updateStopwatch();
            }
        }));
        updateStopwatch.setCycleCount(Timeline.INDEFINITE);
        updateStopwatch.play();
    }

    @FXML
    public void populateUI() {
        solution.setText(textLoader.getText());
        solutionText.setText(solution.toString(lineLength));
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) {
        vim.keyPress(event);
        vimText.setText(vim.toString(lineLength));
        this.updateKeypressCounter();
    }

    @FXML
    public void handleOnKeyReleased(KeyEvent event) {
        vim.keyRelease(event);
    }

    // @FXML
    public void updateStopwatch() {
        if (this.game != null) {
            this.stopwatchText.setText(game.getStopwatch());
        }
    }

    @FXML
    public void updateKeypressCounter() {
        if (this.game != null) {
            this.game.keypress(); 
            this.keypressCounterText.setText(this.game.getKeypressCounter());
            this.updateStopwatch(); //dette må kjøres på interval, TEMPORARY
        }
    }

    // @FXML
    public void startGame() {
        this.game = new Game(this);
        this.keypressCounterText.setText(this.game.getKeypressCounter());
        this.updateStopwatch();
    }

    public void endGame() {
        this.game = null;
    }

    public void nextFile() {
        this.endGame();
        this.textLoader.nextFile();
        this.updateSolution();
    }

    public void prevFile() {
        this.endGame();
        this.textLoader.prevFile();
        this.updateSolution();
    }

    public void updateSolution() {
        // this.solutionText.setText(solution.toString()); // dette funker på magisk vis
        this.solutionText.setText(solution.toString(lineLength));
        solution.setText(textLoader.getText());
    }
}