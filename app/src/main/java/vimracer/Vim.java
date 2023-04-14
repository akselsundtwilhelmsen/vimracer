package vimracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;
import java.util.stream.Collectors;

import javafx.scene.input.KeyEvent;

// TODO: må:  bedre kommandovalidering, tall!
// TODO: bør: Text-objects (dip, ciw, di[ osv), yank og put, flere bevegelser (lett nå med bra regex))

// TODO utenom prog, tester, dokumentasjon


public class Vim extends TextWindow {
    private int[] cursor; //has [col, row, prefcol, linewise(1) or not(0)]
    private char mode; // must be n(ormal), v(isual), or i(nsert) ((visual) l(ine), (visual) b(lock), or r(eplace)?)
    private boolean shiftHeld;

    private String insertedString;
    private String yankedString;
    private boolean yankedLine;

    private VimCommandList commands; 

    public Vim() {
        super();
        this.cursor = new int[4]; 
        Arrays.fill(cursor,0);
        this.mode = 'n';
        this.shiftHeld = false;

        String insertedString = "";
        String yankedString = "";
        boolean yankedLine = false;

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
            cursor = forceValidPos(cursor);
            System.out.println(insertedString);
            return;
        }
        
        if (keyString.equals("SPACE")) keyString = " ";

        if (keyString.startsWith("DIGIT"))  {
            keyString = keyString.substring(5);
            if (keyString.equals("4")) keyString = "$"; //midlertidig
        }

        if (!shiftHeld && keyString.length() == 1) {
            keyString = keyString.toLowerCase();
        }

        
        if (mode == 'i') {
            if (keyString.equals("BACK_SPACE")) {
                backspace();
                if (insertedString.length() > 0) { //TODO: insertedstring bør også kunne være negativ (3 * \b foreks)
                    insertedString = insertedString.substring(0, insertedString.length()-1);
                }
            } else if (keyString.equals("ENTER")) {
                enter();
                insertedString = insertedString + "\n";
            } else {
                insertString(keyString,cursor);
                cursor[0] += keyString.length();
                insertedString = insertedString + keyString;
            }
            return;
        }

        commands.buildCommandList(keyString);

        // System.out.format(", %b", commands.isCommandListExecutable());
        if (commands.isCommandListExecutable()) {
            executeCommandList();
        }

        if (!commands.willCommandListExecutable()) {
            commands.clear();
        }
        System.out.format("\ncommandlist = " + commands.toString());
    }

    private void executeCommandList() { 
        int index = -1;
        int[] movement;
        for (Object command : commands) {
            index++;
            if (command instanceof Integer) continue;
            if (command instanceof int[]) {
                cursor = forceValidPos((int[]) command);
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
                    if (smallerPosition(cursor, movement)) {
                        commands.remove(index + 1);
                        cursor = forceValidPos(cursor);
                    }
                    break;
                case "joinLines":
                    cursor[0] = lines.get(cursor[1]).length();
                    joinLines(cursor[1], cursor[1]+1);
                    break;
                case "change":
                    movement = (int[]) commands.get(index+1);
                    removeBetween(cursor, movement);
                    if (smallerPosition(cursor, movement)) commands.remove(index + 1);
                    setMode('i');
                    break;                                                                                                            
                case "put":
                    if (yankedLine) {
                        break;
                    } else {
                        insertString(yankedString, cursor);
                    }
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
    }

    public void keyRelease(KeyEvent event) {
        if (event.getCode().toString() == "SHIFT") {
            shiftHeld = false;
            return;
        }
    }

    private void insertString(String string, int[] pos) {
        String[] lineArray = string.split("\n");
        String beforeInsert = lines.get(pos[1]).substring(0, pos[0]);
        String afterInsert = lines.get(pos[1]).substring(pos[0]);

        lines.set(pos[1], beforeInsert + lineArray[0]);

        int lastLine = pos[1] + lineArray.length - 1;
        
        for (int line=pos[1]; line < lastLine; line++) {
            insertLine(line);
            lines.set(line, lineArray[line-pos[1]]);
        }
        lines.set(lastLine, lines.get(lastLine) + afterInsert);
    }

    private void removeBetween(int[] pos1, int[] pos2) {
        boolean linewise = pos2[3] == 1;

        if (smallerPosition(pos2, pos1)) {
            int[] temp = pos1;
            pos1 = pos2;
            pos2 = temp;
        }

        yankedString = toStringBetween(pos1, pos2);
        yankedLine = linewise;

        if (linewise) {
            //Remove all lines between and including pos1 and pos2
            for (int i = pos1[1]; i <= pos2[1]; i++) {
                removeLine(pos1[1]);
            }
        } else {
            //remove line after pos1 and before pos2
            if (pos1[1] == pos2[1]) {
                lines.set(pos1[1], lines.get(pos1[1]).substring(0,pos1[0]) + lines.get(pos1[1]).substring(pos2[0]));
            } else {
                lines.set(pos1[1], lines.get(pos1[1]).substring(0, pos1[0]));
                lines.set(pos2[1], lines.get(pos2[1]).substring(pos2[0]));

                //Remove all lines between pos1 and pos2
                for (int i = pos1[1]+1; i < pos2[1]; i++) {
                    removeLine(pos1[1]);
                }
            }
        }
    }

    public String toStringBetween(int[] pos1, int[] pos2) {
        String outString = "";
        boolean linewise = pos2[3] == 1;

        if (smallerPosition(pos2, pos1)) {
            int[] temp = pos1;
            pos1 = pos2;
            pos2 = temp;
        }

        if (linewise) {
            outString = lines.stream()
                .limit(pos2[1]+1).skip(pos1[1])
                .collect(Collectors.joining("\n"));
        } else {
            if (pos1[1] == pos2[1]) {
                outString = lines.get(pos1[1]).substring(pos1[0], pos2[0]);
            } else {
                outString = lines.get(pos1[1]).substring(pos1[0]) + "\n";
                outString = outString + lines.stream()
                    .limit(pos2[1]).skip(pos1[1]+1)
                    .map(s -> s + "\n")
                    .collect(Collectors.joining(""));
                outString = outString + lines.get(pos2[1]).substring(0,pos2[0]);
            }
        }
        return outString;
    }

    private void removeUnderCursor() {
        if (lines.get(cursor[1]).length() == 0) return;
        lines.set(cursor[1], lines.get(cursor[1]).substring(0,cursor[0]) + lines.get(cursor[1]).substring(cursor[0]+1));
        cursor = forceValidPos(cursor);
    }

    private void backspace() {
        if (cursor[0] == 0) return;
        cursor[0]--;
        removeUnderCursor();
    }

    private void enter() {
        //add line below cursor, move text after cursor to the new line, move cursor to start of new line
        insertLine(cursor[1]+1);
        lines.set(cursor[1]+1, lines.get(cursor[1]).substring(cursor[0]));
        lines.set(cursor[1], lines.get(cursor[1]).substring(0,cursor[0]));
        cursor[1]++;
        cursor[0] = 0;
    }


    private void insertLine(int lineNumber) {
        if (0 > lineNumber || lineNumber > size()) throw new IllegalArgumentException();
        if (lineNumber == size()) lines.add("");
        lines.add(lineNumber, "");
    }

    private void removeLine(int lineNumber) {
        if (0 > lineNumber || lineNumber > size()-1) throw new IllegalArgumentException();
        lines.remove(lineNumber);
    }

    private void joinLines(int lineNumber1, int lineNumber2) {
        if (lineNumber1 >= size() && lineNumber2 >= lines.size()) throw new IllegalArgumentException();
        lines.set(lineNumber1, lines.get(lineNumber1) + lines.get(lineNumber2));
        removeLine(lineNumber2);
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

    public void setMode(char mode) {
        if ("ni".indexOf(mode) == -1) throw new IllegalArgumentException();
        this.mode = mode;
    }

    public int[] getCursor() {
        return cursor.clone();
    }

    public int getLineLength(int lineNumber) {
        return lines.get(lineNumber).length();
    }

    public int size() {
        return lines.size();
    }

    public int[] forceValidPos(int[] pos) {
        if (lines.size() == 0) {
            pos[0] = 0;
            pos[1] = 0;
            return pos;
        }
        pos[1] = Math.max(0, Math.min(size()-1, pos[1]));
        int insertModeAppend = (mode == 'i') ? 1 : 0;
        pos[0] = Math.max(0, Math.min(getLineLength(pos[1]) - 1 + insertModeAppend, pos[0]));
        return pos;
    }

    private boolean smallerPosition(int[] smaller, int[] bigger) {
        return (smaller[1] < bigger[1] || (smaller[1] == bigger[1] && smaller[0] < bigger[0]));
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
}
