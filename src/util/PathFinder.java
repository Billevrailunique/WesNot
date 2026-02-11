package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import model.TileModel;

public class PathFinder {
    private static HexCell cell = new HexCell(70);

    /**
     * For two coordinates (x, y) and (x+h, y+k), returns max(|h|, |k|)
     * This is the minimum distance between two hexagons with these coordinates
     * 
     * @param p1 coordinates of the first point
     * @param p2 coordinates of the second point
     * @return the minimum distance between these points
     */
    private static int chebyshevDistance(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        int di = p1.first() - p2.first();
        di = (di >= 0) ? di : -di; // forcing di >= 0
        int dj = p1.second() - p2.second();
        dj = (dj >= 0) ? dj : -dj; // forcing dj >= 0
        return (di >= dj) ? di : dj; // returning the greater of the two
    }

    // static inner class to help with the pathfinding algorithms, loosely
    // represents a single tile
    private static class Node {
        Pair<Integer, Integer> coordinate; // (i, j) coordinates of the node
        int distanceFromSource; // represents the length of the shortest known path from the source of the path
                                // to this node
        Node previous; // Node that lead to this one. If you follow the chain of previous nodes from
                       // the destination, you end up at the source

        Node(Pair<Integer, Integer> coordinate, Node previous, int distanceFromSource) {
            this.distanceFromSource = distanceFromSource;
            this.previous = previous;
            this.coordinate = coordinate;
        }

        @Override
        public String toString() {
            return "(Node @" + coordinate.toString() + " dist:" + this.distanceFromSource + ")";
        }
    }

    /**
     * Converts tiles into a weights array containing all relevant weight and
     * accessibility information
     * if a tile is accessible, the weights array will contain the mobilityPenalty
     * associated with that tile
     * if it isn't accessible, the weights array will contain a -1
     */
    private static int[][] tilesToArray(TileModel[][] tiles) {
        int height = tiles.length;
        int width = tiles[0].length;
        int[][] weights = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!(tiles[i][j].isAccessible())) {
                    weights[i][j] = -1;
                } else {
                    //System.out.println("Tile type: " + tiles[i][j].getTerrain());
                    weights[i][j] = tiles[i][j].getMobilityPenalty();
                }
            }
        }
        return weights;
    }

    public static void printHexPath(TileModel[][] tiles, List<Pair<Integer, Integer>> path) {
        printHexPath(tilesToArray(tiles), path);
    }

    public static void printHexPath(int[][] weights, List<Pair<Integer, Integer>> path) {
        if (path == null) {
            //System.out.println("null path for the following weights");
            path = new ArrayList<>();
        }
        for (int i = 0; i < weights.length; i++) {
            // Even-indexed columns
            for (int j = 0; j < weights[0].length; j += 2) {
                //System.out.printf("%2d", weights[i][j]);
                //System.out.print(path.contains(new Pair<>(j + 1, i - 1)) ? "  ^ " : "    ");
            }
            //System.out.println();

            // Odd-indexed columns
            //System.out.print(" ");
            for (int j = 1; j < weights[0].length; j += 2) {
                //System.out.print(path.contains(new Pair<>(j - 1, i)) ? "^ " : "  ");
                //System.out.printf("%2d  ", weights[i][j]);
            }
            //System.out.println();
        }

        // Final line arrows for odd columns
        //System.out.print("    ");
        for (int j = 1; j < weights[0].length; j += 2) {
            //System.out.print(path.contains(new Pair<>(j, weights.length - 1)) ? "^     " : "      ");
        }
        //System.out.println();
    }

    // renvoie la liste des coordonées des tuiles pour relier par un chemin direct
    // startcoord et endCoord sans prendre en compte le coup des tuiles
    public static List<Pair<Integer, Integer>> getCorridors(TileModel[][] tiles, Pair<Integer, Integer> startCoords,
            Pair<Integer, Integer> endCoords) {
        int rows = tiles.length;
        int cols = tiles[0].length;

        int[][] weights = new int[rows][cols];

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                TileModel tile = tiles[i][j];
                if (tile.isRoom()) {
                    weights[i][j] = -1;
                } else {
                    weights[i][j] = 1;
                }
            }
        }

        return getPath(weights, startCoords, endCoords);
    }

    public static List<Pair<Integer, Integer>> getPath(TileModel[][] tiles, Pair<Integer, Integer> startCoords,
            Pair<Integer, Integer> endCoords) {
        // Creating an array to hold each tiles cost
        int[][] weights = tilesToArray(tiles);

        return getPath(weights, startCoords, endCoords);
    }

    /**
     * Finds the shortest path through a hexagonal array, with weights
     * {@code weights}
     * The cost of landing on a tile is the weights value of that tile
     * 
     * @param weights    costs associated with each tile
     * @param startCoord start of the path
     * @param endCoord   end of the path
     * @return A List of coordinates for each step of the shortest path (minimises
     *         the sum of the weights at each coordinate), {@code null} if no path
     *         is found
     * 
     */
    public static List<Pair<Integer, Integer>> getPath(int[][] weights, Pair<Integer, Integer> startCoord,
            Pair<Integer, Integer> endCoord) {
        // HashMap to contain any nodes we've visited / are visiting (closed and open
        // nodes)
        HashMap<Pair<Integer, Integer>, Node> nodes = new HashMap<>();
        Node sourceNode = new Node(startCoord, null, 0);
        nodes.put(startCoord, sourceNode);

        // PriorityQueue to contain open nodes
        // (nodes that we have discovered but not yet expanded/developed)
        PriorityQueue<Node> openNodes = new PriorityQueue<>();
        openNodes.addOrDecrease(sourceNode, chebyshevDistance(startCoord, endCoord));

        while (!openNodes.isEmpty()) {
            Node current = openNodes.extractMin();
            cell.setCellCoordinates(current.coordinate); // sets the static cell's position to help find neighbors
            if (current.coordinate.equals(endCoord)) {
                break;
            }
            for (int i = 0; i < 6; i++) {
                Pair<Integer, Integer> newCoord = cell.getNeighbourCoordinates(i); // coordinates of the current
                                                                                   // neighbor we're checking
                // Checking if the new coordinate is out of bounds
                if (newCoord.first() < 0 || newCoord.first() >= weights[0].length || newCoord.second() < 0
                        || newCoord.second() >= weights.length)
                    continue;
                // Checking if the new coordinate is inaccessible
                if (weights[newCoord.second()][newCoord.first()] == -1)
                    continue;

                int newDist = current.distanceFromSource + weights[newCoord.second()][newCoord.first()]; // distance
                                                                                                         // from source
                                                                                                         // to this
                                                                                                         // neighbour
                Node newNode = nodes.get(newCoord); // neighbouring node
                if (newNode == null) {
                    newNode = new Node(newCoord, current, newDist);
                    nodes.put(newCoord, newNode);
                    openNodes.addOrDecrease(newNode, newDist + chebyshevDistance(newNode.coordinate, endCoord));
                } else if (newDist < newNode.distanceFromSource) {
                    newNode.distanceFromSource = newDist;
                    newNode.previous = current;
                    openNodes.addOrDecrease(newNode, newDist + chebyshevDistance(newNode.coordinate, endCoord));
                }
            }
        }
        Node endNode = nodes.get(endCoord);
        if (endNode == null) {
            //System.out.println("le problème est ici");
            return null; // No path found
        }

        List<Pair<Integer, Integer>> path = new ArrayList<>();
        while (endNode.previous != null) {
            path.add(endNode.coordinate);
            endNode = endNode.previous;
        }
        path.add(startCoord);
        path = path.reversed();
        return path;
    }

    /**
     * Returns the cost of the least-costly path between two coordinates
     * 
     * @param mapModel   The model used for the pathfinding
     * @param startCoord Start coordinate for the path
     * @param endCoord   End coordinate for the path
     * @return Cost of the least-costly path. -1 if no path is found.
     */
    public static int getPathCost(TileModel[][] tiles, Pair<Integer, Integer> startCoord,
            Pair<Integer, Integer> endCoord) {
        List<Pair<Integer, Integer>> path = getPath(tiles, startCoord, endCoord);
        return costFromPath(tiles, path);
    }

    public static int costFromPath(TileModel[][] tiles, List<Pair<Integer, Integer>> path) {
        int cost = 0;
        if (path == null)
            return -1;
        else {
            for (int i = 1; i < path.size(); i++) {
                Pair<Integer, Integer> currentCoord = path.get(i);
                cost += tiles[currentCoord.second()][currentCoord.first()].getMobilityPenalty();
            }
        }
        return cost;
    }

    /**
     * Finds the furthest valid path to a tile that isn't more costly than
     * {@code troopSpeed}
     * 
     * @param mapModel   map for which the path will be searched
     * @param startCoord (i, j) for the start position of the path
     * @param endCoord   (i, j) for the end position of the path
     * @param troopSpeed speed for the troop. Finds a partial path with cost lower
     *                   than this speed
     * @return list of coordinates representing each tile that's passed through
     */
    public static List<Pair<Integer, Integer>> getPartialPath(TileModel[][] tiles, Pair<Integer, Integer> startCoord,
            Pair<Integer, Integer> endCoord, int troopSpeed) {
        List<Pair<Integer, Integer>> path = getPath(tiles, startCoord, endCoord);
        int endIndex = 1; // (exclusive) index for path
        int partialCost = 0;
        for (int i = 1; i < path.size(); i++) {
            TileModel currentTile = tiles[path.get(i).second()][path.get(i).first()];
            partialCost += currentTile.getMobilityPenalty();
            if (partialCost > troopSpeed)
                break;
            if (currentTile.isAccessible())
                endIndex = i;
        }
        return path.subList(0, endIndex);
    }

    public static Pair<Integer, Integer> nearestValidTile(TileModel[][] tiles, Pair<Integer, Integer> coordinate) {
        Set<Pair<Integer, Integer>> visited = new HashSet<>();
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        visited.add(coordinate);
        queue.add(coordinate);
        while (!queue.isEmpty()) {
            Pair<Integer, Integer> curCoord = queue.remove();
            // If curCoord is a valid tile, return it
            if (0 <= curCoord.second() && curCoord.second() < tiles.length && 0 <= curCoord.first()
                    && curCoord.first() < tiles.length
                    && tiles[curCoord.second()][curCoord.first()].isAccessible()
                    && tiles[curCoord.second()][curCoord.first()].getTroop() == null) {
                        return curCoord;
                    }
            // Calculate neighbors
            cell.setCellCoordinates(curCoord);
            for (int i = 0; i < 6; i++) {
                Pair<Integer, Integer> newCoord = cell.getNeighbourCoordinates(i);
                if (!visited.contains(newCoord)) {
                    visited.add(newCoord);
                    queue.add(newCoord);
                }
            }
        }
        // No accessible tiles found before the queue was emptied
        return coordinate;
    }
}