package Controller;

import Model.DictionaryModel;
import View.FX.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void loadSaveDictionaryView() {
        LoadDictionaryView view = new LoadDictionaryView(stage, this);
        view.show();
    }

    private void showLearningView() {
        LearnWordView view = new LearnWordView(stage, this);
        view.show();
    }

    private void showMatchingView() {
        LearnMatchingView view = new LearnMatchingView(stage, this);
        view.show();
    }

    public boolean unlockMatching() {
        if(model.getDictionary().size() > 4) {
            return true;
        }
        return false;
    }

    public boolean unlockLearning() {
        if(model.getDictionary().size() > 0) {
            return true;
        }
        return false;
    }



    public String handleAddWord(String word, String translation) {
        if (word.isBlank() || translation.isBlank()) {
            return "Proszę wypełnić puste pola.";
        }
        if (model.getDictionary().containsKey(word) ||
                (model.getDictionary().containsKey(translation) && model.getDictionary().get(translation).equals(word))) {
            return "Podane słowa już istnieją.";
        }
        model.addWord(word, translation);
        return "Pomyślnie dodano słówko.";
    }

    public void removeWord(String selectedWord) {
        model.removeWord(selectedWord);
    }

    public void skipWord(String word) {
        String translation = model.searchWord(word);
        if((!model.getSkippedWordsMap().containsKey(word) || !model.getSkippedWordsMap().containsKey(translation))) {
            model.increaseSkippedAnswers();
            model.getSkippedWordsMap().put(word, translation);
            model.getSkippedWordsMap().put(translation, word);
            model.getSkippedWords().add(word);
            model.getShuffledWords().add(word);
        }
    }

    public String getCorrectTranslation(String word) {
        return model.searchWord(word);
    }



    public void startMatching() {
        model.resetProgress();
        showMatchingView();
    }

    public String continueMatching() {
        return model.getNextWord();
    }

    public List<String> getMatchingOptions(String word, int numberOfOptions) {
        return model.getMatchingOptions(word, numberOfOptions);
    }

    public String checkTranslationMatching(String word, String userTranslation) {
        boolean temp = model.isCorrectTranslationMatching(word, userTranslation);
        if (userTranslation == null) {
            return "Proszę wybrać odpowiedź.";
        }
        if (temp) {
            return "Poprawnie.";
        }
        return "Błędna odpowiedź. Poprawna odpowiedź:   " + getCorrectTranslation(word);
    }

//    public boolean isCorrectTranslationMatching(String word, String userTranslation) {
//        return model.isCorrectTranslationMatching(word, userTranslation);
//    }



    public void startLearning() {
        model.resetProgress(); // Resetujemy stan nauki
        showLearningView();    // Przechodzimy do widoku nauki
    }

    public String continueLearning() {
        String word = model.getNextWordLearning();
        if (word != null) {
            if (model.shouldTranslateToEnglish()) {
                return word;
            } else {
                String translation = model.searchWord(word);
                return translation;
            }
        } else {
            getLearningStatistics();
        }
        return null;
    }

    public void markAsCorrect() {
        model.markAnswerAsCorrect();
    }

    public Map<String, Integer> getLearningStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Correct", model.getCorrectAnswers());
        stats.put("Incorrect", model.getIncorrectAnswers());
        stats.put("Skipped", model.getSkippedAnswers());
        stats.put("Total", model.getDictionary().size());
        return stats;
    }

    public String checkAnswerLearning(String word, String userTranslation) {
        if (userTranslation.isEmpty()) {
            return "Proszę wypełnić puste pole.";
        }

        boolean temp = model.isCorrectTranslation(word, userTranslation);

        if(!temp) {
            return "Błędna odpowiedź. Poprawna odpowiedź to:   " + getCorrectTranslation(word);
        }

        return "Poprawnie.";
    }

//    public boolean checkAnswer(String word, String translation) {
//        return model.isCorrectTranslation(word, translation);
//    }



    public void saveDictionaryToFile(String fileName) {
        try {
            model.saveDictionary(fileName);
            System.out.println("Słownik zapisano do pliku: " + fileName);
        } catch (IOException e) {
            System.out.println("Błąd zapisu słownika: " + e.getMessage());
        }
    }

    public void loadDictionaryFromFile(String fileName) {
        try {
            model.loadDictionary(fileName);
            System.out.println("Słownik wczytano z pliku: " + fileName);
        } catch (IOException e) {
            System.out.println("Błąd wczytywania słownika: " + e.getMessage());
        }
    }
}
