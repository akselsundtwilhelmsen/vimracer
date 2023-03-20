package tutorial;

public class VimWindow extends TextWindow {
    Vim vim;

    public VimWindow() {
        super();
        vim = new Vim(this);
    }

}
