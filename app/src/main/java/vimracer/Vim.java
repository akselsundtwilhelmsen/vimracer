package vimracer;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.input.KeyEvent;

public class Vim extends TextWindow{
    private int[] cursor;
    private char mode; // must be n(ormal), v(isual), or i(nsert);

    public Vim() {
        super();
        this.cursor = new int[2];
        Arrays.fill(cursor,0);
        this.mode = 'i';
    }

    public void keyPress(KeyEvent event) {
        if (mode == 'i') {
            insertString(event.getCode().toString());
            return;
        }
        switch (event.getCode().toString()) {
            case "A":
                insertString("a");
                break;
            case "B":
                insertString("b");
                break;
            case "X":
                removeUnderCursor();
        }
        System.out.println(String.format("%d %d",cursor[0],cursor[1]));
    }

    public void moveCursor(int[] pos) {
        if (pos.length != 2) throw new IllegalArgumentException();
        cursor = pos;
    }

    public void insertString(String s) {
        lines.set(cursor[1], lines.get(cursor[1]).substring(0,cursor[0]) + s + lines.get(cursor[1]).substring(cursor[0]));
        cursor[0] += s.length();
    }

    public void removeUnderCursor() {
        if (lines.get(cursor[1]).length() == 0) return;
        lines.set(cursor[1], lines.get(cursor[1]).substring(0,cursor[0]-1) + lines.get(cursor[1]).substring(cursor[0]));
        if (cursor[0] == lines.get(cursor[1]).length()+1) {
            cursor[0]--; 
        }
    }

    public void backspace() {
        if (cursor[0] == 0) return;
        cursor[0]--;
        removeUnderCursor();
    }
}