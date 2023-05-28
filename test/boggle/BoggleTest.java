package boggle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import tree.LexicographicTree;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;



public class BoggleTest {
	private static final Set<String> EXPECTED_WORDS = new TreeSet<>(Arrays.asList(new String[] {"ces", "cesse", "cessent", "cresson", "ego", "encre",
			"encres", "engonce", "engoncer", "engonces", "esse", "gens", "gent", "gesse", "gnose", "gosse", "nes", "net", "nos", "once",
			"onces", "ose", "osent", "pre", "pres", "presse", "pressent", "ressent", "sec", "secs", "sen", "sent", "set", "son",
			"songe", "songent", "sons", "tenson", "tensons", "tes"}));
	private static final String GRID_LETTERS = "rhreypcswnsntego";
	private static LexicographicTree dictionary = null;
	private static Boggle boggle4X4;


	@BeforeAll
	public static void initTestDictionary() {
		System.out.print("Loading dictionary...");
		dictionary = new LexicographicTree("mots/dictionnaire_FR_sans_accents.txt");
		System.out.println(" done.");
		boggle4X4 = new Boggle(4, GRID_LETTERS, dictionary);
	}

	@Test
	void wikipediaExample() {
		Boggle b = new Boggle(4, GRID_LETTERS, dictionary);
		assertNotNull(b);
		assertEquals(GRID_LETTERS, b.letters());
		assertTrue(b.contains("songent"));
		assertFalse(b.contains("sono"));
		Set<String> words = b.solve();
		assertEquals(EXPECTED_WORDS, words);
		assertTrue(words.containsAll(EXPECTED_WORDS));
		assertTrue(EXPECTED_WORDS.containsAll(words));
	}
 




	

	@Test
	void createBoggleWithMoreLetter(){
		String newDict = "rhreypcswnsntegosjdmqlmfdjnksdmljfghkhgcjhvcjfcfcfgsdhgsd";
		Boggle boggle4x4 = new Boggle(4, newDict, dictionary);
		assertNotNull(boggle4x4);
		assertEquals(newDict, boggle4x4.letters());
		assertTrue(boggle4x4.contains("songent"));
		assertFalse(boggle4x4.contains("sono"));
		Set<String> words = boggle4x4.solve();
		assertEquals(EXPECTED_WORDS, words);
		assertTrue(words.containsAll(EXPECTED_WORDS));
		assertTrue(EXPECTED_WORDS.containsAll(words));
	}

	@Test
	void createBoggleSpecifyingLettersWithIncorrectSize(){
		String letter = "rhreypcswnsntegomlkkdnqmlksqnmlnqdsmlnqsdlmndqlmdnlqs";
		assertThrows(IllegalArgumentException.class, () -> new Boggle(-3,letter, dictionary));
		assertThrows(IllegalArgumentException.class, () -> new Boggle(0,letter, dictionary));
	}

	@Test
	void createBoggleNoSpecifyingLettersWithIncorrectSize(){
		assertThrows(IllegalArgumentException.class, () -> new Boggle(-3, dictionary));
		assertThrows(IllegalArgumentException.class, () -> new Boggle(0, dictionary));
	}

	@Test
	void createBoggleWithNullLetters(){
		assertThrows(IllegalArgumentException.class, () -> new Boggle(4, null, dictionary));
	}

	@Test
	void createBoggleWithEmptyLetters(){
		assertThrows(IllegalArgumentException.class, () -> new Boggle(4, "", dictionary));
	}

	@Test
	void createBoggleWithNullDictionary(){
		assertThrows(IllegalArgumentException.class, () -> new Boggle(4, GRID_LETTERS, null));
		assertThrows(IllegalArgumentException.class, () -> new Boggle(4, null));
	}

	@Test
	void createBoggleWithEmptyDictionary(){
		assertThrows(IllegalArgumentException.class, () -> new Boggle(4, GRID_LETTERS, new LexicographicTree()));
		assertThrows(IllegalArgumentException.class, () -> new Boggle(4, new LexicographicTree()));
	}

	@Test
	void createBoggleWithUnauthorizedLetter(){
		assertThrows(IllegalArgumentException.class, () -> new Boggle(4, "rhreypcswnsnteégomlkkdnqmlksqnmlnqdsmlànqsdlmndq==lmdnlqs", dictionary));
	}




	// letters();
	@Test
	public void getLetterFromBoggleGrid(){
		String letters = boggle4X4.letters();
		String expectedLetters = "rhreypcswnsntego";
		assertEquals(expectedLetters, letters);
	}

	//Contains
	@Test
	public void containsUnexpectedWord(){
		assertFalse(boggle4X4.contains("hello"));
	}

	@Test
	public void containsEmptyWord(){
		assertFalse(boggle4X4.contains(""));
	}

	@Test
	public void containsNullWord(){
		assertFalse(boggle4X4.contains(null));
	}

	@Test
	public void containsWordWithSpaces(){
		assertFalse(boggle4X4.contains("sons fort"));
	}

	@Test
	public void containsWordWithSpecialCharacters(){
		assertFalse(boggle4X4.contains("sons!"));
		assertFalse(boggle4X4.contains("céder"));
		assertFalse(boggle4X4.contains("trône"));
	}

	@Test
	public void containsWordWithNumbers(){
		assertFalse(boggle4X4.contains("sons1"));
	}

	@Test
	public void containsWordWithUpperCase(){
		assertTrue(boggle4X4.contains("SONS"));//Car, je fais un toLowerCase au niveau de contains dans la classe Boggle avec utils.StringUtils
	}

	@Test
	public void containsWordWithLowerCase(){
		assertTrue(boggle4X4.contains("sons"));
	}

	@Test
	public void containsWordWithMixedCase(){
		assertTrue(boggle4X4.contains("sOnS"));
	}//Car, je fais un toLowerCase au niveau de contains dans la classe Boggle avec utils.StringUtils

	@Test
	public void containsWordWithNonAdjacentLetters(){
		assertFalse(boggle4X4.contains("sont"));
	}

	@Test
	public void containsWordWithAdjacentLetters(){
		assertTrue(boggle4X4.contains("cesse"));
		assertTrue(boggle4X4.contains("ces"));
		for(String word :EXPECTED_WORDS){
			System.out.println(word);
			assertTrue(boggle4X4.contains(word));
		}
	}

	@Test
	public void containsWordByUsingMultipleTimeASameVertex(){
		assertFalse(boggle4X4.contains("ses"));//s[1,3] => e[0,3] => s[1,3] || s[2,2] => e[0,3] => s[2,2] || s[2,2] => e[3,1] => s[2,2]
	}

	//Solve

	@Test
	void solveBoggle4x4(){
		Set<String> words = boggle4X4.solve();
		assertEquals(EXPECTED_WORDS.size(), words.size());
		assertTrue(words.containsAll(EXPECTED_WORDS));
		assertTrue(EXPECTED_WORDS.containsAll(words));
	}

	@Test
	void solveBoggleNonexistentWordInDictionary() {
		Set<String> words = boggle4X4.solve();
		assertFalse(words.contains("hello"));
		assertFalse(words.contains("Anticonstitutionnellement"));
	}

	@Test
	void solveBoggleDoesNotContainsWordWithLengthLowerThan3(){
		Set<String> words = boggle4X4.solve();
		assertFalse(words.contains("ci"));//Ci is a word in the dictionnary but it's length is lower than 3
	}

	/**
	 * s s s
	 * s e s
	 * s s s => ses => pleins de possibilités
	 */
	@Test
	void solveBoggleWithDifferentWaysToWriteAWord(){
		String letter = "ssssessss";
		Boggle boggle = new Boggle(3, letter, dictionary);
		Set<String> words = boggle.solve();
		assertTrue(words.contains("ses"));
		assertEquals(1, words.size());
	}

	@Test
	void solveGridWithSize1(){
		String grid = "a";
		Boggle boggle = new Boggle(1, grid, dictionary);
		Set<String> words = boggle.solve();
		assertEquals(words.size(),0);
		assertFalse(boggle.contains("a"));
	}
	@Test
	void toStringBoggle4x4(){
		String expectedString ="";
		expectedString += "r h r e\ny p c s\nw n s n\nt e g o\n";
		assertEquals(expectedString, boggle4X4.toString());
	}
}
