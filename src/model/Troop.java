package model;

import java.util.ArrayList;
import model.Attack.AttackType;
import util.Pair;
import util.RandomTaskManager;

/**
 * Represents a military unit in the game with combat capabilities and movement
 * properties.
 * <p>
 * Each troop has:
 * <ul>
 * <li>Specific type with unique characteristics</li>
 * <li>Health points and regeneration chance</li>
 * <li>Movement speed and attack capabilities</li>
 * <li>Position tracking on the game map</li>
 * <li>Ownership and turn state management</li>
 * </ul>
 * </p>
 */
public class Troop {

    /**
     * Enumeration of available troop types with distinct roles.
     */
    public enum TroopType {
        LEADER, // Command unit with balanced stats
        ELF, // Fast ranged attacker
        GOBLIN, // Quick melee fighter
        MAGE, // Support unit with healing
        ORC // Heavy melee brute
    }

    private TroopType type;
    private int health;
    private int speed;
    private double regenChance;
    private final ArrayList<Attack> attacks = new ArrayList<>();
    private boolean isAlive = true;
    private int X;
    private int Y;
    private Pair<Integer, Integer> coordChunk;
    public Pair<Integer, Integer> oldCoord = new Pair<Integer, Integer>(null, null); // needed to escape dungeon
    private final int playerID;
    private boolean attacked = false;
    private boolean moved = false;

    /**
     * Private constructor for internal troop creation.
     * 
     * @param type        The troop classification
     * @param health      Starting hit points
     * @param speed       Movement range per turn
     * @param regenChance Probability of healing each turn
     * @param X           Map column position
     * @param Y           Map row position
     * @param playerID    Owning player identifier
     */
    private Troop(TroopType type, int health, int speed, double regenChance, int X, int Y, int playerID,
            Pair<Integer, Integer> coordChunk, boolean moved, boolean attacked) {
        this.type = type;
        this.health = health;
        this.speed = speed;
        this.regenChance = regenChance;
        this.X = X;
        this.Y = Y;
        this.playerID = playerID;
        this.coordChunk = coordChunk;
        this.attacked = attacked;
        this.moved = moved;
    }

    /**
     * Creates a troop with default health at specified position.
     * 
     * @param type     The troop classification
     * @param X        Map column position
     * @param Y        Map row position
     * @param playerID Owning player identifier
     */
    public Troop(TroopType type, int X, int Y, int playerID, Pair<Integer, Integer> coordChunk) {
        this(type, getDefaultHealth(type), getDefaultSpeed(type),
                getDefaultRegen(type), X, Y, playerID, coordChunk, false,false);
        initializeAttacks(type);
    }

    /**
     * Creates a troop with custom health at specified position.
     * 
     * @param type     The troop classification
     * @param X        Map column position
     * @param Y        Map row position
     * @param playerID Owning player identifier
     * @param health   Custom starting hit points
     */
    public Troop(TroopType type, int X, int Y, int playerID, int health, Pair<Integer, Integer> coordChunk, boolean atk, boolean mv) {
        this(type, health, getDefaultSpeed(type), getDefaultRegen(type), X, Y, playerID, coordChunk,atk,mv);
        initializeAttacks(type);
    }

    /**
     * Initializes attack capabilities based on troop type.
     * 
     * @param type The troop classification
     */
    private void initializeAttacks(TroopType type) {
        switch (type) {
            case LEADER -> attacks.add(new Attack(AttackType.SWORD));
            case ELF -> {
                attacks.add(new Attack(AttackType.DAGGER));
                attacks.add(new Attack(AttackType.BOW));
            }
            case GOBLIN -> attacks.add(new Attack(AttackType.DAGGER));
            case MAGE -> {
                attacks.add(new Attack(AttackType.FIREBALL));
                attacks.add(new Attack(AttackType.HEAL));
            }
            case ORC -> attacks.add(new Attack(AttackType.SMASH));
            default -> throw new IllegalArgumentException("Unknown Troop Type: " + type);
        }
    }

    public Attack mostDamage() {
        switch (type) {
            case LEADER:
                return new Attack(AttackType.SWORD);
            case ELF:
                return new Attack(AttackType.BOW);
            case GOBLIN:
                return new Attack(AttackType.DAGGER);
            case MAGE:
                return new Attack(AttackType.FIREBALL);

            case ORC:
                return new Attack(AttackType.SMASH);
            default:
                throw new IllegalArgumentException("Unknown Troop Type: " + type);
        }
    }

    public Attack mostRange() {
        switch (type) {
            case LEADER:
                return new Attack(AttackType.SWORD);
            case ELF:
                return new Attack(AttackType.BOW);
            case GOBLIN:
                return new Attack(AttackType.DAGGER);
            case MAGE:
                return new Attack(AttackType.FIREBALL);
            case ORC:
                return new Attack(AttackType.SMASH);
            default:
                throw new IllegalArgumentException("Unknown Troop Type: " + type);
        }
    }

    /**
     * Gets default health for troop type.
     */
    public static int getDefaultHealth(TroopType type) {
        return switch (type) {
            case LEADER -> 10;
            case ELF -> 5;
            case GOBLIN -> 4;
            case MAGE -> 7;
            case ORC -> 20;
            default -> throw new IllegalArgumentException("Unknown Troop Type: " + type);
        };
    }

    /**
     * Gets default speed for troop type.
     */
    public static int getDefaultSpeed(TroopType type) {
        return switch (type) {
            case LEADER -> 5;
            case ELF -> 7;
            case GOBLIN -> 8;
            case MAGE -> 6;
            case ORC -> 5;
            default -> throw new IllegalArgumentException("Unknown Troop Type: " + type);
        };
    }

    /**
     * Gets default regeneration chance for troop type.
     */
    public static double getDefaultRegen(TroopType type) {
        return switch (type) {
            case LEADER -> 0.5;
            case ELF -> 0.4;
            case GOBLIN -> 0.2;
            case MAGE -> 0.8;
            case ORC -> 0.1;
            default -> throw new IllegalArgumentException("Unknown Troop Type: " + type);
        };
    }

    /**
     * Gets the recruitment cost for a troop type.
     * 
     * @param t The troop type to price
     * @return Gold cost to recruit
     * @throws AssertionError for invalid types
     */
    public static int getCost(TroopType t) {
        return switch (t) {
            case ELF -> 3;
            case GOBLIN -> 5;
            case MAGE -> 15;
            case ORC -> 17;
            default -> throw new AssertionError("Invalid troop type");
        };
    }

    /**
     * Checks if target is within range of an attack.
     * 
     * @param troop  Target unit
     * @param attack Attack being considered
     * @return true if target is in range
     */
    public boolean reachable(Troop troop, Attack attack) {
        // Convert to cube coordinates for hex distance calculation
        int q1 = X;
        int r1 = Y - (X - (X % 2)) / 2;
        int z1 = -r1 - q1;

        int q2 = troop.getX();
        int r2 = troop.getY() - (troop.getX() - (troop.getX() % 2)) / 2;
        int z2 = -r2 - q2;

        int distance = Math.max(Math.abs(z1 - z2),
                Math.max(Math.abs(r1 - r2), Math.abs(q1 - q2)));

        return distance <= attack.getRange();
    }

    /**
     * Executes an attack against another troop.
     * 
     * @param troop  Target unit
     * @param attack Attack to perform
     * 
     * @return true if damage dealt was positive
     */
    public boolean attack(Troop troop, Attack attack) {
        int damageDealt = 0;
        for (int i = 0; i < attack.getRepeats(); i++) {
            if (RandomTaskManager.nextFloat() > attack.getLuck()) {
                troop.health -= attack.getDamage();
                damageDealt += attack.getDamage();
            }
        }
        if (troop.health <= 0) {
            troop.die();
        }
        return damageDealt != 0;
    }

    // Standard getters and setters

    /** @return Current hit points */
    public int getHealth() {
        return health;
    }

    /** @return Movement range per turn */
    public int getSpeed() {
        return speed;
    }

    /** @return Chance to heal 1 HP per turn */
    public double getRegenChance() {
        return regenChance;
    }

    /** @return Available attacks */
    public ArrayList<Attack> getAttacks() {
        return attacks;
    }

    /** @return Type name string */
    public String getName() {
        return type.name();
        return type.name();
    }

    /** @return Troop classification */
    public TroopType getType() {
        return type;
    }

    /** @return Map column position */
    public int getX() {
        return X;
    }

    /** @return Map row position */
    public int getY() {
        return Y;
    }

    /** @return Position as (X,Y) pair */
    public Pair<Integer, Integer> getCoord() {
        return new Pair<>(X, Y);
    }

    /** @return Owning player ID */
    public int getPlayerID() {
        return playerID;
    }

    /** @return Whether attacked this turn */
    public boolean getAttacked() {
        return attacked;
    }

    /** @return Whether moved this turn */
    public boolean getMoved() {
        return moved;
    }

    /** @return Maximum attack range */
    public int getMaxRange() {
        return attacks.stream()
                .mapToInt(Attack::getRange)
                .max()
                .orElse(0);
    }

    /** @return Fog of war visibility range */
    public int getFOWUpdateRange() {
        return Math.max(getMaxRange(), speed);
    }

    public Pair<Integer, Integer> getCoordChunk() {
        return coordChunk;
    }

    /** @return Living status */
    public boolean isAlive() {
        return isAlive;
    }

    /** Marks unit as defeated */
    public void die() {
        isAlive = false;
    }

    /** @param x New column position */
    public void setX(int x) {
        X = x;
    }

    /** @param y New row position */
    public void setY(int y) {
        Y = y;
    }

    public void setCoordChunk(util.Pair<Integer, Integer> newCoordChunk) {
        coordChunk = newCoordChunk;
    }

    /** @param b Attack status flag */
    public void setAttacked(boolean b) {
        attacked = b;
    }

    /** @param b Movement status flag */
    public void setMoved(boolean b) {
        moved = b;
    }

    /**
     * Generates save string representation.
     * 
     * @return String in format "TYPE,HEALTH,X,Y"
     */
    @Override
    public String toString() {
        return String.format("%s,%d,%d,%d,%d,%d,%b,%b", type, health, X, Y, coordChunk.first(), coordChunk.second(),moved, attacked);
    }
}