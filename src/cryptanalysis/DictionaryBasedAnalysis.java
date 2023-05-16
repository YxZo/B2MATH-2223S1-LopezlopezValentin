package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tree.LexicographicTree;

public class DictionaryBasedAnalysis {

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "mots/dictionnaire_FR_sans_accents.txt";

	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock

	private static final boolean DEBUG = false;

	private LexicographicTree dictionary;
	private List<String> cryptogramWords;

	private static long getCompatibleWordTime = 0;
	private static long RecreatAlphaTime = 0;
	private static long scoreAlphaTime = 0;

	/*
	 * CONSTRUCTOR
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {

		cryptogramWords = Arrays.stream(cryptogram.split("\\s+")).filter(this::filterCrypto).sorted(this::comparelength)
				.distinct().collect(Collectors.toList());
		this.dictionary = dict;

	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Performs a dictionary-based analysis of the cryptogram and returns an
	 * approximated decoding alphabet.
	 * 
	 * @param alphabet The decoding alphabet from which the analysis starts
	 * @return The decoding alphabet at the end of the analysis process
	 */
	public String guessApproximatedAlphabet(String alphabet) {
		if (DEBUG)
			System.out.printf("debut du traitement pour %d mots\n", cryptogramWords.size());

		int currentWordSize = 0;
		String betterAlpha = alphabet;
		int betterScore = 0;
		List<String> listWordOfLenth = new ArrayList<>();
		for (String wordCrypt : cryptogramWords.stream().filter(this::containsRecurringLetters).toList()) {

			// condition si le mot est deja decrypter il n'est plus utilse il ne modifiera
			// pas l'aphabet
			if (dictionary.containsWord(applySubstitution(wordCrypt, betterAlpha).toLowerCase())) {
				continue;
			}
			// permet de faire moin appel au dictionnaire
			// juste quand la taille du mot change
			if (currentWordSize != wordCrypt.length()) {
				currentWordSize = wordCrypt.length();
				listWordOfLenth = dictionary.getWordsOfLength(currentWordSize);
				if (DEBUG)
					System.out.printf(">>>>mot de taille %d(mot trouver dans le dictionnaire: %d)\n\n", currentWordSize,
							listWordOfLenth.size());
			}
			// obtention de la list de mot compatible avec le mùot crypter
			var compatibleWord = getCompatibleWord(wordCrypt, listWordOfLenth);
			
			if (compatibleWord == null)
				continue;

			if (DEBUG) {
				System.out.printf("le mot       :%s\n", wordCrypt);
				System.out.printf("correspond a :%s\n", compatibleWord);
				System.out.printf("le decrypte  :%s\n", applySubstitution(wordCrypt, betterAlpha));
				System.out.printf("il est       :%s\n\n", applySubstitution(wordCrypt, DECODING_ALPHABET));
			}
			// recree l'apha
			alphabet = updateAlpha(betterAlpha, wordCrypt, compatibleWord);

			// calul du score
			int score = getScoreAlphabet(alphabet);
			// on le prend ?
			if (betterScore < score) {
				betterScore = score;
				betterAlpha = alphabet;

			}

			if (DEBUG) {
				System.out.println();
				System.out.println("CHANGE WORD");
				System.out.println();
			}

		}

		return betterAlpha;
	}

	/**
	 * Applies an alphabet-specified substitution to a text.
	 * 
	 * @param text     A text
	 * @param alphabet A substitution alphabet
	 * @return The substituted text
	 */
	public static String applySubstitution(String text, String alphabet) {
		String wordSubstitued = "";
		for (char c : text.toUpperCase().toCharArray()) {
			int index = LETTERS.indexOf(c);
			wordSubstitued += index >= 0 ? alphabet.charAt(index) : c;
		}
		return wordSubstitued;
	}

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * Compares two substitution alphabets.
	 * 
	 * @param a First substitution alphabet
	 * @param b Second substitution alphabet
	 * @return A string where differing positions are indicated with an 'x'
	 */
	private static String compareAlphabets(String a, String b) {
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			result += (a.charAt(i) == b.charAt(i)) ? " " : "x";
		}
		return result;
	}

	/**
	 * Load the text file pointed to by pathname into a String.
	 * 
	 * @param pathname A path to text file.
	 * @param encoding Character set used by the text file.
	 * @return A String containing the text in the file.
	 * @throws IOException
	 */
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static String getPaternWord(String word) {
		if (word.contains("-") || word.contains("\'")) {
			return null;
		}
		String patern = "";
		Map<Character, Integer> corespondance = new HashMap<>();

		int num = 0;

		for (char c : word.toCharArray()) {
			if (!corespondance.containsKey(c)) {
				corespondance.put(c, num++);
			}
			patern += corespondance.get(c);
		}

		return patern;
	}

	private boolean filterCrypto(String word) {
		return word.length() > 2;
	}

	private boolean containsRecurringLetters(String word) {
		int[] letterCount = new int[26];
		for (int i = 0; i < word.length(); i++) {
			char letter = word.charAt(i);
			if (Character.isLetter(letter)) {
				int index = Character.toLowerCase(letter) - 'a';
				letterCount[index]++;
				if (letterCount[index] > 1) {
					return true;
				}
			}
		}
		return false;
	}

	private int comparelength(String o1, String o2) {
		if (o1.length() != o2.length()) {
			return o2.length() - o1.length();
		}
		return o2.compareTo(o1);
	}

	private String getCompatibleWord(String wordCrypt, List<String> listOfWord) {
		long startTime = System.currentTimeMillis();
		String paternCryptWord = getPaternWord(wordCrypt);
		for (String word : listOfWord) {
			if (word.contains("-") || word.contains("\'"))
				continue;
			if (paternCryptWord.equals(getPaternWord(word))) {
				getCompatibleWordTime += System.currentTimeMillis() - startTime;
				return word;
			}
		}
		getCompatibleWordTime += System.currentTimeMillis() - startTime;
		return null;
	}

	private int getScoreAlphabet(String alphabet) {
		long startTime = System.currentTimeMillis();
		int numWordDecrypt = 0;

		for (String word : cryptogramWords) {
			String decrypt = applySubstitution(word, alphabet).toLowerCase();
			if (dictionary.containsWord(decrypt))
				numWordDecrypt++;
		}
		scoreAlphaTime += System.currentTimeMillis() - startTime;
		if (DEBUG) {
			System.out.printf("=> Score decoded : words = %d / valid = %d / invalid = %d\n\n", cryptogramWords.size(),
					numWordDecrypt, cryptogramWords.size() - numWordDecrypt);
		}
		return numWordDecrypt;
	}

	private String updateAlpha(String alpha, String cryptWord, String dictWord) {
		long startTime = System.currentTimeMillis();
		dictWord = dictWord.toUpperCase();
		var newAlpha = alpha;
		for (int i = 0; i < cryptWord.toCharArray().length; i++) {
			int pos = LETTERS.indexOf(cryptWord.charAt(i));
			int posLetterToswap = newAlpha.indexOf(dictWord.charAt(i));

			newAlpha = swapLetters(newAlpha, pos, posLetterToswap);
		}
		RecreatAlphaTime += System.currentTimeMillis() - startTime;

		if (DEBUG) {
			System.out.printf("better       alphabet :%s\n", alpha);
			System.out.printf("New approxim alphabet :%s\n", newAlpha);
			System.out.printf("        Modifications :%s\n\n", compareAlphabets(alpha, newAlpha));
		}

		return newAlpha;
	}

	private String swapLetters(String str, int i, int j) {
		char[] chars = str.toCharArray();
		char temp = chars[i];
		chars[i] = chars[j];
		chars[j] = temp;
		return new String(chars);

	}

	/*
	 * MAIN PROGRAM
	 */

	public static void main(String[] args) {
		/*
		 * Load dictionary
		 */
		System.out.print("Loading dictionary... ");
		LexicographicTree dict = new LexicographicTree(DICTIONARY);
		System.out.println("done.");
		System.out.println();

		/*
		 * Load cryptogram
		 */
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
//		System.out.println("*** CRYPTOGRAM ***\n" + cryptogram.substring(0, 100));
//		System.out.println();

		/*
		 * Decode cryptogram
		 */
		long startTime = System.currentTimeMillis();
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dict);
//		String startAlphabet = LETTERS;
		String startAlphabet = DECODING_ALPHABET;
//		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);

		// Display final results
		System.out.println("time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println("similarTime :" + getCompatibleWordTime / 1000.0);
		System.out.println("RecreatAlphaTime :" + RecreatAlphaTime / 1000.0);
		System.out.println("scoreAlphaTime :" + scoreAlphaTime / 1000.0);
		System.out.println();
		System.out.println("Decoding     alphabet : " + LETTERS);
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + finalAlphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, finalAlphabet));
		System.out.println();

		// Display decoded text
		System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, 200));
		System.out.println();
	}
}