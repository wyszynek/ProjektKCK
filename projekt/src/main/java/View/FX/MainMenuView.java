package View.FX;

import Controller.DictionaryControllerFX;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        Button exitButton = new Button("Wyjdź");

        addWordButton.setOnAction(e -> controller.showAddWordView());
        wordListButton.setOnAction(e -> controller.showWordListView());
        saveDictionaryButton.setOnAction(e -> controller.showSaveDictionaryView());
        exitButton.setOnAction(e -> stage.close());

        layout.getChildren().addAll(addWordButton, wordListButton, saveDictionaryButton, exitButton);

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Menu główne");
    }
}

