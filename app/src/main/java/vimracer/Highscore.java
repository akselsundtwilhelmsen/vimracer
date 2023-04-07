package vimracer;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class Highscore {

    private final String path = "src/main/resources/highscores.json";
    
    public Highscore() {
    }

    private void readFromFile() {

    }

    private void writeToFile() {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("ping");
            writer.close();
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Highscore a = new Highscore();
        a.writeToFile();
    }
}