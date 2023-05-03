package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

	private LexicographicTree dict;
	private List<String> cryptogramWords;

	/*
	 * CONSTRUCTOR
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {

		cryptogramWords = Arrays.stream(cryptogram.split("\\s+"))
				.filter(this::isLongEnough)
				.sorted(this::comparelength)
				.distinct().collect(Collectors.toList());
		this.dict = dict;

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

		return guessAlphabet(alphabet);

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

	// methode pour le stream le tri des mots
	private boolean isLongEnough(String word) {
		var fre = getFrenquency(word);
		boolean is = false;
		for (int i : fre) {
			System.out.println(i);
			if(i>2) {
				is = true;
				break;
			}
		}
		return is && word.length() > 3;
	}

	private int comparelength(String o1, String o2) {
		if (o1.length() != o2.length()) {
			return o2.length() - o1.length(); // overflow impossible since lengths are non-negative
		}
		return o2.compareTo(o1);
	}

	private String guessAlphabet(String alphabet) {

		// copy de la list pour la remmetre a 0 et ajouter que les mots non trouver dans
		// le dictionnaire
		int alphaScore = 0;
		int oldAlphaScrore = 0;
		String betterAlpha = alphabet;
		int i = 0;
		do {

			oldAlphaScrore = alphaScore;

			// application of the substitution
			for (int j = 0; j < this.cryptogramWords.size(); j++) {
				String decrypt = applySubstitution(this.cryptogramWords.get(j), alphabet);
				if (dict.containsWord(decrypt.toLowerCase())) {
					alphaScore++;
				}
			}
			// get better alpha
			if (oldAlphaScrore <= alphaScore) {
				betterAlpha = alphabet;
				//System.out.println(betterAlpha + "is the best alpha");
			}

			var cryptWord = cryptogramWords.get(i);
			var wordLength = dict.getWordsOfLength(cryptWord.length());
			for (String wordDict : wordLength) {
				if (isCompatible(wordDict, cryptWord)) {
					alphabet = makeAlpha(wordDict, cryptWord, alphabet);
					break;
				}
			}
			i++;
			// break;
			// analise of the longest word
			System.out.println(i + " sur " + cryptogramWords.size());

		} while (i < this.cryptogramWords.size());

		return betterAlpha;

	}

	private String makeAlpha(String wordDict, String cryptWord, String alpha) {
		for (int i = 0; i < wordDict.length(); i++) {
			char letterDict = wordDict.toUpperCase().charAt(i);
			char letterCrypt = cryptWord.charAt(i);
			alpha = swapLetters(alpha, alpha.indexOf(letterDict), alpha.indexOf(letterCrypt));

		}

		return alpha;
	}

	public String swapLetters(String str, int i, int j) {
		char[] chars = str.toCharArray();
		char temp = chars[i];
		chars[i] = chars[j];
		chars[j] = temp;
		return new String(chars);
	}

	private boolean isCompatible(String dictWord, String currentWord) {
		var frequenceDictWord = getFrenquency(dictWord);
		var frequenceCurrentWord = getFrenquency(currentWord);

		return !dictWord.contains("\'") && !dictWord.contains("-")
				&& frequenceDictWord.length == frequenceCurrentWord.length
				&& Arrays.equals(frequenceDictWord, frequenceCurrentWord);
	}

	private int[] getFrenquency(String word) {
		// TODO condition de verif
		int[] frenquency = new int[] { 1 };

		var letters = word.toCharArray();
		for (int i = 1; i < letters.length; i++) {
			if (letters[i] == letters[i - 1]) {
				frenquency[frenquency.length - 1]++;
			} else {
				frenquency = Arrays.copyOf(frenquency, frenquency.length + 1);
				frenquency[frenquency.length - 1]++;
			}
		}

		return frenquency;
	}
	
	private boolean isUtil(String word) {
		Map<Character, Integer> fr = new HashMap<>();
		
		return true;
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
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dict);
//		String startAlphabet = LETTERS;
		String startAlphabet = DECODING_ALPHABET;
//		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);

		// Display final results
		System.out.println();
		System.out.println("Decoding     alphabet : " + LETTERS);
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + finalAlphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, finalAlphabet));
		System.out.println();

		// Display decoded text
//		System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, 200));
//		System.out.println();
	}
}
