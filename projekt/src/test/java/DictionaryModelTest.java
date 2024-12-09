import Model.DictionaryModel;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DictionaryModelTest {
    private DictionaryModel dictionaryModel;

    @Before
    public void setUp() {
        dictionaryModel = new DictionaryModel();
        dictionaryModel.addWord("pies", "dog");
        dictionaryModel.addWord("kot", "cat");
    }

    @Test
    public void testSearchWord() {
        // Szukanie tłumaczenia
        assertEquals("dog", dictionaryModel.searchWord("pies"));
        assertEquals("pies", dictionaryModel.searchWord("dog"));

        // Szukanie nieistniejącego słowa
        assertNull(dictionaryModel.searchWord("ryba"));
    }

    @Test
    public void testIsCorrectTranslation() {
        assertTrue(dictionaryModel.isCorrectTranslation("pies", "dog"));

        assertFalse(dictionaryModel.isCorrectTranslation("pies", "cat"));
    }

    @Test
    public void testMarkAnswerAsCorrect() {
        int initialCorrect = dictionaryModel.getCorrectAnswers();
        dictionaryModel.markAnswerAsCorrect();
        assertEquals(initialCorrect + 1, dictionaryModel.getCorrectAnswers());
    }

    @Test
    public void testSkipWord() {
        dictionaryModel.shuffleWords();
        dictionaryModel.increaseSkippedAnswers();

        // Sprawdzamy, czy liczba pominięć się zwiększyła
        assertEquals(1, dictionaryModel.getSkippedAnswers());
    }

    @Test
    public void testSaveAndLoadDictionary() throws Exception {
        String testFile = "testDictionary.txt";

        dictionaryModel.saveDictionary(testFile);

        DictionaryModel loadedModel = new DictionaryModel();
        loadedModel.loadDictionary(testFile);

        assertEquals(dictionaryModel.getDictionary().size(), loadedModel.getDictionary().size());
        assertEquals("dog", loadedModel.searchWord("pies"));
    }

    @Test
    public void testMatchingOptions() {
        dictionaryModel.addWord("ryba", "fish");
        dictionaryModel.addWord("dom", "house");

        List<String> options = dictionaryModel.getMatchingOptions("pies", 3);

        assertTrue(options.contains("dog"));

        assertEquals(3, options.size());
    }
}
