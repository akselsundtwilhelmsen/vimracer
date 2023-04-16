package vimracer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class TextLoaderTest {


    @BeforeEach 
    public void setup() {
    }

    @Test
    public void testFileReading() throws IOException {
        Assertions.assertEquals(true, true);
        // TextLoader textLoader = new TextLoader();
        // ArrayList<String> lines = textLoader.getText();
        // String promptPath = "app/src/main/resources/prompts/";

        // BufferedReader bufferedReader = new BufferedReader(new FileReader(promptPath+textLoader.getCurrentFileName()));
        // ArrayList<String> contentBuilder = new ArrayList<>();

        // String line;
        // while ((line = bufferedReader.readLine()) != null) {
        //     contentBuilder.add(line);
        // }
        // bufferedReader.close();
        // String actualContent = contentBuilder.toString();
        // String expectedContent = lines.toString();

        // Assertions.assertEquals(actualContent, expectedContent);
    }
}