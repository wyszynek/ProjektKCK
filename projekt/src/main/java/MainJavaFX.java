import Controller.DictionaryControllerFX;
import Model.DictionaryModel;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class MainJavaFX extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> {
            event.consume();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdzenie zamknięcia");
            alert.setHeaderText("Czy na pewno chcesz zamknąć aplikację?");
            alert.setContentText("Zmiany, które nie zostały zapisane, zostaną utracone.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                stage.close();
            }
        });

        DictionaryModel model = new DictionaryModel();
        DictionaryControllerFX controller = new DictionaryControllerFX(stage, model);
        controller.showWelcomeWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}