package vimracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javafx.scene.input.KeyEvent;

public class Vim extends TextWindow{
    private int[] cursor;
    private char mode; // must be n(ormal), v(isual), or i(nsert);
    private boolean shiftHeld;

    private ArrayList<Object> commands; //inneholder kommandoer (String), tall, og bevegelser (int[])
    private String currentCommand;

    private final ArrayList<String> LegalMovementCommands;
    private final ArrayList<String> LegalInsertModeCommands;
    private final ArrayList<String> LegalCommands;

    public Vim() {
        super();
        this.cursor = new int[2];
        Arrays.fill(cursor,0);
        this.mode = 'i';
        this.shiftHeld = false;

        this.commands = new ArrayList<>();
        this.currentCommand = new String();

        this.LegalMovementCommands = new ArrayList<>(Arrays.asList("0","F","ge","b","h","l","e","w","t","f","$","|","gg","{","}","k","j","G"));
        this.LegalInsertModeCommands = new ArrayList<>(Arrays.asList("i","I","a","A","o","O"));
        this.LegalCommands = new ArrayList<>();
        this.LegalCommands.addAll(LegalMovementCommands);
        this.LegalCommands.addAll(LegalInsertModeCommands);

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

        if (keyString == "SHIFT") {
            shiftHeld = true;
            return;
        }

        if (keyString == "ESCAPE") {
            mode = 'n';
            if (!validCursorPos(cursor)) cursor[0]--;
            return;
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

        if (! LegalCommands.stream()
            .anyMatch(s -> currentCommand.startsWith(s) && currentCommand.length() <= s.length())) {
                currentCommand = "";
        }

        //covert String-command normal command and add to command list
        if (LegalMovementCommands.contains(keyString)) {
            generateMovement(keyString);
        }

        if (LegalInsertModeCommands.contains(keyString)) {
            generateInsertCommand(keyString);
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
                    case "i":
                        setMode('i');
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

    private int[] getLastMovement() {
        if (commands.size() == 0) {
            return cursor.clone();
        }
        return commands.stream()
            .sorted(Collections.reverseOrder())
            .filter(o -> o instanceof int[])
            .map(o -> (int[]) o)
            .findFirst()
            .orElse(cursor);
    }

    private void generateMovement(String operator) {
        int length = getLastNumber();
        int[] newPos = getLastMovement().clone();
        int[] prevPos = newPos.clone();
        for (int i = 0; i < length; i++) {
            switch (operator) {
                case "|":
                    newPos[0] = 0;
                    operator = "l"; //flytter til høyre hvis length > 1
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
                    operator = "j";
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

    private void generateInsertCommand(String operator) {
        switch (operator) {
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
        }
        commands.add("i");
    }
}