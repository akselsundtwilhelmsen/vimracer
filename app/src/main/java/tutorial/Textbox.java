package tutorial;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

// import javafx.scene.chart.NumberAxis;

public class Textbox {
    private ArrayList<String> text;

    public Textbox() {
        text = new ArrayList<>();
    }

    public void setText(ArrayList<String> text) {
        this.text = text;
    }

    public String toString() {
        return text.stream()
            .collect(Collectors.joining("\n"));
    }

    
    public void setRandomText(int numberOfWords, int numberOfLines) {
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

    public static void main(String[] args) {
        Textbox t = new Textbox();
        ArrayList<String> a = new ArrayList<>();
        a.add("banger");
        a.add("banger");
        a.add("hanger");
        a.add("banger");

        t.setText(a);
        System.out.println(t);

    }


}
