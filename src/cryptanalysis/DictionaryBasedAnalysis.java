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

		cryptogramWords = Arrays.stream(cryptogram.split("\\s+")).filter(this::isLongEnough).sorted(this::comparelength)
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

		return guessAlphabet(alphabet, cryptogramWords, 0);

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
		return word.length() > 3;
	}

	private int comparelength(String o1, String o2) {
		if (o1.length() != o2.length()) {
			return o2.length() - o1.length(); // overflow impossible since lengths are non-negative
		}
		return o2.compareTo(o1);
	}

	private String guessAlphabet(String alphabet, List<String> allWordCrypt, int numWordDecrypt) {

		// copy de la list pour la remmetre a 0 et ajouter que les mots non trouver dans
		// le dictionnaire
		List<String> noDecryptWords = new ArrayList<>();

		// application of the substitution
		for (int i = 0; i < allWordCrypt.size(); i++) {
			String decrypt = applySubstitution(allWordCrypt.get(i), alphabet);
			if (!dict.containsWord(decrypt.toLowerCase())) {
				noDecryptWords.add(decrypt);
			}

		}
		int numDecrypt = this.cryptogramWords.size() - noDecryptWords.size();

//		if (numDecrypt> 0 && numDecrypt == numWordDecrypt)
//			return alphabet;
		System.out.println(noDecryptWords.size());
		if (noDecryptWords.isEmpty())
			return alphabet;

		//System.out.println(noDecryptWords.size());
		
		
		// analise of the longest word

		alphabet = analyse(alphabet, noDecryptWords, numDecrypt);

		alphabet = guessAlphabet(alphabet, noDecryptWords, numDecrypt);

		return alphabet;

	}

	private String analyse(String currentAlpha, List<String> allWordCrypt, int numDecrypt) {
		//System.out.println(numDecrypt);
		for (int i = numDecrypt; i < allWordCrypt.size(); i++) {
			var word  = allWordCrypt.get(i);
//			System.out.println("lancement du traitement pour :" + word+ "\nqui est en vrai :" +applySubstitution(word, DECODING_ALPHABET) );
//			System.out.println();
			List<String> wordFound = dict.getWordsOfLength(word.length());
			//System.out.println(wordFound);
			for (String dictWord : wordFound) {
				// si compatible alors lancer le changement de l'alpha
				if (isCompatible(dictWord, word)) {
//					System.out.println(dictWord + " trouver !!!");
					var newAlpha = updataAlpha(word, dictWord.toUpperCase(), currentAlpha);
					System.out.println("l'alpha devient: " + newAlpha);
					return newAlpha;
				}

			}

			// suppression car pas de correspondance dans le dic
			allWordCrypt.remove(i);
			//System.out.println("rien de trouver\nnombre de mon restant:" + allWordCrypt.size());
			//break;
		}

		return currentAlpha;
	}

	private boolean isCompatible(String dictWord, String currentWord) {
		var frequenceDictWord = getFrenquency(dictWord);
		var frequenceCurrentWord = getFrenquency(currentWord);
//		if(applySubstitution(currentWord, DECODING_ALPHABET).toLowerCase().equals(dictWord)) {
//			System.out.println(frequenceDictWord);
//			System.out.println(frequenceCurrentWord);
//		}
		
		
		return frequenceCurrentWord.equals(frequenceDictWord);
	}
	private List<Integer> getFrenquency(String word) {
		Map<Character, Integer> frequences = new HashMap<>();
		for (char car : word.toCharArray()) {
			frequences.put(car, frequences.getOrDefault(car, 0) + 1);
		}
		var freq = new ArrayList<>(frequences.values());
		Collections.sort(freq);
		return freq;
	}
	private String updataAlpha(String crypt, String dictWord, String alpha) {
		char[] alphaTb = alpha.toCharArray();
		
		for (int i = 0; i < crypt.length(); i++) {
			var goodLetter = dictWord.toUpperCase().charAt(i);
			var cryptLetter = crypt.charAt(i);
			
			if(dictWord.indexOf(goodLetter) == i && crypt.indexOf(cryptLetter) == i ) {
				var temp = alphaTb[alpha.indexOf(goodLetter)];
				alphaTb[alpha.indexOf(goodLetter)] = alphaTb[alpha.indexOf(cryptLetter)];
				alphaTb[alpha.indexOf(cryptLetter)]= temp;
			}
		}
		
		
		return new String(alphaTb);
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
		String startAlphabet = LETTERS;
//		String startAlphabet = DECODING_ALPHABET;
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
