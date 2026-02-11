package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import model.TileModel.Terrains;
import util.Pair;
import util.RandomTaskManager;

/**
 * Handles the generation and configuration of surface terrain for game chunks.
 * <p>
 * This class is responsible for creating diverse and balanced surface maps by:
 * <ul>
 * <li>Placing different terrain types (plains, mountains, deserts, water
 * features)</li>
 * <li>Positioning key landmarks (dungeons, villages, bridges)</li>
 * <li>Ensuring logical terrain distribution and accessibility</li>
 * </ul>
 * </p>
 */
public class Surface {

    private final Chunk chunk;

    /**
     * Constructs a Surface generator for the specified chunk.
     * 
     * @param chunk The chunk to generate surface terrain for
     */
    public Surface(Chunk chunk) {
        this.chunk = chunk;
    }

    /**
     * Fills the entire map with plain terrain as a base layer.
     */
    private void fillWithPlains() {
        for (int i = 0; i < chunk.GRID_HEIGHT; i++) {
            for (int j = 0; j < chunk.GRID_WIDTH; j++) {
                chunk.mapTerrains[i][j] = Terrains.PLAIN;
            }
        }
    }

    /**
     * Places main dungeons in distinct quadrants of the map.
     * <p>
     * Ensures dungeons are:
     * <ul>
     * <li>Evenly distributed across quadrants</li>
     * <li>Sufficiently spaced apart</li>
     * <li>Surrounded by dungeon-themed tiles</li>
     * </ul>
     * </p>
     */
    private void placeDungeons() {
        int quadrantSize = chunk.GRID_HEIGHT / 2;
        boolean occupiedQuadrants[][] = new boolean[2][2];

        for (int g = 0; g < chunk.mapModel.players.size(); g++) {
            int qx, qy;

            // Find an unoccupied quadrant
            do {
                qx = RandomTaskManager.nextInt(0, 1);
                qy = RandomTaskManager.nextInt(0, 1);
            } while (occupiedQuadrants[qx][qy]);

            // Find valid position within quadrant
            int x, y;
            do {
                x = RandomTaskManager.nextInt(0, quadrantSize - 5) + qx * quadrantSize + 1;
                y = RandomTaskManager.nextInt(0, quadrantSize - 5) + qy * quadrantSize + 1;
            } while (x >= chunk.GRID_HEIGHT - 5 || !areDungeonFarEnough(x, y)
                    || (chunk.mapTerrains[x][y] == Terrains.MAINDUNGEON
                            || Arrays.asList(MapModel.dungeons).contains(chunk.mapTerrains[x + 1][y])
                            || Arrays.asList(MapModel.dungeons).contains(chunk.mapTerrains[x - 1][y])));

            chunk.mapTerrains[x][y] = Terrains.MAINDUNGEON;
            chunk.dungeonCoordList.add(new util.Pair<>(x, y));
            fillNeighborWith(MapModel.dungeons, x, y);
        }
    }

    /**
     * Verifies minimum distance between new dungeon and existing ones.
     * 
     * @param row Candidate row coordinate
     * @param col Candidate column coordinate
     * @return true if position is sufficiently far from other dungeons
     */
    private boolean areDungeonFarEnough(int row, int col) {
        for (util.Pair<Integer, Integer> coord : chunk.dungeonCoordList) {
            double dist = Math.sqrt(Math.pow(row - coord.first(), 2) + Math.pow(col - coord.second(), 2));
            if (dist <= 10) {
                return false;
            }
        }
        return true;
    }

    /**
     * Distributes mountain terrain in clustered formations.
     * <p>
     * Mountains are placed in random quadrants with controlled density.
     * </p>
     */
    private void placeMountains() {
        int[] places = biomeGeneration(RandomTaskManager.nextInt(0, 3) + 3, 10, 15);
        int i = 0;
        for (int elt : places) {
            placeRandomTerrain(Terrains.MOUNTAIN, elt, i % 4);
            i++;
        }
    }

    /**
     * Places terrain clusters using breadth-first expansion.
     * 
     * @param terrain  The terrain type to place
     * @param count    Number of tiles to generate
     * @param quadrant Map quadrant to place in (0-3)
     */
    private void placeRandomTerrain(Terrains terrain, int count, int quadrant) {
        int startX = RandomTaskManager.nextInt(0, ((chunk.GRID_HEIGHT - 5) / 4) - 1) + quadrant * chunk.GRID_HEIGHT / 4;
        int startY = RandomTaskManager.nextInt(0, ((chunk.GRID_HEIGHT - 5) / 4) - 1) + quadrant * chunk.GRID_WIDTH / 4;

        // Find valid starting position
        while (chunk.mapTerrains[startX][startY] != Terrains.PLAIN) {
            startX = RandomTaskManager.nextInt(0, chunk.GRID_HEIGHT - 6);
            startY = RandomTaskManager.nextInt(0, chunk.GRID_HEIGHT - 6);
        }

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] { startX, startY });
        chunk.mapTerrains[startX][startY] = terrain;
        int placed = 1;

        // BFS expansion
        while (placed < count && !queue.isEmpty()) {
            int[] pos = queue.poll();
            List<int[]> neighbors = Arrays.asList(
                    new int[] { pos[0] - 1, pos[1] },
                    new int[] { pos[0] + 1, pos[1] },
                    new int[] { pos[0], pos[1] - 1 },
                    new int[] { pos[0], pos[1] + 1 });
            Collections.shuffle(neighbors, RandomTaskManager.getRandom());

            for (int[] neighbor : neighbors) {
                int nx = neighbor[0], ny = neighbor[1];
                if (nx >= 0 && nx < chunk.GRID_HEIGHT && ny >= 0 && ny < chunk.GRID_WIDTH &&
                        chunk.mapTerrains[nx][ny] == Terrains.PLAIN) {
                    chunk.mapTerrains[nx][ny] = terrain;
                    queue.add(new int[] { nx, ny });
                    placed++;
                    if (placed >= count)
                        break;
                }
            }
        }
    }

    /**
     * Calculates terrain distribution across biomes.
     * 
     * @param n   Number of biomes
     * @param min Minimum coverage percentage
     * @param max Maximum coverage percentage
     * @return Array of tile counts per biome
     */
    private int[] biomeGeneration(int n, float min, float max) {
        int totalTiles = ((chunk.GRID_HEIGHT - 3) * chunk.GRID_WIDTH);
        int minTiles = (int) (totalTiles * (min / 100));
        int maxTiles = (int) (totalTiles * (max / 100));
        int totalTerrain = RandomTaskManager.nextInt(minTiles, maxTiles);

        int[] biomes = new int[n];
        Arrays.fill(biomes, totalTerrain / (2 * n));
        for (int i = 0; i < totalTerrain / 2; i++) {
            biomes[RandomTaskManager.nextInt(0, n - 1)]++;
        }
        return biomes;
    }

    /**
     * Places desert terrain in clustered formations.
     */
    private void placeDeserts() {
        int[] places = biomeGeneration(RandomTaskManager.nextInt(0, 3) + 3, 15, 20);
        int i = 0;
        for (int elt : places) {
            placeRandomTerrain(Terrains.DESERT, elt, i % 4);
            i++;
        }
    }

    /**
     * Creates ocean and coastal areas at map bottom.
     * <p>
     * Layout:
     * <ul>
     * <li>Bottom 2 rows: Ocean</li>
     * <li>Third row: Alternating coast tiles</li>
     * </ul>
     * </p>
     */
    private void placeOceanAndCoast() {
        for (int j = 0; j < chunk.GRID_WIDTH; j++) {
            chunk.mapTerrains[chunk.GRID_HEIGHT - 1][j] = Terrains.OCEAN;
            chunk.mapTerrains[chunk.GRID_HEIGHT - 2][j] = Terrains.OCEAN;
            chunk.mapTerrains[chunk.GRID_HEIGHT - 3][j] = j % 2 == 0 ? Terrains.COAST0 : Terrains.COAST1;
        }
    }

    /**
     * Locates all mountain tiles for river source placement.
     * 
     * @return List of mountain coordinates [row,col]
     */
    private LinkedList<int[]> findMountainPosition() {
        LinkedList<int[]> mountains = new LinkedList<>();
        for (int i = 0; i < chunk.GRID_HEIGHT; i++) {
            for (int j = 0; j < chunk.GRID_WIDTH; j++) {
                if (chunk.mapTerrains[i][j] == Terrains.MOUNTAIN) {
                    mountains.add(new int[] { i, j });
                }
            }
        }
        return mountains;
    }

    /**
     * Generates a meandering river from mountain to ocean.
     * <p>
     * Features:
     * <ul>
     * <li>Starts at random mountain</li>
     * <li>Flows downward with random lateral movement</li>
     * <li>Terminates at ocean</li>
     * <li>Avoids dungeon areas</li>
     * </ul>
     * </p>
     */
    private void placeRiver() {
        LinkedList<int[]> mountains = findMountainPosition();
        int[] start = mountains.get(RandomTaskManager.nextInt(0, mountains.size() - 1));
        int x = start[0], y = start[1];

        while (x < chunk.GRID_HEIGHT - 2) {
            chunk.hCell.setCellIndex(y, x);
            if (x == start[0] && y == start[1]) {
                chunk.mapTerrains[x][y] = Terrains.SOURCE;
            } else if (x == chunk.GRID_HEIGHT - 3) {
                chunk.mapTerrains[x][y] = y % 2 == 1 ? Terrains.OCEAN : Terrains.RIVER;
            } else {
                chunk.mapTerrains[x][y] = Terrains.RIVER;
            }

            List<util.Pair<Integer, Integer>> moves = new ArrayList<>();
            if (y > 0)
                moves.add(new util.Pair<>(chunk.hCell.getNeighborJ(4), chunk.hCell.getNeighborI(4)));
            if (y < chunk.GRID_WIDTH - 1)
                moves.add(new util.Pair<>(chunk.hCell.getNeighborJ(2), chunk.hCell.getNeighborI(2)));
            moves.add(new util.Pair<>(chunk.hCell.getNeighborJ(3), chunk.hCell.getNeighborI(3)));

            if (!moves.isEmpty()) {
                do {
                    if (x == chunk.GRID_HEIGHT - 3)
                        return;
                    util.Pair<Integer, Integer> move = moves.get(RandomTaskManager.nextInt(0, moves.size() - 1));
                    x = move.first();
                    y = move.second();
                } while (Arrays.asList(MapModel.dungeons).contains(chunk.mapTerrains[x][y])
                        || chunk.mapTerrains[x][y] == Terrains.MAINDUNGEON);
            }
        }
    }

    /**
     * Places bridges at intervals along rivers.
     * <p>
     * Ensures:
     * <ul>
     * <li>Minimum 3 bridges per river</li>
     * <li>Spacing between bridges</li>
     * <li>Random distribution weighted toward needed areas</li>
     * </ul>
     * </p>
     */
    private void placeBridge() {
        int bridgesPlaced = 0;
        for (int i = chunk.GRID_HEIGHT - 5; i >= 0 && bridgesPlaced < 3; i--) {
            for (int j = 0; j < chunk.GRID_WIDTH && bridgesPlaced < 3; j++) {
                if (chunk.mapTerrains[i][j] == Terrains.RIVER) {
                    if (bridgesPlaced == 0 || RandomTaskManager.nextInt(0, 99) > (99 - 99 / (bridgesPlaced + 1))) {
                        chunk.mapTerrains[i][j] = Terrains.BRIDGE;
                        bridgesPlaced++;
                        i = Math.max(i - 4, 0); // Maintain spacing
                    }
                }
            }
        }
    }

    /**
     * Checks if coordinates are within map bounds.
     * 
     * @param row Row index
     * @param col Column index
     * @return true if coordinates are valid
     */
    public boolean isInsideGrid(int row, int col) {
        return 0 <= row && row < chunk.GRID_WIDTH && 0 <= col && col < chunk.GRID_HEIGHT;
    }

    /**
     * Verifies if neighboring tiles can be overwritten.
     * 
     * @param row Center row
     * @param col Center column
     * @return true if all neighbors are disposable terrain
     */
    private boolean areNeighborDisposable(int row, int col) {
        Set<Terrains> disposable = Set.of(Terrains.DESERT, Terrains.MOUNTAIN, Terrains.PLAIN);
        chunk.hCell.setCellIndex(col, row);

        for (int i = 0; i < 6; i++) {
            int c = chunk.hCell.getNeighborI(i);
            int r = chunk.hCell.getNeighborJ(i);
            if (isInsideGrid(r, c) && !disposable.contains(chunk.mapTerrains[r][c])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fills adjacent tiles with specified terrain variants.
     * 
     * @param terrains Array of terrain variants
     * @param row      Center row
     * @param col      Center column
     */
    private void fillNeighborWith(Terrains[] terrains, int row, int col) {
        chunk.hCell.setCellIndex(col, row);
        for (int i = 0; i < 6; i++) {
            int c = chunk.hCell.getNeighborI(i);
            int r = chunk.hCell.getNeighborJ(i);
            if (isInsideGrid(r, c)) {
                chunk.mapTerrains[r][c] = terrains[i];
            }
        }
    }

    /**
     * Places villages in suitable locations.
     * <p>
     * Requirements:
     * <ul>
     * <li>3-5 villages per map</li>
     * <li>Surrounded by disposable terrain</li>
     * <li>Themed surrounding tiles</li>
     * </ul>
     * </p>
     */
    private void placeVillages() {
        int villagesToPlace = RandomTaskManager.nextInt(5, 10);
        while (villagesToPlace > 0) {
            int row = RandomTaskManager.nextInt(1, chunk.GRID_HEIGHT - 5);
            int col = RandomTaskManager.nextInt(1, chunk.GRID_WIDTH - 2);

            if (areNeighborDisposable(row, col) && areVillagesFarEnough(row, col)) {
                chunk.mapTerrains[row][col] = Terrains.VILLAGE;
                chunk.villageCoordList.add(new util.Pair<>(row, col));
                fillNeighborWith(MapModel.villages, row, col);
                villagesToPlace--;
            }
        }
    }

    private void placeCaverne() {
        LinkedList<int[]> l = findMountainPosition();
        int[] coord = l.get(RandomTaskManager.nextInt(0, l.size() - 1));
        chunk.mapTerrains[coord[0]][coord[1]] = Terrains.CAVERNE;
        chunk.caverne = new Pair<>(new Pair<Integer, Integer>(coord[0], coord[1]), null);
    }

    /**
     * Checks if a proposed village location is sufficiently far from existing
     * villages.
     * 
     * @param row the row coordinate of the proposed village location
     * @param col the column coordinate of the proposed village location
     * @return true if the proposed location is at least 10 units away from all
     *         existing villages,
     *         false if there is any existing village within 10 units distance
     * 
     * @implNote This method uses Euclidean distance (straight-line distance) to
     *           calculate
     *           the separation between villages. The threshold distance is fixed at
     *           10 units.
     *           The method iterates through all existing village coordinates in the
     *           chunk
     *           and returns false immediately upon finding any village that
     *           violates the
     *           minimum distance requirement.
     */
    private boolean areVillagesFarEnough(int row, int col) {
        for (util.Pair<Integer, Integer> coord : chunk.villageCoordList) {
            double dist = Math.sqrt(Math.pow(row - coord.first(), 2) + Math.pow(col - coord.second(), 2));
            if (dist <= 10) {
                return false;
            }
        }
        return true;
    }

    /**
     * Executes full terrain generation sequence.
     * <p>
     * Order of operations:
     * <ol>
     * <li>Base plains layer</li>
     * <li>Dungeon placement</li>
     * <li>Mountain ranges</li>
     * <li>Desert areas</li>
     * <li>Ocean/coastline</li>
     * <li>River systems</li>
     * <li>Bridges</li>
     * <li>Villages</li>
     * </ol>
     * </p>
     */
    public void initMap() {
        fillWithPlains();
        placeDungeons();
        placeMountains();
        placeDeserts();
        placeOceanAndCoast();
        placeRiver();
        placeBridge();
        placeVillages();
        placeCaverne();
    }
}