package tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LetterNodeTest {

	@Test
	void testLetterNode() {
		LetterNode node = new LetterNode('a');
		
		LetterNode nodeFount = node.getLinkTo('p');
		assertNull(nodeFount);
		
		System.out.println(node);
		
		node.linkWord("eee");
		
		System.out.println(node);
	}

}
