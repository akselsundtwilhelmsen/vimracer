package vimracer;

import javafx.scene.input.KeyEvent;

public class NameInput {
    private String name;
    private Controller controller;
    
    public NameInput(Controller controller) {
        name = "";
        this.controller = controller;
    }

    public void keyPress(KeyEvent event) {
        String keyCode = event.getCode().toString().toLowerCase();
        if (keyCode.equals("back_space")) {
            if (name.length() > 0) {
                name = name.substring(0, name.length()-1);
            }
        }
        else if (keyCode.equals("enter")) {
            controller.startGame();
        }
        else if (keyCode.equals("right")) {
            controller.nextFile();
        }
        else if (keyCode.equals("left")) {
            controller.prevFile();
        }
        else {
            if (keyCode.length() == 1 && name.length() < 9) {
                name += event.getCode().toString().toLowerCase();
            }
        }
    }

    public String toString() {
        if (name.equals("")) {
            return "[name]";
        }
        else {
            return name;
        }
    }

    public String getName() {
        return name;
    }
}
