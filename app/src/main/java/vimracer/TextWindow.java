package vimracer;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class TextWindow {
    ArrayList<String> lines;

    public TextWindow() {
        lines = new ArrayList<>();
        lines.add("");
    }

    public void setText(ArrayList text) {
        this.lines = text;
    }

    public void setText(String text) {
        this.lines = new ArrayList<>();
        this.lines.add(text);
    }

    public String toString() {
        return lines.stream()
            .collect(Collectors.joining("\n"));
    }

    public boolean equals(TextWindow t) {
        return this.toString().equals(t.toString());
    }

    public void setRandomText() {
        int numberOfWords = 12;
        int numberOfLines = 8;
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
