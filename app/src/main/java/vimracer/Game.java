package vimracer;

public class Game {
    Stopwatch stopwatch;
    KeypressCounter keypressCounter;
    Controller controller;

    public Game(Controller controller) {
        this.controller = controller;
        startGame();
    }
    
    public void startGame() {
        stopwatch = new Stopwatch();
        keypressCounter = new KeypressCounter();
    }

    public String[] endGame() {
        return new String[]{keypressCounter.getCount(), stopwatch.toString()};
    }

    public void keypress() {
        keypressCounter.keypress();
    }

    public String getStopwatch() {
        return stopwatch.toString();
    }

    public String getKeypressCounter() {
        return keypressCounter.getCount();
    }

    public long getStopwatchLong() {
        return stopwatch.getElapsedTime();
    }
}
