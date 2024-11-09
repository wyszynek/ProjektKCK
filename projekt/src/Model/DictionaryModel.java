package Model;

import java.io.*;
import java.util.*;

public class DictionaryModel {
    private Map<String, String> dictionary = new HashMap<>();
    private Set<String> testedWords = new HashSet<>();
    private int correctAnswers = 0;
    private int incorrectAnswers = 0;
    private int skippedAnswers = 0;
    private Random random = new Random();

    public Map<String, String> getDictionary() {
        return this.dictionary;
    }

    public void addWord(String word, String translation) {
        dictionary.put(word, translation);
    }

    public boolean wordExists(String word) {
        return dictionary.containsKey(word);
    }

    public String searchWord(String word) {
        // Najpierw sprawdzamy, czy 'word' jest kluczem w słowniku
        String translation = dictionary.get(word);

        // Jeśli nie znaleziono tłumaczenia w pierwszej próbie (po kluczu), sprawdzamy odwrotną stronę
        if (translation == null) {
            // Iterujemy po wartościach (tłumaczeniach) w słowniku
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(word)) {
                    // Jeśli znajdziemy odpowiednie tłumaczenie, zwróć klucz
                    translation = entry.getKey();
                    break;
                }
            }
        }

        // Jeśli wciąż brak tłumaczenia, zwróć null (słowo nie istnieje w słowniku)
        return translation;
    }

    public String getRandomWord() {
        if (testedWords.size() == dictionary.size()) {
            return null; // Wszystkie słówka zostały przetestowane
        }
        String word;
        do {
            Object[] words = dictionary.keySet().toArray();
            word = (String) words[random.nextInt(words.length)];
        } while (testedWords.contains(word));

        testedWords.add(word);
        return word;
    }

    public boolean isCorrectTranslation(String word, String userTranslation) {
        boolean correct = dictionary.get(word).equalsIgnoreCase(userTranslation);
        if (correct) {
            correctAnswers++;
        } else {
            incorrectAnswers++;
        }
        return correct;
    }

    public int increaseSkippedAnswers() {
        return skippedAnswers++;
    }
    public void markAnswerAsCorrect() {
        correctAnswers++;
        incorrectAnswers = Math.max(0, incorrectAnswers - 1);
    }
    public void resetProgress() {
        testedWords.clear();
        correctAnswers = 0;
        incorrectAnswers = 0;
        skippedAnswers = 0;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public int getSkippedAnswers() {
        return skippedAnswers;
    }

    public List<String> getMatchingOptions(String word, int numberOfOptions) {
        List<String> options = new ArrayList<>();
        String correctTranslation = dictionary.get(word);

        // Dodajemy poprawne tłumaczenie do listy
        options.add(correctTranslation);

        // Pobieramy losowe tłumaczenia
        List<String> allTranslations = new ArrayList<>(dictionary.values());
        allTranslations.remove(correctTranslation); // Usuwamy poprawne tłumaczenie, by się nie powtórzyło

        Collections.shuffle(allTranslations); // Losujemy kolejność
        for (int i = 0; i < numberOfOptions - 1 && i < allTranslations.size(); i++) {
            options.add(allTranslations.get(i));
        }

        Collections.shuffle(options); // Mieszamy opcje, aby poprawna nie zawsze była na pierwszej pozycji
        return options;
    }

    public void saveDictionary(String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                writer.println(entry.getKey() + "=" + entry.getValue());
            }
        }
    }

    public void loadDictionary(String fileName) throws IOException {
        dictionary.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    dictionary.put(parts[0], parts[1]);
                }
            }
        }
    }
}
