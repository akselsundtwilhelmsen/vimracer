package vimracer;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.scene.input.KeyEvent;

public class Vim extends TextWindow{
    private ArrayList<Integer> cursor;
    private char mode; // must be n(ormal), v(isual), or i(nsert);

    public Vim() {
        super();
        this.cursor = new ArrayList<>();
        this.mode = 'n';
    }

    public void keyPress(KeyEvent event) {
        switch (event.getCode().toString()) {
            case "A":
                this.setText("Aa");
                break;
            case "B":
                this.setText("Bb");
                break;
        }
    }

    public void moveCursor(ArrayList<Integer> vector) {

    }
}