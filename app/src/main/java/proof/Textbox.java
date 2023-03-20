package proof;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Textbox {
    private ArrayList<String> text;

    public Textbox() {
        text = new ArrayList<>();
    }

    public void setText(ArrayList<String> text) {
        this.text = text;
    }

    public String toString() {
        return text.stream()
            .collect(Collectors.joining("\n"));
    }

    public static void main(String[] args) {
        Textbox t = new Textbox();
        ArrayList<String> a = new ArrayList<>();
        a.add("banger");
        a.add("banger");
        a.add("hanger");
        a.add("banger");

        t.setText(a);
        System.out.println(t);

    }


}
