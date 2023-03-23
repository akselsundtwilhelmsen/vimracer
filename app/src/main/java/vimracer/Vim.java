package vimracer;

import java.util.Arrays;
import javafx.scene.input.KeyEvent;

public class Vim extends TextWindow{
    private int[] cursor;
    private char mode; // must be n(ormal), v(isual), or i(nsert);
    private boolean shiftHeld;

    public Vim() {
        super();
        this.cursor = new int[2];
        Arrays.fill(cursor,0);
        this.mode = 'i';
        this.shiftHeld = false;
    }

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

        switch (keyString) {
            case "a":
                insertString("a");
                break;
            case "b":
                insertString("b");
                break;
            case "x":
                removeUnderCursor();
            System.out.println(String.format("%d %d",cursor[0],cursor[1]));
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

}