package vimracer;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class TextWindow {
    private ArrayList<String> text;

    public void setText(ArrayList text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = new ArrayList<>();
        this.text.add(text);
    }

    public String toString() {
        return text.stream()
            .collect(Collectors.joining("\n"));
    }

    public boolean equals(TextWindow t) {
        return this.toString().equals(t.toString());
    }

    public void setRandomText() {
        int numberOfWords = 12;
        int numberOfLines = 8;
        text = new ArrayList<>();
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
            text.add(randomString);
        }
    }
    
}