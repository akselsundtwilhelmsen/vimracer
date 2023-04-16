package vimracer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Leaderboard {

    private String path = "app/src/main/resources/prompts/highscores/";
    private ArrayList<String> lines; // file lines
    private ArrayList<String[]> scores;
    private TextLoader textLoader;
    private int currentSortingIndex = 2;
    
    public Leaderboard(TextLoader textloader) {
        this.textLoader = textloader;
        readFromFile(textloader.getCurrentFileName());
    }

    public void readFromFile(String fileName) {
        String filePath = path+"highscores_"+fileName;
        File file = new File(filePath);
        try {
            if (!file.createNewFile()) { // creates new file if it doesn't exist
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
        if (name.equals("")) return; // shouldn't write with no name

        String fileName = textLoader.getCurrentFileName();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path+"highscores_"+fileName));
            String[] oldLine = {"", "", ""};
            boolean alreadyWritten = false;
            for (String[] line : scores) {
                if (name.trim().equals(line[0].trim())) { // check if name is already on the leaderboard
                    alreadyWritten = true;
                    oldLine = line;
                }
                else {
                    writer.write(line[0]+","+line[1]+","+line[2]+"\n");
                }
            }

            if (alreadyWritten) {
                if (Integer.valueOf(keypress) < Integer.valueOf(oldLine[1]) || Integer.valueOf(time) < Integer.valueOf(oldLine[2])) {
                    writer.write(name+","+keypress+","+time+"\n");
                    String[] entry = {name, keypress, time};
                    scores.remove(oldLine);
                    scores.add(entry);
                }
            }
            else {
                writer.write(name+","+keypress+","+time+"\n");
                String[] entry = {name, keypress, time};
                scores.add(entry);
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

    public void sort(boolean next) {
        if (next) {
            if (currentSortingIndex == 1) {
                currentSortingIndex = 2;
            }
            else {
                currentSortingIndex = 1;
            }
        }
        sortByIndex(currentSortingIndex);
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
        if (scores != null) {
            for (String[] line: scores) {
                outString += line[0]+"\n"+line[1]+"\n"+formatTime(line[2])+"\n\n";
                counter += 1;
                if (counter >= 8) {
                    break;
                }
            }
        }
        return outString;
    }

    private String formatTime(String time) {
        long elapsedTime = Long.valueOf(time);
        long milliSec = elapsedTime % 1000;
        long sec = (elapsedTime / 1000) % 60;
        long min = (elapsedTime / 1000) / 60;
        return String.format("%02d:%02d:%03d", min, sec, milliSec);
    }
}