import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static Map<String, String> dictionary = new HashMap<>();
    private static GUIScreen guiScreen;

    public static void main(String[] args) {
        Screen screen = TerminalFacade.createScreen();
        guiScreen = TerminalFacade.createGUIScreen(screen);
        guiScreen.getScreen().startScreen();

        Window mainWindow = new Window("Fiszkoteka");
        mainWindow.setWindowSizeOverride(new TerminalSize(40, 15));
        mainWindow.setSoloWindow(true);

        loadDictionary("abc1.txt");

        Panel mainPanel = new Panel();
        mainPanel.addComponent(new Button("Dodaj słówko", Main::addWord));
        mainPanel.addComponent(new Button("Wyszukaj słówko", Main::searchWord));
        mainPanel.addComponent(new Button("Nauka słówek", Main::learnWords));
        mainPanel.addComponent(new Button("Zapisz słownik", Main::saveDictionary));
        mainPanel.addComponent(new Button("Wczytaj słownik", Main::loadDictionary));
        mainPanel.addComponent(new Button("Wyjście", Main::exit));

        mainWindow.addComponent(mainPanel);
        guiScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
    }

    private static void addWord() {
        Window addWordWindow = new Window("Dodaj nowe słówko");
        addWordWindow.setWindowSizeOverride(new TerminalSize(30, 10));

        Panel panel = new Panel();
        panel.addComponent(new Label("Podaj słówko (ang):"));
        TextBox wordInput = new TextBox();
        panel.addComponent(wordInput);
        panel.addComponent(new Label("Podaj tłumaczenie:"));
        TextBox translationInput = new TextBox();
        panel.addComponent(translationInput);

        panel.addComponent(new Button("Dodaj", new Action() {
            @Override
            public void doAction() {
                String word = wordInput.getText();
                String translation = translationInput.getText();

                if (!word.isEmpty() && !translation.isEmpty()) {
                    dictionary.put(word, translation);
                    showMessage("Dodano słówko", "Słówko '" + word + "' zostało dodane.");
                    addWordWindow.close();
                } else {
                    showMessage("Błąd", "Podano nieprawidłowe słówko lub tłumaczenie.");
                }
            }
        }));

        panel.addComponent(new Button("Anuluj", addWordWindow::close));

        addWordWindow.addComponent(panel);
        guiScreen.showWindow(addWordWindow, GUIScreen.Position.CENTER);
    }

    private static void searchWord() {
        Window searchWindow = new Window("Wyszukaj słówko");
        searchWindow.setWindowSizeOverride(new TerminalSize(30, 10));

        Panel panel = new Panel();
        panel.addComponent(new Label("Podaj słówko (ang):"));
        TextBox searchInput = new TextBox();
        panel.addComponent(searchInput);

        panel.addComponent(new Button("Szukaj", new Action() {
            @Override
            public void doAction() {
                String word = searchInput.getText();
                String translation = dictionary.get(word);

                if (translation != null) {
                    showMessage("Wynik wyszukiwania", "Słówko: " + word + "\nTłumaczenie: " + translation);
                } else {
                    showMessage("Nie znaleziono", "Słówko '" + word + "' nie zostało znalezione.");
                }
                searchWindow.close();
            }
        }));

        panel.addComponent(new Button("Anuluj", searchWindow::close));

        searchWindow.addComponent(panel);
        guiScreen.showWindow(searchWindow, GUIScreen.Position.CENTER);
    }

    private static void learnWords() {
        List<Map.Entry<String, String>> wordList = new ArrayList<>(dictionary.entrySet());
        Collections.shuffle(wordList);

        int[] index = {0};
        int[] correctCount = {0};
        int[] incorrectCount = {0};
        int[] skippedCount  = {0};

        showLearningWindow(wordList, index, correctCount, incorrectCount, skippedCount );
    }

    private static void showLearningWindow(List<Map.Entry<String, String>> wordList, int[] index, int[] correctCount, int[] incorrectCount, int[] skippedCount ) {
        if (index[0] >= wordList.size()) {
            showMessage("Koniec nauki!",
                    "Poprawne odpowiedzi: " + correctCount[0] +
                            "\nBłędne odpowiedzi: " + incorrectCount[0] +
                            "\nPominięte odpowiedzi: " + skippedCount[0]);
            return;
        }

        Window learnWindow = new Window("Nauka słówek");
        learnWindow.setWindowSizeOverride(new TerminalSize(40, 15));

        Panel panel = new Panel();
        Label wordLabel = new Label("Podaj tłumaczenie dla: " + wordList.get(index[0]).getKey());
        TextBox answerInput = new TextBox();

        panel.addComponent(wordLabel);
        panel.addComponent(answerInput);

        panel.addComponent(new Button("Sprawdź", () -> {
            String correctAnswer = wordList.get(index[0]).getValue();
            String userAnswer = answerInput.getText().trim();

            if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                correctCount[0]++;
                showMessage("Poprawnie", "Dobrze! Poprawna odpowiedź.");
            } else {
                incorrectCount[0]++;
                showMessage("Niepoprawnie", "Źle. Poprawna odpowiedź: " + correctAnswer);
            }

            index[0]++;
            learnWindow.close();
            showLearningWindow(wordList, index, correctCount, incorrectCount, skippedCount);
        }));

        panel.addComponent(new Button("Anuluj", learnWindow::close));

        panel.addComponent(new Button("Pomiń", () -> {
            skippedCount[0]++;
            index[0]++;
            learnWindow.close();
            showLearningWindow(wordList, index, correctCount, incorrectCount, skippedCount);
        }));

        learnWindow.addComponent(panel);
        guiScreen.showWindow(learnWindow, GUIScreen.Position.CENTER);
    }


    private static void saveDictionary() {
        Window saveWindow = new Window("Zapisz słownik");
        saveWindow.setWindowSizeOverride(new TerminalSize(30, 10));

        Panel panel = new Panel();
        panel.addComponent(new Label("Podaj nazwę pliku:"));
        TextBox fileNameInput = new TextBox();
        panel.addComponent(fileNameInput);

        panel.addComponent(new Button("Zapisz", new Action() {
            @Override
            public void doAction() {
                String fileName = fileNameInput.getText();
                if (!fileName.isEmpty()) {
                    saveDictionary(fileName);
                    showMessage("Zapisano", "Słownik został zapisany do pliku: " + fileName);
                    saveWindow.close();
                } else {
                    showMessage("Błąd", "Nazwa pliku nie może być pusta.");
                }
            }
        }));

        panel.addComponent(new Button("Anuluj", saveWindow::close));

        saveWindow.addComponent(panel);
        guiScreen.showWindow(saveWindow, GUIScreen.Position.CENTER);
    }

    private static void loadDictionary() {
        Window loadWindow = new Window("Wczytaj słownik");
        loadWindow.setWindowSizeOverride(new TerminalSize(30, 10));

        Panel panel = new Panel();
        panel.addComponent(new Label("Podaj nazwę pliku do wczytania:"));
        TextBox fileNameInput = new TextBox();
        panel.addComponent(fileNameInput);

        panel.addComponent(new Button("Wczytaj", new Action() {
            @Override
            public void doAction() {
                String fileName = fileNameInput.getText();
                if (!fileName.isEmpty()) {
                    dictionary.clear(); // Czyszczenie obecnego słownika
                    loadDictionary(fileName);
                    showMessage("Wczytano", "Słownik został wczytany z pliku: " + fileName);
                    loadWindow.close();
                } else {
                    showMessage("Błąd", "Nazwa pliku nie może być pusta.");
                }
            }
        }));

        panel.addComponent(new Button("Anuluj", loadWindow::close));

        loadWindow.addComponent(panel);
        guiScreen.showWindow(loadWindow, GUIScreen.Position.CENTER);
    }

    private static void saveDictionary(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                writer.println(entry.getKey() + "=" + entry.getValue());
            }
        } catch (IOException e) {
            showMessage("Błąd zapisu", "Nie udało się zapisać słownika do pliku: " + fileName);
        }
    }

    private static void loadDictionary(String fileName) {
        if (Files.exists(Paths.get(fileName))) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        dictionary.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                showMessage("Błąd odczytu", "Nie udało się wczytać słownika z pliku: " + fileName);
            }
        } else {
            showMessage("Błąd", "Plik o nazwie " + fileName + " nie istnieje.");
        }
    }

    private static void showMessage(String title, String message) {
        if (guiScreen == null) {
            System.err.println("Error: guiScreen is null, cannot display message.");
            return;
        }

        Window messageWindow = new Window(title);
        messageWindow.setWindowSizeOverride(new TerminalSize(30, 6));

        Panel panel = new Panel();
        panel.addComponent(new Label(message));
        panel.addComponent(new Button("OK", messageWindow::close));

        messageWindow.addComponent(panel);
        guiScreen.showWindow(messageWindow, GUIScreen.Position.CENTER);
    }

    private static void exit() {
        guiScreen.getScreen().stopScreen();
        System.exit(0);
    }
}
