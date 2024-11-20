import Controller.DictionaryControllerFX;
import Model.DictionaryModel;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainJavaFX extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DictionaryModel model = new DictionaryModel();
        DictionaryControllerFX controller = new DictionaryControllerFX(stage, model);
        controller.showWelcomeWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}