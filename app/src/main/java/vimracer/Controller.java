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
    Leaderboard leaderboard;
    Game game;

    @FXML private Text solutionText;
    @FXML private Text vimText;
    @FXML private Pane vimPane;
    @FXML public Text stopwatchText;
    @FXML public Text keypressCounterText;
    @FXML public Text leaderboardText;

    final int lineLength = 86;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        textLoader = new TextLoader();
        vim = new Vim();
        solution = new TextWindow();
        leaderboard = new Leaderboard(textLoader);
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
        this.updateSolution();
        this.updateLeaderboard();
        this.setVimText();
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) {
        // her må det sjekkes om det er klart for å skrives
        vim.keyPress(event);
        vimText.setText(vim.toString(lineLength));
        this.updateKeypressCounter();
        textLoader.compareToSolution();
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
    public void startGame() { // on button start game
        this.game = new Game(this);
        this.keypressCounterText.setText(this.game.getKeypressCounter());
        this.updateStopwatch();
    }

    public void endGame() { // on button end game
        this.game = null;
    }

    public void nextFile() { // on button next 
        this.endGame();
        this.textLoader.nextFile();
        this.leaderboard.readFromFile(this.textLoader.getCurrentFileName());
        this.updateLeaderboard();
        this.updateSolution();
        this.setVimText();
    }

    public void prevFile() { // on button previous
        this.endGame();
        this.textLoader.prevFile();
        this.leaderboard.readFromFile(this.textLoader.getCurrentFileName());
        this.updateLeaderboard();
        this.updateSolution();
        this.setVimText();
    }

    @FXML
    public void setVimText() {
        this.vim.setText(textLoader.getGarbledText());
        this.vimText.setText(vim.toString(lineLength));
    }

    @FXML
    public void sortLeaderboard() {
        this.leaderboard.nextSort();
        this.updateLeaderboard();
    }

    @FXML
    private void updateSolution() {
        this.solution.setText(textLoader.getText());
        this.solutionText.setText(solution.toString(lineLength));
    }

    @FXML
    private void updateLeaderboard() {
        this.leaderboardText.setText(this.leaderboard.toString());
    }
}