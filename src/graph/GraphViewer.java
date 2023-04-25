package graph;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jgrapht.Graph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxEdgeLabelLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import boggle.BoggleNode;

import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

public class GraphViewer extends JFrame {

    private mxGraphComponent graphComponent;

    public GraphViewer(Graph<BoggleNode, DefaultWeightedEdge> graph) {
    	
    	super("Graph Viewer");


        // Create the JGraphXAdapter and mxGraphComponent
        JGraphXAdapter<BoggleNode, DefaultWeightedEdge> jgxAdapter = new JGraphXAdapter<>(graph);
        graphComponent = new mxGraphComponent(jgxAdapter);
        // Configure the layout
        mxGraphLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());

        // Add the mxGraphComponent to the JFrame
        getContentPane().add(graphComponent, BorderLayout.CENTER);
        
        
        
     // Configure the JFrame
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    
}
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTree;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultTreeModel;
//
//import tree.LetterNode;
//import tree.LexicographicTree;
//
//public class GraphViewer extends JFrame{
//    public GraphViewer(LexicographicTree tree){
//
//        // create a tree model
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
//        addSubNodes(root, tree.getRoot());
//        DefaultTreeModel model = new DefaultTreeModel(root);
//
//        // create a JTree and put it in a JScrollPane
//        JTree jTree = new JTree(model);
//        JScrollPane scrollPane = new JScrollPane(jTree);
//        scrollPane.setPreferredSize(new Dimension(600, 400));
//
//        // create a JPanel to hold the JTree
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        // create a JFrame to hold the JPanel
//        JFrame frame = new JFrame("Lexicographic Tree");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.add(panel);
//        frame.pack();
//        frame.setLocationRelativeTo(null); // center the frame
//        frame.setVisible(true);
//    }
//    private static void addSubNodes(DefaultMutableTreeNode parent, LetterNode node) {
//        if (node.getSubNode() == null) {
//            return;
//        }
//        LetterNode[] subNodes = node.getSubNode();
//        if(subNodes == null)
//            return;
//        for (LetterNode subNode : subNodes) {
//            DefaultMutableTreeNode child = new DefaultMutableTreeNode(subNode.getLetter());
//            parent.add(child);
//            addSubNodes(child, subNode);
//        }
//    }
//
//}