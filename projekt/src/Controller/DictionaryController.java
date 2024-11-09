package Controller;

import Model.DictionaryModel;
import View.DictionaryView;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class DictionaryController {
    private DictionaryModel model;
    private DictionaryView view;

    public DictionaryController(DictionaryView view) {
        this.model = new DictionaryModel();
        this.view = view;
    }

    public void showMainMenu() {
        view.showMainMenu(this);
    }

    public void showAddWordPanel() {
        view.showAddWordPanel(this);
    }

    public void showSearchWordPanel() {
        view.showSearchWordPanel(this);
    }

    public void showLearningModes() {
        view.learningMode(this);
    }

    public void exit() {
        view.exit();
    }

    public void addWord(String word, String translation) {
        if (model.wordExists(word)) {
            view.showMessage("Błąd", "Słówko '" + word + "' już istnieje \nw słowniku.");
        } else if (!word.isEmpty() && !translation.isEmpty()) {
            model.addWord(word, translation);
            view.showMessage("Dodano słówko", "Słówko '" + word + "' zostało dodane.");
        } else {
            view.showMessage("Błąd", "Podano nieprawidłowe \nsłówko lub tłumaczenie.");
        }
        showMainMenu();
    }

    public void searchWord(String word) {
        String translation = model.searchWord(word);

        if (translation != null) {
            view.showMessage("Wynik wyszukiwania", "Słówko: " + word + "\nTłumaczenie: " + translation);
        } else {
            view.showMessage("Nie znaleziono", "Słówko '" + word + "'\nnie zostało znalezione.");
        }
        showMainMenu();
    }

    public void saveDictionary() {
        String fileName = promptFileName("Zapisz słownik", "Podaj nazwę pliku:");
        if (!fileName.isEmpty()) {
            try {
                model.saveDictionary(fileName);
                view.showMessage("Zapisano", "Słownik został zapisany \ndo pliku: " + fileName);
            } catch (IOException e) {
                view.showMessage("Błąd zapisu", "Nie udało się zapisać \nsłownika do pliku: " + fileName);
            }
        }
    }

    public void loadDictionary() {
        String fileName = promptFileName("Wczytaj słownik", "Podaj nazwę pliku \ndo wczytania:");
        if (!fileName.isEmpty()) {
            try {
                model.loadDictionary(fileName);
                view.showMessage("Wczytano", "Słownik został wczytany \nz pliku: " + fileName);
            } catch (IOException e) {
                view.showMessage("Błąd odczytu", "Nie udało się wczytać \nsłownika z pliku: " + fileName);
            }
        }
    }

    public String promptFileName(String windowTitle, String promptText) {
        final String[] fileName = {""};
        Window fileNameWindow = new Window(windowTitle);
        fileNameWindow.setWindowSizeOverride(new TerminalSize(30, 10));

        Panel panel = new Panel();
        panel.addComponent(new Label(promptText));
        TextBox fileNameInput = new TextBox();
        panel.addComponent(fileNameInput);

        panel.addComponent(new Button("OK", () -> {
            fileName[0] = fileNameInput.getText();
            fileNameWindow.close();
        }));
        panel.addComponent(new Button("Anuluj", fileNameWindow::close));

        fileNameWindow.addComponent(panel);
        view.getGuiScreen().showWindow(fileNameWindow, GUIScreen.Position.CENTER);

        return fileName[0];
    }

    public void startLearningMode() {
        if(model.getDictionary().isEmpty()) {
            view.showMessage("Error", "Słownik pusty, dodaj \nsłówko aby móc korzystać \nz nauki.");
            return;
        }
        model.resetProgress();
        model.shuffleWords();
        continueLearning();
    }

    public void continueLearning() {
        String word = model.getNextWord(); // Using getNextWord() now
        if (word != null) {
            view.showLearningPanel(this, word);
        } else {
            endLearningSession();
        }
    }

    public void checkAnswer(String word, String userTranslation) {
        boolean correct = model.isCorrectTranslation(word, userTranslation);
        if (correct) {
            view.showMessage("Poprawna odpowiedź", "Twoja odpowiedź jest poprawna!");
            continueLearning();
        } else {
            String correctTranslation = model.searchWord(word);
            view.showIncorrectAnswerPanel(this, word, userTranslation, correctTranslation);
        }
    }

    public void markAnswerAsCorrect() {
        model.markAnswerAsCorrect();
        continueLearning();
    }

    public void skipWord(String word) {
        if (!model.getSkippedWords().contains(word)) {
            model.increaseSkippedAnswers();
            model.getSkippedWords().add(word);
            model.getShuffledWords().add(word);
            continueLearning();
        } else {
            String correctTranslation = model.searchWord(word);
            view.showMessage("Ponowne pominięcie", "Poprawna odpowiedź to: " + correctTranslation);
            continueLearning();
        }
    }

    public void skipWord2(String word) {
        if (!model.getSkippedWords().contains(word)) {
            model.increaseSkippedAnswers();
            model.getSkippedWords().add(word);
            model.getShuffledWords().add(word);
            continueMatching();
        } else {
            String correctTranslation = model.searchWord(word);
            view.showMessage("Ponowne pominięcie", "Poprawna odpowiedź to: " + correctTranslation);
            continueMatching();
        }
    }

    public void endLearningSession() {
        int correct = model.getCorrectAnswers();
        int incorrect = model.getIncorrectAnswers();
        int skipped = model.getSkippedAnswers();
        view.showResults(correct, incorrect, skipped);
        showMainMenu();
    }

    public void startMatchingMode() {
        if (model.getDictionary().size() < 4) {
            view.showMessage("Error", "Słownik posiada za mało \nsłówek aby uruchomić ten \ntryb (potrzeba min 5).");
            return;
        }
        model.resetProgress();
        model.shuffleWords();
        continueMatching();
    }

    public void continueMatching() {
        String word = model.getNextWord(); // Using getNextWord() now
        if (word != null) {
            List<String> options = model.getMatchingOptions(word, 4);
            view.showMatchingPanel(this, word, options);
        } else {
            endLearningSession();
        }
    }

    public void checkMatchingAnswer(String word, String selectedTranslation) {
        boolean correct = model.isCorrectTranslation(word, selectedTranslation);
        if (correct) {
            view.showMessage("Poprawna odpowiedź", "Twoja odpowiedź jest poprawna!");
        } else {
            String correctTranslation = model.searchWord(word);
            view.showMessage("Niepoprawna odpowiedź", "Poprawna odpowiedź to:\n" + correctTranslation);
        }
        continueMatching();
    }
}
