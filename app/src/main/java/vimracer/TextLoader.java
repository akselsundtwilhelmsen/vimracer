package vimracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

public class TextLoader {
    private ArrayList<String> lines;
    private ArrayList<String> newLines;
    String promptPath = "src/main/resources/prompts/";
    private ArrayList<String> fileNameArray = new ArrayList<>();

    public TextLoader() {
        lines = new ArrayList<>();
        newLines = new ArrayList<>();
        this.listFiles(promptPath);
        this.readFromFile("p2.txt"); // temporary
        System.out.println(fileNameArray);
    }

    public void readFromFile(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(promptPath+fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
        }
        catch (IOException error) {
            System.out.println("error");
            error.printStackTrace();
        }
    }

    public void listFiles(String promptPath) {
        // fileNameArray = this.listFiles(promptPath);
        // System.out.println(fileNameArray);

        // ArrayList<String> fileNameArray = new ArrayList<String>();
        File directory = new File(promptPath);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                this.fileNameArray.add(files[i].getName());
            }
        }
    }

    public void randomFileNameArrayOrder() {

    }

    public void nextFile() {
        System.out.println("next file");
    }

    public void prevFile() {
        System.out.println("previous file");
    }

    public void garbleByWord(int intensityPercentage) {
        //intensityPercentage is the percentage of words affected by the garbler
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
                    String alteredWord = ""; // maybe change = ""; to ;
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

    private void setDefaultText() {
        lines = new ArrayList<>();
        lines.add("The quick brown fox jumped over the lazy dog.");
    }


    public ArrayList<String> getText() {
        this.garbleByWord(100);
        return this.newLines;
    }
}