package tree;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;

/* ---------------------------------------------------------------- */

/*
 * Constructor
 */
public class LexicographicTreeTest {
	private static final String[] WORDS = new String[] { "aide", "as", "au", "aux", "bu", "bus", "but", "et", "ete" };
	private static final LexicographicTree DICT = new LexicographicTree();

	@BeforeAll
	static void initTestDictionary() {
		for (int i = 0; i < WORDS.length; i++) {
			DICT.insertWord(WORDS[i]);
		}
	}

	@Test
	void constructor_EmptyDictionary() {
		LexicographicTree dict = new LexicographicTree();
		assertNotNull(dict);
		assertEquals(0, dict.size());
	}

	@Test
	void insertWord_General() {
		LexicographicTree dict = new LexicographicTree();
		for (int i = 0; i < WORDS.length; i++) {
			dict.insertWord(WORDS[i]);
			assertEquals(i + 1, dict.size(), "Mot " + WORDS[i] + " non inséré");
			dict.insertWord(WORDS[i]);

			assertEquals(i + 1, dict.size(), "Mot " + WORDS[i] + " en double");
		}
	}

	@Test
	void containsWord_General() {
		for (String word : WORDS) {
			assertTrue(DICT.containsWord(word), "Mot " + word + " non trouvé");
		}
		for (String word : new String[] { "", "aid", "ai", "aides", "mot", "e" }) {
			assertFalse(DICT.containsWord(word), "Mot " + word + " inexistant trouvé");
		}
	}

	@Test
	void getWords_General() {

		assertEquals(WORDS.length, DICT.getWords("").size());
		assertArrayEquals(WORDS, DICT.getWords("").toArray());

		assertEquals(0, DICT.getWords("x").size());

		assertEquals(3, DICT.getWords("bu").size());
		assertArrayEquals(new String[] { "bu", "bus", "but" }, DICT.getWords("bu").toArray());
	}

	@Test
	void getWordsOfLength_General() {
		assertEquals(4, DICT.getWordsOfLength(3).size());
		assertArrayEquals(new String[] { "aux", "bus", "but", "ete" }, DICT.getWordsOfLength(3).toArray());
	}

	@Test
	void testEmptyTree() {
		LexicographicTree tree = new LexicographicTree();
		assertEquals(0, tree.size());
	}

	@Test
	void testInsertWord() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		assertTrue(tree.containsWord("chat"));
		assertEquals(1, tree.size());
	}

	@Test
	void testInsertDuplicateWord() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chat");
		assertEquals(1, tree.size());
	}

	@Test
	void testInsertWordWithHyphenAndApostrophe() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("aujourd'hui");
		tree.insertWord("tire-bouchon");
		assertTrue(tree.containsWord("aujourd'hui"));
		assertTrue(tree.containsWord("tire-bouchon"));
		assertEquals(2, tree.size());
	}

	@Test
	void testContainsWord() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chien");
		assertTrue(tree.containsWord("chien"));
		assertFalse(tree.containsWord("chat"));
	}

	@Test
	void testGetWordsWithPrefix() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");

		List<String> wordsWithPrefix = tree.getWords("ch");
		assertEquals(3, wordsWithPrefix.size());
		assertEquals("chat", wordsWithPrefix.get(0));
		assertEquals("cheval", wordsWithPrefix.get(1));
		assertEquals("chien", wordsWithPrefix.get(2));
	}

	@Test
	void testGetWordsOfLength() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");

		List<String> wordsOfLength = tree.getWordsOfLength(4);
		assertEquals(1, wordsOfLength.size());
		assertTrue(wordsOfLength.contains("chat"));
	}
	 @Test
	    void testInsertEmptyWord() {
		 LexicographicTree tree = new LexicographicTree();
	        tree.insertWord("");
	        assertEquals(0, tree.size());
	    }

	    @Test
	    void testContainsEmptyWord() {
	    	LexicographicTree tree = new LexicographicTree();
	        assertFalse(tree.containsWord(""));
	    }

	    @Test
	    void testGetWordsWithNonExistentPrefix() {
	    	LexicographicTree tree = new LexicographicTree();
	        tree.insertWord("chat");
	        tree.insertWord("chien");
	        tree.insertWord("cheval");
	        tree.insertWord("oiseau");

	        List<String> wordsWithNonExistentPrefix = tree.getWords("xyz");
	        assertEquals(0, wordsWithNonExistentPrefix.size());
	    }

	    @Test
	    void testGetWordsWithPrefixLongerThanWord() {
	    	LexicographicTree tree = new LexicographicTree();
	        tree.insertWord("chat");
	        tree.insertWord("chien");
	        tree.insertWord("cheval");
	        tree.insertWord("oiseau");

	        List<String> wordsWithLongPrefix = tree.getWords("chienoiseau");
	        assertEquals(0, wordsWithLongPrefix.size());
	    }
	    
	    @Test
	    void testInsertWordsFromFile() {
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
	    void testEmptyPrefix() {
	    	LexicographicTree tree = new LexicographicTree();
	        tree.insertWord("chat");
	        tree.insertWord("chien");
	        tree.insertWord("cheval");
	        tree.insertWord("oiseau");

	        List<String> allWords = tree.getWords("");
	        assertEquals(4, allWords.size());
	        assertTrue(allWords.contains("chat"));
	        assertTrue(allWords.contains("chien"));
	        assertTrue(allWords.contains("cheval"));
	        assertTrue(allWords.contains("oiseau"));
	    }

	    @Test
	    void testGetWordsOfInvalidLength() {
	    	LexicographicTree tree = new LexicographicTree();
	        tree.insertWord("chat");
	        tree.insertWord("chien");
	        tree.insertWord("cheval");
	        tree.insertWord("oiseau");

	        List<String> wordsOfInvalidLength = tree.getWordsOfLength(-1);
	        assertEquals(0, wordsOfInvalidLength.size());
	    }
	    
	    @Test
	    void testInsertAndSearchWords() {

	    	LexicographicTree tree = new LexicographicTree();
	        tree.insertWord("chat");
	        tree.insertWord("chien");
	        tree.insertWord("cheval");

	        assertTrue(tree.containsWord("chat"));
	        assertTrue(tree.containsWord("chien"));
	        assertTrue(tree.containsWord("cheval"));
	        assertFalse(tree.containsWord("oiseau"));
	        assertEquals(3, tree.size());
	    }

	    @Test
	    void testInsertAndSearchWordsWithSpecialCharacters() {
	    	LexicographicTree tree = new LexicographicTree();
	        tree.insertWord("aujourd'hui");
	        tree.insertWord("tire-bouchon");

	        assertTrue(tree.containsWord("aujourd'hui"));
	        assertTrue(tree.containsWord("tire-bouchon"));
	        assertFalse(tree.containsWord("chat"));
	        assertEquals(2, tree.size());
	    }

	    @Test
	    void testInsertAndGetWordsWithPrefixAndLength() {
	    	LexicographicTree tree = new LexicographicTree();
	        tree.insertWord("chat");
	        tree.insertWord("chien");
	        tree.insertWord("cheval");
	        tree.insertWord("oiseau");

	        List<String> wordsWithPrefix = tree.getWords("ch");
	        List<String> wordsOfLength = tree.getWordsOfLength(4);
	        
	        assertEquals(3, wordsWithPrefix.size());
	        assertEquals(1, wordsOfLength.size());
	        assertTrue(wordsWithPrefix.contains("chat"));
	        assertTrue(wordsWithPrefix.contains("cheval"));
	        assertTrue(wordsWithPrefix.contains("chien"));
	        assertTrue(wordsOfLength.contains("chat"));
	        assertFalse(wordsOfLength.contains("chien"));
	    }
	    
	    @ParameterizedTest
	    @MethodSource("insertWordsArgumentsProvider")
	    void testInsertWords(String word1, String word2, String word3, int expectedSize) {

	    	LexicographicTree tree = new LexicographicTree();
	        tree.insertWord(word1);
	        tree.insertWord(word2);
	        tree.insertWord(word3);

	        assertEquals(expectedSize, tree.size());
	    }

	    static Stream<Arguments> insertWordsArgumentsProvider() {
	        return Stream.of(
	            Arguments.of("chat", "chien", "cheval", 3),
	            Arguments.of("aujourd'hui", "tire-bouchon", "chat", 3),
	            Arguments.of("chat", "chat", "chien", 2)
	        );
	    }

	    @ParameterizedTest
	    @MethodSource("getWordsArgumentsProvider")
	    void testGetWords(String insertedWord, String prefix, int expectedSize) {
	    	LexicographicTree tree = new LexicographicTree();
	        tree.insertWord(insertedWord);

	        List<String> wordsWithPrefix = tree.getWords(prefix);
	        assertEquals(expectedSize, wordsWithPrefix.size());
	    }

	    static Stream<Arguments> getWordsArgumentsProvider() {
	        return Stream.of(
	            Arguments.of("chat", "ch", 1),
	            Arguments.of("aujourd'hui", "aujourd", 1),
	            Arguments.of("chien", "chat", 0)
	        );
	    }

}
