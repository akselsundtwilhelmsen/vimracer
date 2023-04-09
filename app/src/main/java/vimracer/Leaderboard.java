package vimracer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Leaderboard {

    private final String path = "src/main/resources/prompts/highscores/";
    private ArrayList<String> lines; // file lines
    private ArrayList<String[]> scores;
    private int currentSortingIndex = 2;
    
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

                this.fileToArray();
            }
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    public void writeToFile(String name, String keypress, String time) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(name+","+keypress+","+time);
            for (String[] line : scores) {
                writer.write(line[0]+","+line[1]+","+line[2]);
            }
            writer.close();
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    private void fileToArray() {
        scores = new ArrayList<String[]>();
        for (int i=0; i < lines.size(); i++) {
            // scores.put(lines.get(i), lines.get(i+1));
            scores.add(lines.get(i).split(","));
        }
        this.sortByIndex(2); // starts with being sorted by time
    }

    public void nextSort() {
        if (currentSortingIndex == 1) {
            this.sortByIndex(2);
            currentSortingIndex = 2;
        }
        else {
            this.sortByIndex(1);
            currentSortingIndex = 1;
        }
    }

    private void sortByIndex(int index) { // insertion sort
        int i = 1;
        int j;
        while (i < scores.size()) {
            j = i;
            while ((j > 0) && (Integer.parseInt(scores.get(j)[index]) < Integer.parseInt(scores.get(j-1)[index]))) {
                scores.add(j, scores.remove(j-1)); // swap place in scores
                j -= 1;
            }
            i += 1;
        }
    }

    public String toString() { //TODO: option to sort by keypresses or time
        String outString = "";
        for (String[] line: scores) {
            outString += line[0]+"\n⌨️"+line[1]+"\n🕒"+line[2]+"\n\n";
        }
        return outString;
    }
}