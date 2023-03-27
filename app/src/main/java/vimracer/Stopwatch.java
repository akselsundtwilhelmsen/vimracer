package vimracer;

public class Stopwatch {
    private long startTime;

    public Stopwatch() {
        this.startTime = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public String toString() { // 00:00:00
        long elapsedTime = this.getElapsedTime();
        long milliSec = elapsedTime % 1000;
        long sec = (elapsedTime / 1000) % 60;
        long min = (elapsedTime / 1000) / 60;
        return String.format("%02d:%02d:%03d", min, sec, milliSec);
    }
}