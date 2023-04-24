package graph;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import tree.LetterNode;
import tree.LexicographicTree;

public class GraphViewer extends JFrame{
    public GraphViewer(LexicographicTree tree){

        // create a tree model
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        addSubNodes(root, tree.getRoot());
        DefaultTreeModel model = new DefaultTreeModel(root);

        // create a JTree and put it in a JScrollPane
        JTree jTree = new JTree(model);
        JScrollPane scrollPane = new JScrollPane(jTree);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        // create a JPanel to hold the JTree
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // create a JFrame to hold the JPanel
        JFrame frame = new JFrame("Lexicographic Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // center the frame
        frame.setVisible(true);
    }
    private static void addSubNodes(DefaultMutableTreeNode parent, LetterNode node) {
        if (node.getSubNode() == null) {
            return;
        }
        LetterNode[] subNodes = node.getSubNode();
        if(subNodes == null)
            return;
        for (LetterNode subNode : subNodes) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(subNode.getLetter());
            parent.add(child);
            addSubNodes(child, subNode);
        }
    }

}