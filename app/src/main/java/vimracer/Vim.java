package vimracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import javafx.scene.input.KeyEvent;

public class Vim extends TextWindow{
    private int[] cursor;
    private char mode; // must be n(ormal), v(isual), or i(nsert);
    private boolean shiftHeld;

    private ArrayList<Object> commands; //inneholder kommandoer (String), tall, og bevegelser (int[])
    private String currentCommand;

    private final ArrayList<String> LegalMovementKeys;
    private final ArrayList<String> LegalInsertModeKeys;
    private final ArrayList<String> LegalOperatorKeys; 
    private final ArrayList<String> LegalKeys;

    public Vim() {
        super();
        this.cursor = new int[2];
        Arrays.fill(cursor,0);
        this.mode = 'i';
        this.shiftHeld = false;

        this.commands = new ArrayList<>();
        this.currentCommand = new String();

        this.LegalMovementKeys = new ArrayList<>(Arrays.asList("0","F","ge","b","h","l","e","w","t","f","$","|","gg","{","}","k","j","G"));
        this.LegalInsertModeKeys = new ArrayList<>(Arrays.asList("i","I","a","A","o","O"));
        this.LegalOperatorKeys = new ArrayList<>(Arrays.asList("d","D","y","Y","c","C",">","<","x","X"));
        this.LegalKeys = new ArrayList<>();
        this.LegalKeys.addAll(LegalMovementKeys);
        this.LegalKeys.addAll(LegalInsertModeKeys);
        this.LegalKeys.addAll(LegalOperatorKeys);

        // int[] test = new int[2];
        // test[0] = -1;
        // test[1] = 0;
        // if (validCursorPos(test)) System.err.println("wtf!!");
    }


    // Plan for normal (og viusal) mode
    // få KeyEvent input
    // fortløpende til en array med lett-håndterbare kommandoer
    // når denne listen er utførbar vil den bli utført

    public void keyPress(KeyEvent event) {
        String keyString = event.getCode().toString();

        // behandler input, TODO: bør kanskje finne ut om dette kan gjøres automatisk
        if (keyString == "SHIFT") {
            shiftHeld = true;
            return;
        }

        if (keyString == "ESCAPE") {
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
            if (keyString == "BACK_SPACE") {
                backspace();
            } else {
                insertString(keyString);
            }
            System.out.println(String.format("%d %d",cursor[0],cursor[1]));
            return;
        }

        //generate legal String-command TODO: numbers
        currentCommand = currentCommand + keyString;

        if (! LegalKeys.stream()
            .anyMatch(s -> currentCommand.startsWith(s) && currentCommand.length() <= s.length())) {
                currentCommand = "";
        }

        //covert String-command normal command and add to command list
        if (LegalMovementKeys.contains(keyString)) {
            generateMovement(keyString);
        }

        if (LegalInsertModeKeys.contains(keyString)) {
            generateInsertCommand(keyString);
        }

        if (LegalOperatorKeys.contains(keyString)) {
            generateOperationCommand(keyString);
        }

        //execute the command list 
        //TODO: the command list can fill over multiple keypresses (e.g. d-i-w)
        //TODO: it must dedect if it's executable (execute and clear), start of a legal command (continue), or illegal command (clear)
        //temporary soulution is to just execute and clear it

        for (Object command : commands) {
            if (command instanceof int[]) {
                cursor = ((int[]) command);
            } else if (command instanceof String) {
                switch ((String) command) {
                    case "insert":
                        setMode('i');
                        break;
                    case "insertLine":
                        insertLine(cursor[1]);
                        cursor[0] = 0;
                        break;
                    case "deleteMotion":
                        if (getLastMovement().equals(cursor)) return; //midlertidig
                        removeBetween(cursor,getLastMovement());
                        break;
                }
            }
        }
        commands.clear();
        System.out.println(String.format("%d %d",cursor[0],cursor[1]));
    }

    public void keyRelease(KeyEvent event) {
        if (event.getCode().toString() == "SHIFT") {
            shiftHeld = false;
            return;
        }
    }

    private void moveCursor(int[] pos) {
        if (!validCursorPos(pos)) throw new IllegalArgumentException();
        cursor = pos;
    }

    private void insertString(String s) {
        lines.set(cursor[1], lines.get(cursor[1]).substring(0,cursor[0]) + s + lines.get(cursor[1]).substring(cursor[0]));
        cursor[0] += s.length();
    }

    private void removeBetween(int[] pos1, int[] pos2) {
        if (pos1[1] > pos2[1] || (pos1[1] == pos2[1] && pos1[0] > pos2[0])) {
            int[] temp = pos1;
            pos1 = pos2;
            pos2 = temp;
        }

        //fjerner mellom posisjoner på samme linje
        if (pos1[1] == pos2[1]) {
            lines.set(pos1[1], lines.get(pos1[1]).substring(0,pos1[0]) + lines.get(pos1[1]).substring(pos2[0]));
            return;
        }

        //fjerner fra 1. posisjon og ut linjen, alle linjene mellom, og til 2. posisjon
        lines.set(pos1[1], lines.get(pos1[1]).substring(0,pos1[0]));
        for (int i = 0; i < pos2[1]-pos1[1]-2; i++) {
            removeLine(pos1[1]+1);
        }
        lines.set(pos1[1]+1, lines.get(pos1[1]+1).substring(pos2[0]));

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

    public void setMode(char mode) {
        if ("ni".indexOf(mode) == -1) throw new IllegalArgumentException();
        this.mode = mode;
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

    private int getLastNumber() {
        if (commands.size() == 0) {
            return 1;
        }
        if (!(commands.get(commands.size()-1) instanceof Integer)) {
            return 1;
        }
        return (int) commands.get(commands.size()-1);
    }

    //last movement in command list, returns cursor position if non found
    private int[] getLastMovement() {
        Stream<Object> commandStream = commands.stream();
        commandStream = Stream.concat(Stream.of(cursor), commandStream);
        return commandStream
            .filter(o -> o instanceof int[])
            .map(o -> (int[]) o)
            .reduce((a,b) -> b)
            .get();
    }

    //last command in command list, returns cursor position if non found
    private String getLastCommand() {
        return commands.stream()
            .sorted(Collections.reverseOrder())
            .filter(o -> o instanceof String)
            .map(o -> (String) o)
            .findFirst()
            .orElse("");
    }

    private void generateMovement(String key) {
        int length = getLastNumber();
        int[] newPos = getLastMovement().clone();
        int[] prevPos = newPos.clone();
        for (int i = 0; i < length; i++) {
            switch (key) {
                case "|":
                    newPos[0] = 0;
                    key = "l"; //flytter til høyre hvis length > 1
                    break;
                case "0":
                    newPos[0] = 0;
                    break;
                case "F":
                    break;
                case "ge":
                    break;
                case "b":
                    break;
                case "h":
                    newPos[0] = prevPos[0]-1;
                    break;
                case "l":
                    newPos[0] = prevPos[0]+1;
                    break;
                case "e":
                    break;
                case "w":
                    break;
                case "t":
                    break;
                case "f":
                    break;
                case "$":
                    newPos[0] = lines.get(newPos[1]).length()-1;
                    key = "j";
                    break;
                case "gg":
                    newPos[1] = 0;
                    break;
                case "{":
                    break;
                case "}":
                    break;
                case "k":
                    newPos[1] = prevPos[1]-1;
                    break;
                case "j":
                    newPos[1] = prevPos[1]+1;
                    break;
                case "G":
                    newPos[1] = lines.size()-1;
            }
        }
        commands.add(newPos);
    }

    private void generateInsertCommand(String key) {
        switch (key) {
            case "i":
                break;
            case "I":
                generateMovement("0");
                break;
            case "a":
                generateMovement("l");
                break;
            case "A":
                generateMovement("$");
                generateMovement("l");
                break;
            case "o":
                generateMovement("j");
                commands.add("insertLine");
                break;
            case "O":
                commands.add("insertLine");
                break;
        }
        commands.add("insert");
    }

    private void generateOperationCommand(String key) {
        // String lastCommand = getLastCommand();
        switch (key) {
            case "d":
                commands.add("deleteMotion");
                break;
            case "D":
                commands.add("deleteMotion");
                generateMovement("$");
                break;
        }
    }
}