package tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a lexicographic tree data structure for storing and searching
 * words.
 */
public class LexicographicTree {

	private LetterNode root;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Constructor : creates an empty lexicographic tree.
	 */
	public LexicographicTree() {
		root = new LetterNode('\0');
	}

	/**
	 * Constructor : creates a lexicographic tree populated with words
	 * 
	 * @param filename A text file containing the words to be inserted in the tree
	 * @throws IOException
	 */
	public LexicographicTree(String filename) {
		this();
		try {
			List<String> list = Files.readAllLines(Paths.get(filename));
			for (String str : list) {
				insertWord(str);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Returns the number of words present in the lexicographic tree.
	 * 
	 * @return The number of words present in the lexicographic tree
	 */
	public int size() {
		return sizeRecursive(root);
	}

	/**
	 * Inserts a word in the lexicographic tree if not already present.
	 * 
	 * @param word A word
	 */
	public void insertWord(String word) {
		if (word != null) {
			insertRecursive(root, word);
		}
	}

	/**
	 * Determines if a word is present in the lexicographic tree.
	 * 
	 * @param word A word
	 * @return True if the word is present, false otherwise
	 */
	public boolean containsWord(String word) {
		return word != null && !word.isEmpty() && searchRecursive(root, word);
	}

	/**
	 * Returns an alphabetic list of all words starting with the supplied prefix. If
	 * 'prefix' is an empty string, all words are returned.
	 * 
	 * @param prefix Expected prefix
	 * @return The list of words starting with the supplied prefix
	 */
	public List<String> getWords(String prefix) {
		List<String> words = new ArrayList<>();
		LetterNode node = getNodeForPrefix(root, prefix);
		if (node != null) {
			collectWords(node, prefix, words);
		}
		return words;
	}

	/**
	 * Returns an alphabetic list of all words of a given length. If 'length' is
	 * lower than or equal to zero, an empty list is returned.
	 * 
	 * @param length Expected word length
	 * @return The list of words with the given length
	 */
	public List<String> getWordsOfLength(int length) {
		List<String> words = new ArrayList<>();
		getWordsOfLengthRecursive(root, "", length, words);
		return words;
	}

	/**
	 * Checks if a prefix or word exists in the lexicographic tree.
	 * 
	 * @param prefix The prefix or word to check.
	 * @return -1 if the prefix is not found, 0 if the prefix is found but is not a
	 *         complete word, 1 if the prefix is found and is a complete word.
	 */
	public int hasPrefixOrWord(String prefix) {
		LetterNode node = getNodeForPrefix(root, prefix);
		return (node == null) ? -1 : (node.isLeaf) ? 1 : 0;
	}

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * Recursively finds the node corresponding to the given prefix.
	 * 
	 * @param node   The current node being examined.
	 * @param prefix The prefix to search for.
	 * @return The node corresponding to the prefix, or null if not found.
	 */
	private LetterNode getNodeForPrefix(LetterNode node, String prefix) {
		if (prefix.isEmpty()) {
			return node;
		}

		char firstChar = prefix.charAt(0);
		String remainingPrefix = prefix.substring(1);

		for (LetterNode child = node.child; child != null; child = child.sibling) {
			if (child.charValue == firstChar) {
				return getNodeForPrefix(child, remainingPrefix);
			} else if (child.charValue > firstChar) {
				break;
			}
		}

		return null;
	}

	/**
	 * Collects all words starting from the given node and with the given prefix.
	 * 
	 * @param node   The current node being examined.
	 * @param prefix The prefix of the words.
	 * @param words  The list of words found.
	 */
	private void collectWords(LetterNode node, String prefix, List<String> words) {
		if (node.isLeaf) {
			words.add(prefix);
		}

		for (LetterNode child = node.child; child != null; child = child.sibling) {
			collectWords(child, prefix + child.charValue, words);
		}
	}

	/**
	 * Recursively inserts a word into the lexicographic tree.
	 * 
	 * @param node The current node being examined.
	 * @param key  The remaining key to be inserted.
	 */
	private void insertRecursive(LetterNode node, String key) {
		
		// condition d'arret
		// i loop until the key was empty
		if (key.isBlank()) {
			node.isLeaf = true;
			return;
		}

		// decoupage du mot entre
		int i = 0;
		char firstChar;
		do {
			firstChar = key.charAt(i);
			i++;
		}while(!Character.isLowerCase(firstChar) 
				&& firstChar != '-' 
				&& firstChar != '\'' 
				&&  i < key.length());
			
		String remainingKey = key.substring(i);

		
		LetterNode child = node.child;
		LetterNode prev = null;

		// tant que child est different de null et que la valeur de de child est plus
		// petite que la premier lettre de la cle
		while (child != null && child.charValue < firstChar) {
			prev = child;
			child = child.sibling;
		}

		if (child == null || child.charValue != firstChar) {

			// creation de la novelle node
			LetterNode newNode = new LetterNode(firstChar);
			newNode.sibling = child;

			if (prev != null) {
				// ajout en tant que frere (le but ici est de faire une liste chainée sur les
				// lettres qui se suive)
				prev.sibling = newNode;
			} else {
				// ajout du lien sur la node parent
				node.child = newNode;
			}
			child = newNode;
		}

		insertRecursive(child, remainingKey);
	}


	/**
	 * Recursively collects all words of a given length starting from the given node
	 * and with the given prefix.
	 * 
	 * @param node   The current node being examined.
	 * @param prefix The prefix of the words.
	 * @param length The length of the words to collect.
	 * @param words  The list of words found.
	 */
	private void getWordsOfLengthRecursive(LetterNode node, String prefix, int length, List<String> words) {
		if (node == null) {
			return;
		}

		if (length == 0 && node.isLeaf) {
			words.add(prefix);
		} else if (length > 0) {
			for (LetterNode child = node.child; child != null; child = child.sibling) {
				getWordsOfLengthRecursive(child, prefix + child.charValue, length - 1, words);
			}
		}
	}

	/**
	 * Recursively searches for a word in the lexicographic tree.
	 * 
	 * @param node The current node being examined.
	 * @param key  The remaining key to be searched.
	 * @return True if the word is found, false otherwise.
	 */
	private boolean searchRecursive(LetterNode node, String key) {
		if (key.isEmpty()) {
			return node.isLeaf;
		}

		char firstChar = key.charAt(0);
		String remainingKey = key.substring(1);

		for (LetterNode child = node.child; child != null; child = child.sibling) {
			if (child.charValue == firstChar) {
				return searchRecursive(child, remainingKey);
			} else if (child.charValue > firstChar) {
				break;
			}
		}
		return false;
	}

	/**
	 * Recursively calculates the size of the lexicographic tree.
	 * 
	 * @param node The current node being examined.
	 * @return The size of the lexicographic tree.
	 */
	private int sizeRecursive(LetterNode node) {
		if (node == null) {
			return 0;
		}

		int size = node.isLeaf ? 1 : 0;
		for (LetterNode child = node.child; child != null; child = child.sibling) {
			size += sizeRecursive(child);
		}

		return size;
	}

	/*
	 * TEST FUNCTIONS
	 */

	private static String numberToWordBreadthFirst(long number) {
		String word = "";
		int radix = 13;
		do {
			word = (char) ('a' + (int) (number % radix)) + word;
			number = number / radix;
		} while (number != 0);
		return word;
	}

	private static void testDictionaryPerformance(String filename) throws IOException {
		long startTime;
		int repeatCount = 20;

		// Create tree from list of words
		startTime = System.currentTimeMillis();
		System.out.println("Loading dictionary...");
		LexicographicTree dico = null;
		for (int i = 0; i < repeatCount; i++) {
			dico = new LexicographicTree(filename);
		}
		System.out.println("Load time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println("Number of words : " + dico.size());
		System.out.println();

		// Search existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching existing words in dictionary...");
		File file = new File(filename);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
					String word = input.nextLine();
					boolean found = dico.containsWord(word);
					if (!found) {
						System.out.println(word + " / " + word.length() + " -> " + found);
					}
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search non-existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching non-existing words in dictionary...");
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
					String word = input.nextLine() + "xx";
					boolean found = dico.containsWord(word);
					if (found) {
						System.out.println(word + " / " + word.length() + " -> " + found);
					}
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search words of increasing length in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching for words of increasing length...");
		for (int i = 0; i < 4; i++) {
			int total = 0;
			for (int n = 0; n <= 28; n++) {
				int count = dico.getWordsOfLength(n).size();
				total += count;
			}
			if (dico.size() != total) {
				System.out.printf("Total mismatch : dict size = %d / search total = %d\n", dico.size(), total);
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();
	}

	private static void testDictionarySize() {
		final int MB = 1024 * 1024;
		System.out.print(Runtime.getRuntime().totalMemory() / MB + " / ");
		System.out.println(Runtime.getRuntime().maxMemory() / MB);

		LexicographicTree dico = new LexicographicTree();
		long count = 0;
		while (true) {
			dico.insertWord(numberToWordBreadthFirst(count));
			count++;
			if (count % MB == 0) {
				System.out.println(count / MB + "M -> " + Runtime.getRuntime().freeMemory() / MB);
			}
		}
	}

	/*
	 * MAIN PROGRAM
	 */

	public static void main(String[] args) throws IOException {
//		LexicographicTree dico = new LexicographicTree();
//		dico.insertWord("test");
//		dico.insertWord("testes");
//		dico.insertWord("testee");
//		dico.insertWord("ter");
//		var test = "test";
		// CTT : test de performance insertion/recherche
		testDictionaryPerformance("mots/dictionnaire_FR_sans_accents.txt");

		// CST : test de taille maximale si VM -Xms2048m -Xmx2048m
		testDictionarySize();
	}
}
