package tree;

import java.util.Arrays;

public class LetterNode {
	private char letter;
	private LetterNode[] children;
	private boolean isFinal;

	/*****************/
	/* CONSTRUCTOR */
	/*****************/
	public LetterNode(char letter) {
		this.letter = letter;
		this.isFinal = false;
	}

	/************/
	/* PUBLIC */
	/************/

	/**
	 * add a word to the tree
	 * 
	 * @param str the word to add
	 * @return if the word is add
	 */
	public boolean linkWord(String str) {
		if (str == null || str.length() == 0) {
			isFinal = true;
			return false;
		}		
		boolean isAdd = false;
		char letter = str.charAt(0);
		var node = this.getLinkTo(letter);
		if (node != null) {
			isAdd |= node.linkWord(str.substring(1));
		} else {
	        var newNode = new LetterNode(letter);
	        this.addToTable(newNode);
	        newNode.linkWord(str.substring(1));
	        isAdd = true;
	    }
	    return isAdd;

	}

	/**
	 * get the link to the node with the letter
	 * 
	 * @param letter the letter to find
	 * @return the node with the letter or null if not found
	 */
	public LetterNode getLinkTo(char letter) {
		if (children == null || children.length == 0) {
			return null;
		}
		int index = getIndex(letter);
		if(index>= children.length)
			return null;
		return children[index];
	}

	/**
	 * check if the tree contain the word that launch the recursive function on the
	 * children
	 * 
	 * @param word the word to find
	 * @return if the word is contain
	 */
	public boolean containt(String word) {
		if (word == null || word.length() == 0) {
			return isFinal;
		}
		var child = this.getLinkTo(word.charAt(0));
		if (child == null) {
			return false;
		}
		return child.containt(word.substring(1));
	}

	/*************/
	/* PRIVATE */
	/*************/

	/**
	 * add a node to the table of children
	 * 
	 * @param node the node to add
	 */
	private void addToTable(LetterNode node) {
	    int index = getIndex(node.letter);

	    if (children == null) {
	        children = new LetterNode[index + 1];
	    } else if (index >= children.length) {
	        children = Arrays.copyOf(children, index + 1);
	    }

	    children[index] = node;
	}
	private static int getIndex(char letter) {
	    int index = letter - 'a';
	    if ('-' == letter)
	        index = 26;
	    else if ('\'' == letter)
	        index = 27;
	    return index;
	}

	/************/
	/* GETTER */
	/************/

	/**
	 * simple getter of isFinal
	 * 
	 * @return true if the letter is the last of a word
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * getter on the char letter store in the node
	 * 
	 * @return the letter
	 */
	public char getLetter() {
		return (char) letter;
	}

	/**
	 * getter on the children
	 * 
	 * @return the tab of children
	 */
	public LetterNode[] getSubNode() {
		return children;
	}

	/**
	 * for the debug
	 * 
	 * @return the letter
	 */
	@Override
	public String toString() {
		return "LetterNode [letter=" + letter + ", children=" + children + ", isFinal=" + isFinal + "]";
	}
}
