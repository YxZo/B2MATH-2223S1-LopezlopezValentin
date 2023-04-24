package tree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;

/* ---------------------------------------------------------------- */

/*
 * Constructor
 */
public class LexicographicTreeTestFile {
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

}
