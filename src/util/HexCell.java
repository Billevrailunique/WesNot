package util;

public class HexCell {
    private static final int[] NEIGHBORS_DI = { 0, 1, 1, 0, -1, -1 };
    private static final int[][] NEIGHBORS_DJ = { { -1, -1, 0, 1, 0, -1 }, { -1, 0, 1, 1, 1, 0 } };

    private final int[] CORNERS_DX;
    private final int[] CORNERS_DY;

    private int X;
    private int Y;

    private int I;
    private int J;

    public final int RADIUS;

    public final int HEIGHT;

    public final int WIDTH;

    private final int SIDE;

    public static final int NUM_NEIGHBORS = 6;
    public static final int NUM_CORNERS = 6;

    public static final int UP = 0;
    public static final int TOP_RIGHT = 1;
    public static final int BOTTOM_RIGHT = 2;
    public static final int BOTTOM = 3;
    public static final int BOTTOM_LEFT = 4;
    public static final int TOP_LEFT = 5;

    public HexCell(int radius) {
        RADIUS = radius;
        WIDTH = radius * 2;
        HEIGHT = (int) ((float) radius * Math.sqrt(3));
        SIDE = radius * 3 / 2;

        int[] cdx = { RADIUS / 2, SIDE, WIDTH, SIDE, RADIUS / 2, 0 };
        int[] cdy = { 0, 0, HEIGHT / 2, HEIGHT, HEIGHT, HEIGHT / 2 };
        CORNERS_DX = cdx;
        CORNERS_DY = cdy;
    }

    public void setCellIndex(int i, int j) {
        I = i;
        J = j;
        X = i * SIDE;
        Y = HEIGHT * (2 * j + (i % 2)) / 2;
    }

    /**
     * function that sets the indices of the HexCell to be coord.fst, coord.snd
     * where i and j are the hexagonal equivalents of x and y
     * 
     * @param i is the column
     * @param j is the row
     */
    public void setCellCoordinates(Pair<Integer, Integer> coord) {
        this.setCellIndex(coord.first(), coord.second());
    }

    /**
     * function that sets the indices of the HexCell to what they need to be so that
     * it contains the point (x, y)
     * 
     * 
     * @param x is the x coordinate of the point
     * @param y is the y cordinate of the point
     */
    public void setCellByPoint(int x, int y) {
        int ci = (int) Math.floor((float) x / (float) SIDE);
        int cx = x - SIDE * ci;

        int ty = y - (ci % 2) * HEIGHT / 2;
        int cj = (int) Math.floor((float) ty / (float) HEIGHT);
        int cy = ty - HEIGHT * cj;

        if (cx > Math.abs(RADIUS / 2 - RADIUS * cy / HEIGHT)) {
            setCellIndex(ci, cj);
        } else {
            setCellByPoint(ci - 1, cj + (ci % 2) - ((cy < HEIGHT / 2) ? 1 : 0));
        }
    }

    public int getindexI() {
        return I;
    }

    public int getindexJ() {
        return J;
    }

    public int getCenterX() {
        return X + RADIUS;
    }

    public int getCenterY() {
        return Y + HEIGHT / 2;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    /**
     * @param neighborId is a number between 0 and 5
     *                   UP = 0;
     *                   TOP_RIGHT = 1;
     *                   BOTTOM_RIGHT = 2;
     *                   BOTTOM = 3;
     *                   BOTTOM_LEFT = 4;
     *                   TOP_LEFT = 5;
     * 
     * @return the column index i of the neigbor with id `neighborId`
     */
    public int getNeighborI(int neighborId) {
        return I + NEIGHBORS_DI[neighborId];
    }

    /**
     * @param neighborId is a number between 0 and 5
     *                   NEIGHBORS_UP = 0;
     *                   NEIGHBORS_TOP_RIGHT = 1;
     *                   NEIGHBORS_BOTTOM_RIGHT = 2;
     *                   NEIGHBORS_BOTTOM = 3;
     *                   NEIGHBORS_BOTTOM_LEFT = 4;
     *                   NEIGHBORS_TOP_LEFT = 5;
     * 
     * @return the row index j of the neigbor with id `neighborId`
     */
    public int getNeighborJ(int neighborId) {
        return J + NEIGHBORS_DJ[I % 2][neighborId];
    }

    public Pair<Integer, Integer> getNeighbourCoordinates(int neighborId) {
        return new Pair<>(getNeighborI(neighborId), getNeighborJ(neighborId));
    }

    // update the array of corners of the HexCell
    public void computeCorners(int[] cornersX, int[] cornersY) {
        for (int k = 0; k < NUM_CORNERS; k++) {
            cornersX[k] = X + CORNERS_DX[k];
            cornersY[k] = Y + CORNERS_DY[k];
        }
    }
}