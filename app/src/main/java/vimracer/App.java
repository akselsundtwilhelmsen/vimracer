package tutorial;
import javafx.application.Application;  
import javafx.stage.Stage;  
import javafx.scene.Scene;            
import javafx.scene.layout.HBox;

public class App extends Application {  

    @Override  
    public void start(Stage primaryStage) throws Exception {  
        TextWindow fasit = new TextWindow();  
        TextWindow feil = new VimWindow();  
        

        HBox root = new HBox(10);
        root.getChildren().addAll(fasit,feil);
        Scene scene = new Scene(root,1200,900);      
        //vim input
        // scene.setOnKeyPressed(e -> {
        //     System.out.println("The 'A' key was pressed");
        // });
        primaryStage.setScene(scene);  
        primaryStage.setTitle("First JavaFX Application");  
        primaryStage.show(); 
    }  

    public static void main(String[] args) {
        launch(args);
    }
}  
