package vimracer;

public class Game {
    Stopwatch stopwatch;
    KeypressCounter keypressCounter;
    Controller controller;

    public Game(Controller controller) {
        this.controller = controller;
        this.startGame();
    }
    
    public void startGame() {
        this.stopwatch = new Stopwatch();
        this.keypressCounter = new KeypressCounter();
    }

    public String[] endGame() {
        return new String[]{keypressCounter.getCount(), stopwatch.toString()};
    }

    public void keypress() {
        this.keypressCounter.keypress();
    }

    public String getStopwatch() {
        return this.stopwatch.toString();
    }

    public String getKeypressCounter() {
        return this.keypressCounter.getCount();
    }

}
