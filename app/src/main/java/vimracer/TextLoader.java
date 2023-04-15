package vimracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class TextLoader {
    private int currentIndex;
    private String promptPath = "app/src/main/resources/prompts/";
    private String scramblePath = promptPath + "scrambles/";
    private ArrayList<String> lines;
    private ArrayList<String> newLines;
    private ArrayList<String> fileNameArray = new ArrayList<>();

    public TextLoader() {
        this.lines = new ArrayList<>();
        this.newLines = new ArrayList<>();
        this.currentIndex = 0;
        listFiles(promptPath); // get files
        Collections.shuffle(fileNameArray); // randomize file name order
        readPrompt();
        readScramble();
    }

    public void readPrompt() {
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

    public void readScramble() {
        String filePath = scramblePath+"scramble_"+fileNameArray.get(currentIndex);
        File file = new File(filePath);
        try {
            if (!file.createNewFile()) { // creates new file if it doesn't exist
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                newLines = new ArrayList<String>(); // clears current lines
                String line;
                while ((line = reader.readLine()) != null) {
                    newLines.add(line);
                }
                reader.close();
            }
            else { // create a new scramble if one doesn't exist
                scrambleCurrentPrompt();
            }
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    public void scrambleCurrentPrompt() {
        scrambleByWord(30);
        scrambleByLine(30);
        writeScramble();
    }

    private void writeScramble() {
        String fileName = getCurrentFileName();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(scramblePath+"scramble_"+fileName));
            for (String line : newLines) {
                    writer.write(line+"\n");
            }
            writer.close();
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    private void listFiles(String promptPath) {
        File directory = new File(promptPath);
        File[] files = directory.listFiles();
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
        readPrompt();
        readScramble();
    }

    public void prevFile() {
        if (currentIndex > 0) {
            currentIndex--;
        }
        else {
            currentIndex = fileNameArray.size()-1;
        }
        readPrompt();
        readScramble();
    }

    public String getCurrentFileName() {
        return fileNameArray.get(currentIndex);
    }

    private void scrambleByWord(int impactPercentage) {
        //impactPercentage is the percentage of words affected by the scrambler
        newLines = new ArrayList<>();
        for (String line : lines) {
            String[] wordArray = line.split(" ");
            String newLine = "";

            for (String word : wordArray) {
                if (word.equals("")) {
                    continue;
                }
                if (impactCalculator(impactPercentage)) {
                    String alteredWord = "";
                    Random random = new Random();
                    switch (random.nextInt(4)) {
                        case 0:
                            alteredWord = delete(word);
                            break;
                        case 1:
                            alteredWord = insert(word);
                            break;
                        case 2:
                            alteredWord = changeLetters(word, 10);
                            break;
                        case 3:
                            alteredWord = removeLetters(word, 10);
                            break;
                    }
                    newLine += alteredWord + " ";
                }
                else {
                    newLine += word + " ";
                }
            }
            newLine = newLine.replaceAll("  ", " "); // avoids double spaces
            newLine = newLine.trim();
            newLines.add(newLine);
        }
    }

    private void scrambleByLine(int impactPercentage) {
        for (int i=0; i < lines.size(); i++) {
            if (impactCalculator(impactPercentage)) {
                Random random = new Random();
                switch (random.nextInt(2)) {
                    case 0:
                        swapLines(i);
                        break;
                    case 1:
                        addLine(i+1);
                        break;
                }
            }
        }
    }

    private void swapLines(int index) {
        if (index > 0) {
            newLines.add(index, newLines.remove(index-1)); // swap place in scores
        }
    }

    private void addLine(int index) {
        newLines.add(index, "");
    }

    private String delete(String word) {
        return "";
    }

    private String insert(String word) {
        // return "inserted" + " " + word;
        return word;
    }

    private String changeLetters(String word, int impactPercentage) {
        String possibleCharacters = "qwertyuiopasdfghjklzxcvbnm";
        String outString = "";
        for (char letter : word.toCharArray()) {
            Random random = new Random();
            if (impactCalculator(impactPercentage)) {
                outString += possibleCharacters.charAt(random.nextInt(possibleCharacters.length()));
            }
            else {
                outString += letter;
            }
        }
        return outString;
    }

    private String removeLetters(String word, int impactPercentage) {
        String outString = "";
        Random random = new Random();
        for (int i = 0; i < word.length(); i++) {
            if (impactCalculator(impactPercentage)) {
                outString += word.substring(i, i+1);
            }
        }
        return outString;
    }

    private boolean impactCalculator(int impactPercentage) {
        Random random = new Random();
        if (impactPercentage <= 0 || impactPercentage > 100) return false;
        return random.nextInt(100/impactPercentage) == 0;
    }

    public ArrayList<String> getText() {
        return lines;
    }

    public ArrayList<String> getScrambledText() {
        readScramble();
        return newLines;
    }

    // public boolean compareToSolution() { // might require a faster implementation
    //     int loopLength = Math.min(lines.size(), newLines.size());
    //     for (int i=0; i < loopLength; i++) {
    //         if (!lines.get(i).trim().equals(newLines.get(i).trim())) {
    //             return false;
    //         }
    //     }
    //     return true;
    // }

    public static void main(String[] args) {
        TextLoader t = new TextLoader();
        t.scrambleCurrentPrompt();
        System.out.println(t.getScrambledText());
    }
}