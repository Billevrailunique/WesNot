package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import model.TileModel.Terrains;
import util.HexCell;
import util.Pair;
import util.RandomTaskManager;

/**
 * Represents a section of the game world map, called a Chunk.
 * A Chunk is defined by its type (e.g., "surface" or "dungeon"),
 * its position within the universe, and its own terrain grid.
 */
public class Chunk {
    /** The type of the chunk, such as "surface" or "dungeon". */
    public final String type;

    /** The coordinates of the chunk within the overall universe map. */
    public final Pair<Integer, Integer> coordInUnivers;

    /** The seed used for procedural generation within this chunk. */
    public long seed;

    /** The Swing panel used to render the chunk (optional, can be null). */
    public JPanel pane;

    /**
     * Static reference to a hexagonal cell representation for rendering or logic.
     */
    public static HexCell hCell;

    /** The height of the terrain grid in number of tiles. */
    public final int GRID_HEIGHT;

    /** The width of the terrain grid in number of tiles. */
    public final int GRID_WIDTH;

    /** 2D array representing the type of terrain at each tile of the chunk. */
    public TileModel.Terrains[][] mapTerrains;

    /** 2D array representing the tiles themselves within the chunk. */
    public TileModel[][] map;

    private Pair<Integer, Integer> landingPoint;

    /**
     * 2D array indicating whether a tile has been discovered (true) or not (false).
     */
    public boolean[][] discovered;
    public MapModel mapModel;

    /**
     * A mapping of dungeon coordinates within this chunk to their corresponding
     * Chunk objects.
     */
    HashMap<Pair<Integer, Integer>, Chunk> dungeonsCoord = new HashMap<>();

    /** A list of coordinates where dungeons are located within this chunk. */
    List<Pair<Integer, Integer>> dungeonCoordList = new ArrayList<>();
    Pair<Pair<Integer, Integer>, Chunk> caverne = null; // null unless it's a surface, then it's the coord of the
                                                        // unique caverne
    // of the map
    Pair<Integer, Integer> oldChunk; // coord of previous Chunk encounter (cool
                                     // when you exit a dungeon)

    List<Pair<Integer, Integer>> villageCoordList = new ArrayList<>();

    /**
     * Constructs a Chunk using a given type and a long seed.
     * The seed is split into x and y coordinates for internal use.
     *
     * @param type The type of the chunk (e.g., "surface" or "dungeon").
     * @param seed A long seed used for procedural content generation.
     */
    public Chunk(String type, long seed) {
        this(type, new Pair<>((int) (seed & 0xFFFFFFFFL), (int) (seed >>> 32)), 40, 40, null);
    }

    /**
     * Constructs a Chunk object and initializes its terrain grid and seed.
     *
     * @param type   The type of the chunk, e.g., "surface" or "dungeon".
     * @param coord  The coordinates of the chunk within the world.
     * @param height The height of the terrain grid.
     * @param width  The width of the terrain grid.
     */
    public Chunk(String type, Pair<Integer, Integer> coord, int height, int width, MapModel mapModel) {
        GRID_HEIGHT = height;
        GRID_WIDTH = width;
        this.mapModel = mapModel;

        this.type = type;
        this.mapModel = mapModel;

        mapTerrains = new Terrains[GRID_HEIGHT][GRID_WIDTH];
        map = new TileModel[GRID_HEIGHT][GRID_WIDTH];
        discovered = new boolean[GRID_HEIGHT][GRID_WIDTH];

        coordInUnivers = coord;
        oldChunk = coord;

        int x = coord.first();
        int y = coord.second();

        // Compose seed using the x and y coordinates
        seed = ((long) y << 32) | (x & 0xFFFFFFFFL);
        RandomTaskManager.setSeed(seed);

        if (hCell == null) {
            hCell = new HexCell(70);
        }

        if (type.equals("surface")) {
            Surface surface = new Surface(this);
            surface.initMap();
            //System.out.println("Initialisation over");
        }

        if (type.equals("dungeon")) {
            Dungeon dungeon = new Dungeon(this);
            dungeon.initMap();
        }

        if (type.equals("caverne")) {
            Caverne caverne = new Caverne(this);
            caverne.initMap();
        }
    }

    /**
     * Checks whether a given row and column are within the grid boundaries of this
     * chunk.
     *
     * @param row The row index to check.
     * @param col The column index to check.
     * @return true if the row and column are within bounds; false otherwise.
     */
    public boolean isInsideGrid(int row, int col) {
        return 0 <= row && row < GRID_WIDTH && 0 <= col && col < GRID_HEIGHT;
    }

    public Pair<Integer, Integer> posWhereLand() {

        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                if (mapTerrains[i][j] == Terrains.EXIT) {
                    //System.out.println("entré/ sorti en (" + i + "," + j + ")");
                    return new Pair<Integer, Integer>(i, j);
                }
            }
        }
        return new Pair<Integer, Integer>(0, 0);
    }

    public Pair<Integer, Integer> getLandingPoint() {
        if (type.equals("caverne") || type.equals("dungeon")) {
            return landingPoint;
        } else {
            //System.out.println("not a dj nor a cave, no acces to landingpoint");
            return null;
        }
    }

    public void setLandingPoint(Pair<Integer, Integer> pos) {
        if (type.equals("caverne") || type.equals("dungeon")) {
            landingPoint = pos;
        } else {
            //System.out.println("not a dj nor a cave, can not set landingpoint");
        }
    }
}
