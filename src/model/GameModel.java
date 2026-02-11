package model;

import java.util.ArrayList;
import view.GUI;

/**
 * The GameModel class represents the core game state and logic.
 * It manages game rounds, player turns, interactions, and overall game
 * progression.
 */
public class GameModel {
    /**
     * Enumeration of possible interaction modes in the game.
     */
    public enum InteractionModes {
        ATTACKING,
        MOVING,
        RECRUITING,
        IDLE,
        CLAIMING
    }

    private int rounds = 1;
    private int time;
    private ArrayList<PlayerModel> players;
    private int playerTurn = 0;
    private Troop selectedTroop;
    private Attack selectedAttack;
    private TileModel selectedTile;
    private InteractionModes interactionMode;
    private Troop.TroopType recruit;

    /**
     * Constructs a new GameModel with the specified players.
     * If no players are provided or loading from save is disabled, creates default
     * players.
     * 
     */
    public GameModel() {
        if (GUI.loadSave) {
            load();
        }
        this.players = new ArrayList<>();
        interactionMode = InteractionModes.IDLE;
    }

    /**
     * Loads game model data from saved state.
     * Parses round and turn information from the save file.
     */
    public void load() {
        //System.out.println("Load GM");
        String data = GUI.getLoadedData().get(0);
        String[] dataArray = data.split(";");
        setTurn(Math.max(Integer.parseInt(dataArray[1]), 0));
        setRound(Math.max(Integer.parseInt(dataArray[0]), 1));
        Settings.FOW = Boolean.parseBoolean(dataArray[2]);
    }

    /**
     * Returns the current player whose turn it is.
     * 
     * @return the current PlayerModel instance
     */
    public PlayerModel getCurrentPlayer() {
        return players.get(playerTurn);
    }

    /**
     * Finds a player by their name.
     * 
     * @param s the name of the player to find
     * @return the PlayerModel with matching name, or null if not found
     */
    public PlayerModel getPlayerByName(String s) {
        for (PlayerModel p : players) {
            if (p.getName().equals(s)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns the current round number.
     * 
     * @return the current round number
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * Returns the current game time.
     * 
     * @return the current time value
     */
    public int getTime() {
        return time;
    }

    /**
     * Returns the list of all players in the game.
     * 
     * @return ArrayList of PlayerModel instances
     */
    public ArrayList<PlayerModel> getPlayers() {
        return players;
    }

    /**
     * Returns the index of the current player's turn.
     * 
     * @return the current player turn index
     */
    public int getPlayerTurn() {
        return playerTurn;
    }

    /**
     * Returns the currently selected tile.
     * 
     * @return the selected TileModel
     */
    public TileModel getSelectedTile() {
        return selectedTile;
    }

    /**
     * Returns the currently selected troop.
     * 
     * @return the selected Troop
     */
    public Troop getSelectedTroop() {
        return selectedTroop;
    }

    /**
     * Returns the troop type being recruited.
     * 
     * @return the TroopType being recruited
     */
    public Troop.TroopType getRecruit() {
        return this.recruit;
    }

    /**
     * Sets the troop type to be recruited.
     * 
     * @param t the TroopType to set for recruitment
     */
    public void setRecruit(Troop.TroopType t) {
        this.recruit = t;
    }

    /**
     * Sets the currently selected troop.
     * 
     * @param selected the Troop to select
     */
    public void setSelectedTroop(Troop selected) {
        this.selectedTroop = selected;
    }

    /**
     * Sets the current player turn.
     * 
     * @param n the turn index to set (will be modulo 3)
     */
    public void setTurn(int n) {
        this.playerTurn = n % 3;
    }

    /**
     * Sets the current round number.
     * 
     * @param n the round number to set
     */
    public void setRound(int n) {
        this.rounds = n;
    }

    /**
     * Sets the currently selected tile.
     * 
     * @param tile the TileModel to select
     */
    public void setSelectedTile(TileModel tile) {
        this.selectedTile = tile;
    }

    /**
     * Advances to the next player's turn.
     * If the next player is an AutomatedPlayer, triggers their automatic turn.
     * 
     * @return a boolean that indicates if the player who's turn it is is an AI
     */
    public boolean nextTurn() {
        players.get(playerTurn).addScore(150);
        playerTurn++;
        //System.out.println("Turn " + playerTurn);
        //System.out.println("Round " + rounds);
        PlayerModel player = getPlayers().get(getPlayerTurn());
        if (player.hasBeenEliminated()) {
            nextTurn();
        }
        if (player instanceof AutomatedPlayer) {
            ((AutomatedPlayer) player).playRound();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Resets the turn counter to the first player.
     */
    public void resetTurn() {
        playerTurn = 0;
    }

    /**
     * Advances to the next round.
     * Awards territory rewards to all players.
     */
    public void addRound() {
        rounds++;
        for (PlayerModel player : players) {
            player.addTerritoryRewards();
        }
    }

    /**
     * Returns the current interaction mode.
     * 
     * @return the current InteractionModes value
     */
    public InteractionModes getInteractionMode() {
        return interactionMode;
    }

    /**
     * Sets the current interaction mode.
     * 
     * @param m the InteractionModes value to set
     */
    public void setInteractionMode(InteractionModes m) {
        this.interactionMode = m;
    }

    /**
     * Returns the currently selected attack.
     * 
     * @return the selected Attack
     */
    public Attack getSelectedAttack() {
        return selectedAttack;
    }

    /**
     * Sets the currently selected attack.
     * 
     * @param attack the Attack to select
     */
    public void setSelectedAttack(Attack attack) {
        this.selectedAttack = attack;
    }
}