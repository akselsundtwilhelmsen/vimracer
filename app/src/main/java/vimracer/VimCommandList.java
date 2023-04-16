package vimracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class VimCommandList implements Iterable {

    private ArrayList<Object> commands;
    private String keyPresses;
    private Vim vim;

    private final ArrayList<String> VerticalMovementKeys = new ArrayList<>(Arrays.asList("gg","{","}","k","j","G"));
    private final ArrayList<String> MovementKeys = new ArrayList<>(Arrays.asList("0","F","ge","b","h","l","e","E","w","W","t","f","$","|"));
    private final ArrayList<String> InsertModeKeys = new ArrayList<>(Arrays.asList("i","I","a","A","o","O"));
    private final ArrayList<String> OperatorKeys = new ArrayList<>(Arrays.asList("d","D","y","Y","c","C","p","P",">","<","x","X","J"));
    private final ArrayList<String> Keys;

    private final ArrayList<String> OperatorFollowKeys = new ArrayList<>(Arrays.asList("i","a"));
    private final ArrayList<String> TextObjectFollowKeys = new ArrayList<>(Arrays.asList("w","p"));

    private final ArrayList<String> MovementOperationCommands = new ArrayList<>(Arrays.asList("deleteMotion","change","yank","addIndent","removeIndent"));
    private final ArrayList<String> StationaryOperationCommnads = new ArrayList<>(Arrays.asList("joinLines","put"));
    
    //regexes
    public static final Pattern wordBeginning = Pattern.compile("([\\w\\s][^\\w\\s])|(\\W\\w)");
    public static final Pattern WORDBeginning = Pattern.compile("(\\s.)");
    public static final Pattern wordEnd = Pattern.compile("([^\\w\\s][\\w\\s])|(\\w\\W)|[^\\s]$");
    public static final Pattern WORDEnd = Pattern.compile("(.\\s)|[^\\s]$");

    //other constants
    static final int MAX_NUMBER = 1000000;

    public VimCommandList(Vim vim) {
        this.commands = new ArrayList<>();
        this.keyPresses = "";
        this.vim = vim;

        this.MovementKeys.addAll(VerticalMovementKeys);
        this.Keys = new ArrayList<>();
        this.Keys.addAll(MovementKeys);
        this.Keys.addAll(InsertModeKeys);
        this.Keys.addAll(OperatorKeys);
    }

    public void buildCommandList(String keyPress) {
        //generate legal String-command TODO: numbers
        keyPresses = keyPresses + keyPress;

        System.out.format("\nkeypress: %s",keyPresses);
        
        //covert String-command normal command and add to command list
        if (Character.isDigit(keyPress.charAt(0))){
            generateNumber(Character.getNumericValue(keyPress.charAt(0)));
        } else if (MovementKeys.contains(keyPresses)) {
            generateMovement(keyPresses);
        } else if (InsertModeKeys.contains(keyPresses)) {
            generateInsertCommand(keyPresses);
        } else if (OperatorKeys.contains(keyPresses)) {
            generateOperationCommand(keyPresses);
        } else if (Keys.stream()
            .anyMatch(s -> (s.startsWith(keyPresses)))) {
                return;
        }
        keyPresses = "";
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
            if (command instanceof Integer) {
                if (size() <= index+1) return false;
            }
            index++;
        }
        return (commands.size() > 0);
    }

    public boolean willCommandListExecutable() {
        int index = -1;
        for (Object command : commands) {
            index++;
            if (command instanceof String) {
                command = (String) command;
                if (MovementOperationCommands.indexOf(command) != -1) {
                    if (size() <= index+1) {
                        continue;
                    }

                    if (size() <= index+2 && (get(index+1) instanceof Integer)) {
                        continue;
                    }

                    if (!(get(index+1) instanceof int[])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void clear() {
        commands.clear();;
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

    private int useLastNumber() {
        if (commands.size() == 0) {
            return 1;
        }
        if (!(commands.get(commands.size()-1) instanceof Integer)) {
            return 1;
        }
        return (int) commands.get(commands.size()-1);
    }

    //last movement in command list, returns cursor position if none found
    private int[] getLastMovement() {
        Stream<Object> commandStream = commands.stream();
        commandStream = Stream.concat(Stream.of(vim.getCursor()), commandStream);
        return commandStream
            .filter(o -> o instanceof int[])
            .map(o -> (int[]) o)
            .reduce((a,b) -> b)
            .get();
    }

    // //last command in command list
    // private String getLastCommand() {
    //     return commands.stream()
    //         .sorted(Collections.reverseOrder())
    //         .filter(o -> o instanceof String)
    //         .map(o -> (String) o)
    //         .findFirst()
    //         .orElse("");
    // }

    private void generateNumber(Integer number) {
        if (size() > 0) {
            Object lastCommand = commands.get(size()-1);
            if (lastCommand instanceof Integer) {
                commands.set(size()-1, Math.min(MAX_NUMBER, (Integer) lastCommand * 10 + number));
                return;
            }
        }
        commands.add(number);
    }

    private void generateMovement(String key, boolean unsafe) {
        int[] newPos = getLastMovement().clone();
        int length = 1;
        if (size() > 0) {
            Object lastCommand = commands.get(size()-1);
            if (lastCommand instanceof Integer) {
                length = (int) lastCommand;
                commands.remove(size()-1);
            }
        }
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
                    newPos = vim.prevInstanceOf(wordEnd, newPos, false);
                    break;
                case "b":
                    newPos = vim.prevInstanceOf(wordBeginning, newPos, true);
                    break;
                case "h":
                    newPos[0]--;
                    break;
                case "l":
                    newPos[0]++;
                    break;
                case "e":
                    newPos = vim.nextInstanceOf(wordEnd,newPos,false);
                    break;
                case "E":
                    newPos = vim.nextInstanceOf(WORDEnd,newPos,false);
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
                    newPos[0] = vim.getLineLength(newPos[1])-1;
                    newPos[2] = 2147483647;
                    // key = "j";
                    break;
                case "gg":
                    newPos[1] = 0;
                    break;
                case "{":
                    break;
                case "}":
                    break;
                case "k":
                    newPos[1]--;
                    break;
                case "j":
                    newPos[1]++;
                    break;
                case "G":
                    newPos[1] = vim.size()-1;
            }
        }

        //cursorvalidation and setteing pref
        if (!unsafe) {
            if (VerticalMovementKeys.indexOf(key) != -1) {
                newPos[0] = newPos[2];
                newPos = vim.forceValidPos(newPos);
            } else {
                newPos = vim.forceValidPos(newPos);
                newPos[2] = newPos[0];
            } 
        }

        //is linewisej
        if (VerticalMovementKeys.indexOf(key) != -1  || key.equals("line")) {
            newPos[3] = 1;
        } else {
            newPos[3] = 0;
        }

        commands.add(newPos);
    }

    private void generateMovement(String key) {
        generateMovement(key, false);
    }

    private String getLastCommand() {
        if (size() == 0) return null;
        if (! (commands.get(commands.size()-1) instanceof String)) return null;
        return (String) commands.get(commands.size()-1);
    }

    private void generateInsertCommand(String key) {
        int lineNumber = getLastMovement()[1];
        switch (key) {
            case "i":
                break;
            case "I":
                generateMovement("0");
                break;
            case "a":
                if (!(vim.getLineLength(lineNumber) == 0)) {
                    generateMovement("l", true);
                }
                break;
            case "A":
                generateMovement("$");
                if (!(vim.getLineLength(lineNumber) == 0)) {
                    generateMovement("l", true);
                }
                break;
            case "o":
                generateMovement("j", true);
                commands.add("insertLine");
                break;
            case "O":
                commands.add("insertLine");
                break;
        }
        commands.add("insert");
    }

    private void generateOperationCommand(String key) {
        String lastCommand = getLastCommand();
        switch (key) {
            case "d":
                if ("deleteMotion".equals(lastCommand)) {
                    generateMovement("line");
                    break;
                } 
                commands.add("deleteMotion");
                break;
            case "D":
                commands.add("deleteMotion");
                generateMovement("$");
                break;
            case "c":
                if ("change".equals(lastCommand)) {
                    generateMovement("line");
                    break;
                } 
                commands.add("change");
                break;
            case "C": //TODO: hvorfor funker ikke+
                commands.add("change");
                generateMovement("$");
                generateMovement("l",true);
                break;
            case "y":
                if ("yank".equals(lastCommand)) {
                    generateMovement("line");
                    break;
                } 
                commands.add("yank");
                break;
            case "Y":
                commands.add("yank");
                generateMovement("line");
                break;
            case "p":
                commands.add("put");
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
                break;
            case ">":
                commands.add("addIndent");
                break;
        }
    }

    public String toString() {
        if (size() == 0) {
            return "[]";
        }
        String outString = "[";
        for (Object command : commands) {
            if (command instanceof String) {
                outString = outString + (String) command;
            } else if (command instanceof Integer) {
                outString = outString + ((Integer) command);
            } else if (command instanceof int[]) {
                int[] movement = (int[]) command;
                outString = outString + String.format("[%d, %d]", movement[0], movement[1]);
                if (movement[3] == 1) outString = outString + "l";
            }
            outString = outString + ", ";
        }
        return outString.substring(0,outString.length()-2) + "]";
    }

    public Iterator iterator() {
        return commands.iterator();
    }
}
