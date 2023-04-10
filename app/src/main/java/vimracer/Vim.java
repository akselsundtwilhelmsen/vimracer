package vimracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;
import java.util.stream.Collectors;

import javafx.scene.input.KeyEvent;

// TODO: må: kjørende program, pekervalidering, fungerend removeBetween(), bedre kommandovalidering
// TODO: bør: Text-objects (dip, ciw, di[ osv), yank og put, flere bevegelser (lett nå med bra regex))

// TODO utenom prog, tester, dokumentasjon

public class Vim extends TextWindow {
    private int[] cursor;
    private char mode; // must be n(ormal), v(isual), or i(nsert) ((visual) l(ine), (visual) b(lock), or r(eplace)?)
    private boolean shiftHeld;

    private VimCommandList commands; 

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
                insertString(keyString,cursor);
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
                case "addIndent":
                    movement = (int[]) commands.get(index+1);
                    int start = cursor[1];
                    int end = movement[1];
                    if (start < end) {
                        int temp = start;
                        start = end;
                        end = temp;
                    }

                    for (int i = start; i <= end; i++) {
                        addIndent(i);
                    }
                    break;
                case "removeIndent":
                    movement = (int[]) commands.get(index+1);
                    // int start = cursor[1];
                    // int end = movement[1];
                    // if (start < end) {
                    //     int temp = start;
                    //     start = end;
                    //     end = temp;
                    // }

                    // for (int i = start; i <= end; i++) {
                    //     removeIndent(i);
                    // }
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

    private void insertString(String s, int[] pos) {
        lines.set(pos[1], lines.get(pos[1]).substring(0,pos[0]) + s + lines.get(pos[1]).substring(pos[0]));
        pos[0] += s.length();
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

    private void addIndent(int lineNumber) {
        // insertString("    ", (int[]) Arrays.asList(0,lineNumber));
    }

    private void removeIndent(int lineNumber) {
        if (lines.get(lineNumber).startsWith("    ")) {
            // removeBetween(cursor, cursor);
        } else if (lines.get(lineNumber).startsWith("\t")) {
            //remove
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
        //convert String-array to string whith linebreaks starting from from
        String restOfText = lines.get(from[1]).substring(from[0]+1);
        restOfText = restOfText + "\n" + lines.stream().skip(from[1]+1).collect(Collectors.joining("\n")); 
        //find match of regex
        Matcher matcher = regex.matcher(restOfText);
        if (matcher.find()) {
            String untilRegex;
            if (endOfRegex) {
                untilRegex = restOfText.substring(0,matcher.end());
                newPos[0] += matcher.end();
            } else {
                untilRegex = restOfText.substring(0,matcher.start()+1);
                newPos[0] += matcher.start()+1;
            }

            //find coordinate in String-array
            Matcher newLine = Pattern.compile("\\n").matcher(untilRegex);
            while (newLine.find()) {
                newPos[0] -= lines.get(newPos[1]).length() + 1;
                newPos[1]++;
            }
        }
        return newPos;
    }

    public int[] prevInstanceOf(Pattern regex, int[] from, boolean endOfRegex) {
        int[] newPos = from.clone();
        //convert String-array to strin.g whith linebreaks starting from from
        String untilPosText = lines.get(from[1]).substring(0,from[0]);
        untilPosText = lines.stream().limit(from[1]).collect(Collectors.joining("\n")) + "\n" + untilPosText; 
        //find match of regex
        Matcher matcher = regex.matcher(untilPosText);
        boolean found = false;
        int regexIndex = 0;
        while (matcher.find()) {
            found = true;
            if (endOfRegex) {
                regexIndex = matcher.end()-1;
            } else {
                regexIndex = matcher.start();
            }
        }

        //find coordinate in String-array
        if (found) {
            String regexToPos = untilPosText.substring(regexIndex);
            newPos[0] -= regexToPos.length();

            Matcher newLine = Pattern.compile("\\n").matcher(regexToPos);
            while (newLine.find()) {
                newPos[1]--;
                newPos[0] += lines.get(newPos[1]).length() + 1;
            }
        }
        return newPos;
    }

    @Override
    public String toString(int lineLength) {
        return toString(lineLength, cursor);
    }

    public static void main(String[] args) {
        Vim vim = new Vim();
        ArrayList<String> vimtext = new ArrayList<>();
        vimtext.add("null linjer");
        vimtext.add("en ener");
        vimtext.add("to");
        vimtext.add("tre");
        vimtext.add("");
        vimtext.add("fem");
        vimtext.add("seks");
        vim.setText(vimtext);
        int[] pos1 = {0,4};
        int[] pos2 = {5,0};
        vim.removeBetween(pos1, pos2);
        System.out.println(vim.toString(86));
    }
}