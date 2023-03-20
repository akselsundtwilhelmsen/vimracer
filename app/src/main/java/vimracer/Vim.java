package vimracer;

import java.util.ArrayList;

public class Vim {
    private ArrayList<String> text;
    private ArrayList<Integer> cursor;
    private Controller controller;

    public Vim() {
        text = new ArrayList<>();
        cursor = new ArrayList<>();
    }

    public void setText(String text) {
        controller.setVimText(text);
    }
}
