package boggle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import tree.LexicographicTree;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class BoggleTest {
	private static final Set<String> EXPECTED_WORDS = new TreeSet<>(Arrays.asList(new String[] {"ces", "cesse", "cessent", "cresson", "ego", "encre",
			"encres", "engonce", "engoncer", "engonces", "esse", "gens", "gent", "gesse", "gnose", "gosse", "nes", "net", "nos", "once",
			"onces", "ose", "osent", "pre", "pres", "presse", "pressent", "ressent", "sec", "secs", "sen", "sent", "set", "son",
			"songe", "songent", "sons", "tenson", "tensons", "tes"}));
	private static final String GRID_LETTERS = "rhreypcswnsntego";
	private static LexicographicTree dictionary = null;

	@BeforeAll
	public static void initTestDictionary() throws NoSuchFileException {
		System.out.print("Loading dictionary...");
		dictionary = new LexicographicTree("mots/dictionnaire_FR_sans_accents.txt");
		System.out.println(" done.");
	}
	
	@Test
	void wikipediaExample() {
		Boggle b = new Boggle(4, GRID_LETTERS, dictionary);
		assertNotNull(b);
//		assertEquals(GRID_LETTERS, b.letters());
//		assertTrue(b.contains("songent"));
//		assertFalse(b.contains("sono"));
		assertEquals(EXPECTED_WORDS, b.solve());
	}

}
