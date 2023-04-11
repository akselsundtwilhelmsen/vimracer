package vimracer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Leaderboard {

    private String path = "src/main/resources/prompts/highscores/";
    private ArrayList<String> lines; // file lines
    private ArrayList<String[]> scores;
    private TextLoader textLoader;
    private int currentSortingIndex = 2;
    
    public Leaderboard(TextLoader textloader) {
        setPath(); // TODO: proper fix
        this.textLoader = textloader;
        readFromFile(textloader.getCurrentFileName());
    }

    private void setPath() { // cheesy fix, TODO: fix properly
        File directory = new File(path);
        if (!directory.exists()) {
            path = "app/src/main/resources/prompts/highscores/";
        }
    }

    public void readFromFile(String fileName) {
        String filePath = path+"highscores_"+fileName;
        File file = new File(filePath);
        try {
            if (!file.createNewFile()) {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                lines = new ArrayList<String>(); // clears current lines
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();

                fileToArray();
            }
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    public void writeToFile(String name, String keypress, String time) {
        if (name.equals("")) { // can't write to an empty
            return;
        }
        String fileName = textLoader.getCurrentFileName();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path+"highscores_"+fileName));
            for (String[] line : scores) {
                if (name.trim().equals(line[0].trim())) { // check if name is already on the leaderboard
                    if (Integer.valueOf(keypress)> Integer.valueOf(line[1]) || Integer.valueOf(time) > Integer.valueOf(line[2])) {
                        writer.write(name+","+keypress+","+time+"\n");
                    }
                }
                else {
                    writer.write(line[0]+","+line[1]+","+line[2]+"\n");
                }
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
        sortByIndex(2); // starts with being sorted by time
    }

    public void nextSort() {
        if (currentSortingIndex == 1) {
            sortByIndex(2);
            currentSortingIndex = 2;
        }
        else {
            sortByIndex(1);
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

    public String toString() {
        String outString = "";
        int counter = 0; // to stop the loop before overflowing the textbox
        for (String[] line: scores) {
            outString += line[0]+"\n"+line[1]+"\n"+formatTime(line[2])+"\n\n";
            counter += 1;
            if (counter >= 8) {
                break;
            }
        }
        return outString;
    }

    public String formatTime(String time) {
        long elapsedTime = Long.valueOf(time);
        long milliSec = elapsedTime % 1000;
        long sec = (elapsedTime / 1000) % 60;
        long min = (elapsedTime / 1000) / 60;
        return String.format("%02d:%02d:%03d", min, sec, milliSec);
    }
}