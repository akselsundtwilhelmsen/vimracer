package vimracer;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Vim {
    private ArrayList<String> text;
    private ArrayList<Integer> cursor;

    public Vim() {
        text = new ArrayList<>();
        cursor = new ArrayList<>();
    }

    public void setText(String text) {
        this.text = new ArrayList<>();
        this.text.add(text);
        
    }

    public String toString() {
        return text.stream()
            .collect(Collectors.joining("\n"));
    }
}
