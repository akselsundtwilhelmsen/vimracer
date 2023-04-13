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

    TextWindow solution;
    Vim vim;
    public Stopwatch stopwatch;
    KeypressCounter keypressCounter;
    TextLoader textLoader;
    Leaderboard leaderboard;
    Game game;
    NameInput nameInput;

    @FXML private Text solutionText;
    @FXML private TextFlow vimText;
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
    public void startGame() { // on button start game
        game = new Game(this);
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
    public void setVimText() {
        vim = new Vim(); // resets the cursor 
        vim.setText(textLoader.getScrambledText());
        populateTextFlow(vimText, lineLength);
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) { // on keypress
        vim.keyPress(event);
        populateTextFlow(vimText, lineLength);

        updateKeypressCounter();
        // if (textLoader.compareToSolution()) {
        //     gameWon();
        // }
    }

    @FXML
    private void populateTextFlow(TextFlow textFlow, int maxLineLength) {
        textFlow.getChildren().clear();
        final int padding = 2;
        maxLineLength -= padding + 2; // to account for line number and padding
        boolean different = false;

        String paddingStringOverflow = " "; // amount of padding needed after line overflow
        for (int i=0; i < padding; i++) {
            paddingStringOverflow += " ";
        }

        int[] cursor = vim.getCursor();
        final int offset = 3; // for the cursor to account for line number and padding
        ArrayList<String> lines = vim.getArray();

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
                // når linjen er mindre enn linelength!!
                outString += paddingString + lineNumber + " " + line + " \n";
            }
            else {
                // når linjen er lenger enn linelength!!
                outString += paddingString + lineNumber + " " + line.substring(0, maxLineLength) + " \n";
                int counter = 1;
                while (true) {
                    counter += 1;
                    if (line.length() > maxLineLength*counter) {
                        outString += paddingStringOverflow + line.substring((counter-1)*maxLineLength, counter*maxLineLength) + " \n";
                    }
                    else {
                        outString += paddingStringOverflow + line.substring((counter-1)*maxLineLength) + " \n";
                        break;
                    }
                }
            }

            Color comparisonColor = Color.rgb(255, 255, 255);
            String solutionLine = solution.getArray().get(lineNumber-1).trim();
            if (line.trim().equals(solutionLine)) {
                comparisonColor = Color.rgb(75, 75, 75);
            }
            else {
                different = true;
            }

            if (lineNumber.equals(cursor[1]+1)) {
                int overflowPaddingOffset = 0; // to account for the leading spaces after a linebreak
                if (cursor[0] >= maxLineLength) {
                    overflowPaddingOffset = 5;
                }

                // before cursor
                Text beforeCursor = new Text(outString.substring(0, cursor[0]+offset+overflowPaddingOffset));
                beforeCursor.setFill(comparisonColor);
                beforeCursor.setFont(Font.font("Ubuntu mono", FontWeight.NORMAL, 13.0));
                vimText.getChildren().add(beforeCursor);

                // on cursor
                String cursorCharacter = outString.substring(cursor[0]+offset+overflowPaddingOffset, cursor[0]+1+offset+overflowPaddingOffset); // to check if it's a space (or newline)
                Text onCursor;
                if (cursorCharacter.equals(" ") || cursorCharacter.equals(null)) { // to make the cursor visible when on a space
                    onCursor = new Text("_");
                }
                else {
                    onCursor = new Text(outString.substring(cursor[0]+offset+overflowPaddingOffset, cursor[0]+1+offset+overflowPaddingOffset));
                }
                onCursor.setFill(Color.RED);
                onCursor.setFont(Font.font("Ubuntu mono", FontWeight.NORMAL, 13.0));
                vimText.getChildren().add(onCursor);

                // after cursor
                Text afterCursor = new Text(outString.substring(cursor[0]+1+offset+overflowPaddingOffset));
                afterCursor.setFill(comparisonColor);
                afterCursor.setFont(Font.font("Ubuntu mono", FontWeight.NORMAL, 13.0));
                vimText.getChildren().add(afterCursor);

            }
            else {
                Text lineText = new Text(outString);
                lineText.setFill(comparisonColor); // INNI ELSE
                lineText.setFont(Font.font("Ubuntu mono", FontWeight.NORMAL, 13.0));
                vimText.getChildren().add(lineText);
            }
        }
        textFlow.requestLayout();
        
        if (!different) {
            gameWon();
        }
        System.out.println(different);
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