package vimracer;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Highscore {

    private final String path = "src/main/resources/highscores.txt";
    private HashMap<String, String> scores;
    private ArrayList<String> lines;
    
    public Highscore() {
        // this.readFromFile();
        // this.toHashMap();
    }

    private void readFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.path));
            this.lines = new ArrayList<String>(); // clears current lines
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    private void writeToFile() {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("ching" +"\n" + "yes");
            writer.close();
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    private void toHashMap() {
        scores = new HashMap<String, String>();
        for (int i=0; i < lines.size(); i += 2) {
            scores.put(lines.get(i), lines.get(i+1));
        }
    }

    public String toString() {
        return this.scores.toString();
    }

    public static void main(String[] args) {
        Highscore a = new Highscore();
        // a.writeToFile();
        a.readFromFile();
        a.toHashMap();
        System.out.println(a.toString());
    }
}