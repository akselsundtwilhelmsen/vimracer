package vimracer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;  
import javafx.scene.Scene;            

public class App extends Application {  

    @Override  
    public void start(Stage primaryStage) throws Exception {  
        primaryStage.setTitle("Example App");
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("App.fxml"))));
        primaryStage.show();
    }  

    public static void main(String[] args) {
        launch(args);
    }
}  
