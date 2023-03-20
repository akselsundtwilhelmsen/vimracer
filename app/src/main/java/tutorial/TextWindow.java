package tutorial;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;  
import javafx.scene.text.Text;

public class TextWindow extends StackPane {
    private Rectangle background;
    private Text text;
    private Textbox textbox;
    private int width;
    private int height;

    public TextWindow(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.background = new Rectangle();
        background.setWidth(width);
        background.setHeight(height);
        background.setFill(Color.rgb(200,200,200,1));
        this.textbox = new Textbox();
        this.text = new Text();
        this.getChildren().addAll(background, text);
    }

    public TextWindow() {
        this(400, 300);
    }
}