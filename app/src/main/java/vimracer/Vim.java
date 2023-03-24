package vimracer;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.input.KeyEvent;

public class Vim extends TextWindow{
    private int[] cursor;
    private char mode; // must be n(ormal), v(isual), or i(nsert);
    private boolean shiftHeld;
    private ArrayList<Object> commands; //inneholder kommandoer (String), tall, og bevegelser (int[])

    private final ArrayList<String> legalMovementKeys;

    public Vim() {
        super();
        this.cursor = new int[2];
        Arrays.fill(cursor,0);
        this.mode = 'i';
        this.shiftHeld = false;
        this.commands = new ArrayList<>();

        this.legalMovementKeys = new ArrayList<>(Arrays.asList("0","F","ge","b","h","l","e","w","t","f","$","|"));
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

        
        if (legalMovementKeys.contains(keyString)) {
            generateMovement(keyString);
            cursor = ((int[]) commands.get(commands.size()-1));
            System.out.println(String.format("%d %d",cursor[0],cursor[1]));
            // System.out.println(String.format("%d %d", ((int[])commands.get(commands.size()-1))[0],((int[])commands.get(commands.size()-1))[1]));
        }
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

    private boolean validCursorPos(int[] pos) {
        int currentLength = lines.get(cursor[1]).length();
        if (pos.length != 2) return false;
        if (0 > cursor[1] || cursor[1] >= lines.size()) return false;
        if (cursor[0] < 0) return false;
        if (mode == 'n' && currentLength != 0 && currentLength <= cursor[0]) return false;
        if (currentLength < cursor[0]) return false;
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

    private void generateMovement(String operator) {
        int length = getLastNumber();
        int[] newPos = cursor.clone();
        int[] prevPos = cursor.clone();
        for (int i = 0; i < length; i++) {
            switch (operator) {
                case "0":
                    newPos[0] = 0;
                case "F":
                    break;
                case "ge":
                    break;
                case "b":
                    break;
                case "h":
                    System.out.println(String.format("newpos: %d %d",newPos[0],newPos[1]));
                    newPos[0]--;
                    System.out.println(String.format("newpos: %d %d",newPos[0],newPos[1]));
                case "l":
                    newPos[0]++;
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
                case "|":
                    newPos[0] = 0;
                    operator = "l"; //flytter til høyre hvis length > 1
            }

            System.out.println(String.format("newpos: %d %d",newPos[0],newPos[1]));
            if (! validCursorPos(newPos)) {
                System.out.println("clamped'!");
                newPos = prevPos.clone();
            }
            prevPos = newPos.clone();

        }
        commands.add(newPos);
    }
}