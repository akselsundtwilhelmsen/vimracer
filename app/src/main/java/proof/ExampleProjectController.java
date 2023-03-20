package proof;

import java.util.ArrayList;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ExampleProjectController {

    // @FXML
    // private TextField firstNumber, secondNumber, operator;

    @FXML
    private Label vim_text;

    // private void initCalculator(String operator) {
    //     calculator = new Calculator(operator);
    // }

    public static String generateRandomWords(int numberOfWords) {
        String randomString = "";
        Random random = new Random();
        for(int i = 0; i < numberOfWords; i++)
        {
            char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for(int j = 0; j < word.length; j++)
            {
                word[j] = (char)('a' + random.nextInt(26));
            }
            randomString = randomString + new String(word) + " ";
        }
        return randomString.trim()+".";
    }

    @FXML
    private void handleButtonClick() {
        generateText();
    }

    public void generateText() {
        Textbox text = new Textbox();
        ArrayList<String> random = new ArrayList<>();
        random.add(generateRandomWords(10));
        random.add(generateRandomWords(10));
        random.add(generateRandomWords(10));
        random.add(generateRandomWords(10));
        random.add(generateRandomWords(10));
        random.add(generateRandomWords(10));
        random.add(generateRandomWords(10));
        random.add(generateRandomWords(10));
        text.setText(random);
        vim_text.setText(text.toString());

    }
}
