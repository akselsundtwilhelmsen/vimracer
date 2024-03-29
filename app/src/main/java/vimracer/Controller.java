package vimracer;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class Controller implements Initializable {

    @FXML private Text solutionText;
    @FXML private TextFlow vimText;
    @FXML private Pane vimPane;
    @FXML private Pane nameInputPane;
    @FXML public Text stopwatchText;
    @FXML public Text keypressCounterText;
    @FXML public Text leaderboardText;
    @FXML public Text nameInputText;

    TextWindow solution;
    Vim vim;
    Stopwatch stopwatch;
    KeypressCounter keypressCounter;
    TextLoader textLoader;
    Leaderboard leaderboard;
    Game game;
    NameInput nameInput;

    double fontSize;
    final int lineLength = 71;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        textLoader = new TextLoader();
        vim = new Vim();
        solution = new TextWindow();
        leaderboard = new Leaderboard(textLoader);
        nameInput = new NameInput(this);
        fontSize = solutionText.getFont().getSize();

        populateUI();

        final Controller c = this; // Oskar & Mathias hack
        Timeline updateStopwatch = new Timeline(new KeyFrame(Duration.seconds(1/60f), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                c.updateStopwatch();
            }
        }));
        updateStopwatch.setCycleCount(Timeline.INDEFINITE);
        updateStopwatch.play();

        nameInputPane.requestFocus(); // dette funker ikke her
    }

    @FXML
    private void populateUI() {
        updateSolution();
        updateLeaderboard();
        setVimText();
    }

    @FXML
    public void startGame() { // on button start game
        game = new Game();
        setVimText();
        keypressCounterText.setText(game.getKeypressCounter());
        updateStopwatch();
        vimText.requestFocus();
    }

    @FXML
    public void endGame() { // on button end game
        game = null;
        // setVimText();
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
        leaderboard.sort(true);
        updateLeaderboard();
    }

    @FXML
    public void sortLeaderboard(boolean next) {
        leaderboard.sort(next);
        updateLeaderboard();
    }

    @FXML
    private void setVimText() {
        vim = new Vim(); // resets the cursor 
        vim.setText(textLoader.getScrambledText());
        populateTextFlow(vimText, lineLength);
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) { // on keypress
        vim.keyPress(event);
        populateTextFlow(vimText, lineLength);
        updateKeypressCounter();
    }

    @FXML
    private void populateTextFlow(TextFlow textFlow, int maxLineLength) {
        textFlow.getChildren().clear();
        final int padding = 2;
        maxLineLength -= padding + 2; // to account for line number and padding

        String paddingStringOverflow = " "; // amount of padding needed after line overflow
        for (int i=0; i < padding; i++) {
            paddingStringOverflow += " ";
        }

        int[] cursor = vim.getCursor();
        final int offset = 3; // for the cursor to account for line number and padding
        ArrayList<String> lines = vim.getArray();

        boolean different; // gets set to true if two lines are different
        if (lines.size() == 0) {
            different = true;
        }
        else {
            different = false;
        }

        Integer lineNumber = 0;
        for (String line : lines) {
            lineNumber++;

            int currentPadding = padding - ("" + lineNumber).length(); // amount of padding needed for the current line
            String paddingString = "";
            for (int i=0; i < currentPadding; i++) {
                paddingString += " ";
            }

            String outString = "";
            if (line.length() < maxLineLength) {
                outString += paddingString + lineNumber + " " + line + " \n";
            }
            else {
                outString += paddingString + lineNumber + " " + line.substring(0, maxLineLength) + " \n";
                int counter = 1;
                while (true) {
                    counter += 1;
                    if (line.length() > maxLineLength*counter) {
                        outString += paddingStringOverflow + line.substring((counter-1)*maxLineLength, counter*maxLineLength) + " \n";
                    }
                    else {
                        outString += paddingStringOverflow + line.substring((counter-1)*maxLineLength) + " \n";
                        if (counter*maxLineLength == line.length()) { // hack to avoid overflow in beforeCursor on linebreak
                            outString += "    \n";
                        }
                        break;
                    }
                }
            }

            Color comparisonColor = Color.rgb(255, 255, 255);
            if (lineNumber-1 < solution.getArray().size()) {
                String solutionLine = solution.getArray().get(lineNumber-1).trim();
                if (line.trim().equals(solutionLine)) {
                    comparisonColor = Color.rgb(75, 75, 75);
                }
                else {
                    different = true;
                }
            }

            if (lineNumber.equals(cursor[1]+1)) {
                int overflowPaddingOffset = 3+(5*((cursor[0])/maxLineLength)); // to account for the leading spaces after a linebreak

                // before cursor
                Text beforeCursor = new Text(outString.substring(0, cursor[0]+overflowPaddingOffset));
                beforeCursor.setFill(comparisonColor);
                beforeCursor.setFont(Font.font("Ubuntu mono", FontWeight.NORMAL, fontSize));
                vimText.getChildren().add(beforeCursor);

                // on cursor
                String cursorCharacter = outString.substring(cursor[0]+overflowPaddingOffset,
                                                                cursor[0]+1+overflowPaddingOffset); // to check if it's a space (or newline)
                Text onCursor;
                if (cursorCharacter.equals(" ") || cursorCharacter.equals(null)) { // to make the cursor visible when on a space
                    onCursor = new Text("_");
                }
                else {
                    onCursor = new Text(cursorCharacter);
                }
                onCursor.setFill(Color.RED);
                onCursor.setFont(Font.font("Ubuntu mono", FontWeight.NORMAL, fontSize));
                vimText.getChildren().add(onCursor);

                // after cursor
                Text afterCursor = new Text(outString.substring(cursor[0]+1+overflowPaddingOffset));
                afterCursor.setFill(comparisonColor);
                afterCursor.setFont(Font.font("Ubuntu mono", FontWeight.NORMAL, fontSize));
                vimText.getChildren().add(afterCursor);
            }
            else {
                Text lineText = new Text(outString);
                lineText.setFill(comparisonColor);
                lineText.setFont(Font.font("Ubuntu mono", FontWeight.NORMAL, fontSize));
                vimText.getChildren().add(lineText);
            }
        }
        textFlow.requestLayout();
        
        if (!different) {
            gameWon();
        }
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
        if (game == null) {
            nameInput.keyPress(event);
        }
        updateNameInput();
    }

    @FXML
    private void updateNameInput() {
        nameInputText.setText(nameInput.toString());
    }

    @FXML
    public void handleOnKeyReleased(KeyEvent event) {
        vim.keyRelease(event);
    }

    @FXML
    private void updateStopwatch() {
        if (game != null) {
            stopwatchText.setText(game.getStopwatch());
        }
        else {
            stopwatchText.setText("00:00:000");
        }
    }

    @FXML
    private void updateKeypressCounter() {
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
        if (game == null) {
            nameInputPane.requestFocus();
        }
    }

    @FXML
    private void gameWon() {
        ArrayList<String> gameWonArray = new ArrayList<>();
        gameWonArray.add(nameInput.getName());
        gameWonArray.add(game.getKeypressCounter());
        gameWonArray.add(game.getStopwatch());
        vim = new Vim();
        vim.setText(gameWonArray);
        populateTextFlow(vimText, lineLength);

        leaderboard.writeToFile(nameInput.getName(), game.getKeypressCounter(), String.valueOf(game.getStopwatchLong()));
        sortLeaderboard(false);
        updateLeaderboard();
        endGame();
    }
}