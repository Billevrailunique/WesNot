package model;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import model.TileModel.Terrains;
import model.Troop.TroopType;
import util.Pair;
import util.PathFinder;
import util.RandomTaskManager;
import util.Saveable;
import view.GUI;
import view.GameView;

/**
 * Represents the model of a map containing a grid of tiles.
 * This class manages the grid dimensions, initializes the tile map,
 * and provides methods to access the map and check tile accessibility.
 */
public class MapModel implements Saveable {

    /**
     * Enum representing cardinal directions and special "NO","DUNGEON","CAVERNE"
     * direction.
     */
    public enum Directions {
        NORTH, SOUTH, EAST, WEST, DUNGEON, CAVERNE, NO;

        @Override
        public String toString() {
            switch (this) {
                case NORTH:
                    return "au Nord";
                case EAST:
                    return "à l'Est";
                case NO:
                    return "non";
                case SOUTH:
                    return "au Sud";
                case WEST:
                    return "à l'Ouest";
                case DUNGEON:
                    return "dungeon";
                case CAVERNE:
                    return "caverne";
            }
            return "non";
        }
    }

    private static util.Pair<Integer, Integer> coordSpawn;
    private Directions dir = Directions.NO;

    // Terrain type arrays
    public static final Terrains[] plains = { Terrains.PLAIN0, Terrains.PLAIN1, Terrains.PLAIN2 };
    public static final Terrains[] desert = { Terrains.DESERT1, Terrains.DESERT0, Terrains.DESERT2, Terrains.DESERT3 };
    public static final Terrains[] dungeons = { Terrains.DUNGEON0, Terrains.DUNGEON1, Terrains.DUNGEON2,
            Terrains.DUNGEON3, Terrains.DUNGEON4, Terrains.DUNGEON5 };
    public static final Terrains[] villages = { Terrains.VILLAGE0, Terrains.VILLAGE1, Terrains.VILLAGE2,
            Terrains.VILLAGE3, Terrains.VILLAGE4, Terrains.VILLAGE5 };
    private static final Terrains[] mountains = { Terrains.MOUNTAIN0, Terrains.MOUNTAIN1, Terrains.MOUNTAIN2,
            Terrains.MOUNTAIN3 };

    // HashMap storing generated chunks with their universe coordinates
    public HashMap<Pair<Integer, Integer>, Chunk> chunks;
    // Currently displayed chunk
    public Chunk showedChunk;

    private GameModel gameModel;

    private ArrayList<String> loadedData;
    public ArrayList<PlayerModel> players;

    /**
     * Constructs a new MapModel instance and initializes the game world.
     * 
     * @param gameModel the GameModel object mapmodel shares players with
     */
    public MapModel(GameModel gameModel, int nbPlayer, int nbAi) {
        chunks = new HashMap<>();
        this.gameModel = gameModel;

        this.players = gameModel.getPlayers();

        if (GUI.loadSave) {
            load();
        } else {
            for (int i = 1; i <= nbPlayer; i++) {

                PlayerModel dummy = new PlayerModel("P" + i, new ArrayList<>(), 0);
                players.add(dummy);
            }

            for (int i = 1; i <= nbAi; i++) {
                AutomatedPlayer ai = new AutomatedPlayer("AI" + i, new ArrayList<>(), gameModel, this, 1);
                players.add(ai);
            }
        }

        // Create first chunk with random coordinates
        Pair<Integer, Integer> coord = new Pair<Integer, Integer>((int) GUI.seed, (int) (GUI.seed >>> 32));
        showedChunk = new Chunk("surface", coord, 40, 40, this);

        chunks.put(coord, showedChunk);
        //System.out.println("mapmodel instancié, chunks créer, je lui ajoute la 1ere
        // map et ses coord");
        //System.out.println(chunks.toString());
        assert (chunks.get(coord) != null);

        // Initialize troops and tiles
        int count = 0;
        for (int i = 0; i < showedChunk.GRID_HEIGHT; i++) {
            for (int j = 0; j < showedChunk.GRID_WIDTH; j++) {

                if (showedChunk.mapTerrains[i][j] == Terrains.MAINDUNGEON) {
                    if (!GUI.loadSave) {
                        players.get(count).getFaction().add(new Troop(Troop.TroopType.LEADER, j, i, count, coord));
                    }
                    count++;
                }
                builTileModel(i, j);
            }
        }

        // Set up player territories
        for (PlayerModel player : players) {
            for (Troop troop : player.getFaction()) {
                if (troop.getCoordChunk().equals(showedChunk.coordInUnivers)) {
                    showedChunk.map[troop.getY()][troop.getX()].setTroop(troop);
                }
            }
            for (Pair<Integer, Integer> p : player.getClaimedTilesCoord()) {
                player.addClaimedTile(showedChunk.map[p.second()][p.first()]);
                setNeighbourID(player.playerID, p.second(), p.first());
            }
            if (!GUI.loadSave) {
                player.addClaimedTile(
                        showedChunk.map[player.getLeader().getCoord().second()][player.getLeader().getCoord().first()]);
                setNeighbourID(player.playerID, player.getLeader().getCoord().second(),
                        player.getLeader().getCoord().first());
            }
        }
        coordSpawn = new util.Pair<>(players.get(0).getLeader().getY(), players.get(0).getLeader().getX());

        initValueFOW();
        updateFOW(coordSpawn.first(), coordSpawn.second(), players.get(0).getLeader().getFOWUpdateRange());
        printMap();
    }

    /**
     * Loads game data from saved state.
     * Parses player information and troop data from the save file.
     */
    @Override
    public final void load() {
        //System.out.println("Load gc");
        String data = GUI.getLoadedData().get(1);
        String[] playersInfo = data.split("\\#");
        int playerID = 0;
        for (String s : playersInfo) {
            String[] singularPlayerInfo = s.split(";");
            ArrayList<Troop> faction = new ArrayList<>();
            String name = singularPlayerInfo[0];
            boolean isPlayer = name.length() < 3
                    || !name.substring(0, 3).equals("BOT");
            int factionInfoIndex = isPlayer ? 3 : 5;
            String[] factionStrings = singularPlayerInfo[factionInfoIndex].split(",");
            for (int i = 0; i < factionStrings.length; i += 8) {
                //System.out.println(factionStrings[i] + " PID : " + playerID);
                faction.add(new Troop(Troop.TroopType.valueOf(factionStrings[i]),
                        Integer.parseInt(factionStrings[i + 2]), Integer.parseInt(factionStrings[i + 3]),
                        playerID,
                        Integer.parseInt(factionStrings[i + 1]), new Pair<>(
                                Integer.parseInt(factionStrings[i + 4]), Integer.parseInt(factionStrings[i + 5])),
                        Boolean.parseBoolean(factionStrings[i + 6]), Boolean.parseBoolean(factionStrings[i + 7])));
            }
            String claimedTiles = singularPlayerInfo[singularPlayerInfo.length - 1];
            Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");
            Matcher matcher = pattern.matcher(claimedTiles);
            ArrayList<Pair<Integer, Integer>> claimedTilesCoordinates = new ArrayList<>();
            while (matcher.find()) {
                int col = Integer.parseInt(matcher.group(1));
                int row = Integer.parseInt(matcher.group(2));
                claimedTilesCoordinates.add(new Pair<>(col, row));
            }
            if (isPlayer) {
                PlayerModel player = new PlayerModel(name, faction, playerID);
                player.setGold(Integer.parseInt(singularPlayerInfo[1]));
                player.setScore(Integer.parseInt(singularPlayerInfo[2]));
                playerID++;
                player.setClaimedTilesCoord(claimedTilesCoordinates);
                this.players.add(player);
            } else {
                AutomatedPlayer player = new AutomatedPlayer(name.substring(3), faction, this.gameModel,
                        this, singularPlayerInfo[3], singularPlayerInfo[4], playerID);
                player.setGold(Integer.parseInt(singularPlayerInfo[1]));
                player.setScore(Integer.parseInt(singularPlayerInfo[2]));
                playerID++;
                player.setClaimedTilesCoord(claimedTilesCoordinates);
                this.players.add(player);
            }

        }

    }

    /**
     * Returns the loaded game data.
     * 
     * @return ArrayList containing loaded game data
     */
    public ArrayList<String> getLoadedData() {
        return loadedData;
    }

    /**
     * Generates a hash string representing the current game state for saving.
     * 
     * @return String containing game state information
     */
    @Override
    public String getContentToHash() {
        String playersSave = "###\n";
        String gameInfo = "###\n" + this.gameModel.getRounds() + ";" + gameModel.getPlayerTurn() + ";" + Settings.FOW
                + "\n";
        for (int i = 0; i < this.gameModel.getPlayers().size(); i++) {
            playersSave += this.gameModel.getPlayers().get(i).getSave();
        }
        playersSave += "\n###";
        return gameInfo + playersSave + "\n" + this.showedChunk.seed;
    }

    /**
     * Builds a TileModel at the specified coordinates with appropriate terrain.
     * 
     * @param i The row coordinate
     * @param j The column coordinate
     */
    public final void builTileModel(int i, int j) {
        if (showedChunk.mapTerrains[i][j] != Terrains.PLAIN
                && showedChunk.mapTerrains[i][j] != Terrains.DESERT
                && showedChunk.mapTerrains[i][j] != Terrains.MOUNTAIN) {
            showedChunk.map[i][j] = new TileModel(showedChunk.mapTerrains[i][j], null, i, j);
        } else {
            if (showedChunk.mapTerrains[i][j] == Terrains.DESERT) {
                showedChunk.map[i][j] = new TileModel(desert[RandomTaskManager.nextInt(0, desert.length - 1)], null, i,
                        j);
            } else if (showedChunk.mapTerrains[i][j] == Terrains.MOUNTAIN) {
                showedChunk.map[i][j] = new TileModel(mountains[RandomTaskManager.nextInt(0, mountains.length - 1)],
                        null, i, j);
            } else {
                showedChunk.map[i][j] = new TileModel(plains[RandomTaskManager.nextInt(0, plains.length - 1)], null, i,
                        j);
            }
        }
    }

    public void updateTroop() {
        for (PlayerModel player : players) {
            for (Troop troop : player.getFaction()) {
                if (troop.getCoordChunk() == showedChunk.coordInUnivers && troop.isAlive()) {
                    addTroop(troop);
                }
            }
        }
    }

    /**
     * Initializes the Fog of War (FOW) by setting all tiles as undiscovered.
     */
    public final void initValueFOW() {
        for (boolean[] row : showedChunk.discovered) {
            Arrays.fill(row, false);
        }
    }

    /**
     * Gets the current edge direction if a unit is at the map edge.
     * 
     * @return The current direction if at edge, or NO if not at edge
     */
    public Directions getDir() {
        return dir;
    }

    /**
     * Updates the Fog of War (FOW) around a specified position.
     * 
     * @param row The row coordinate of the center position
     * @param col The column coordinate of the center position
     * @param rec The recursion depth for FOW update (radius of visibility)
     */
    public final void updateFOW(int row, int col, int rec) {
        showedChunk.discovered[row][col] = true;
        if (rec <= 0) {
            return;
        }

        for (int i = 0; i < 6; i++) {
            Chunk.hCell.setCellIndex(col, row);
            int c = Chunk.hCell.getNeighborI(i);
            int r = Chunk.hCell.getNeighborJ(i);
            if (isInsideGrid(r, c)) {
                showedChunk.discovered[r][c] = true;
                if (rec > 1) {
                    updateFOW(r, c, rec - 1);
                }
            }
        }
    }

    /**
     * Checks if a tile at given coordinates has been discovered.
     * 
     * @param row The row coordinate
     * @param col The column coordinate
     * @return True if the tile has been discovered, false otherwise
     */
    public boolean isDiscovered(int row, int col) {
        return showedChunk.discovered[row][col];
    }

    /**
     * Prints an array to the console for debugging purposes.
     * 
     * @param array The array to print
     */
    public static void printArray(int[] array) {
        //System.out.print("[");
        for (int i = 0; i < array.length; i++) {
            //System.out.print(array[i]);
            if (i < array.length - 1) {
                //System.out.print(", ");
            }
        }
        //System.out.println("]");
    }

    /**
     * Prints a boolean matrix to the console for debugging purposes.
     * 
     * @param matrice The matrix to print
     */
    public static void printMatrix(boolean[][] matrice) {
        for (boolean[] ligne : matrice) {
            for (boolean valeur : ligne) {
                //System.out.print(valeur ? "1 " : "0 ");
            }
            //System.out.println();
        }
    }

    /**
     * Gets the current map grid.
     * 
     * @return The 2D array of TileModel objects representing the map
     */
    public TileModel[][] getMap() {
        return showedChunk.map;
    }

    /**
     * Gets the terrain types of the current map.
     * 
     * @return The 2D array of Terrains representing the map
     */
    public Terrains[][] getTerrains() {
        return showedChunk.mapTerrains;
    }

    /**
     * Checks if a tile at given coordinates is accessible.
     * 
     * @param row The row coordinate
     * @param col The column coordinate
     * @return True if the tile exists and is accessible, false otherwise
     */
    public boolean isAccessible(int row, int col) {
        return isInsideGrid(row, col) && showedChunk.map[row][col].isAccessible();
    }

    /**
     * Checks if given coordinates are within the map bounds.
     * 
     * @param row The row coordinate
     * @param col The column coordinate
     * @return True if the coordinates are valid, false otherwise
     */
    public boolean isInsideGrid(int row, int col) {
        return showedChunk.isInsideGrid(row, col);
    }

    /**
     * Sets the player ID for a tile and its neighbors.
     * 
     * @param playerID The player ID to set
     * @param row      The row coordinate of the center tile
     * @param col      The column coordinate of the center tile
     */
    public final void setNeighbourID(int playerID, int row, int col) {
        Chunk.hCell.setCellIndex(col, row);
        if (isInsideGrid(row, col)) {
            showedChunk.map[row][col].setPlayerID(playerID);
            for (int i = 0; i < 6; i++) {
                int c = Chunk.hCell.getNeighborI(i);
                int r = Chunk.hCell.getNeighborJ(i);
                if (isInsideGrid(r, c)) {
                    showedChunk.map[r][c].setPlayerID(playerID);
                }
            }
        }
    }

    /**
     * Prints the current map terrain to the console for debugging.
     */
    public final void printMap() {
        for (Terrains[] row : showedChunk.mapTerrains) {
            for (Terrains cell : row) {
                //System.out.print(cell.toString().charAt(0) + " ");
            }
            //System.out.println();
        }
    }

    /**
     * Adds a troop to the map at its current position.
     * 
     * @param unit The troop to add
     */
    public void addTroop(Troop unit) {
        showedChunk.map[unit.getY()][unit.getX()].setTroop(unit);
    }

    /**
     * Removes a troop from the map at its current position.
     * 
     * @param unit The troop to remove
     */
    private void removeTroop(Troop unit) {
        showedChunk.map[unit.getY()][unit.getX()].setTroop(null);
    }

    /**
     * Checks if a position is at the edge of the map.
     * 
     * @param row The row coordinate
     * @param col The column coordinate
     * @return The direction of the edge, or null if not at edge
     */
    public Directions isEdge(int row, int col) {
        if (row == 0) {
            return Directions.NORTH;
        }
        if (col == 0) {
            return Directions.WEST;
        }
        if (row == showedChunk.GRID_HEIGHT - 1) {
            return Directions.SOUTH;
        }
        if (col == showedChunk.GRID_WIDTH - 1) {
            return Directions.EAST;
        }
        if (showedChunk.map[row][col].getTerrain() == Terrains.MAINDUNGEON) {
            return Directions.DUNGEON;
        }
        if (showedChunk.map[row][col].getTerrain() == Terrains.CAVERNE) {
            return Directions.CAVERNE;
        }
        return Directions.NO;
    }

    /**
     * Moves a troop to a new position on the map.
     * 
     * @param unit The troop to move
     * @param row  The target row coordinate
     * @param col  The target column coordinate
     */
    public void move(Troop unit, int row, int col, Pair<Integer, Integer> coordChunk) {
        removeTroop(unit);
        unit.setX(PathFinder.nearestValidTile(showedChunk.map, new Pair<Integer, Integer>(col, row)).first());
        unit.setY(PathFinder.nearestValidTile(showedChunk.map, new Pair<Integer, Integer>(col, row)).second());
        unit.setCoordChunk(coordChunk);
        addTroop(unit);

        if (unit.getType() == TroopType.LEADER) {
            Directions direct = isEdge(row, col);
            this.dir = direct;
            if (direct != null) {
                // dire au bouton de changDir de prendre tel text et de s'afficher
                GameView.direction = direct.toString();

            }
        }
    }

    /**
     * Gets the spawn coordinates of the first player's leader.
     * 
     * @return Pair containing spawn coordinates (row, col)
     */
    public static Pair<Integer, Integer> getCoordSpawn() {
        return coordSpawn;
    }

    public Chunk changeChunk(Directions direct, Troop leader) {
        Pair<Integer, Integer> coordChunk = new Pair<>(0, 0);
        Pair<Integer, Integer> landingCoord = new Pair<>(0, 0);
        landingCoord = landingCoord.copie(leader.getCoord());
        coordChunk = coordChunk.copie(showedChunk.coordInUnivers);
        Pair<Integer, Integer> tempvar = new Pair<Integer, Integer>(0, 0);
        tempvar = tempvar.copie(showedChunk.coordInUnivers);
        //System.out.println("coordonnée avant changement : " +
        // leader.getCoord().toString());
        switch (direct) {
            case NORTH:
                coordChunk.setSecond(coordChunk.second() - 1);
                break;
            case EAST:
                coordChunk.setFirst(coordChunk.first() + 1);
                break;
            case SOUTH:
                coordChunk.setSecond(coordChunk.second() + 1);
                break;
            case WEST:
                coordChunk.setFirst(coordChunk.first() - 1);
                break;
            case DUNGEON:
            case CAVERNE:
                coordChunk = coordChunk.copie(leader.getCoord());
                break;
            case NO:
                coordChunk = coordChunk.copie(showedChunk.oldChunk);
                break;
        }
        if (direct != Directions.NO && direct != Directions.CAVERNE && direct != Directions.DUNGEON) {
            if (chunkExist(coordChunk)) {
                showedChunk = chunks.get(coordChunk);
            } else {
                showedChunk = new Chunk("surface", coordChunk, 40, 40, this);
                chunks.put(coordChunk, showedChunk);
            }
        } else if (direct == Directions.DUNGEON) {
            chunks.put(tempvar, showedChunk);
            if (dungeonExist(coordChunk)) {
                Chunk oldChunk = showedChunk;
                showedChunk = showedChunk.dungeonsCoord.get(coordChunk);
                showedChunk.oldChunk = oldChunk.coordInUnivers;
            } else {
                Chunk oldChunk = showedChunk;
                showedChunk = new Chunk("dungeon", coordChunk, 40, 40, this);
                showedChunk.oldChunk = oldChunk.coordInUnivers;
                oldChunk.dungeonsCoord.put(coordChunk, showedChunk);
            }
            leader.oldCoord = leader.oldCoord.copie(leader.getCoord());
            //System.out.println("je rentre dans le chunk de coord : " +
            // coordChunk.toString() + ". en partant de "
            // + tempvar.toString());
        } else if (direct == Directions.CAVERNE) {
            if (showedChunk.caverne.second() == null) {
                Chunk oldChunk = showedChunk;
                showedChunk = new Chunk("caverne", oldChunk.coordInUnivers, 40, 40, this);
                showedChunk.oldChunk = oldChunk.coordInUnivers;
            } else {
                Chunk oldChunk = showedChunk;
                showedChunk = showedChunk.caverne.second();
                showedChunk.oldChunk = oldChunk.coordInUnivers;
            }
        } else if (direct == Directions.NO) {
            if (chunkExist(coordChunk)) {
                showedChunk = chunks.get(coordChunk);
            } else {
                //System.out.println("le chunk de coordonée " + coordChunk.toString() + ". n'existe pas");
                //System.out.println(chunks.toString());
            }
        }

        // printMap();

        // Calculate landing position in new chunk
        switch (dir) {
            case NORTH:
                landingCoord.setSecond(showedChunk.GRID_HEIGHT - 1);
                break;
            case EAST:
                landingCoord.setFirst(0);
                break;
            case NO:
                landingCoord = landingCoord.copie(leader.oldCoord);
                break;
            case CAVERNE:
                landingCoord = showedChunk.getLandingPoint();
                break;
            case DUNGEON:
                landingCoord = showedChunk.getLandingPoint();
                //System.out.println("landing point vaut " +
                // showedChunk.getLandingPoint().toString());
                break;
            case SOUTH:
                landingCoord.setSecond(0);
                break;
            case WEST:
                landingCoord.setFirst(showedChunk.GRID_WIDTH - 1);
                break;
            default:
                break;
        }
        // Move leader to new position
        final Pair<Integer, Integer> test = landingCoord;
        SwingUtilities.invokeLater(() -> {
            updateTroop();
            move(leader, test.second(), test.first(), showedChunk.coordInUnivers);
            updateFOW(PathFinder.nearestValidTile(showedChunk.map, test).second(),
                    PathFinder.nearestValidTile(showedChunk.map, test).first(), leader.getFOWUpdateRange());
        });

        return showedChunk;
    }

    /**
     * Checks if a chunk exists at the given universe coordinates.
     * 
     * @param coordInUniverse The coordinates to check
     * @return True if the chunk exists, false otherwise
     */
    public boolean chunkExist(Pair<Integer, Integer> coordInUniverse) {
        return chunks.get(coordInUniverse) != null;
    }

    /**
     * Checks if a dungeon exists at the given coordinates.
     * 
     * @param coord The coordinates to check
     * @return True if the dungeon exists, false otherwise
     */
    public boolean dungeonExist(Pair<Integer, Integer> coord) {
        return showedChunk.dungeonsCoord.get(coord) != null;
    }
}