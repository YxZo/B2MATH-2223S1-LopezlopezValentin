package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tree.LexicographicTree;

public class DictionaryBasedAnalysis {

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "mots/dictionnaire_FR_sans_accents.txt";

	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock

	private LexicographicTree dict;
	private List<String> cryptogramWord;

	/*
	 * CONSTRUCTOR
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		this.dict = dict;
		this.cryptogramWord = new ArrayList<>();
		for (String word : cryptogram.split("\\s+")) {
			if(word.length()> 3)
			cryptogramWord.add(word.toLowerCase());
		}
		Collections.sort(cryptogramWord, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return Integer.compare(s2.length(), s1.length());
			}
		});

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
		int i = 0;
		for (String word : cryptogramWord) {
			word = applySubstitution(word, alphabet);
			var frenquencyWordCrypt = getFrenquency(word);
//			System.out.println("the frequency of crypt (" + word + ") :");
//			System.out.println("\t\t" + frenquencyWordCrypt);
//			System.out.println();

			if (dict.containsWord(word))
				continue;
			var listWordPossible = dict.getWordsOfLength(word.length());

			for (String foundWord : listWordPossible) {

				if (foundWord.contains("-") || foundWord.contains("\'"))
					continue;
				var frenquencyWordDict = getFrenquency(foundWord);

				if (frenquencyWordDict.equals(frenquencyWordCrypt)) {

//					System.out.println("-----------------------");
//					System.out.println("\tword found :" + foundWord);
//					System.out.println("\t\t" + frenquencyWordDict);
//					System.out.println();
					int index = 0;
					for (int frequence : frenquencyWordDict) {
						index += frequence - 1;
						char letter = foundWord.charAt(index);

						alphabet = swapLetterAlpha(alphabet, letter, word.charAt(index));
//						System.out.println("the alphabet is now : " + alphabet);
					}

				}

			}
//			System.out.println("============================");
			if(i++ %100 == 0) {
				System.out.println("en cours "+i+ " sur "+ this.cryptogramWord.size());
				System.out.println("the alpahbet actual :" + alphabet);
			}
			
		}

		return alphabet; // TODO
	}

	/**
	 * Applies an alphabet-specified substitution to a text.
	 * 
	 * @param text     A text
	 * @param alphabet A substitution alphabet
	 * @return The substituted text
	 */
	public static String applySubstitution(String text, String alphabet) {
		StringBuilder sb = new StringBuilder();
	    for (char c : text.toCharArray()) {
	        if (Character.isUpperCase(c)) {
	            sb.append(alphabet.charAt(c - 'A'));
	        } else {
	            sb.append(c);
	        }
	    }
	    return sb.toString(); // TODO
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

	private List<Integer> getFrenquency(String word) {
		Map<Character, Integer> frequences = new HashMap<>();
		for (char car : word.toCharArray()) {
			frequences.put(car, frequences.getOrDefault(car, 0) + 1);
		}
		return new ArrayList<>(frequences.values());
	}

	private String swapLetterAlpha(String alpha, char letter1, char letter2) {
		int indexL1 = alpha.indexOf(Character.toUpperCase(letter1));
		int indexL2 = alpha.indexOf(Character.toUpperCase(letter2));
		char[] charArray = alpha.toCharArray();
		
		char temp = charArray[indexL1];
		charArray[indexL1] = charArray[indexL2];
		charArray[indexL2] = temp;
		return new String(charArray);
	}
	
	private String applySubstitutionWord(String word, String alphabet) {	
		
		String wordSubstitued ="";
		for(char c: word.toUpperCase().toCharArray()) {
			wordSubstitued += LETTERS.charAt(alphabet.indexOf(c));
		}
		
		return wordSubstitued;
	}
	

	/*
	 * MAIN PROGRAM
	 */

	public static void main(String[] args) throws NoSuchFileException {
		
		
		
		
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
		
		System.out.println(dba.applySubstitutionWord("Plus", "VNSTBIQLWOZUEJMRYGCPDKHXAF"));
		String startAlphabet = LETTERS;
//		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
//		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);
//		
//		// Display final results
//		System.out.println();
//		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
//		System.out.println("Approximated alphabet : " + finalAlphabet);
//		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, finalAlphabet));
//		System.out.println();
//		
//		// Display decoded text
//		System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, 200));
//		System.out.println();
	}
}
