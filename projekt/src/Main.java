import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.*;
import java.util.*;

public class Main {
    private static Map<String, String> dictionary = new HashMap<>();
    private static GUIScreen guiScreen;
    private static Panel mainPanel;

    public static void main(String[] args) {
        Screen screen = TerminalFacade.createScreen();
        guiScreen = TerminalFacade.createGUIScreen(screen);
        guiScreen.getScreen().startScreen();

        Window mainWindow = new Window("Fiszkoteka");
        mainWindow.setWindowSizeOverride(new TerminalSize(40, 15));
        mainWindow.setSoloWindow(true);

        mainPanel = new Panel();
        updateMainPanel("main");
        mainWindow.addComponent(mainPanel);
        guiScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
    }

    private static void updateMainPanel(String mode) {
        mainPanel.removeAllComponents();

        switch (mode) {
            case "main" -> {
                mainPanel.addComponent(new Button("Dodaj słówko", () -> updateMainPanel("addWord")));
                mainPanel.addComponent(new Button("Wyszukaj słówko", () -> updateMainPanel("searchWord")));
                mainPanel.addComponent(new Button("Nauka słówek", Main::learningMode));
                mainPanel.addComponent(new Button("Zapisz słownik", Main::saveDictionary));
                mainPanel.addComponent(new Button("Wczytaj słownik", Main::loadDictionary));
                mainPanel.addComponent(new Button("Wyjście", Main::exit));
            }
            case "addWord" -> setupAddWordPanel();
            case "searchWord" -> setupSearchWordPanel();
            case "learning" -> setupLearningPanel();
            case "matching" -> setupMatchingLearningPanel();
        }
    }

    private static void setupAddWordPanel() {
        mainPanel.addComponent(new Label("Dodaj nowe słówko"));
        mainPanel.addComponent(new Label("Podaj słówko (ang):"));
        TextBox wordInput = new TextBox();
        mainPanel.addComponent(wordInput);
        mainPanel.addComponent(new Label("Podaj tłumaczenie:"));
        TextBox translationInput = new TextBox();
        mainPanel.addComponent(translationInput);

        mainPanel.addComponent(new Button("Dodaj", () -> {
            String word = wordInput.getText();
            String translation = translationInput.getText();

            if (!word.isEmpty() && !translation.isEmpty()) {
                dictionary.put(word, translation);
                showMessage("Dodano słówko", "Słówko '" + word + "' zostało dodane.");
            } else {
                showMessage("Błąd", "Podano nieprawidłowe słówko lub tłumaczenie.");
            }
            updateMainPanel("main");
        }));
        mainPanel.addComponent(new Button("Anuluj", () -> updateMainPanel("main")));
    }

    private static void setupSearchWordPanel() {
        mainPanel.addComponent(new Label("Wyszukaj słówko"));
        mainPanel.addComponent(new Label("Podaj słówko (ang/pol):"));
        TextBox searchInput = new TextBox();
        mainPanel.addComponent(searchInput);

        mainPanel.addComponent(new Button("Szukaj", () -> {
            String word = searchInput.getText();
            String translation = dictionary.get(word);

            if (translation == null) {
                for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                    if (entry.getValue().equalsIgnoreCase(word)) {
                        translation = entry.getKey();
                        break;
                    }
                }
            }

            if (translation != null) {
                showMessage("Wynik wyszukiwania", "Słówko: " + word + "\nTłumaczenie: " + translation);
            } else {
                showMessage("Nie znaleziono", "Słówko '" + word + "' nie zostało znalezione.");
            }
            updateMainPanel("main");
        }));
        mainPanel.addComponent(new Button("Anuluj", () -> updateMainPanel("main")));
    }

    private static void learningMode() {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(new Label("Wybierz tryb nauki"));

        mainPanel.addComponent(new Button("Test", Main::startLearningSession));
        mainPanel.addComponent(new Button("Dopasowanie", Main::startMatchingLearningSession));
        mainPanel.addComponent(new Button("Powrót", () -> updateMainPanel("main")));

        guiScreen.getScreen().refresh();
    }

    private static void startLearningSession() {
        if (dictionary.isEmpty()) {
            showMessage("Nie znaleziono", "Słownik pusty, dodaj \nsłówko aby móc korzystać \nz nauki.");
            return;
        }
        updateMainPanel("learning");
    }

    private static void startMatchingLearningSession() {
        if (dictionary.size() < 4) {
            showMessage("Nie znaleziono", "Słownik posiada za mało \nsłówek aby uruchomić ten \ntryb (potrzeba min 5).");
            return;
        }
        updateMainPanel("matching");
    }

    private static void setupMatchingLearningPanel() {
        mainPanel.removeAllComponents();

        List<Map.Entry<String, String>> wordList = new ArrayList<>(dictionary.entrySet());
        Collections.shuffle(wordList);

        // Wybieramy losowe słówko jako poprawną odpowiedź
        Map.Entry<String, String> correctEntry = wordList.get(0);

        // Tworzymy listę błędnych odpowiedzi
        Set<String> incorrectAnswers = new HashSet<>();
        int i = 1;
        while (incorrectAnswers.size() < 3 && i < wordList.size()) {
            String incorrectAnswer = wordList.get(i).getValue();
            if (!incorrectAnswer.equals(correctEntry.getValue())) {
                incorrectAnswers.add(incorrectAnswer);
            }
            i++;
        }

        // Dodajemy prawidłową i nieprawidłowe odpowiedzi do listy wyboru
        List<String> options = new ArrayList<>(incorrectAnswers);
        options.add(correctEntry.getValue());
        Collections.shuffle(options);

        Panel matchingPanel = new Panel();
        matchingPanel.addComponent(new Label("Wybierz tłumaczenie dla: " + correctEntry.getKey()));

        for (String option : options) {
            matchingPanel.addComponent(new Button(option, () -> {
                if (option.equals(correctEntry.getValue())) {
                    showMessage("Poprawnie!", "Gratulacje! To była poprawna odpowiedź.");
                } else {
                    showMessage("Niepoprawnie", "Spróbuj ponownie! Poprawna odpowiedź to: " + correctEntry.getValue());
                }
                updateMainPanel("matching");
            }));
        }

        matchingPanel.addComponent(new Button("Powrót", () -> updateMainPanel("main")));
        mainPanel.addComponent(matchingPanel);
        guiScreen.getScreen().refresh();
    }

    private static void setupLearningPanel() {
        List<Map.Entry<String, String>> wordList = new ArrayList<>(dictionary.entrySet());
        Collections.shuffle(wordList);

        int[] index = {0};
        int[] correctCount = {0};
        int[] incorrectCount = {0};
        int[] skippedCount = {0};

        // Lista do przechowywania pytań, które zostały pominięte po raz pierwszy
        List<Map.Entry<String, String>> firstSkippedQuestions = new ArrayList<>();

        // Wywołanie metody do wyświetlenia pytania
        showLearningContent(wordList, firstSkippedQuestions, index, correctCount, incorrectCount, skippedCount);
    }

    private static void showLearningContent(List<Map.Entry<String, String>> wordList, List<Map.Entry<String, String>> firstSkippedQuestions,
                                            int[] index, int[] correctCount, int[] incorrectCount, int[] skippedCount) {
        mainPanel.removeAllComponents();

        // Sprawdza, czy indeks jest poza listą pytań
        if (index[0] >= wordList.size()) {
            showMessage("Koniec nauki!", "Poprawne odpowiedzi: " + correctCount[0] +
                    "\nBłędne odpowiedzi: " + incorrectCount[0] +
                    "\nPominięte odpowiedzi: " + skippedCount[0]);
            updateMainPanel("main");
            return;
        }

        // Wyświetla pytanie
        Map.Entry<String, String> currentQuestion = wordList.get(index[0]);
        mainPanel.addComponent(new Label("Podaj tłumaczenie dla: " + currentQuestion.getKey()));
        TextBox answerInput = new TextBox();
        mainPanel.addComponent(answerInput);

        // Przycisk "Sprawdź" do sprawdzania odpowiedzi
        mainPanel.addComponent(new Button("Sprawdź", () -> {
            String correctAnswer = currentQuestion.getValue();
            String userAnswer = answerInput.getText().trim();

            if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                correctCount[0]++;
                showMessage("Poprawnie", "Dobrze! Poprawna odpowiedź.");
            } else {
                incorrectCount[0]++;
                showMessage("Niepoprawnie", "Źle. Poprawna odpowiedź:\n" + correctAnswer);
            }

            index[0]++;
            showLearningContent(wordList, firstSkippedQuestions, index, correctCount, incorrectCount, skippedCount);
        }));

        // Przycisk "Pomiń" do pominięcia pytania
        mainPanel.addComponent(new Button("Pomiń", () -> {
            String correctAnswer = currentQuestion.getValue();

            // Jeśli pytanie jest pominięte po raz pierwszy, pokażemy poprawną odpowiedź i przeniesiemy je na koniec listy
            if (!firstSkippedQuestions.contains(currentQuestion)) {
                skippedCount[0]++;
                firstSkippedQuestions.add(currentQuestion);
                wordList.add(currentQuestion);  // Dodajemy na koniec listy do ponownego podejścia
            } else {
                // Jeśli już było raz pominięte, zwiększamy licznik ostatecznych pominięć
                skippedCount[0]++;
                showMessage("Pominięte", "Poprawna odpowiedź to:\n" + correctAnswer);
            }

            index[0]++;
            showLearningContent(wordList, firstSkippedQuestions, index, correctCount, incorrectCount, skippedCount);
        }));

        // Przycisk "Anuluj" do powrotu do głównego menu
        mainPanel.addComponent(new Button("Anuluj", () -> updateMainPanel("main")));
        guiScreen.getScreen().refresh();
    }

    private static void showMessage(String title, String message) {
        Window messageWindow = new Window(title);
        messageWindow.setWindowSizeOverride(new TerminalSize(30, 7));

        Panel panel = new Panel();
        panel.addComponent(new Label(message));
        panel.addComponent(new Button("OK", messageWindow::close));

        messageWindow.addComponent(panel);
        guiScreen.showWindow(messageWindow, GUIScreen.Position.CENTER);
    }

    private static void saveDictionary() {
        String fileName = promptFileName("Zapisz słownik", "Podaj nazwę pliku:");
        if (!fileName.isEmpty()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                    writer.println(entry.getKey() + "=" + entry.getValue());
                }
                showMessage("Zapisano", "Słownik został zapisany do pliku: " + fileName);
            } catch (IOException e) {
                showMessage("Błąd zapisu", "Nie udało się zapisać słownika do pliku: " + fileName);
            }
        }
    }

    private static void loadDictionary() {
        String fileName = promptFileName("Wczytaj słownik", "Podaj nazwę pliku do wczytania:");
        if (!fileName.isEmpty()) {
            dictionary.clear();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        dictionary.put(parts[0], parts[1]);
                    }
                }
                showMessage("Wczytano", "Słownik został wczytany z pliku: " + fileName);
            } catch (IOException e) {
                showMessage("Błąd odczytu", "Nie udało się wczytać słownika z pliku: " + fileName);
            }
        }
    }

    private static String promptFileName(String windowTitle, String promptText) {
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
        guiScreen.showWindow(fileNameWindow, GUIScreen.Position.CENTER);

        return fileName[0];
    }

    private static void exit() {
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
