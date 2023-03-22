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
        this.mode = 'n';
    }

    public void keyPress(KeyEvent event) {
        switch (event.getCode().toString()) {
            case "A":
                insertChar('a');
                break;
            case "B":
                insertChar('b');
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

    public void insertChar(char c) {
        lines.set(cursor[1], lines.get(cursor[1]).substring(0,cursor[0]) + c + lines.get(cursor[1]).substring(cursor[0]));
        cursor[0]++;
    }

    public void removeUnderCursor() {
        if (lines.get(cursor[1]).length() == 0) {
            return;
        }
        lines.set(cursor[1], lines.get(cursor[1]).substring(0,cursor[0]-1) + lines.get(cursor[1]).substring(cursor[0]));
        if (cursor[0] == lines.get(cursor[1]).length()+1) {
            cursor[0]--; 
        }
    }
}