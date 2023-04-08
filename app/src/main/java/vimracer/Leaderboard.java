package vimracer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Leaderboard {

    private final String path = "src/main/resources/prompts/highscores/";
    private ArrayList<String> lines;
    private LinkedHashMap<String, String> scores;
    
    public Leaderboard(TextLoader textloader) {
        this.readFromFile(textloader.getCurrentFileName());
    }

    public void readFromFile(String fileName) {
        String filePath = this.path+"highscores_"+fileName;
        File file = new File(filePath);
        try {
            if (!file.createNewFile()) {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                this.lines = new ArrayList<String>(); // clears current lines
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();

                this.toHashMap();
            }
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    public void writeToFile(String name, String time) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(name+"\n"+time+"\n");
            for (String entry : scores.keySet()) {
                writer.write(entry+"\n"+scores.get(entry)+"\n");
            }
            writer.close();
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    private void toHashMap() {
        scores = new LinkedHashMap<String, String>();
        for (int i=0; i < lines.size(); i += 2) {
            scores.put(lines.get(i), lines.get(i+1));
        }
    }

    public String toString() {
        String outString = "";
        for (String entry : scores.keySet()) {
            outString += entry+"\n"+scores.get(entry)+"\n";
        }
        return outString;
    }
}