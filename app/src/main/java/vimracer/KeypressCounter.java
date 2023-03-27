package vimracer;

public class KeypressCounter {
    private int count;

    public KeypressCounter() {
        this.count = 0;
    }

    public void keypress() {
        this.count++;
    }

    public void keypress(int n) {
        for (int i = 0; i < n; i++) {
            this.count++;
        }
    }

    public int getCount() {
        return this.count;
    }
}
