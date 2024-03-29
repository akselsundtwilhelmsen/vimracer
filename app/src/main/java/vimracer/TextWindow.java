package vimracer;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TextWindow {
    protected ArrayList<String> lines;

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

    public ArrayList<String> getArray() {
        return lines;
    }

    public String toString() {
        return lines.stream().collect(Collectors.joining("\n"));
    }

    public String toString(int maxLineLength, int[] highlightPos) { // deprecated, only used to the solution window
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

    public String toString(int maxLineLength) { 
        return toString(maxLineLength, null);
    }
}