package View.FX;

import Controller.DictionaryControllerFX;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

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
        exitButton.setOnAction(e -> stage.close());

        if(controller.unlockMatching() == false) {
            matchingButton.setDisable(true);
        }

        if(controller.unlockLearning() == false) {
            learnButton.setDisable(true);
        }

        layout.getChildren().addAll(addWordButton, wordListButton, saveDictionaryButton, loadDictionaryButton, learnButton, matchingButton, exitButton);

        Scene scene = new Scene(layout, 500, 400);

        URL resource = getClass().getResource("/mainwindow-style.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.out.println("CSS file not found!");
        }

        stage.setScene(scene);
        stage.setTitle("Menu główne");
    }
}

