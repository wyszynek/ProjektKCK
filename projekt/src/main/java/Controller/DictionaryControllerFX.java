package Controller;

import Model.DictionaryModel;
import View.FX.*;
import javafx.stage.Stage;

import java.io.IOException;

public class DictionaryControllerFX {
    private Stage stage;
    private DictionaryModel model;

    public DictionaryControllerFX(Stage stage, DictionaryModel model) {
        this.stage = stage;
        this.model = model;
    }

    public void showWelcomeWindow() {
        WelcomeView view = new WelcomeView(stage, this);
        view.show();
    }

    public void showMainMenu() {
        MainMenuView view = new MainMenuView(stage, this);
        view.show();
    }

    public void showAddWordView() {
        AddWordView view = new AddWordView(stage, this);
        view.show();
    }

    public void showWordListView() {
        WordListView view = new WordListView(stage, this, model.getDictionary());
        view.show();
    }

    public void showSaveDictionaryView() {
        SaveDictionaryView view = new SaveDictionaryView(stage, this);
        view.show();
    }

    // Logika obsługi dodawania słówka
    public void addWord(String word, String translation) {
        if (word.isBlank() || translation.isBlank()) {
            System.out.println("Słówko i tłumaczenie nie mogą być puste."); // Można dodać informację w GUI
            return;
        }
        model.addWord(word, translation);
    }

    public void saveDictionaryToFile(String fileName) {
        try {
            model.saveDictionary(fileName);
            System.out.println("Słownik zapisano do pliku: " + fileName);
        } catch (IOException e) {
            System.out.println("Błąd zapisu słownika: " + e.getMessage());
        }
    }
}
