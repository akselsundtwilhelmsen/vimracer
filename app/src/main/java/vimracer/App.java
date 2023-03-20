package vimracer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;  
import javafx.scene.Scene;            

public class App extends Application {  

    @Override  
    public void start(Stage primaryStage) throws Exception {  
        FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
        Scene scene = new Scene(loader.load());
        loader.setController(new Controller());

        primaryStage.setTitle("Example App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }  

    public static void main(String[] args) {
        launch(args);
    }
}  
