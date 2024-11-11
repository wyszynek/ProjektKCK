import Model.DictionaryModel;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DictionaryModelTest {
    private DictionaryModel dictionaryModel;

    @Before
    public void setUp() {
        dictionaryModel = new DictionaryModel();
        dictionaryModel.addWord("pies", "dog");
    }

    @Test
    public void testAddWord() {
        // Dodajemy słowo do słownika
        dictionaryModel.addWord("kot", "cat");

        // Sprawdzamy, czy słowo zostało dodane poprawnie
        assertEquals("cat", dictionaryModel.getDictionary().get("kot"));
    }

    @Test
    public void testRemoveWord() {
        // Usuwamy słowo "pies" ze słownika
        boolean removed = dictionaryModel.removeWord("pies");

        // Sprawdzamy, czy metoda zwróciła true (usunięcie się powiodło)
        assertTrue(removed);

        // Sprawdzamy, czy słowo zostało faktycznie usunięte ze słownika
        assertNull(dictionaryModel.getDictionary().get("pies"));
    }
}
