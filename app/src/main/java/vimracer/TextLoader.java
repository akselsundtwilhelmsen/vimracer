package vimracer;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

public class TextLoader {
    private ArrayList<String> lines;

    public TextLoader() {
        lines = new ArrayList<>();
    }

    public void garbler(int intensityPercentage) {
        //intensityPercentage is the percentage of words affected by the garbler
        int wordStartIndex = 0;
        int lineNumber = 0;
        for (String line : this.lines) {
            String[] wordArray = line.split(" ");

            for (String word : wordArray) {
                Random random = new Random();
                int[] cursor = {wordStartIndex, lineNumber};
                if (random.nextInt(101 - intensityPercentage) == 1) {
                    switch (random.nextInt(4)) {
                        case 0:
                            this.delete(cursor, word.length());
                            break;
                        case 1:
                            this.insert(cursor, word.length());
                            break;
                        case 2:
                            this.change(cursor, word.length());
                            break;
                        case 3:
                            this.capitalize(cursor, word.length());
                            break;
                    }
                }
                wordStartIndex += word.length() + 1; // increase by wordlength + space
            }
            lineNumber += 1;
        }
    }

    public void delete(int[] cursor, int wordlength) {
    }

    public void insert(int[] cursor, int wordlength) {
    }

    public void change(int[] cursor, int wordlength) {
    }

    public void capitalize(int[] cursor, int wordlength) {
    }

    public static void main(String[] args) {
        try {
        File myObj = new File("exampletext.txt");
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            System.out.println(data);
        }
        myReader.close();
        } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
        }
    }

    private void setDefaultText() {
        lines = new ArrayList<>();
        lines.add("The quick brown fox jumped over the lazy dog.");
        lines.add("Mary had a little lamb, its fleece was white as snow.");
        lines.add("The rain in Spain falls mainly on the plain.");
        lines.add("To be or not to be, that is the question.");
        lines.add("Ask not what your country can do for you, ask what you can do for your country.");
        lines.add("I have a dream that one day this nation will rise up and live out the true meaning of its creed: We hold these truths to be self-evident, that all men are created equal.");
        lines.add("It is a truth universally acknowledged, that a single man in possession of a good fortune, must be in want of a wife.");
        lines.add("In the beginning God created the heavens and the earth.");
        lines.add("The quick brown fox jumped over the lazy dog.");
    }
}