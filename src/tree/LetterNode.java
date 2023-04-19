package tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class LetterNode {
    private char letter;
    private LetterNode[] children;    
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
        
        for (LetterNode child : children) {
            if (child.letter == letter) {
                return child;
            }
        }

        return null;
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
        }
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
     * @return the letter
     */
    @Override
    public String toString() {
        return "LetterNode [letter=" + letter + ", children=" + children + ", isFinal=" + isFinal
                + "]";
    }

	public LetterNode[] getSubNode() {
		
		return children;
	}

}

