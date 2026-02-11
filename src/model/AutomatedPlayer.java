package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.TileModel.Terrains;
import model.Troop.TroopType;
import util.Pair;
import util.PathFinder;

/**
 * The AutomatedPlayer class represents a computer-controlled player in the
 * game.
 * It extends PlayerModel with AI behaviors for attacking, moving, recruiting
 * troops,
 * and making strategic decisions based on game state and personality traits.
 */
public class AutomatedPlayer extends PlayerModel {
    private final static int MOVES_PER_TURN = 3;
    private GameModel gameModel;
    private MapModel mapModel;

    private ArrayList<Troop> used;
    private Random r;

    private float targetprioritization;

    private float aggression;
    private float caution;
    private float recruitment;
    private float revenge;

    private PlayerModel lastAttacker;
    private PlayerModel target;

    private static final Terrains[] RECRUITMENT_TERRAINS = {
            Terrains.MAINDUNGEON, Terrains.DUNGEON0, Terrains.DUNGEON1,
            Terrains.DUNGEON2, Terrains.DUNGEON3, Terrains.DUNGEON4, Terrains.DUNGEON5,
            Terrains.VILLAGE, Terrains.VILLAGE0, Terrains.VILLAGE1, Terrains.VILLAGE2,
            Terrains.VILLAGE3, Terrains.VILLAGE4, Terrains.VILLAGE5 };

    /**
     * Enumeration of possible tasks the automated player can perform.
     */
    public enum tasks {
        ATTACKING,
        REVENGE,
        ESCAPING,
        RECRUITING,
        IDLE
    }

    tasks currentTask = tasks.IDLE;

    /**
     * Constructs a new AutomatedPlayer with basic parameters.
     * 
     * @param name      the name of the automated player (prefixed with "BOT")
     * @param faction   the initial troops belonging to this player
     * @param gameModel reference to the game model
     * @param mapModel  reference to the map model
     */
    public AutomatedPlayer(String name, ArrayList<Troop> faction, GameModel gameModel, MapModel mapModel, int id) {
        super("BOT" + name, faction, id);
        this.gameModel = gameModel;
        this.mapModel = mapModel;
        this.r = new Random();
        this.used = new ArrayList<>();
        this.targetprioritization = r.nextFloat();
        do {
            aggression = r.nextFloat();
            caution = r.nextFloat();
            recruitment = r.nextFloat();
            revenge = 1 - (aggression + caution + recruitment);
        } while (!ValidWeights());
    }

    /**
     * Constructs a new AutomatedPlayer with advanced parameters including behavior
     * traits.
     * 
     * @param name      the name of the automated player
     * @param faction   the initial troops belonging to this player
     * @param gameModel reference to the game model
     * @param mapModel  reference to the map model
     * @param lastAtk   name of the player who last attacked this bot
     * @param target    name of this bot's current target player
     */
    public AutomatedPlayer(String name, ArrayList<Troop> faction, GameModel gameModel, MapModel mapModel,
            String lastAtk, String target, int id) {
        super("BOT" + name, faction, id);
        this.gameModel = gameModel;
        this.mapModel = mapModel;
        if (!lastAtk.equals("NULL")) {
            this.lastAttacker = gameModel.getPlayerByName(lastAtk);
        }
        if (!target.equals("NULL")) {
            this.target = gameModel.getPlayerByName(target);
        }
        this.r = new Random();
        this.used = new ArrayList<>();
    }

    private boolean ValidWeights() {
        return 0 <= aggression && aggression <= 1
                && 0 <= caution && caution <= 1
                && 0 <= recruitment && recruitment <= 1
                && 0 <= revenge && revenge <= 1;
    }

    /**
     * Executes the automated player's turn using the task-based behavior
     * system.
     * Performs actions based on current task state (attacking, escaping,
     * recruiting, etc).
     */
    public void playRound() {
        if (hasBeenEliminated())
            return;

        used.removeAll(faction);

        int loopCounter = 0;
        do {
            if (currentTask == tasks.IDLE) {
                selectTask();
            }
            Troop leader = getLeader();
            switch (currentTask) {
                case ATTACKING:
                    //System.out.println("attacking");
                    if (target == null || target.hasBeenEliminated()) {
                        target = selectTarget();
                    }
                    for (int i = 0; i < MOVES_PER_TURN * targetprioritization; i++) {
                        Pair<Troop, Troop> pair = findClosestPair(target);
                        if (pair == null)
                            break;
                        boolean attackSuccess = attemptAttack(pair);

                        used.add(pair.first());
                        if (attackSuccess) {
                            //System.out.println("attack success");
                            currentTask = tasks.IDLE;
                        }
                    }
                    break;
                case ESCAPING:
                    //System.out.println("escaping");
                    Troop weakTroop = findMyWeakestTroop();
                    if (weakTroop == null || weakTroop == leader) {
                        currentTask = tasks.IDLE;
                        break;
                    }
                    if (distance(weakTroop, leader) > 5) {
                        moveToward(weakTroop, leader);
                    } else {
                        currentTask = tasks.IDLE;
                    }
                    break;
                case RECRUITING:
                    //System.out.println("recruiting");
                    if (!leader.isAlive()) {
                        break;
                    }
                    Pair<Integer, Integer> village = nearestVillage();
                    if (village == null || village.first() == null) {
                        currentTask = tasks.IDLE;
                        break;
                    }
                    int s = leader.getSpeed();
                    if (distance(leader, village) > s) {
                        moveToward(leader, village);
                        used.add(leader);
                    } else {
                        moveToward(leader, village);
                        boolean recruitSuccess = recruit();
                        if (recruitSuccess) {
                            used.add(leader);
                        }
                        currentTask = tasks.IDLE;
                    }
                    break;
                case REVENGE:
                    //System.out.println("getting revenge");
                    Pair<Troop, Troop> pair = new Pair<Troop, Troop>(null, null);
                    if (lastAttacker != null) {
                        if (!lastAttacker.hasBeenEliminated())
                            pair = findClosestPair(lastAttacker);
                        else {
                            lastAttacker = null;
                            currentTask = tasks.IDLE;
                        }
                    } else {
                        currentTask = tasks.IDLE;
                    }
                    if (pair == null || pair.first() == null) {
                        currentTask = tasks.IDLE;
                        break;
                    }
                    boolean attackSuccess = attemptAttack(pair);
                    used.add(pair.first());
                    if (attackSuccess) {
                        lastAttacker = null;
                        currentTask = tasks.IDLE;
                    }
                    break;
                default:
                    break;
            }
            loopCounter++;
        } while ((used.size() < MOVES_PER_TURN) && used.size() != faction.size() && loopCounter < 15);
        //System.out.println("turn finished\n");
    }

    /**
     * Selects the next task for the automated player to perform.
     */
    private void selectTask() {
        //System.out.println("selecting task");
        float r = this.r.nextFloat();
        if (r < aggression) {
            currentTask = tasks.ATTACKING;
        } else if (aggression <= r && r < aggression + caution) {
            currentTask = tasks.ESCAPING;
        } else if (aggression + caution <= r && r < aggression + caution + recruitment) {
            currentTask = tasks.RECRUITING;
        } else if (aggression + caution + recruitment <= r) {
            currentTask = tasks.REVENGE;
        }
        //System.out.println("task is : " + currentTask);
    }

    /**
     * Selects a target player based on weakest faction size.
     * 
     * @return the player with the smallest faction
     */
    private PlayerModel selectTarget() {
        ArrayList<PlayerModel> players = gameModel.getPlayers();
        int numPlayers = gameModel.getPlayers().size();
        int weakestPlayerId = -1;
        int minSize = -1;
        for (int i = 0; i < numPlayers; i++) {
            int s = players.get(i).getFaction().size();
            if (weakestPlayerId == -1 && !players.get(i).hasBeenEliminated()) {
                weakestPlayerId = i;
                minSize = s;
            }
            if (s < minSize) {
                weakestPlayerId = i;
                minSize = s;
            }
        }
        return players.get(weakestPlayerId);
    }

    /**
     * Handles troop recruitment for the automated player.
     * 
     * @return boolean that indicates whether the action succeded or failed
     */
    private boolean recruit() {
        TileModel[][] tiles = mapModel.showedChunk.map;
        Pair<Integer, Integer> coords = PathFinder.nearestValidTile(tiles, getLeader().getCoord());
        TroopType type = TroopType.values()[1 + r.nextInt(4)];
        // this should randomly select a troop typ ethat isnt leader
        if (gold >= Troop.getCost(type)) {
            gold -= Troop.getCost(type);
            Troop newTroop = new Troop(type, coords.first(), coords.second(), playerID,
                    mapModel.showedChunk.coordInUnivers);
            mapModel.addTroop(newTroop);
            return true;
        }
        return false;
    }

    /**
     * Generates a save string representing the automated player's state.
     * 
     * @return a formatted string containing all necessary save data
     */
    @Override
    public String getSave() {
        String res = lastAttacker != null && target != null
                ? this.name + ";" + this.gold + ";" + this.score + ";" + lastAttacker.getName() + ";" + target.getName()
                        + ";"
                : lastAttacker != null
                        ? this.name + ";" + this.gold + ";" + this.score + ";" + lastAttacker.getName() + ";" + "NULL"
                                + ";"
                        : target != null
                                ? this.name + ";" + this.gold + ";" + this.score + ";" + "NULL" + ";" + target.getName()+";"
                                : this.name + ";" + this.gold + ";" + this.score + ";" + "NULL" + ";" + "NULL" + ";";
        for (int i = 0; i < faction.size() - 1; i++) {
            res += faction.get(i).toString() + ",";
        }
        res += faction.get(faction.size() - 1).toString();

        return res + "#";
    }

    /**
     * Finds the coordinates of the nearest village to the leader.
     * 
     * @return Pair containing village coordinates
     */
    private Pair<Integer, Integer> nearestVillage() {
        Pair<Integer, Integer> closestVillage = new Pair<Integer, Integer>(null, null);
        ArrayList<TileModel> villages = new ArrayList<>();
        Integer minDistance = null;
        for (TileModel tileModel : claimedTiles) {
            if (Arrays.asList(RECRUITMENT_TERRAINS).contains(tileModel.getTerrain())) {
                villages.add(tileModel);
            }
        }
        for (TileModel village : villages) {
            for (Troop troop : faction) {
                if (!used.contains(troop)) {
                    Pair<Integer, Integer> villageCoords = village.getCoordinates();
                    Pair<Integer, Integer> troopCoords = troop.getCoord();

                    TileModel[][] tiles = mapModel.showedChunk.map;

                    List<Pair<Integer, Integer>> path = PathFinder.getPath(tiles, troopCoords, villageCoords);

                    int distance = PathFinder.costFromPath(tiles, path);
                    if (minDistance == null || distance < minDistance) {
                        minDistance = distance;
                        closestVillage = villageCoords;
                    }
                }
            }
        }
        return closestVillage;
    }

    /**
     * Finds the closest pair of troops between this player and an enemy.
     * 
     * @param enemy the enemy player to find closest troops against
     * @return Pair containing this player's troop and enemy's troop
     */
    private Pair<Troop, Troop> findClosestPair(PlayerModel enemy) {
        int closestid = findTroop();
        if (closestid < 0)
            return null;
        Troop closest = faction.get(closestid);
        Troop closestEnemy = findEnemyTroop(enemy);
        if (closestEnemy == null)
            return null;
        int minDistance = distance(closest, closestEnemy);
        for (Troop troop : faction) {
            for (Troop enemyTroop : enemy.getFaction()) {
                if (used.contains(troop)) {
                    continue;
                }
                int d = distance(troop, enemyTroop);
                if (d < minDistance) {
                    closest = troop;
                    closestEnemy = enemyTroop;
                    minDistance = d;
                }
            }
        }
        if (used.contains(closest)) {
            return null;
        }
        return new Pair<Troop, Troop>(closest, closestEnemy);
    }

    /**
     * Finds the first living troop in this player's faction.
     * 
     * @return index of living troop, or -1 if none found
     */
    private int findTroop() {
        for (int i = 0; i < faction.size(); i++) {
            if (faction.get(i).isAlive()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds a living enemy troop from a specific player's faction.
     * 
     * @param enemyPlayer the player to search for troops
     * @return a living enemy troop, or null if none found
     */
    private Troop findEnemyTroop(PlayerModel enemyPlayer) {
        for (Troop enemy : enemyPlayer.getFaction()) {
            if (enemy.isAlive())
                return enemy;
        }
        return null;
    }

    /**
     * Calculates the movement distance between two troops.
     * 
     * @param t1 the first troop
     * @param t2 the second troop
     * @return movement cost between the troops
     */
    private int distance(Troop t1, Troop t2) {
        int cost = PathFinder.getPathCost(mapModel.showedChunk.map, t1.getCoord(), t2.getCoord());
        return cost;
    }

    /**
     * Calculates the movement distance between a troop and coordinates.
     * 
     * @param t      the troop
     * @param coords the target coordinates
     * @return movement cost between troop and coordinates
     */
    private int distance(Troop t, Pair<Integer, Integer> coords) {
        int cost = PathFinder.getPathCost(mapModel.showedChunk.map, t.getCoord(), coords);
        return cost;
    }

    /**
     * Attempts an attack between two troops.
     * 
     * @param pair Pair containing attacking troop and target troop
     * @return true if attack was successful, false otherwise
     */
    private boolean attemptAttack(Pair<Troop, Troop> pair) {
        Troop troop = pair.first();
        Troop enemyTroop = pair.second();
        int d = distance(troop, enemyTroop);
        int s = troop.getSpeed();
        if (d <= s) {
            moveToward(troop, enemyTroop);
            boolean success = troop.attack(enemyTroop, troop.mostDamage());
            if (!enemyTroop.isAlive()) {
                int i = enemyTroop.getX();
                int j = enemyTroop.getY();
                mapModel.getMap()[j][i].setTroop(null);
            }
            return success;
        } else {
            moveToward(troop, enemyTroop);
            if (troop.reachable(enemyTroop, troop.mostRange())) {
                boolean success = troop.attack(enemyTroop, troop.mostRange());
                if (!enemyTroop.isAlive()) {
                    int i = enemyTroop.getX();
                    int j = enemyTroop.getY();
                    mapModel.getMap()[j][i].setTroop(null);
                }
                return success;
            }
        }
        return false;
    }

    /**
     * Moves a troop toward another troop.
     * 
     * @param t1 the troop to move
     * @param t2 the target troop
     */
    private void moveToward(Troop t1, Troop t2) {
        moveToward(t1, t2.getCoord());
    }

    /**
     * Moves a troop toward specific coordinates.
     * 
     * @param t      the troop to move
     * @param coords the target coordinates
     */
    private void moveToward(Troop t, Pair<Integer, Integer> coords) {
        //System.out.println("moving!");
        TileModel[][] tiles = mapModel.showedChunk.map;
        List<Pair<Integer, Integer>> path = PathFinder.getPartialPath(tiles, t.getCoord(), coords, t.getSpeed());
        //System.out.println(path);
        //System.out.println();
        Pair<Integer, Integer> step = path.getLast();
        mapModel.move(t, step.second(), step.first(), mapModel.showedChunk.coordInUnivers);
    }

    /**
     * Finds the weakest troop in this player's faction.
     * 
     * @return the troop with lowest health, or null if none found
     */
    private Troop findMyWeakestTroop() {
        int weakestTroopId = findTroop();
        if (weakestTroopId < 0)
            return null;
        for (int i = 0; i < faction.size(); i++) {
            if (!used.contains(faction.get(i))
                    && faction.get(i).getHealth() < faction.get(weakestTroopId).getHealth()) {
                weakestTroopId = i;
            }
        }
        return faction.get(weakestTroopId);
    }
}