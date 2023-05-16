package tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import boggle.Boggle;

public class LexicographicTree {
	private LetterNode root;
	private int size;
	/********************
	 *	 CONSTRUCTORS	*
	 ********************/
	

	/**
	 * Constructor : creates an empty lexicographic tree.
	 */
	public LexicographicTree() {
		root = new LetterNode(' ');
	}

	/**
	 * Constructor : creates a lexicographic tree populated with words
	 * 
	 * @param filename A text file containing the words to be inserted in the tree
	 * @throws NoSuchFileException
	 */
	public LexicographicTree(String filename) {
		this();
		try {
			var list = Files.readAllLines(Paths.get(filename));
			list.forEach(str -> this.insertWord(str));
		} catch (Exception e) {
			// ici pour ne pas avoir des erreurs dans le testes et tout de meme pouvoir gere
			// le faite que l'erreur soit lance
			throw new RuntimeException(new NoSuchFileException(filename));
		}
	}

	/************************
	 *	 PUBLIC METHODS		*
	 ************************/

	/**
	 * Returns the number of words present in the lexicographic tree.
	 * 
	 * @return The number of words present in the lexicographic tree
	 */
	public int size() {
		return size;
	}


	/**
	 * Inserts a word in the lexicographic tree if not already present.
	 * 
	 * @param word A word to insert
	 */
	public void insertWord(String word) {
		if (root.linkWord(word))
			size++;
	}

	/**
	 * Determines if a word is present in the lexicographic tree.
	 * 
	 * @param word A word
	 * @return True if the word is present, false otherwise
	 */
	public boolean containsWord(String word) {
		return root.containt(word);
	}

	/**
	 * Returns an alphabetic list of all words starting with the supplied prefix. If
	 * 'prefix' is an empty string, all words are returned.
	 * 
	 * @param prefix Expected prefix
	 * @return The list of words starting with the supplied prefix
	 */
	public List<String> getWords(String prefix) {
		List<String> foundWord = new ArrayList<>();
		LetterNode startNode = getNodeOfPrefix(prefix);
		if (startNode != null) {
			getWordform(foundWord, startNode, prefix);
		}
		return foundWord;
	}

	/**
	 * Returns an alphabetic list of all words of a given length. If 'length' is
	 * lower than or equal to zero, an empty list is returned.
	 * 
	 * @param length Expected word length
	 * @return The list of words with the given length
	 */
	public List<String> getWordsOfLength(int length) {
		if (length <= 0)
			return List.of();
		List<String> foundWord = new ArrayList<>();
		getSubWord(length, root, "", foundWord);
		return foundWord;
	}

	/**
	 * this method in this project is make for the boggle {@link Boggle}
	 *  
	 * @param prefix the prefix or the word
	 * @return -1 is not found, 0 if found but not a word, 1 if is a word
	 */
	public int hasPrefixOrWord(String prefix) {
		var node = getNodeOfPrefix(prefix);
		return (node == null) ? -1
				: node.isFinal() ? 1
						: 0;

	}

	/************************
	 *	 PRIVATE METHODS	*
	 ************************/

	/**
	 * make recursive call to get the node of prefix to go the deepest node of
	 * prefix
	 * 
	 * @param prefix the start of a word
	 * @return the letter node of the prefix 
	 * 		  or null is there are no words with the prefix
	 */
	private LetterNode getNodeOfPrefix(String prefix) {
		LetterNode node = root;
		for (int i = 0; i < prefix.length(); i++) {
			node = node.getLinkTo(prefix.charAt(i));
			if (node == null)
				return null;
		}
		return node;
	}

	/**
	 * this method make recursive call to travel 
	 * on the children nodes to find all the words
	 * 
	 * !!!
	 * the list of all words is fill with the new words
	 * this is the reason of the return void type
	 * !!!
	 * 
	 * @param allWord the list of all the word
	 * @param node the start node
	 * @param prefix the current prefix of the word
	 */
	private void getWordform(List<String> allWord, LetterNode node, String prefix) {

		var subedNode = node.getSubNode();
		
		if (node.isFinal() || subedNode == null) {
			allWord.add(prefix);
		}
		//check if the node have children
		if (subedNode == null)
			return;
		for (LetterNode subNode : subedNode) {
			if(subNode!= null)
				getWordform(allWord, subNode, prefix + subNode.getLetter());
		}

	}

	/**
	 * 
	 * this methode make recursive call the travel on the tree
	 * to find all word of lenth "n"
	 * 
	 * !!!
	 * the list of all words is fill with the new words
	 * this is the reason of the return void type
	 * !!!
	 * 
	 * @param n the size of the word
	 * @param parent
	 * @param prefix
	 * @param foundWord
	 */
	private void getSubWord(int n, LetterNode parent, String prefix, List<String> foundWord) {
		if (n <= 0) {
			if (parent.isFinal()) {
				foundWord.add(prefix);
				return;
			} else
				return;
		}
		if (parent.getSubNode() == null) {
			return;
		}

		for (LetterNode node : parent.getSubNode()) {
			if (node != null)
				getSubWord(n - 1, node, prefix + node.getLetter(), foundWord);
		}
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

	private static void testDictionaryPerformance(String filename) throws NoSuchFileException {
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

		// new GraphViewer(dico);

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

	public static void main(String[] args) throws NoSuchFileException {

		// CTT : test de performance insertion/recherche
		testDictionaryPerformance("mots/dictionnaire_FR_sans_accents.txt");

		// CST : test de taille maximale si VM -Xms2048m -Xmx2048m
		 testDictionarySize();
	}
}