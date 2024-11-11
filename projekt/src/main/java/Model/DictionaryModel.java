package Model;

import java.io.*;
import java.util.*;

public class DictionaryModel {
    private Map<String, String> dictionary = new HashMap<>();
    private List<String> shuffledWords = new ArrayList<>();
    private List<String> skippedWords = new ArrayList<>();
    private List<String> skippedWordsAsMapValue = new ArrayList<>();
    private int currentIndex = 0;
    private int correctAnswers = 0;
    private int incorrectAnswers = 0;
    private int skippedAnswers = 0;
    private Random random = new Random();
    private boolean translateToEnglish = true;

    public Map<String, String> getDictionary() {
        return this.dictionary;
    }

    public void addWord(String word, String translation) {
        dictionary.put(word, translation);
    }
    public boolean removeWord(String word) {
        if (dictionary.containsKey(word)) {
            dictionary.remove(word);
            return true;
        } else {
            // Szukamy w wartościach
            String keyToRemove = null;
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                if (entry.getValue().equals(word)) {
                    keyToRemove = entry.getKey();
                    break;
                }
            }
            if (keyToRemove != null) {
                dictionary.remove(keyToRemove);
                return true;
            }
        }
        return false;
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

    public void shuffleWords() {
        Collections.shuffle(shuffledWords, random);
        currentIndex = 0; // Resetujemy bieżący indeks, aby po przetasowaniu zaczął zaczynać się od początku
    }

    public String getNextWordLearning() {
        if (currentIndex < shuffledWords.size()) {
            String word = shuffledWords.get(currentIndex++);
            translateToEnglish = !translateToEnglish; // tryb tłumaczenia
            return word;
        }
        return null;
    }

    public boolean shouldTranslateToEnglish() {
        return translateToEnglish;
    }

    public boolean isCorrectTranslation(String word, String userTranslation) {
        String correctTranslation = null;

        if (dictionary.containsKey(word)) {
            correctTranslation = dictionary.get(word); // Tłumaczenie z polskiego na angielski
        }
        // Jeśli 'word' nie jest kluczem, sprawdzamy, czy jest wartością
        else if (dictionary.containsValue(word)) {
            correctTranslation = getKeyByValue(dictionary, word); // Tłumaczenie z angielskiego na polski
        }

        if (correctTranslation == null) {
            return false; // Brak tłumaczenia
        }

        boolean correct = correctTranslation.equalsIgnoreCase(userTranslation);
        boolean inSkippedList = skippedWords.contains(word);
        boolean inSkippedValue = skippedWordsAsMapValue.contains(word);

        if (correct) {
            if (inSkippedList || inSkippedValue) {
                skippedAnswers--;
            }
            correctAnswers++;
        } else {
            if(inSkippedList || inSkippedValue) {
                skippedAnswers--;
            }
            incorrectAnswers++;
        }

        return correct;
    }

    public String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void increaseSkippedAnswers() {
        skippedAnswers++;
    }

    public void markAnswerAsCorrect() {
        correctAnswers++;
        incorrectAnswers = Math.max(0, incorrectAnswers - 1);
    }

    public void resetProgress() {
        currentIndex = 0;
        skippedWords.clear();
        skippedWordsAsMapValue.clear();
        shuffledWords.clear();
        shuffledWords.addAll(dictionary.keySet());
        Collections.shuffle(shuffledWords, random);
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

    public List<String> getSkippedWords() {
        return this.skippedWords;
    }

    public List<String> getSkippedWordsAsMapValue() {
        return this.skippedWordsAsMapValue;
    }

    public List<String> getShuffledWords() {
        return this.shuffledWords;
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

    public String getNextWord() {
        if (currentIndex < shuffledWords.size()) {
            return shuffledWords.get(currentIndex++);
        }
        return null;
    }

    public boolean isCorrectTranslationMatching(String word, String userTranslation) {
        boolean correct = dictionary.get(word).equalsIgnoreCase(userTranslation);
        boolean inSkippedList = skippedWords.contains(word);
        if (correct) {
            if(inSkippedList) {
                skippedAnswers--;
            }
            correctAnswers++;
        } else {
            if(inSkippedList) {
                skippedAnswers--;
            }
            incorrectAnswers++;
        }
        return correct;
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
