package vimracer;

import java.util.ArrayList;
import java.util.Random;

public class TextLoader {
    private ArrayList<String> lines;
    private ArrayList<String> newLines;

    public TextLoader() {
        lines = new ArrayList<>();
        newLines = new ArrayList<>();
        this.setDefaultText();
    }

    public void garbleByWord(int intensityPercentage) {
        //intensityPercentage is the percentage of words affected by the garbler
        int lineNumber = 0;
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
        lines.add("Mary had a little lamb, its fleece was white as snow.");
        lines.add("The rain in Spain falls mainly on the plain.");
        lines.add("To be or not to be, that is the question.");
        lines.add("Ask not what your country can do for you, ask what you can do for your country.");
        lines.add("I have a dream that one day this nation will rise up and live out the true meaning of its creed: We hold these truths to be self-evident, that all men are created equal.");
        lines.add("It is a truth universally acknowledged, that a single man in possession of a good fortune, must be in want of a wife.");
        lines.add("In the beginning God created the heavens and the earthlineNumber.");
        lines.add("The quick brown fox jumped over the lazy dog.");
        lines.add("The quick brown fox jumped over the lazy dog.");
        lines.add("The quick brown fox jumped over the lazy dog.");
        lines.add("The quick brown fox jumped over the lazy dog.");
        lines.add("The quick brown fox jumped over the lazy dog.");
        lines.add("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789ferdig");
    }


    public ArrayList<String> getText() {
        this.garbleByWord(100);
        return this.newLines;
    }
}