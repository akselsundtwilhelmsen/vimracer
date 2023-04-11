package vimracer;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javafx.scene.shape.HLineTo;


public class TextWindow {
    ArrayList<String> lines;

    public TextWindow() {
        lines = new ArrayList<>();
        lines.add("");
    }

    public void setText(ArrayList<String> text) {
        this.lines = text;
    }

    public void setText(String text) {
        this.lines = new ArrayList<>();
        this.lines.add(text);
    }

    public String toString() { // without line numbers
        return lines.stream().collect(Collectors.joining("\n"));
    }

    public String toString(int maxLineLength, int[] highlightPos) { // with line numbers
        final int padding = 2;
        maxLineLength -= padding + 2; // to account for line number and padding

        String outString = "";
        int lineNumber = 0;

        for (String line : lines) {
            lineNumber++;

            int currentPadding = padding - ("" + lineNumber).length(); // amount of padding needed for the current line
            String paddingString = "";
            for (int i=0; i < currentPadding; i++) {
                paddingString += " ";
            }

            String paddingStringOverflow = " "; // amount of padding needed after line overflow
            for (int i=0; i < padding; i++) {
                paddingStringOverflow += " ";
            }

            if (highlightPos != null) { // TODO: gjør skikkelig
                if (highlightPos[1] == lineNumber-1) {
                    if (line == "") {
                        line = "█";
                    } else {
                        String newLine = line.substring(0, highlightPos[0]) + "█";
                        if (line.length() > highlightPos[0]+1) {
                            newLine = newLine + line.substring(highlightPos[0]+1);
                        }
                        line = newLine;
                    }
                }
            }

            if (line.length() < maxLineLength) {
                outString += paddingString + lineNumber + " " + line + "\n";
            }
            else {
                outString += paddingString + lineNumber + " " + line.substring(0, maxLineLength) + "\n";
                int counter = 1;
                while (true) {
                    counter += 1;
                    if (line.length() > maxLineLength*counter) {
                        outString += paddingStringOverflow + line.substring((counter-1)*maxLineLength, counter*maxLineLength) + "\n";
                    }
                    else {
                        outString += paddingStringOverflow + line.substring((counter-1)*maxLineLength) + "\n";
                        break;
                    }
                }
            }
        }
        return outString;
    }

    public ArrayList<String> toArray(int maxLineLength) { // with line numbers
        final int padding = 2;
        maxLineLength -= padding + 2; // to account for line number and padding

        ArrayList<String> outArray = new ArrayList<String>();
        int lineNumber = 0;

        for (String line : lines) {
            lineNumber++;

            int currentPadding = padding - ("" + lineNumber).length(); // amount of padding needed for the current line
            String paddingString = "";
            for (int i=0; i < currentPadding; i++) {
                paddingString += " ";
            }

            String paddingStringOverflow = " "; // amount of padding needed after line overflow
            for (int i=0; i < padding; i++) {
                paddingStringOverflow += " ";
            }

            if (line.length() < maxLineLength) {
                outArray.add(paddingString + lineNumber + " " + line + "\n");
            }
            else {
                outArray.add(paddingString + lineNumber + " " + line.substring(0, maxLineLength) + "\n");
                int counter = 1;
                while (true) {
                    counter += 1;
                    if (line.length() > maxLineLength*counter) {
                        outArray.add(paddingStringOverflow + line.substring((counter-1)*maxLineLength, counter*maxLineLength) + "\n");
                    }
                    else {
                        outArray.add(paddingStringOverflow + line.substring((counter-1)*maxLineLength) + "\n");
                        break;
                    }
                }
            }
        }
        System.out.println(outArray);
        return outArray;
    }

    public String toString(int maxLineLength) { 
        return toString(maxLineLength, null);
    }

    public boolean equals(TextWindow t) {
        return this.toString().equals(t.toString());
    }
}