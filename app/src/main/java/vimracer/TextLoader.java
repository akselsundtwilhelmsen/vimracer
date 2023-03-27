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
        // newLines.addAll(this.lines);
    }

    public void garbleByWord(int intensityPercentage) {
        //intensityPercentage is the percentage of words affected by the garbler
        // int wordStartIndex = 0;
        int lineNumber = 0;
        for (String line : this.lines) {
            // wordStartIndex = 0;
            String[] wordArray = line.split(" ");
            String newLine = "";

            for (String word : wordArray) {
                if (word.equals("")) {
                    continue;
                }
                Random random = new Random();
                int randint = random.nextInt(101 - intensityPercentage)+1;
                // int[] cursor = {wordStartIndex, lineNumber};
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
                // wordStartIndex += word.length() + 1; // increase by wordlength + space
            }
            // lineNumber += 1;
            newLine = newLine.replaceAll("  ", " ");
            newLines.add(newLine);
        }
    }

    public String delete(String word) {
        // String oldLine = this.lines.get(cursor[1]);
        // String newLine = oldLine.substring(0, cursor[0]) + oldLine.substring(cursor[0]+wordlength);
        // newLine = newLine.replaceAll("  ", " "); // removes double spaces
        // this.newLines.set(cursor[1], newLine);
        return "";
    }

    public String insert(String word) {
        // String insert = "INSERTEDWORD"; // TODO: change, generate word
        // String oldLine = this.lines.get(cursor[1]);
        // String newLine = oldLine.substring(0, cursor[0]) + insert + " " + oldLine.substring(cursor[0]);
        // this.newLines.set(cursor[1], newLine);
        return "inserted" + " " + word;
    }

    public String change(String word) {
        // String change = "CHANGEDWORD"; // TODO: change, generate word
        // String oldLine = this.lines.get(cursor[1]);
        // String newLine = oldLine.substring(0, cursor[0]) + change + " " + oldLine.substring(cursor[0]+change.length());
        // this.newLines.set(cursor[1], newLine);
        return "changed";
    }

    public String capitalize(String word) {
        // String oldLine = this.lines.get(cursor[1]);
        // String word = oldLine.substring(cursor[0], cursor[0]+wordlength);
        // word = word.toUpperCase();
        // String newLine = oldLine.substring(0, cursor[0]) + word + " " + oldLine.substring(cursor[0]+word.length());
        // this.newLines.set(cursor[1], newLine);
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