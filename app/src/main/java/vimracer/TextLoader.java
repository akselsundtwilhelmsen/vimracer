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
    private int currentIndex;
    private String promptPath = "src/main/resources/prompts/";
    private ArrayList<String> lines;
    private ArrayList<String> newLines;
    private ArrayList<String> fileNameArray = new ArrayList<>();

    public TextLoader() {
        setPath(); // TODO: proper fix
        this.lines = new ArrayList<>();
        this.newLines = new ArrayList<>();
        this.currentIndex = 0;
        listFiles(promptPath); // get files
        Collections.shuffle(fileNameArray); // randomize file name order
        readFromFile();
    }

    private void setPath() { // cheesy fix, TODO: fix properly
        File directory = new File(promptPath);
        if (!directory.exists()) {
            promptPath = "app/src/main/resources/prompts/";
        }
    }

    public void readFromFile() {
        if (fileNameArray.size() > 0) {
            String fileName = fileNameArray.get(currentIndex);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(promptPath+fileName));
                lines = new ArrayList<String>(); // clears current lines
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
        File[] files = directory.listFiles();
        System.out.println(directory); // TODO: fjern
        System.out.println(directory.exists());
        System.out.println(directory.isDirectory());
        System.out.println(directory.listFiles());
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    fileNameArray.add(files[i].getName());
                }
            }
        }
    }

    public void nextFile() {
        if (currentIndex < fileNameArray.size()-1) {
            currentIndex++;
        }
        else {
            currentIndex = 0;
        }
        readFromFile();
    }

    public void prevFile() {
        if (currentIndex > 0) {
            currentIndex--;
        }
        else {
            currentIndex = fileNameArray.size()-1;
        }
        readFromFile();
    }

    public String getCurrentFileName() {
        return fileNameArray.get(currentIndex);
    }

    public void garbleByWord(int intensityPercentage) {
        //intensityPercentage is the percentage of words affected by the garbler
        newLines = new ArrayList<>();
        for (String line : lines) {
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
                            alteredWord = delete(word);
                            break;
                        case 1:
                            alteredWord = insert(word);
                            break;
                        case 2:
                            alteredWord = change(word);
                            break;
                        case 3:
                            alteredWord = capitalize(word);
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
        return lines;
    }

    public ArrayList<String> getGarbledText() {
        garbleByWord(100);
        return newLines;
    }

    public boolean compareToSolution() { // might require a faster implementation
        int loopLength = Math.min(lines.size(), newLines.size());
        for (int i=0; i < loopLength; i++) {
            // System.out.println(lines.get(i)+";");
            // System.out.println(newLines.get(i).trim()+";");
            // System.out.println(lines.get(i).equals(newLines.get(i)));
            // System.out.println("---------------------------");
            if (!lines.get(i).trim().equals(newLines.get(i).trim())) {
                return false;
            }
        }
        return true;
    }
}