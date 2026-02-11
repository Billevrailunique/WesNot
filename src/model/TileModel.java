package model;

import java.awt.*;
import java.util.Arrays;
import util.Pair;

/**
 * Represents an individual tile on the game map with terrain properties and
 * occupant information.
 * <p>
 * Each tile maintains:
 * <ul>
 * <li>Terrain type and associated properties</li>
 * <li>Occupying troop unit (if any)</li>
 * <li>Ownership information</li>
 * <li>Accessibility and movement characteristics</li>
 * </ul>
 * </p>
 */
public class TileModel {

    /**
     * Enumeration of all possible terrain types in the game.
     * <p>
     * Includes natural features, structures, and special locations.
     * </p>
     */
    public enum Terrains {
        SOURCE, RIVER, BRIDGE, COAST0, COAST1,
        PLAIN0, PLAIN1, PLAIN2, PLAIN3, PLAIN,
        DESERT, DESERT0, DESERT1, DESERT2, DESERT3,
        MOUNTAIN, MOUNTAIN0, MOUNTAIN1, MOUNTAIN2, MOUNTAIN3,
        OCEAN,
        MAINDUNGEON, DUNGEON0, DUNGEON1, DUNGEON2, DUNGEON3, DUNGEON4, DUNGEON5,
        VILLAGE, VILLAGE0,
        VILLAGE1, VILLAGE2, VILLAGE3, VILLAGE4, VILLAGE5,
        CAVERNE,
        EXIT, REWARD,
        MUR, SOL
    }

    private int playerID; // -1 = unclaimed, 0+ = player/AI IDs
    private Troop unit;
    private Terrains terrain;
    private Pair<Integer, Integer> coordinates;
    private final Terrains[] claimable = { Terrains.MAINDUNGEON, Terrains.VILLAGE };
    private final Terrains[] nearlyClaimable = {
            Terrains.DUNGEON0, Terrains.DUNGEON1, Terrains.DUNGEON2,
            Terrains.DUNGEON3, Terrains.DUNGEON4, Terrains.DUNGEON5,
            Terrains.VILLAGE0, Terrains.VILLAGE1, Terrains.VILLAGE2,
            Terrains.VILLAGE3, Terrains.VILLAGE4, Terrains.VILLAGE5
    };
    private int value;
    private Image terrain_Image;

    /**
     * Constructs a basic tile with terrain and optional troop.
     * 
     * @param terrain The base terrain type
     * @param unit    The occupying troop (can be null)
     */
    public TileModel(Terrains terrain, Troop unit) {
        this.terrain = terrain;
        this.unit = unit;
        this.playerID = -1;
        this.value = calculateTerrainValue(terrain);
    }

    /**
     * Constructs a positioned tile with terrain and optional troop.
     * 
     * @param terrain The base terrain type
     * @param unit    The occupying troop (can be null)
     * @param row     The map row coordinate
     * @param col     The map column coordinate
     */
    public TileModel(Terrains terrain, Troop unit, int row, int col) {
        this(terrain, unit);
        this.coordinates = new Pair<>(col, row);
    }

    /**
     * Constructs a fully specified tile with ownership.
     * 
     * @param terrain  The base terrain type
     * @param unit     The occupying troop (can be null)
     * @param row      The map row coordinate
     * @param col      The map column coordinate
     * @param playerId The owning player ID (-1 for neutral)
     */
    public TileModel(Terrains terrain, Troop unit, int row, int col, int playerId) {
        this(terrain, unit, row, col);
        this.playerID = playerId;
    }

    /**
     * Calculates the strategic value of terrain types.
     * 
     * @param terrain The terrain to evaluate
     * @return The point value (0 for most terrain)
     */
    private int calculateTerrainValue(Terrains terrain) {
        return switch (terrain) {
            case VILLAGE -> 5;
            case MAINDUNGEON -> 15;
            default -> 0;
        };
    }

    /**
     * Gets the tile's map coordinates.
     * 
     * @return Pair containing (column, row) coordinates
     */
    public Pair<Integer, Integer> getCoordinates() {
        return this.coordinates;
    }

    public boolean isRoom() {
        return switch (terrain) {
            case SOL -> true;
            default -> false;
        };
    }

    /**
     * Gets the tile's strategic value.
     * 
     * @return The point value for controlling this tile
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Checks if this tile is adjacent to claimable territory.
     * 
     * @return true if tile borders claimable locations
     */
    public boolean isNearlyClaimable() {
        return Arrays.asList(nearlyClaimable).contains(this.terrain);
    }

    /**
     * Updates the tile's strategic value based on current terrain.
     */
    public void setValue() {
        this.value = calculateTerrainValue(this.terrain);
    }

    /**
     * Gets the occupying troop unit.
     * 
     * @return The troop object or null if empty
     */
    public Troop getTroop() {
        return this.unit;
    }

    /**
     * Gets the owning player ID.
     * 
     * @return Player ID or -1 if unclaimed
     */
    public int getPlayerID() {
        return this.playerID;
    }

    /**
     * Gets the terrain type.
     * 
     * @return The tile's terrain enum value
     */
    public Terrains getTerrain() {
        return this.terrain;
    }

    /**
     * Places a troop unit on this tile.
     * 
     * @param unit The troop to place (can be null to clear)
     */
    public void setTroop(Troop unit) {
        this.unit = unit;
    }

    /**
     * Sets the controlling player.
     * 
     * @param id The player ID (-1 to make neutral)
     */
    public void setPlayerID(int id) {
        this.playerID = id;
    }

    /**
     * Gets the movement cost for traversing this tile.
     * 
     * @return Movement penalty (higher = more difficult)
     */
    public int getMobilityPenalty() {
        return switch (this.terrain) {
            case PLAIN, PLAIN1, PLAIN2, PLAIN3, PLAIN0, BRIDGE, EXIT, REWARD, SOL -> 1;
            case DESERT, DESERT0, DESERT1, DESERT2, DESERT3, COAST0, COAST1 -> 2;
            case MAINDUNGEON, DUNGEON0, DUNGEON1, DUNGEON2, DUNGEON3, DUNGEON4, DUNGEON5 -> 2;
            case VILLAGE, VILLAGE0, VILLAGE1, VILLAGE2, VILLAGE3, VILLAGE4, VILLAGE5 -> 2;
            case MOUNTAIN, MOUNTAIN1, MOUNTAIN2, MOUNTAIN3, CAVERNE -> 3;
            case MUR -> 1000;
            case OCEAN, SOURCE, RIVER -> 4;
            default -> 1;
        };
    }

    /**
     * Checks if this tile can be claimed by players.
     * 
     * @return true if claimable and currently neutral
     */
    public boolean isClaimable() {
        return Arrays.asList(claimable).contains(this.terrain)
                && this.playerID == -1;
    }

    /**
     * Gets the visual representation of the terrain.
     * 
     * @return The terrain's image asset
     */
    public Image getTerrainImage() {
        return this.terrain_Image;
    }

    /**
     * Determines if the tile can be entered by standard units.
     * 
     * @return false for water tiles, true for most others
     */
    public boolean isAccessible() {
        return switch (this.terrain) {
            case OCEAN, RIVER, COAST0, COAST1, MUR -> false;
            default -> true;
        };
    }

    /**
     * Indicates if a certain tilemodel represents a village
     * 
     * @return boolean
     */
    public boolean isVillage() {
        return this.getTerrain() == Terrains.VILLAGE
                || this.getTerrain() == Terrains.VILLAGE0
                || this.getTerrain() == Terrains.VILLAGE1
                || this.getTerrain() == Terrains.VILLAGE2
                || this.getTerrain() == Terrains.VILLAGE3
                || this.getTerrain() == Terrains.VILLAGE4
                || this.getTerrain() == Terrains.VILLAGE5;

    }

    /**
     * Generates a debug string representation.
     * 
     * @return Formatted string with tile details
     */
    @Override
    public String toString() {
        String base = String.format("Coordinates (col,row): %d;%d Terrain: %s Player: %d",
                this.coordinates.first(),
                this.coordinates.second(),
                this.terrain,
                this.playerID);

        return this.unit != null
                ? base + " Troop: " + this.unit.getName()
                : base + " Troop: null";
    }
}