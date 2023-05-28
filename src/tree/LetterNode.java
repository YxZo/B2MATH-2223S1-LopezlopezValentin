package tree;

/**
 * internal class for the traitement in LexicographicalTree
 * 
 * 
 * @author valen
 *
 */
class LetterNode {
	boolean isLeaf;
	LetterNode child;
	LetterNode sibling;
	char charValue;

    LetterNode(char charValue) {
        this.charValue = charValue;
    }

    
}