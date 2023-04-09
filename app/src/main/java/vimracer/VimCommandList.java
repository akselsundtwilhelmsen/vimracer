package vimracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class VimCommandList implements Iterator {

    private ArrayList<Object> commands;
    private String keyPresses;
    private int index;
    private Vim vim;

    private final ArrayList<String> MovementKeys = new ArrayList<>(Arrays.asList("0","F","ge","b","h","l","e","w","W","t","f","$","|","gg","{","}","k","j","G"));
    private final ArrayList<String> InsertModeKeys = new ArrayList<>(Arrays.asList("i","I","a","A","o","O"));
    private final ArrayList<String> OperatorKeys = new ArrayList<>(Arrays.asList("d","D","y","Y","c","C",">","<","x","X","J"));
    private final ArrayList<String> Keys;

    private final ArrayList<String> OperatorFollowKeys = new ArrayList<>(Arrays.asList("i","a"));
    private final ArrayList<String> OperatorFollowFollowKeys = new ArrayList<>(Arrays.asList("w","p",""));

    private final ArrayList<String> MovementOperationCommands = new ArrayList<>(Arrays.asList("deleteMotion","change","addIndent","removeIndent"));
    private final ArrayList<String> StationaryOperationCommnads = new ArrayList<>(Arrays.asList("joinLines"));
    
    //regexes
    static final Pattern wordBeginning = Pattern.compile("([\\w\\s][^\\w\\s])|(\\W\\w)");
    static final Pattern WORDBeginning = Pattern.compile("(\\s.)");
    static final Pattern wordEnd = Pattern.compile("([^\\w\\s][\\w\\s])|(\\w\\W)|[^\\s]$");

    public VimCommandList(Vim vim) {
        this.commands = new ArrayList<>();
        this.keyPresses = "";
        this.index = -1;
        this.vim = vim;

        this.Keys = new ArrayList<>();
        this.Keys.addAll(MovementKeys);
        this.Keys.addAll(InsertModeKeys);
        this.Keys.addAll(OperatorKeys);
    }

    public void buildCommandList(String keyPress) {
        //generate legal String-command TODO: numbers
        keyPresses = keyPresses + keyPress;

        System.out.format(", keypress: %s",keyPresses);
        
        //covert String-command normal command and add to command list
        if (MovementKeys.contains(keyPresses)) {
            generateMovement(keyPresses);
        } else if (InsertModeKeys.contains(keyPresses)) {
            generateInsertCommand(keyPresses);
        } else if (OperatorKeys.contains(keyPresses)) {
            generateOperationCommand(keyPresses);
        }

        if (Keys.stream()
            .anyMatch(s -> !(keyPresses.startsWith(s)) || keyPresses.equals(s))) {
                keyPresses = "";
        }
    }

    public boolean isCommandListExecutable() {
        int index = 0;
        for (Object command : commands) {
            if (command instanceof String) {
                command = (String) command;
                if (MovementOperationCommands.indexOf(command) != -1) {
                    if (commands.size() <= index+1) {
                        return false;
                    }
                    if (!(commands.get(index+1) instanceof int[])) {
                        return false;
                    }
                }
            }
            index++;
        }
        return (commands.size() > 0);
    }

    public void clear() {
        index = -1;
        commands.clear();;
    }

    public boolean hasNext() {
        return (commands.size() > index + 1);
    }

    public Object next() {
        index++;
        return (commands.get(index));
    }

    public void remove(int index) {
        commands.remove(index);
    }

    public Object get(int index) {
        return commands.get(index);
    }

    public int size() {
        return commands.size();
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

    //last movement in command list, returns cursor position if non found
    private int[] getLastMovement() {
        Stream<Object> commandStream = commands.stream();
        commandStream = Stream.concat(Stream.of(vim.getCursor()), commandStream);
        return commandStream
            .filter(o -> o instanceof int[])
            .map(o -> (int[]) o)
            .reduce((a,b) -> b)
            .get();
    }

    //last command in command list
    private String getLastCommand() {
        return commands.stream()
            .sorted(Collections.reverseOrder())
            .filter(o -> o instanceof String)
            .map(o -> (String) o)
            .findFirst()
            .orElse("");
    }

    private void generateMovement(String key) {
        int length = getLastNumber();
        int[] newPos = getLastMovement().clone();
        int[] prevPos = newPos.clone();
        for (int i = 0; i < length; i++) {
            switch (key) {
                case "|":
                    newPos[0] = 0;
                    key = "l"; //flytter til høyre hvis length > 1
                    break;
                case "0":
                    newPos[0] = 0;
                    break;
                case "F":
                    break;
                case "ge":
                    break;
                case "b":
                    newPos = vim.prevInstanceOf(WORDBeginning, prevPos, false);
                    break;
                case "h":
                    newPos[0] = prevPos[0]-1;
                    break;
                case "l":
                    newPos[0] = prevPos[0]+1;
                    break;
                case "e":
                    newPos = vim.nextInstanceOf(wordEnd,newPos,false);
                    break;
                case "w":
                    newPos = vim.nextInstanceOf(wordBeginning,newPos,true);
                    break;
                case "W":
                    newPos = vim.nextInstanceOf(WORDBeginning,newPos,true);
                    break;
                case "t":
                    break;
                case "f":
                    break;
                case "$":
                    // newPos[0] = lines.get(newPos[1]).length()-1;
                    key = "j";
                    break;
                case "gg":
                    newPos[1] = 0;
                    break;
                case "{":
                    break;
                case "}":
                    break;
                case "k":
                    newPos[1] = prevPos[1]-1;
                    break;
                case "j":
                    newPos[1] = prevPos[1]+1;
                    break;
                case "G":
                    // newPos[1] = lines.size()-1;
            }
        }
        commands.add(newPos);
    }

    private void generateInsertCommand(String key) {
        switch (key) {
            case "i":
                break;
            case "I":
                generateMovement("0");
                break;
            case "a":
                generateMovement("l");
                break;
            case "A":
                generateMovement("$");
                generateMovement("l");
                break;
            case "o":
                generateMovement("j");
                commands.add("insertLine");
                break;
            case "O":
                commands.add("insertLine");
                break;
        }
        commands.add("insert");
    }

    private void generateOperationCommand(String key) {
        // String lastCommand = getLastCommand();
        switch (key) {
            case "d":
                commands.add("deleteMotion");
                break;
            case "D":
                commands.add("deleteMotion");
                generateMovement("$");
                break;
            case "c":
                commands.add("change");
                break;
            case "x":
                commands.add("deleteMotion");
                generateMovement("l");
                generateMovement("h");
                break;
            case "X":
                commands.add("deleteMotion");
                generateMovement("h");
                break;
            case "J":
                commands.add("joinLines");
                break;
            case "<":
                commands.add("removeIndent");
            case ">":
                commands.add("addIndent");
        }
    }
}
