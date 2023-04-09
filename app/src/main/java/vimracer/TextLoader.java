package vimracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Math;

public class TextLoader {
    private ArrayList<String> lines;
    private ArrayList<String> newLines;
    private final String promptPath = "src/main/resources/prompts/";
    private ArrayList<String> fileNameArray = new ArrayList<>();
    private int currentIndex;

    public TextLoader() {
        this.lines = new ArrayList<>();
        this.newLines = new ArrayList<>();
        this.currentIndex = 0;
        this.listFiles(promptPath); // get files
        Collections.shuffle(this.fileNameArray); // randomize file name order
        this.readFromFile();
    }

    public void readFromFile() {
        if (fileNameArray.size() > 0) {
            String fileName = this.fileNameArray.get(this.currentIndex);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(promptPath+fileName));
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
    }

    public void listFiles(String promptPath) {
        File directory = new File(promptPath);
        System.out.println(directory);
        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    this.fileNameArray.add(files[i].getName());
                }
            }
        }
    }

    public void nextFile() {
        if (currentIndex < this.fileNameArray.size()-1) {
            currentIndex++;
        }
        else {
            currentIndex = 0;
        }
        this.readFromFile();
    }

    public void prevFile() {
        if (currentIndex > 0) {
            currentIndex--;
        }
        else {
            currentIndex = this.fileNameArray.size()-1;
        }
        this.readFromFile();
    }

    public String getCurrentFileName() {
        return this.fileNameArray.get(currentIndex);
    }

    public void garbleByWord(int intensityPercentage) {
        //intensityPercentage is the percentage of words affected by the garbler
        newLines = new ArrayList<>();
        for (String line : this.lines) {
            String[] wordArray = line.split(" ");
            String newLine = "";

            for (String word : wordArray) {
                if (word.equals("")) {
                    continue;
                }
                Random random = new Random();
                int randint = random.nextInt(101 - intensityPercentage)+1;
                if (randint == 1) {
                    String alteredWord = "";
                    switch (random.nextInt(4)) {
                        case 0:
                            alteredWord = this.delete(word);
                            break;
                        case 1:
                            alteredWord = this.insert(word);
                            break;
                        case 2:
                            alteredWord = this.change(word);
                            break;
                        case 3:
                            alteredWord = this.capitalize(word);
                            break;
                    }
                    newLine += alteredWord + " ";
                }
                else {
                    newLine += word + " ";
                }
            }
            newLine = newLine.replaceAll("  ", " "); //avoid double spaces
            newLines.add(newLine);
        }
    }

    public String delete(String word) {
        return "";
    }

    public String insert(String word) {
        return "inserted" + " " + word;
    }

    public String change(String word) {
        return "changed";
    }

    public String capitalize(String word) {
        return word.toUpperCase();
    }

    public ArrayList<String> getText() {
        return this.lines;
    }

    public ArrayList<String> getGarbledText() {
        this.garbleByWord(100);
        return this.newLines;
    }

    public boolean compareToSolution() { // might require a faster implementation
        int loopLength = Math.min(lines.size(), newLines.size());
        for (int i=0; i < loopLength; i++) {
            System.out.println(lines.get(i));
            System.out.println(newLines.get(i));
            if (!lines.get(i).trim().equals(newLines.get(i).trim())) {return false;}
        }
        return true;
    }
}