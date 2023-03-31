package vimracer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
        Scene scene = new Scene(loader.load());
        Controller controller = new Controller();
        loader.setController(controller);

        primaryStage.setTitle("Example App");
        primaryStage.setScene(scene);
        primaryStage.show();

        Timeline updateStopwatch = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.updateStopwatch();
            }
        }));
        updateStopwatch.setCycleCount(Timeline.INDEFINITE);
        updateStopwatch.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}