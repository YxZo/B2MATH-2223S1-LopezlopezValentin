package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private static int nbrCallGestScore = 0;

	/*
	 * CONSTRUCTOR
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		cryptogramWords = Arrays.stream(cryptogram.split("\\s+"))
                .filter(word -> word.length() > 2)
                .distinct()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .collect(Collectors.toList());
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
		if (alphabet == null || alphabet.length() != 26 || !isValideAlphabet(alphabet)) {
			throw new IllegalArgumentException("l'aphabet est invalide");
		}
		alphabet = alphabet.toUpperCase();

		if (DEBUG)
			System.out.printf("debut du traitement pour %d mots\n", cryptogramWords.size());

		int currentWordSize = 0;
		String betterAlpha = alphabet;
		int betterScore = 0;
		List<String> listWordOfLenth = new ArrayList<>();
		for (String wordCrypt : cryptogramWords.stream().filter(this::containsRecurringLetters).collect(Collectors.toList())) {

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
		if (alphabet == null || alphabet.length() == 0 || alphabet.length() != 26 || !isValideAlphabet(alphabet)) {
			throw new IllegalArgumentException("l'aphabet est invalide");
		}
		if (text == null) {
			throw new IllegalArgumentException("le texte est invalide");
		}
		if (text.length() == 0)
			return text;
		String wordSubstitued = "";
		for (char c : text.toUpperCase().toCharArray()) {
			if(!Character.isLetter(c) && c != ' ' && c != '\n')
				continue;
			
			int index = LETTERS.indexOf(c);
			wordSubstitued += index >= 0 ? alphabet.charAt(index) : c;
		}
		return wordSubstitued;
	}
	

	/*******************/
	/* PRIVATE METHODS */
	/*******************/

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

	/**
	 * Returns the pattern of a word. Each unique character is replaced by a unique number.
	 * Characters are replaced by the order of their first occurrence.
	 *
	 * @param word The word to generate a pattern from
	 * @return The pattern of the word
	 */
	private String getPaternWord(String word) {
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

	/**
	 * Checks if a word contains recurring letters.
	 *
	 * @param word The word to be checked
	 * @return true if the word contains recurring letters, false otherwise
	 */
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

	/**
	 * Returns a compatible word from the dictionary for the given encrypted word.
	 *
	 * @param wordCrypt The encrypted word
	 * @param listOfWord The list of dictionary words of the same length as the encrypted word
	 * @return A compatible word from the dictionary
	 */
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
	
	/**
	 * Calculates the score of an alphabet based on the number of valid decrypted words.
	 *
	 * @param alphabet The alphabet to be scored
	 * @return The score of the alphabet
	 */
	private int getScoreAlphabet(String alphabet) {
		long startTime = System.currentTimeMillis();
		nbrCallGestScore++;
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
	
	/**
	 * Updates a given alphabet based on a given encrypted word and its corresponding dictionary word.
	 *
	 * @param alpha The current alphabet
	 * @param cryptWord The encrypted word
	 * @param dictWord The corresponding dictionary word
	 * @return The updated alphabet
	 */
	private String updateAlpha(String alpha, String cryptWord, String dictWord) {
		long startTime = System.currentTimeMillis();
		dictWord = dictWord.toUpperCase();
		var newAlpha = alpha;
		for (int i = 0; i < cryptWord.toCharArray().length; i++) {
			int pos = LETTERS.indexOf(cryptWord.charAt(i));
			int posLetterToswap = newAlpha.indexOf(dictWord.charAt(i));

			newAlpha = swapLetters(newAlpha, pos, posLetterToswap);
		}
		

		if (DEBUG) {
			System.out.printf("better       alphabet :%s\n", alpha);
			System.out.printf("New approxim alphabet :%s\n", newAlpha);
			System.out.printf("        Modifications :%s\n\n", compareAlphabets(alpha, newAlpha));
		}
		RecreatAlphaTime += System.currentTimeMillis() - startTime;

		return newAlpha;
	}
	
	/**
	 * Swaps two characters at the given indices in a string.
	 *
	 * @param str The string in which characters are to be swapped
	 * @param i The index of the first character
	 * @param j The index of the second character
	 * @return The updated string
	 */
	private String swapLetters(String str, int i, int j) {
		char[] chars = str.toCharArray();
		char temp = chars[i];
		chars[i] = chars[j];
		chars[j] = temp;
		return new String(chars);

	}
	/**
	 * Checks if a given alphabet is valid, that is, it contains only unique characters.
	 *
	 * @param alphabet The alphabet to be checked
	 * @return true if the alphabet is valid, false otherwise
	 */
	private static boolean isValideAlphabet(String alphabet) {
	    // Utiliser un ensemble (Set) pour stocker les lettres uniques de l'alphabet
	    Set<Character> caracteres = new HashSet<>();

	    for (int i = 0; i < alphabet.length(); i++) {
	        char lettre = alphabet.charAt(i);

	        // Vérifier si la lettre existe déjà dans l'ensemble
	        if (caracteres.contains(lettre)) {
	            return false; // Une lettre identique a déjà été trouvée
	        }

	        // Ajouter la lettre à l'ensemble
	        caracteres.add(lettre);
	    }

	    return true; // Toutes les lettres sont uniques
	}

	/*
	 * MAIN PROGRAM
	 */

	public static void main(String[] args) throws IOException {
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
//		String startAlphabet = DECODING_ALPHABET;
		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);

		// Display final results
		System.out.println("time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println("similarTime :" + getCompatibleWordTime / 1000.0);
		System.out.println("RecreatAlphaTime :" + RecreatAlphaTime / 1000.0);
		System.out.println("scoreAlphaTime :" + scoreAlphaTime / 1000.0);
		System.out.println("nbr Call get scrore :"+ nbrCallGestScore);
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