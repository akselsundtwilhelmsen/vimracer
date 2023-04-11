package vimracer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
    NameInput nameInput;

    @FXML private Text solutionText;
    @FXML private Text vimText;
    @FXML private Pane vimPane;
    @FXML private Pane nameInputPane;
    @FXML public Text stopwatchText;
    @FXML public Text keypressCounterText;
    @FXML public Text leaderboardText;
    @FXML public Text nameInputText;

    final int lineLength = 86;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        textLoader = new TextLoader();
        vim = new Vim();
        solution = new TextWindow();
        leaderboard = new Leaderboard(textLoader);
        nameInput = new NameInput(this);

        populateUI();
        // nameInputPane.requestFocus(); // TODO: dette funker ikke her

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
        updateSolution();
        updateLeaderboard();
        setVimText();
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) {
        vim.keyPress(event);
        vimText.setText(vim.toString(lineLength));
        updateKeypressCounter();
        if (textLoader.compareToSolution()) {
            gameWon();
        }
    }

    @FXML
    public void startGame() { // on button start game
        game = new Game(this);
        vim = new Vim(); // TODO: Hvorfor gjør dette at alt slettes når man begynner å skrive? Spør Brage.
        setVimText();
        keypressCounterText.setText(game.getKeypressCounter());
        updateStopwatch();
        vimText.requestFocus();
    }

    @FXML
    public void endGame() { // on button end game
        game = null;
        updateStopwatch();
        updateKeypressCounter();
        nameInputPane.requestFocus();
    }

    public void nextFile() { // on button next 
        endGame();
        textLoader.nextFile();
        leaderboard.readFromFile(textLoader.getCurrentFileName());
        updateLeaderboard();
        updateSolution();
        setVimText();
    }

    public void prevFile() { // on button previous
        endGame();
        textLoader.prevFile();
        leaderboard.readFromFile(textLoader.getCurrentFileName());
        updateLeaderboard();
        updateSolution();
        setVimText();
    }

    @FXML
    public void sortLeaderboard() {
        leaderboard.nextSort();
        updateLeaderboard();
    }

    @FXML
    public void setVimText() {
        vim.setText(textLoader.getScrambledText());
        vimText.setText(vim.toString(lineLength));
    }

    @FXML
    private void updateSolution() {
        solution.setText(textLoader.getText());
        solutionText.setText(solution.toString(lineLength));
    }

    @FXML
    private void updateLeaderboard() {
        leaderboardText.setText(leaderboard.toString());
    }

    @FXML
    public void nameInputKeyPressed(KeyEvent event) {
        nameInput.keyPress(event);
        updateNameInput();
    }

    @FXML
    public void updateNameInput() {
        nameInputText.setText(nameInput.toString());
    }

    @FXML
    public void handleOnKeyReleased(KeyEvent event) {
        vim.keyRelease(event);
    }

    @FXML
    public void updateStopwatch() {
        if (game != null) {
            stopwatchText.setText(game.getStopwatch());
        }
        else {
            stopwatchText.setText("00:00:000");
        }
    }

    @FXML
    public void updateKeypressCounter() {
        if (game != null) {
            game.keypress(); 
            keypressCounterText.setText(game.getKeypressCounter());
        }
        else {
            keypressCounterText.setText("0");
        }
    }

    @FXML
    private void nameFocus() {
        nameInputPane.requestFocus();
    }

    @FXML
    private void gameWon() {
        String gameWonString = "Correct"; // TODO: gjør skikkelig
        vimText.setText(gameWonString);
        leaderboard.writeToFile(nameInput.toString(), game.getKeypressCounter(), String.valueOf(game.getStopwatchLong()));
        endGame();
    }
}