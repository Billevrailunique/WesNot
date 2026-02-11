package test;

import java.util.List;
import java.util.Random;
import util.Pair;
import util.PathFinder;

public class PathFinderTest {
    private static void randomFill(int[][] weights) {
        Random random = new Random();
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                // Setting the weights value to be in the set {-1, 1, 2, 3, 4}
                int r = random.nextInt(0, 5);
                if (r == 0) r = -1; 
                weights[i][j] = r;
            }
        }
    }

    private static void printArr(int[][] weights) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                //System.out.print(""+weights[i][j]+" ");
            }
            //System.out.println();
        }
    }

    // prints an array with formatting to match it being a set of hexagons
    private static void printHexArr(int[][] weights) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j+=2) {
                //System.out.print(""+weights[i][j]+"   ");
            }
            //System.out.println();
            for (int j = 1; j < weights[0].length; j+=2) {
                //System.out.print("  "+weights[i][j]+" ");
            }
            //System.out.println();
        }
    }
    

    public static void main(String[] args) {
        // Testing if it ever fails to find a path:

        // int size = 5; // Size of the area to pathfind
        // int[][] weights = new int[size][size]; // Weights for each tile
        // for (int i = 0; i < 100000; i++) {
        //     randomFill(weights); // Fills with random values
        //     List<Pair<Integer, Integer>> path = PathFinder.getPath(weights, new Pair<>(0, 0), new Pair<>(size-1, size-1));
        //     if (path == null) printHexPath(weights, path); // Prints out the path on top of the weights
        // }

        // Normal test:
        
        int size = 12;
        int[][] weights = new int[size][size];
        randomFill(weights);
        List<Pair<Integer, Integer>> path = PathFinder.getPath(weights, new Pair<>(0, 0), new Pair<>(size-1, size-1));
        PathFinder.printHexPath(weights, path);
    }
}
