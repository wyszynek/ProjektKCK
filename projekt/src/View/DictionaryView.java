package View;

import Controller.DictionaryController;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DictionaryView {
    private GUIScreen guiScreen;
    private Panel mainPanel;
    private static final int WORDS_PER_PAGE = 10;  // Limit liczby słów na stronę
    private int currentPage = 0;  // Numer bieżącej strony

    public DictionaryView(GUIScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    public GUIScreen getGuiScreen() {
        return this.guiScreen;
    }

    public void showMainMenu(DictionaryController controller) {
        mainPanel = new Panel();
        mainPanel.addComponent(new Button("Dodaj słówko", controller::showAddWordPanel));
        mainPanel.addComponent(new Button("Usuń słówko", controller::showDeleteWordPanel));
        mainPanel.addComponent(new Button("Nauka słówek", controller::showLearningModes));
        mainPanel.addComponent(new Button("Lista słówek", controller::showAllWordsPanel));
        mainPanel.addComponent(new Button("Zapisz słownik", controller::saveDictionary));
        mainPanel.addComponent(new Button("Wczytaj słownik", controller::loadDictionary));
        mainPanel.addComponent(new Button("Wyjście", controller::exit));

        Window mainWindow = new Window("Fiszkoteka");
        mainWindow.setWindowSizeOverride(new TerminalSize(40, 15));
        mainWindow.setSoloWindow(true);
        mainWindow.addComponent(mainPanel);
        guiScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
    }

    public void showAllWordsPanel(Map<String, String> allWords, DictionaryController controller) {
        mainPanel.removeAllComponents();

        if (allWords.isEmpty()) {
            mainPanel.addComponent(new Label("Brak słówek w słowniku."));
            mainPanel.addComponent(new Button("Powrót", controller::showMainMenu));
        } else {
            mainPanel.addComponent(new Button("Wyszukaj słówko", controller::showSearchWordPanel));
            mainPanel.addComponent(new Button("Powrót", controller::showMainMenu));

            // Konwertujemy Map na Listę dla łatwego dostępu przez indeks
            List<Map.Entry<String, String>> wordsList = new ArrayList<>(allWords.entrySet());
            int totalWords = wordsList.size();
            int totalPages = (totalWords + WORDS_PER_PAGE - 1) / WORDS_PER_PAGE;

            // Upewniamy się, że currentPage jest w zakresie
            if (currentPage < 0) {
                currentPage = 0;
            } else if (currentPage >= totalPages) {
                currentPage = totalPages - 1;
            }

            // Ustalamy zakres słów dla bieżącej strony
            int startIndex = currentPage * WORDS_PER_PAGE;
            int endIndex = Math.min(startIndex + WORDS_PER_PAGE, totalWords);

            // Dodajemy słówka na bieżącej stronie do panelu
            for (int i = startIndex; i < endIndex; i++) {
                Map.Entry<String, String> entry = wordsList.get(i);
                mainPanel.addComponent(new Label(entry.getKey() + " - " + entry.getValue()));
            }

            // Panel nawigacji z przyciskami "Poprzednie" i "Następne"
            Panel navigationPanel = new Panel();
            if (currentPage > 0) {
                navigationPanel.addComponent(new Button("Poprzednie", () -> {
                    currentPage--;
                    showAllWordsPanel(allWords, controller);  // Odświeżenie dla poprzedniej strony
                }));
            }
            if (currentPage < totalPages - 1) {
                navigationPanel.addComponent(new Button("Następne", () -> {
                    currentPage++;
                    showAllWordsPanel(allWords, controller);  // Odświeżenie dla następnej strony
                }));
            }
            mainPanel.addComponent(navigationPanel);
        }

        guiScreen.getScreen().refresh();
    }

    public void resetPagination() {
        currentPage = 0;
    }


    public void showMessage(String title, String message) {
        Window messageWindow = new Window(title);
        messageWindow.setWindowSizeOverride(new TerminalSize(31, 7));

        Panel panel = new Panel();
        panel.addComponent(new Label(message));
        panel.addComponent(new Button("OK", messageWindow::close));

        messageWindow.addComponent(panel);
        guiScreen.showWindow(messageWindow, GUIScreen.Position.CENTER);
    }

    public void showIncorrectAnswerPanel(DictionaryController controller, String word, String userTranslation, String correctTranslation) {
        Window incorrectAnswerWindow = new Window("Niepoprawna odpowiedź");
        incorrectAnswerWindow.setWindowSizeOverride(new TerminalSize(40, 10));

        Panel panel = new Panel();
        panel.addComponent(new Label("Twoja odpowiedź: " + userTranslation));
        panel.addComponent(new Label("Poprawna odpowiedź: " + correctTranslation));

        panel.addComponent(new Button("Następne słówko", () -> {
            incorrectAnswerWindow.close();
            controller.continueLearning();
        }));
        panel.addComponent(new Button("Oznacz jako poprawne", () -> {
            controller.markAnswerAsCorrect();
            incorrectAnswerWindow.close();
        }));

        incorrectAnswerWindow.addComponent(panel);
        guiScreen.showWindow(incorrectAnswerWindow, GUIScreen.Position.CENTER);
    }

    public void showAddWordPanel(DictionaryController controller) {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(new Label("Dodaj nowe słówko"));
        mainPanel.addComponent(new Label("Podaj słówko (ang):"));
        TextBox wordInput = new TextBox();
        mainPanel.addComponent(wordInput);
        mainPanel.addComponent(new Label("Podaj tłumaczenie:"));
        TextBox translationInput = new TextBox();
        mainPanel.addComponent(translationInput);

        mainPanel.addComponent(new Button("Dodaj", () -> controller.addWord(wordInput.getText(), translationInput.getText())));
        mainPanel.addComponent(new Button("Anuluj", controller::showMainMenu));

        guiScreen.getScreen().refresh();
    }

    public void showDeletionWordPanel(DictionaryController controller) {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(new Label("Usuwanie słówka"));
        mainPanel.addComponent(new Label("Podaj słówko do usunięcia (ang/pl):"));
        TextBox wordInput = new TextBox();
        mainPanel.addComponent(wordInput);

        mainPanel.addComponent(new Button("Usuń", () -> controller.deleteWord(wordInput.getText())));
        mainPanel.addComponent(new Button("Anuluj", controller::showMainMenu));
        guiScreen.getScreen().refresh();
    }

    public void showSearchWordPanel(DictionaryController controller) {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(new Label("Wyszukaj słówko"));
        mainPanel.addComponent(new Label("Podaj słówko (ang/pol):"));
        TextBox searchInput = new TextBox();
        mainPanel.addComponent(searchInput);

        mainPanel.addComponent(new Button("Wyszukaj", () -> controller.searchWord(searchInput.getText())));
        mainPanel.addComponent(new Button("Anuluj", controller::showMainMenu));

        guiScreen.getScreen().refresh();
    }

    public void learningMode(DictionaryController controller) {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(new Label("Wybierz tryb nauki"));

        mainPanel.addComponent(new Button("Test", controller::startLearningMode));
        mainPanel.addComponent(new Button("Dopasowanie", controller::startMatchingMode));
        mainPanel.addComponent(new Button("Powrót", controller::showMainMenu));

        guiScreen.getScreen().refresh();
    }

    public void showLearningPanel(DictionaryController controller, String word) {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(new Label("Przetłumacz słówko: " + word));

        TextBox translationInput = new TextBox();
        mainPanel.addComponent(translationInput);

        mainPanel.addComponent(new Button("Sprawdź", () -> {
            controller.checkAnswer(word, translationInput.getText());
        }));
        mainPanel.addComponent(new Button("Pomiń", () -> {
            controller.skipWord(word);
        }));
        mainPanel.addComponent(new Button("Powrót", controller::showMainMenu));

        guiScreen.getScreen().refresh();
    }

    public void showResults(int correct, int incorrect, int skipped) {
        Window resultsWindow = new Window("Wyniki nauki");
        resultsWindow.setWindowSizeOverride(new TerminalSize(30, 10));

        Panel panel = new Panel();
        panel.addComponent(new Label("Wyniki sesji:"));
        panel.addComponent(new Label("Poprawne odpowiedzi: " + correct));
        panel.addComponent(new Label("Niepoprawne odpowiedzi: " + incorrect));
        panel.addComponent(new Label("Pominięte odpowiedzi: " + skipped));
        panel.addComponent(new Button("OK", resultsWindow::close));

        resultsWindow.addComponent(panel);
        guiScreen.showWindow(resultsWindow, GUIScreen.Position.CENTER);
    }

    public void showMatchingPanel(DictionaryController controller, String word, List<String> options) {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(new Label("Wybierz tłumaczenie dla słówka:\n" + word));
        mainPanel.addComponent(new EmptySpace());

        for (String option : options) {
            mainPanel.addComponent(new Button(option, () -> {
                controller.checkMatchingAnswer(word, option);
            }));
        }

        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(new Button("Pomiń", () -> {
            controller.skipWord2(word);
        }));
        mainPanel.addComponent(new Button("Powrót", controller::showMainMenu));

        guiScreen.getScreen().refresh();
    }
    public void exit() {
        Window messageWindow = new Window("Wyjście");
        messageWindow.setWindowSizeOverride(new TerminalSize(40, 7));

        Panel panel = new Panel();
        panel.addComponent(new Label("Niezapisany słownik nie zostanie \nutworzony, czy napewno chcesz wyjść?"));
        panel.addComponent(new Button("Wyjdź", () -> {
            guiScreen.getScreen().stopScreen();
            System.exit(0);
        }));

        panel.addComponent(new Button("Anuluj", messageWindow::close));

        messageWindow.addComponent(panel);
        guiScreen.showWindow(messageWindow, GUIScreen.Position.CENTER);
    }
}
