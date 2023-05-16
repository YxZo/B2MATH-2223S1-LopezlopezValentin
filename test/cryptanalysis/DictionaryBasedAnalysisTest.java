package cryptanalysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;

import tree.LexicographicTree;

public class DictionaryBasedAnalysisTest {
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String ENCODING_ALPHABET = "YESUMZRWFNVHOBJTGPCDLAIXQK"; // Sherlock
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	private static LexicographicTree dictionary = null;

	@BeforeAll
	public static void initTestDictionary() {
		dictionary = new LexicographicTree("mots/dictionnaire_FR_sans_accents.txt");
	}
	
	@Test
	void applySubstitutionTest() {
		String message = "DEMANDE RENFORTS IMMEDIATEMENT";
		String encoded = "UMOYBUM PMBZJPDC FOOMUFYDMOMBD";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}

	@Test
	void guessApproximatedAlphabetTest() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 9, "Moins de 9 correspondances trouvÃ©es [" + score + "]");
	}
	
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
//	@Test
//	void testIsSimilarFrequence() {

//		assertTrue(DictionaryBasedAnalysis.isWordFrequenceEquals("test", "test"));
//		assertTrue(DictionaryBasedAnalysis.isWordFrequenceEquals("rr", "rr"));
//		assertTrue(DictionaryBasedAnalysis.isWordFrequenceEquals("abc", "bde"));
//		assertFalse(DictionaryBasedAnalysis.isWordFrequenceEquals("abcd", "bde"));
//		assertTrue(DictionaryBasedAnalysis.isWordFrequenceEquals("vvv", "aaa"));
//		assertTrue(DictionaryBasedAnalysis.isWordFrequenceEquals("abc", "bde"));
//		assertTrue(DictionaryBasedAnalysis.isWordFrequenceEquals("pepepepep", "bobobobob"));
//		

//	}

//	@Test
//	void testGetFrequence() {
////		var dict = new DictionaryBasedAnalysis(CRYPTOGRAM_FILE, dictionary);
////		//var alpha = dict.recreateAlphabet("SJBCSFMBSFMLCMOMBD", "CONSCIENCIEUSEMENT", "ANSTJIGHFOKUEBMPQRCDLVWXYZ");
////		System.out.println();
////		System.out.println("     alphabet         : " + alpha);
////		System.out.println("true alphabet         : " + "ANSTJIGHFOKUEBMPQRCDLVWXYZ");
////		System.out.println("Remaining differences : " + DictionaryBasedAnalysis.compareAlphabets(alpha, "ANSTJIGHFOKUEBMPQRCDLVWXYZ"));
////		assertEquals("ANSTJIGHFOKUEBMPQRCDLVWXYZ", alpha);
//	}

}