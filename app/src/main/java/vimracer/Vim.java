package vimracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;
import java.util.stream.Collectors;

import javafx.scene.input.KeyEvent;

public class Vim extends TextWindow {
    private int[] cursor;
    private char mode; // must be n(ormal), v(isual), or i(nsert);
    private boolean shiftHeld;

    private VimCommandList commands; //inneholder kommandoer (String), tall, og bevegelser (int[])

    public Vim() {
        super();
        this.cursor = new int[2];
        Arrays.fill(cursor,0);
        this.mode = 'i';
        this.shiftHeld = false;

        this.commands = new VimCommandList(this);
    }


    // Plan for normal (og viusal) mode
    // få KeyEvent input
    // fortløpende til en array med lett-håndterbare kommandoer
    // når denne listen er utførbar vil den bli utført

    public void keyPress(KeyEvent event) {
        String keyString = event.getCode().toString();

        // behandler input, TODO: bør kanskje finne ut om dette kan gjøres automatisk
        if (keyString.equals("SHIFT")) {
            shiftHeld = true;
            return;
        }

        if (keyString.equals("ESCAPE")) {
            mode = 'n';
            if (!validCursorPos(cursor)) cursor[0]--;
            return;
        }

        if (keyString.startsWith("DIGIT"))  {
            keyString = keyString.substring(5);
        }

        if (!shiftHeld && keyString.length() == 1) {
            keyString = keyString.toLowerCase();
        }

        
        if (mode == 'i') {
            if (keyString.equals("1")) keyString = " "; //TODO: get regular enter and space to work and remove these
            if (keyString.equals("2")) keyString = "ENTER";

            if (keyString == "BACK_SPACE") {
                backspace();
            } else if (keyString == "ENTER") { //does not work because enter presses the button
                insertLine(cursor[1]+1);
                cursor[1]++;
                cursor[0] = 0;
            } else {
                insertString(keyString);
            }
            return;
        }

        commands.buildCommandList(keyString);

        if (commands.isCommandListExecutable()) {
            executeCommandList();
        }
    }

    //TODO: the command list can fill over multiple keypresses (e.g. d-i-w)
    //TODO: it must dedect if it's executable (execute and clear), start of a legal command (continue), or illegal command (clear)
    //temporary soulution is to just execute and clear it
    private void executeCommandList() { 
        int index = 0;
        int[] movement;
        Object command;
        while (commands.hasNext()) {
            command = commands.next();
            if (command instanceof int[]) {
                cursor = (int[]) command;
                continue;
            }
            switch ((String) command) {
                case "insert":
                    setMode('i');
                    break;
                case "insertLine":
                    insertLine(cursor[1]);
                    cursor[0] = 0;
                    break;
                case "deleteMotion":
                    movement = (int[]) commands.get(index+1);
                    removeBetween(cursor, movement);
                    if (! smallerPosition(cursor, movement)) commands.remove(index + 1);
                    break;
                case "joinLines":
                    cursor[0] = lines.get(cursor[1]).length();
                    joinLines(cursor[1], cursor[1]+1);
                    break;
                case "change":
                    movement = (int[]) commands.get(index+1);
                    removeBetween(cursor, movement);
                    if (! smallerPosition(cursor, movement)) commands.remove(index + 1);
                    setMode('i');
                    break;                                                                                                            
            }
        }
        commands.clear();
        System.out.format("\nPosition: %d %d",cursor[0],cursor[1]);
    }

    public void keyRelease(KeyEvent event) {
        if (event.getCode().toString() == "SHIFT") {
            shiftHeld = false;
            return;
        }
    }

    private void insertString(String s) {
        lines.set(cursor[1], lines.get(cursor[1]).substring(0,cursor[0]) + s + lines.get(cursor[1]).substring(cursor[0]));
        cursor[0] += s.length();
    }

    private void removeBetween(int[] pos1, int[] pos2) {
        if (smallerPosition(pos1, pos2)) {
            int[] temp = pos1;
            pos1 = pos2;
            pos2 = temp;
        }

        //fjerner mellom posisjoner på samme linje on the same line
        if (pos1[1] == pos2[1]) {
            lines.set(pos1[1], lines.get(pos1[1]).substring(0,pos1[0]) + lines.get(pos1[1]).substring(pos2[0]));
            return;
        }

        //Remove all lines between and including pos1 and pos2
        for (int i = 0; i < pos2[1]-pos1[1]+1; i++) {
            removeLine(pos1[1]);
        }
    }

    private void removeUnderCursor() {
        if (lines.get(cursor[1]).length() == 0) return;
        lines.set(cursor[1], lines.get(cursor[1]).substring(0,cursor[0]) + lines.get(cursor[1]).substring(cursor[0]+1));
        if (! validCursorPos(cursor)) { 
            cursor[0]--; 
        }
    }

    private void backspace() {
        if (cursor[0] == 0) return;
        cursor[0]--;
        removeUnderCursor();
    }

    private void insertLine(int lineNumber) {
        if (0 > lineNumber || lineNumber > lines.size()) throw new IllegalArgumentException();
        if (lineNumber == lines.size()) lines.add("");
        lines.add(lineNumber, "");
    }

    private void removeLine(int lineNumber) {
        if (0 > lineNumber || lineNumber > lines.size()-1) throw new IllegalArgumentException();
        lines.remove(lineNumber);
    }

    private void joinLines(int lineNumber1, int lineNumber2) {
        if (lineNumber1 >= lines.size() && lineNumber2 >= lines.size()) throw new IllegalArgumentException();
        lines.set(lineNumber1, lines.get(lineNumber1) + lines.get(lineNumber2));
        removeLine(lineNumber2);
    }

    public void setMode(char mode) {
        if ("ni".indexOf(mode) == -1) throw new IllegalArgumentException();
        this.mode = mode;
    }

    public int[] getCursor() {
        return cursor.clone();
    }

    private boolean validCursorPos(int[] cursor) {
        if (cursor.length != 2) return false;
        if (0 > cursor[1] || cursor[1] >= lines.size()) return false;
        int currentLength = lines.get(cursor[1]).length();
        if (cursor[0] < 0) return false;
        // if (mode == 'n' && currentLength != 0 && currentLength <= cursor[0]) return false;
        // if (currentLength < cursor[0]) return false;
        return true;
    }

    private boolean smallerPosition(int[] smaller, int[] bigger) {
        return (smaller[1] > bigger[1] || (smaller[1] == bigger[1] && smaller[0] > bigger[0]));
    }

    public int[] nextInstanceOf(Pattern regex, int[] from, boolean endOfRegex) {
        int[] newPos = from.clone();
        String string = lines.get(from[1]).substring(from[0]+1);
        Matcher matcher = regex.matcher(string);
        if (matcher.find()) {
            newPos[0] = (lines.get(from[1]).length() - string.length());
            if (endOfRegex) {
                newPos[0] += matcher.end()-1;
            } else {
                newPos[0] += matcher.start();
            }
        } else if (lines.size() > from[1]+2) {
            from[0] = 0;
            from[1]++;
            return nextInstanceOf(regex, from, endOfRegex); //this might be very inefficient (creates new variables each recurtion)
        }
        return newPos;
    }

    public int[] prevInstanceOf(Pattern regex, int[] from, boolean endOfRegex) {
        int[] newPos = from.clone();
        String string = lines.get(from[1]).substring(0,from[0]);
        Matcher matcher = regex.matcher(string);
        boolean found = false;
        while (matcher.find()) {
            found = true;
            if (endOfRegex) {
                newPos[0] = matcher.end()-1;
            } else {
                newPos[0] = matcher.start();
            }
            System.out.format(", next pattern: \"%s\", starts at %d",matcher.group(0),matcher.start());
        }
        if (!found && from[1] > 1) {
            from[1]--;
            from[0] = lines.get(from[1]).length();
            return prevInstanceOf(regex, from, endOfRegex); //this might be very inefficient (creates new variables each recurtion)
        }
        return newPos;
    }

    public int[] nextInstanceOfMultiline(Pattern regex, int[] from, boolean endOfRegex) {
        int[] newPos = from.clone();
        //convert String-array to string whith linebreaks starting from from
        String restOfText = lines.get(from[1]).substring(from[0]+1) + "\n";
        restOfText += lines.stream().skip(from[1]+1).collect(Collectors.joining("\n")); 
        //find match of regex
        Matcher matcher = regex.matcher(restOfText);
        if (matcher.find()) {
            String untilRegex;
            if (endOfRegex) {
                untilRegex = restOfText.substring(matcher.end()-1);
                newPos[0] += matcher.end()-1;
            } else {
                untilRegex = restOfText.substring(matcher.start());
                newPos[0] += matcher.start();
            }

            //find coordinate in String-array
            int prevLinesSize = 0;
            Matcher newLine = Pattern.compile("\\n").matcher(untilRegex);
            while (newLine.find()) {
                prevLinesSize += lines.get(newPos[1]).length();
                newPos[1]++;
            }
            newPos[0] -= prevLinesSize;
        }
        return newPos;
    }

    public static void main(String[] args) {
        Vim vim = new Vim();
        ArrayList<String> vimtext = new ArrayList<>();
        vimtext.add("first line of many");
        vimtext.add("Second line of some");
        vimtext.add("third line by far");
        vimtext.add("fourth line gone");
        vimtext.add("fifth and final line");
        vim.setText(vimtext);
        // String string = vimtext.get(0).substring(6) + "\n";
        // string += vimtext.stream().skip(1).collect(Collectors.joining("\n"));
        System.out.println(vim.toString());
        int[] newPos = vim.nextInstanceOf(Pattern.compile("go"), vim.getCursor(), false);
        System.out.format("%d %d\n", newPos[0], newPos[1]);
        // System.out.println(string);
    }
}