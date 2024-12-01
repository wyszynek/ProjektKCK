package View.FX;

import Controller.DictionaryControllerFX;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        learnButton.setOnAction(e -> controller.startLearning());
        matchingButton.setOnAction(e -> controller.startMatching());
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

        if (!controller.unlockMatching()) {
            matchingButton.setDisable(true);
            Tooltip matchingTooltip = new Tooltip("Opcja Dopasowania jest niedostępna. Upewnij się, że masz wymagane dane.");
            Tooltip.install(matchingButton, matchingTooltip);
        }

        if (!controller.unlockLearning()) {
            learnButton.setDisable(true);
            Tooltip learningTooltip = new Tooltip("Opcja Test jest niedostępna. Dodaj więcej słów, aby odblokować.");
            Tooltip.install(learnButton, learningTooltip);
        }

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
}

