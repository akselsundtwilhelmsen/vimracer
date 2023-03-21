package vimracer;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.scene.input.KeyEvent;

public class Vim {
    private ArrayList<String> lines;
    private ArrayList<Integer> cursor;
    private char mode; // must be n(ormal), v(isual), or i(nsert);

    public Vim() {
        this.lines = new ArrayList<>();
        this.cursor = new ArrayList<>();
        this.mode = 'n';
    }

    public void keyPress(KeyEvent event) {
        switch (event.getCode().toString()) {
            case "A":
                this.setText("Aa");
                break;
            case "B":
                this.setText("Bb");
                break;
        }
    }

    public void moveCursor(ArrayList<Integer> vector) {

    }

    public void setText(String text) {
        this.lines = new ArrayList<>();
        this.lines.add(text);
    }

    public String toString() {
        return lines.stream().collect(Collectors.joining("\n"));
    }

    public void setRandomText(int numberOfWords, int numberOfLines) {
        lines = new ArrayList<>();
        String randomString = "";
        Random random = new Random();
        for (int k = 0; k < numberOfLines; k++) {
            for(int i = 0; i < numberOfWords; i++) {
                char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
                for(int j = 0; j < word.length; j++) {
                    word[j] = (char)('a' + random.nextInt(26));
                }
                randomString = randomString + new String(word) + " ";
            }
            lines.add(randomString);
        }
    }
}