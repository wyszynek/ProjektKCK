package View.FX;

import Controller.DictionaryControllerFX;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class LearnMatchingView {
    private Stage stage;
    private DictionaryControllerFX controller;

    private Label wordLabel;
    private VBox optionsLayout; // Layout do przechowywania RadioButtonów
    private Label feedbackLabel;
    private Button checkButton;
    private Button skipButton;
    private Button nextButton;

    private String currentWord;
    private ToggleGroup optionsGroup; // Grupa dla RadioButtonów

    public LearnMatchingView(Stage stage, DictionaryControllerFX controller) {
        this.stage = stage;
        this.controller = controller;

        wordLabel = new Label();  // Wyświetlane słowo
        optionsLayout = new VBox(10); // Layout dla RadioButtonów
        feedbackLabel = new Label();  // Informacja zwrotna dla użytkownika
        checkButton = new Button("Sprawdź");
        skipButton = new Button("Pomiń");
        nextButton = new Button("Przejdź do następnego");
        nextButton.setVisible(false);      // Ukryty na początku

        optionsGroup = new ToggleGroup(); // Tworzymy grupę dla RadioButtonów
    }

    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        Button backButton = new Button("Powrót");

        checkButton.setOnAction(e -> checkTranslation());

        skipButton.setOnAction(e -> {
            controller.skipWord(currentWord);
            loadNextWord();
        });

        backButton.setOnAction(e -> controller.showMainMenu());

        nextButton.setOnAction(e -> loadNextWord());

        layout.getChildren().addAll(wordLabel, optionsLayout, feedbackLabel, checkButton, skipButton, nextButton, backButton);

        Scene scene = new Scene(layout, 500, 400);

        URL resource = getClass().getResource("/styles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.out.println("CSS file not found!");
        }

        stage.setScene(scene);
        stage.setTitle("Tryb Nauki przez Dopasowanie");

        loadNextWord();
    }

    private void loadNextWord() {
        nextButton.setVisible(false);
        skipButton.setDisable(false);
        checkButton.setDisable(false);
        optionsGroup.selectToggle(null);

        currentWord = controller.continueMatching();

        if (currentWord != null) {
            wordLabel.setText("Przetłumacz słówko:    " + currentWord);
            optionsLayout.getChildren().clear(); // Czyszczenie poprzednich opcji
            feedbackLabel.setText("");

            // Pobieramy opcje dopasowania
            List<String> options = controller.getMatchingOptions(currentWord, 4); // 4 opcje
            for (String option : options) {
                RadioButton radioButton = new RadioButton(option);
                radioButton.setToggleGroup(optionsGroup);
                radioButton.setStyle("-fx-text-fill: white;");
                optionsLayout.getChildren().add(radioButton);
            }
        } else {
            showStatistics(); // Koniec nauki - pokaż statystyki
        }
    }

    private void checkTranslation() {
        String userTranslation = getSelectedOption();
        String feedback = controller.checkTranslationMatching(currentWord, userTranslation);
        feedbackLabel.setText(feedback);

        if (feedback.equals("Proszę wybrać odpowiedź.")) {
            return; // Zatrzymaj metodę
        }

        for (javafx.scene.Node node : optionsLayout.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                checkBox.setDisable(true);
            }
        }

        checkButton.setDisable(true);
        skipButton.setDisable(true);
        nextButton.setVisible(true);

//        if (userTranslation != null) {
//            // Sprawdzamy, czy odpowiedź jest poprawna
//            if (controller.isCorrectTranslationMatching(currentWord, userTranslation)) {
//                feedbackLabel.setText("Poprawnie!");
//            } else {
//                feedbackLabel.setText("Błędna odpowiedź. Poprawna odpowiedź:   " + controller.getCorrectTranslation(currentWord));
//            }
//
//            // Blokowanie wszystkich opcji (Checkboxów)
//            for (javafx.scene.Node node : optionsLayout.getChildren()) {
//                if (node instanceof CheckBox) {
//                    CheckBox checkBox = (CheckBox) node;
//                    checkBox.setDisable(true);  // Zablokuj wszystkie checkboxy
//                }
//            }
//
//            checkButton.setDisable(true);
//            skipButton.setDisable(true);
//            nextButton.setVisible(true);
//        } else {
//            feedbackLabel.setText("Proszę wybrać odpowiedź.");
//        }
    }

    private String getSelectedOption() {
        RadioButton selectedRadioButton = (RadioButton) optionsGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            return selectedRadioButton.getText();
        }
        return null;
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

        Scene statsScene = new Scene(statsLayout, 400, 300);
        stage.setScene(statsScene);
    }
}
