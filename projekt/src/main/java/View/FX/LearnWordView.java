package View.FX;

import Controller.DictionaryControllerFX;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;

public class LearnWordView {
    private Stage stage;
    private DictionaryControllerFX controller;

    private Label wordLabel;
    private TextField translationField;
    private Label feedbackLabel;
    private Button checkButton;
    private Button skipButton;
    private Button nextButton;
    private Button correctButton;

    private String currentWord;

    public LearnWordView(Stage stage, DictionaryControllerFX controller) {
        this.stage = stage;
        this.controller = controller;

        wordLabel = new Label();
        translationField = new TextField();
        translationField.setPromptText("Wpisz tłumaczenie...");
        feedbackLabel = new Label();
        checkButton = new Button("Sprawdź");
        skipButton = new Button("Pomiń");
        nextButton = new Button("Przejdź do następnego");
        correctButton = new Button("Odznacz jako poprawne");
        nextButton.setVisible(false);
    }

    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(correctButton, nextButton);
        buttonBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("Powrót");

        checkButton.setOnAction(e -> checkTranslation());
        skipButton.setOnAction(e -> skipWord());
        backButton.setOnAction(e -> controller.showMainMenu());
        nextButton.setOnAction(e -> loadNextWord());
        correctButton.setOnAction(e -> markAsCorrect());

        layout.getChildren().addAll(wordLabel, translationField, feedbackLabel, checkButton, skipButton, buttonBox, backButton);

        Scene scene = new Scene(layout, 500, 400);

        URL resource = getClass().getResource("/styles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.out.println("CSS file not found!");
        }

        stage.setScene(scene);
        stage.setTitle("Tryb Nauki");

        loadNextWord(); // Ładujemy pierwsze słowo
    }

    private void loadNextWord() {
        nextButton.setVisible(false);    // Ukrywamy przycisk "Przejdź do następnego"
        correctButton.setVisible(false);
        skipButton.setDisable(false);   // Odblokowujemy przycisk "Pomiń"
        checkButton.setDisable(false);  // Odblokowujemy przycisk "Sprawdź"
        currentWord = controller.continueLearning();
        if (currentWord != null) {
            wordLabel.setText("Przetłumacz słówko:    " + currentWord);
            translationField.clear();
            feedbackLabel.setText("");
        } else {
            showStatistics(); // Koniec nauki - pokaż statystyki
        }
    }

    private void checkTranslation() {
        String userTranslation = translationField.getText();
        if (controller.checkAnswer(currentWord, userTranslation)) {
            feedbackLabel.setText("Poprawnie!");
        } else {
            feedbackLabel.setText("Błędna odpowiedź. Poprawna odpowiedź to: " + controller.getCorrectTranslation(currentWord));
        }
        nextButton.setVisible(true);
        skipButton.setDisable(true);
        checkButton.setDisable(true);
        correctButton.setVisible(true);
    }

    private void skipWord() {
        controller.skipWord(currentWord);
        loadNextWord(); // Ładujemy kolejne słowo
    }

    private void markAsCorrect() {
        controller.markAsCorrect();
        loadNextWord(); // Ładujemy kolejne słowo
    }

    private void showStatistics() {
        Map<String, Integer> stats = controller.getLearningStatistics();

        VBox statsLayout = new VBox(10);
        statsLayout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Podsumowanie Nauki:");
        Label correctLabel = new Label("Poprawne odpowiedzi: " + stats.get("Correct") + "/" + stats.get("Total"));
        Label incorrectLabel = new Label("Błędne odpowiedzi: " + stats.get("Incorrect") + "/" + stats.get("Total"));
        Label skippedLabel = new Label("Pominięte słowa: " + stats.get("Skipped") + "/" + stats.get("Total"));

        Button backButton = new Button("Powrót do menu");
        backButton.setOnAction(e -> controller.showMainMenu());

        statsLayout.getChildren().addAll(titleLabel, correctLabel, incorrectLabel, skippedLabel, backButton);

        Scene statsScene = new Scene(statsLayout, 500, 400);
        stage.setScene(statsScene);
    }
}
