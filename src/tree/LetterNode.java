package tree;

import java.util.Arrays;
import java.util.HashMap;

class LetterNode implements Comparable<LetterNode> {
	private char letter;
	private LetterNode[] children;
//	private HashMap<Character, LetterNode> childrens;
	private boolean isFinal;

	public LetterNode(char letter) {
		this.letter = letter;
		this.isFinal = false;

	}

	/**
	 * 
	 * @param str
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

	public LetterNode getLinkTo(char letter) {
		if (children == null || children.length == 0) {
			return null;
		}
//		if (children[children.length - 1].letter < letter || children[0].letter > letter)
//			return null;
		
		for (LetterNode child : children) {
			if (child.letter == letter) {
				return child;
			}
		}
		return null;
//		if (childrens == null) {
//			return null;
//		}
//		return childrens.get(letter);

	}

	public boolean isFinal() {
		return isFinal;
	}

	public char getLetter() {
		return letter;
	}

	// ajoute un noeud à la table des enfants
	private void addToTable(LetterNode node) {
		if (children == null) {
			children = new LetterNode[1];
			children[0] = node;
		} else {
			LetterNode[] newChildren = Arrays.copyOf(children, children.length + 1);
			newChildren[children.length] = node;
			children = newChildren;
//			sort();
		}
//		if( childrens == null)
//			childrens = new HashMap<>();
//		childrens.put(node.getLetter(), node);

	}

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

	/**
	 * for the debug
	 * 
	 * @return the letter
	 */
	@Override
	public String toString() {
		return "LetterNode [letter=" + letter + ", children=" + children + ", isFinal=" + isFinal + "]";
	}

//	public Collection<LetterNode> getSubNode() {
	public LetterNode[] getSubNode() {
		return children;
//		return childrens== null ? null : childrens.values();
	}

	public void sort() {
		Arrays.sort(this.children);
	}

	@Override
	public int compareTo(LetterNode o) {
		// TODO Auto-generated method stub
		return Character.compare(letter, o.letter);
	}

}
