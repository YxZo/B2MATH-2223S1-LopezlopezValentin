package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
				.filter(this::filterCrypto)
				.sorted(this::comparelength)
				.distinct()
				.collect(Collectors.toList());
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
		List<String> wordOfLength = new ArrayList<>();
		String betterAlphabet = alphabet;
		int scoreBetterAlphabet = 0;
		for (String cryptWord : cryptogramWords) {
//			int score = getScoreAlphabet(alphabet);
//			
//			if(scoreBetterAlphabet <= score) {
//				betterAlphabet = alphabet;
//				scoreBetterAlphabet = score;
//			}
			
			
			if(wordOfLength.isEmpty() || wordOfLength.get(0).length() != cryptWord.length()) {
				wordOfLength = dict.getWordsOfLength(cryptWord.length());
			}
			
			for (String dictWord : wordOfLength) {
				dictWord = dictWord.toUpperCase();
				if (isSimilar(cryptWord, dictWord)) {
					alphabet = recreatAlphabet(cryptWord, dictWord, betterAlphabet);
					
					int score = getScoreAlphabet(alphabet);
					
					if(scoreBetterAlphabet <= score) {
						betterAlphabet = alphabet;
						scoreBetterAlphabet = score;
						break;
					}
				}
				
			}
		}
		return betterAlphabet;
	}
	
	private String recreatAlphabet(String cryptWord, String dictWord, String currentDecodingAlphabet) {
		for (int i = 0; i < cryptWord.length(); i++) {
			char letterDict = dictWord.toUpperCase().charAt(i);
			char letterCrypt = cryptWord.charAt(i);
			
			int pos = LETTERS.indexOf(letterCrypt);
			
			
			currentDecodingAlphabet = swapLetters(currentDecodingAlphabet, pos, currentDecodingAlphabet.indexOf(letterDict));
		}
	    return currentDecodingAlphabet;
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

	private boolean filterCrypto(String word) {
		var frequ = getFrenquency(word);		
		return word.length() > 3 && frequ.get(frequ.size() - 1) >= 4;
	}

	private int comparelength(String o1, String o2) {
		if (o1.length() != o2.length()) {
			return o2.length() - o1.length(); // overflow impossible since lengths are non-negative
		}
		return o2.compareTo(o1);
	}

	private String swapLetters(String str, int i, int j) {
		char[] chars = str.toCharArray();
		char temp = chars[i];
		chars[i] = chars[j];
		chars[j] = temp;
		return new String(chars);
	}

	private int getScoreAlphabet(String alphabet) {
		int numWordDecrypt = 0;

		for (String word : cryptogramWords) {
			String decrypt = applySubstitution(word, alphabet).toLowerCase();
			if (dict.containsWord(decrypt))
				numWordDecrypt++;
		}

		return numWordDecrypt;
	}

	private boolean isSimilar(String word1, String dictWord) {
		if(dictWord.contains("-") || dictWord.contains("\'")) {
			return false;
		}
		var frequ1 = getFrenquency(word1);
		var frequ2 = getFrenquency(dictWord);

		return frequ1.equals(frequ2);
	}

	private List<Integer> getFrenquency(String word) {
		List<Integer> listVal = new ArrayList<>(getFrenquencyMap(word).values());
		Collections.sort(listVal);
		return listVal;
	}
	private Map<Character, Integer> getFrenquencyMap(String word) {
		Map<Character, Integer> fr = new HashMap<>();
		for (char c : word.toCharArray()) {
			fr.put(c, fr.getOrDefault(c, 0) + 1);
		}		
		return fr;
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
		String startAlphabet = LETTERS;
//		String startAlphabet = DECODING_ALPHABET;
//		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);

		// Display final results
		System.out.println("time : " + (System.currentTimeMillis() - startTime) / 1000.0);
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
