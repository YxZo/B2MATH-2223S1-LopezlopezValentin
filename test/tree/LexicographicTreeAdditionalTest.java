package tree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LexicographicTreeAdditionalTest {
    private static final String[] WORDS = new String[]{"aide", "as", "au", "aux", "bu", "bus", "but", "et", "ete"};
    private static final LexicographicTree DICT = new LexicographicTree();

    @BeforeAll
    public static void initTestDictionary() {
        for (String word : WORDS) {
            DICT.insertWord(word);
        }
    }

    @Test
    void containsWord_EmptyWord() {
        assertFalse(DICT.containsWord(""));
    }

    @Test
    void getWordsWithNonexistentPrefix() {
        List<String> wordsWithNonexistentPrefix = DICT.getWords("xyz");
        assertEquals(0, wordsWithNonexistentPrefix.size());
    }

    @Test
    void getWordsWithPrefixLongerThanWord() {
        List<String> wordsWithLongPrefix = DICT.getWords("chienoiseau");
        assertEquals(0, wordsWithLongPrefix.size());
    }

    @Test
    void insertWordsFromFile() {
        try {
            LexicographicTree treeFromFile = new LexicographicTree("mots/dictionnaire_FR_test_1.txt");
            assertTrue(treeFromFile.containsWord("zythums"));
            assertTrue(treeFromFile.containsWord("zythum"));
            assertTrue(treeFromFile.containsWord("zythons"));
            assertTrue(treeFromFile.containsWord("zython"));
            assertEquals(4, treeFromFile.size());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void emptyPrefix() {
        List<String> allWords = DICT.getWords("");
        assertEquals(9, allWords.size());
        assertTrue(allWords.contains("aide"));
        assertTrue(allWords.contains("as"));
        assertTrue(allWords.contains("au"));
        assertTrue(allWords.contains("aux"));
        assertTrue(allWords.contains("bu"));
        assertTrue(allWords.contains("bus"));
        assertTrue(allWords.contains("but"));
        assertTrue(allWords.contains("et"));
        assertTrue(allWords.contains("ete"));
    }

    @Test
    void getWordsOfInvalidLength() {
        List<String> wordsOfInvalidLength = DICT.getWordsOfLength(-1);
        assertEquals(0, wordsOfInvalidLength.size());
    }
}
