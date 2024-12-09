package View.FX;

import Controller.DictionaryControllerFX;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import javax.tools.Tool;
import java.net.URL;
import java.util.Optional;

public class MainMenuView {
    private Stage stage;
    private DictionaryControllerFX controller;

    public MainMenuView(Stage stage, DictionaryControllerFX controller) {
        this.stage = stage;
        this.controller = controller;
    }

    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        Button addWordButton = new Button("Dodaj słówko");
        Button wordListButton = new Button("Lista słówek");
        Button saveDictionaryButton = new Button("Zapisz słownik");
        Button loadDictionaryButton = new Button("Wczytaj słownik");
        Button learnButton = new Button("Test");
        Button matchingButton = new Button("Dopasowania");
        Button exitButton = new Button("Wyjdź");

        addWordButton.setOnAction(e -> controller.showAddWordView());
        wordListButton.setOnAction(e -> controller.showWordListView());
        saveDictionaryButton.setOnAction(e -> controller.showSaveDictionaryView());
        loadDictionaryButton.setOnAction(e -> controller.loadSaveDictionaryView());
        learnButton.setOnAction(e -> {
            if (controller.unlockLearning() == false) {
                showLearningConfirmation();
            } else {
                controller.startLearning();
            }
        });
        matchingButton.setOnAction(e -> {
            if (controller.unlockMatching() == false) {
                showMatchingConfirmation();
            } else {
                controller.startMatching();
            }
        });
        exitButton.setOnAction(e -> {
            Alert exitConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
            exitConfirmation.setTitle("Potwierdzenie Wyjścia");
            exitConfirmation.setHeaderText("Czy na pewno chcesz wyjść?");
            exitConfirmation.setContentText("Wszystkie niezapisane dane mogą zostać utracone.");

            Optional<ButtonType> result = exitConfirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                stage.close();
            }
        });

        layout.getChildren().addAll(addWordButton, wordListButton, saveDictionaryButton, loadDictionaryButton, learnButton, matchingButton, exitButton);

        Scene scene = new Scene(layout, 500, 400);

        URL resource = getClass().getResource("/styles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.out.println("CSS file not found!");
        }

        stage.setScene(scene);
        stage.setTitle("Menu główne");
    }

    public void showMatchingConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Brak wystarczającej liczby słówek");
        alert.setHeaderText("Nie możesz rozpocząć nauki");
        alert.setContentText("Aby aktywować tryb nauki, słownik musi zawierać przynajmniej 5 słówek.");

        alert.showAndWait();
    }

    public void showLearningConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Brak wystarczającej liczby słówek");
        alert.setHeaderText("Nie możesz rozpocząć nauki");
        alert.setContentText("Aby aktywować tryb nauki, musisz dodać więcej słówek do słownika.");

        alert.showAndWait();
    }
}

