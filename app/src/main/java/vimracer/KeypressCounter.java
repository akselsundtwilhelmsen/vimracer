package vimracer;

public class KeypressCounter {
    private int count;

    public KeypressCounter() {
        this.count = 0;
    }

    public void keypress() {
        this.count++;
    }

    public void keypress(int n) { // maybe unnecessary
        this.count += n;
    }

    public String getCount() {
        return "" + this.count;
    }
}