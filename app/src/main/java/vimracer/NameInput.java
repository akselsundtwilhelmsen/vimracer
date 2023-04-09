package vimracer;

import javafx.scene.input.KeyEvent;

public class NameInput {
    private String name;
    
    public NameInput() {
        name = "";
    }

    public void keyPress(KeyEvent event) {
        String keyCode = event.getCode().toString().toLowerCase();
        if (keyCode.equals("back_space")) {
            if (name.length() > 0) {
                name = name.substring(0, name.length()-1);
            }
        }
        else {
            if (keyCode.length() == 1 && name.length() < 9) {
                name += event.getCode().toString().toLowerCase();
            }
        }
    }

    public String toString() {
        return name;
    }
}
