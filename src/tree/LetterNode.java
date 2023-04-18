package tree;

import java.util.Arrays;

public class LetterNode {
    private char letter;
    private LetterNode[] children;
    private boolean isFinal = false;

    public LetterNode(char letter) {
        this.letter = letter;
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
        var founNode = this.getLinkTo(letter);
        if (founNode != null) {
            isAdd |= founNode.linkWord(str.substring(1));
        } else {
            var newNode = new LetterNode(letter);
            this.addToTable(newNode);
            isAdd |= newNode.linkWord(str.substring(1));
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
        var letter = word.charAt(0);
        var child = this.getLinkTo(letter);
        if (child == null) {
            return false;
        }
        return child.containt(word.substring(1));
    }

    public LetterNode[] getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "LetterNode [letter=" + letter + ", children=" + Arrays.toString(children) + ", isFinal=" + isFinal
                + "]";
    }

}
