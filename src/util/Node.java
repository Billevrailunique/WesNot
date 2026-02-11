package util;

import java.util.ArrayList;

/**
 * A generic binary tree node implementation with visualization capabilities.
 * <p>
 * This class represents nodes in a binary tree structure, where each node:
 * <ul>
 * <li>Stores content of generic type T</li>
 * <li>Maintains references to left and right child nodes</li>
 * <li>Keeps track of its parent node</li>
 * <li>Provides tree analysis methods</li>
 * </ul>
 * </p>
 * 
 * @param <T> The type of content stored in the node
 */
public class Node<T> {
    private Node<T> gauche; // Left child node
    private Node<T> droite; // Right child node
    private Node<T> padre; // Parent node
    private T content; // Contained data

    /**
     * Constructs a fully specified node.
     * 
     * @param padre   Parent node reference
     * @param gauche  Left child node
     * @param droite  Right child node
     * @param content Data to store in node
     */
    public Node(Node<T> padre, Node<T> gauche, Node<T> droite, T content) {
        this.padre = padre;
        this.gauche = gauche;
        this.droite = droite;
        this.content = content;
    }

    public Node(T content, Node<T> gauche, Node<T> droit) {
        this.droite = droit;
        this.gauche = gauche;
        this.content = content;
    }

    /**
     * Constructs a node with only parent reference.
     * 
     * @param padre Parent node reference
     */
    public Node(Node<T> padre) {
        this(padre, null, null, null);
    }

    /**
     * Constructs an empty root node.
     */
    public Node() {
        this(null, null, null, null);
    }

    // ACCESSOR METHODS

    /**
     * Gets the node's stored content.
     * 
     * @return The contained data
     */
    public T getContent() {
        return content;
    }

    /**
     * Gets the right child node.
     * 
     * @return Right child reference
     */
    public Node<T> getDroite() {
        return droite;
    }

    /**
     * Gets the left child node.
     * 
     * @return Left child reference
     */
    public Node<T> getGauche() {
        return gauche;
    }

    /**
     * Gets the parent node.
     * 
     * @return Parent node reference
     */
    public Node<T> getPadre() {
        return padre;
    }

    // MUTATOR METHODS

    /**
     * Sets the left child node.
     * 
     * @param fils Node to set as left child
     */
    public void setGauche(Node<T> fils) {
        this.gauche = fils;
    }

    /**
     * Sets the right child node.
     * 
     * @param fils Node to set as right child
     */
    public void setDroite(Node<T> fils) {
        this.droite = fils;
    }

    /**
     * Sets the node's content.
     * 
     * @param content Data to store in node
     */
    public void setContent(T content) {
        this.content = content;
    }

    // TREE ANALYSIS METHODS

    /**
     * Counts all leaf nodes in the subtree.
     * 
     * @return Number of leaf nodes
     */
    public int nbFeuille() {
        return countLeaves(this);
    }

    /**
     * Recursively counts leaf nodes.
     * 
     * @param node Current node in traversal
     * @return Number of leaves in subtree
     */
    private int countLeaves(Node<T> node) {
        if (node == null) {
            return 0;
        }
        if (node.getGauche() == null && node.getDroite() == null) {
            return 1;
        }
        return countLeaves(node.getGauche()) + countLeaves(node.getDroite());
    }

    // VISUALIZATION METHODS

    /**
     * Recursively prints tree structure with ASCII art.
     * 
     * @param node   Current node in traversal
     * @param prefix Line prefix for formatting
     * @param isTail Whether node is last child
     */
    public void afficherArbre(Node<T> node, String prefix, boolean isTail) {
        if (node == null) {
            return;
        }

        //System.out.println(prefix + (isTail ? "└── " : "├── ") + node.getContent());
        if (node.getGauche() != null || node.getDroite() != null) {
            afficherArbre(node.getGauche(), prefix + (isTail ? "    " : "│   "), false);
            afficherArbre(node.getDroite(), prefix + (isTail ? "    " : "│   "), true);
        }
    }

    /**
     * Initiates tree visualization from root node.
     */
    public void afficherArbre() {
        afficherArbre(this, "", true);
    }

    // COMPARISON METHOD FOR DUNGEON GENERATION

    /**
     * Finds all extremal leaf nodes (rooms) in a binary tree for dungeon
     * generation.
     *
     * @param tree      The root of the subtree to analyze.
     * @param searchMin True to search for minimal coordinate(s), false for maximal.
     * @param coupeVert True to compare vertical axis (X), false for horizontal (Y).
     * @return List of leaf nodes (rooms) with the extremal coordinate on the chosen
     *         axis.
     */
    public static ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> cmpTree(
            Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> tree,
            boolean searchMin, boolean coupeVert) {

        if (tree == null)
            return new ArrayList<>();

        // Base case: leaf node
        if (tree.gauche == null && tree.droite == null) {
            ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> leaf = new ArrayList<>();
            leaf.add(tree.content);
            return leaf;
        }

        // Recursive calls
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> left = cmpTree(tree.gauche, searchMin,
                coupeVert);
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> right = cmpTree(tree.droite, searchMin,
                coupeVert);

        // If one side is empty, return the other
        if (left.isEmpty())
            return right;
        if (right.isEmpty())
            return left;

        // Extract comparison values
        int leftVal = getCompareValue(left.get(0), searchMin, coupeVert);
        int rightVal = getCompareValue(right.get(0), searchMin, coupeVert);

        if (searchMin) {
            if (leftVal < rightVal)
                return left;
            if (rightVal < leftVal)
                return right;
        } else {
            if (leftVal > rightVal)
                return left;
            if (rightVal > leftVal)
                return right;
        }

        // Equal values — combine both
        left.addAll(right);
        return left;
    }

    /**
     * Helper to extract the relevant coordinate for comparison.
     */
    private static int getCompareValue(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> room,
            boolean searchMin, boolean coupeVert) {
        if (searchMin) {
            return coupeVert ? room.first().first() : room.first().second();
        } else {
            return coupeVert ? room.second().first() : room.second().second();
        }
    }
}