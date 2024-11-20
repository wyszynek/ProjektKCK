package View.FX;

import Controller.DictionaryControllerFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;
import java.util.stream.Collectors;

public class WordListView {
    private Stage stage;
    private DictionaryControllerFX controller;
    private ObservableList<String> words;

    public WordListView(Stage stage, DictionaryControllerFX controller, Map<String, String> dictionary) {
        this.stage = stage;
        this.controller = controller;

        // Tworzenie listy słów na podstawie przekazanej mapy
        this.words = FXCollections.observableArrayList(
                dictionary.entrySet().stream()
                        .map(entry -> entry.getKey() + " - " + entry.getValue())
                        .collect(Collectors.toList())
        );
    }

    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        TextField searchField = new TextField();
        searchField.setPromptText("Wyszukaj słówko");

        ListView<String> wordList = new ListView<>();
        wordList.setItems(words);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            wordList.setItems(FXCollections.observableArrayList(
                    words.stream()
                            .filter(word -> word.toLowerCase().contains(newVal.toLowerCase()))
                            .collect(Collectors.toList())
            ));
        });

        Button backButton = new Button("Powrót");
        backButton.setOnAction(e -> controller.showMainMenu());

        layout.getChildren().addAll(searchField, wordList, backButton);

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Lista słówek");
    }
}

