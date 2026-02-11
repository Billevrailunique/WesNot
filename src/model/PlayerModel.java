package model;

import java.util.ArrayList;
import model.Troop.TroopType;
import util.HexCell;

/**
 * Represents a player in the game with their associated attributes and
 * behaviors.
 * <p>
 * This class models player information including their name, faction (troops),
 * claimed territories, gold resources, and score.
 * </p>
 * 
 * @author El mouhtadi Ilyès
 */
public class PlayerModel {

    /**
     * The HexCell object handling coordinates trnasformation
     */
    protected HexCell cell;

    /**
     * The name of the player
     */
    protected final String name;

    /**
     * The list of troops belonging to this player
     */
    protected ArrayList<Troop> faction;

    /**
     * The list of tiles claimed by this player
     */
    protected ArrayList<TileModel> claimedTiles = new ArrayList<>();

    private ArrayList<util.Pair<Integer, Integer>> claimedTilesCoord = new ArrayList<>();

    /**
     * The amount of gold the player possesses (used for recruiting and score
     * calculation)
     */
    protected int gold = 10;

    /**
     * The player's current score
     */
    protected int score = 0;
    /**
     * 
     * The ID of the player
     */
    protected int playerID;

    /**
     * Constructs a new PlayerModel with the specified name and faction.
     * 
     * @param name    The name of the player
     * @param faction The initial list of troops belonging to the player
     */
    public PlayerModel(String name, ArrayList<Troop> faction, int id) {
        cell = new HexCell(70);
        this.name = name;
        this.faction = faction;
        this.playerID = id;
    }

    // GETTERS AND SETTERS

    /**
     * Gets the player's faction (list of troops).
     * 
     * @return An ArrayList containing the player's troops
     */
    public ArrayList<Troop> getFaction() {
        return faction;
    }

    public void setClaimedTilesCoord(ArrayList<util.Pair<Integer, Integer>> claimedTiles) {
        this.claimedTilesCoord = claimedTiles;
    }

    public ArrayList<util.Pair<Integer, Integer>> getClaimedTilesCoord() {
        return this.claimedTilesCoord;
    }

    /**
     * Gets the list of tiles claimed by this player.
     * 
     * @return An ArrayList of claimed TileModel objects
     */
    public ArrayList<TileModel> getClaimedTiles() {
        return claimedTiles;
    }

    /**
     * Generates a save string representation of the player's data.
     * <p>
     * The format is: "name;gold;troop1,troop2,...#"
     * </p>
     * 
     * @return A string containing the player's save data
     */
    public String getSave() {
        String res = this.name + ";" + this.gold + ";" + this.score + ";";
        for (int i = 0; i < faction.size() - 1; i++) {
            res += faction.get(i).toString() + ",";
        }
        res += faction.get(faction.size() - 1).toString() + ";";
        for (int j = 0; j < claimedTiles.size() - 1; j++) {
            res += claimedTiles.get(j).getCoordinates().toString() + ",";
        }
        res += claimedTiles.getLast().getCoordinates().toString();
        return res + "#";
    }

    /**
     * Gets the leader troop of this player's faction.
     * 
     * @return The leader Troop object, or null if no leader exists
     */
    public Troop getLeader() {
        for (Troop t : faction) {
            if (t.getType() == TroopType.LEADER) {
                return t;
            }
        }
        return null;
    }

    /**
     * Gets the player's name.
     * 
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's current gold amount.
     * 
     * @return The amount of gold
     */
    public int getGold() {
        return gold;
    }

    public void setScore(int n) {
        score = n;
    }

    /**
     * Checks if the player has been eliminated from the game.
     * <p>
     * A player is considered eliminated when all their troops are dead.
     * </p>
     * 
     * @return true if the player has no living troops, false otherwise
     */
    public boolean hasBeenEliminated() {
        int numAlive = 0;
        for (Troop troop : faction) {
            numAlive += troop.isAlive() ? 1 : 0;
        }
        return numAlive == 0;
    }

    // RESOURCE MANAGEMENT METHODS

    /**
     * Adds gold to the player's inventory and increases their score.
     * 
     * @param a The amount of gold to add
     */
    public void addGold(int a) {
        gold += a;
        score += a * 10;
    }

    /**
     * Sets the player's gold to a specific amount.
     * 
     * @param n The new gold amount
     */
    public void setGold(int n) {
        gold = n;
    }

    /**
     * Subtracts gold from the player's inventory and decreases their score.
     * 
     * @param a The amount of gold to subtract
     */
    public void subGold(int a) {
        gold -= a;
        score = Math.max(score - a * 10, 0);
    }

    // SCORE MANAGEMENT METHODS

    /**
     * Gets the player's current score.
     * 
     * @return The player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Adds points to the player's score.
     * 
     * @param a The number of points to add
     */
    public void addScore(int a) {
        //System.out.println("added " + a + " to score");
        score += a;
    }

    /**
     * Subtracts points from the player's score.
     * 
     * @param a The number of points to subtract
     */
    public void subScore(int a) {
        score -= a;
    }

    // TERRITORY MANAGEMENT METHODS

    /**
     * Adds a tile to the player's list of claimed territories.
     * 
     * @param t The TileModel to add
     */
    public void addClaimedTile(TileModel t) {
        if (!claimedTilesCoord.contains(t.getCoordinates())) {

            claimedTilesCoord.add(t.getCoordinates());
        }
        claimedTiles.add(t);
        t.setPlayerID(this.playerID);
    }

    /**
     * Adds gold rewards for all claimed territories.
     * <p>
     * Each claimed tile contributes its value to the player's gold.
     * </p>
     */
    public void addTerritoryRewards() {
        for (TileModel claimedTile : claimedTiles) {
            addGold(claimedTile.getValue());
        }
    }
}