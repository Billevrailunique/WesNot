package model;

/**
 * The Attack class represents a combat attack with specific attributes and properties.
 * It defines different types of attacks that troops can use in combat situations.
 */
public class Attack {
    private AttackType type;
    private int damage; // how much damage an attack does
    private int range; // from how many tiles away can they attack
    private int size; // how many tiles can their attack affect
    private int repeats; // how many times it's repeated
    private double luck; // probability modifier for attack success

    /**
     * Enumeration of different attack types available in the game.
     * Each type has distinct characteristics and properties.
     */
    public enum AttackType {
        DAGGER,
        SWORD,
        BOW,
        FIREBALL,
        HEAL,
        SMASH
    }

    /**
     * Constructs a new Attack with the specified type and initializes its properties.
     * 
     * @param type the type of attack to create (DAGGER, SWORD, BOW, etc.)
     * @throws IllegalArgumentException if an unknown attack type is provided
     */
    public Attack(AttackType type) {
        this.type = type;
        switch (type) {
            case DAGGER:
                this.damage = 1;
                this.range = 1;
                this.size = 1;
                this.repeats = 3;
                break;
            case SWORD:
                this.damage = 5;
                this.range = 1;
                this.size = 1;
                this.repeats = 2;
                break;
            case BOW:
                this.damage = 3;
                this.range = 5;
                this.size = 1;
                this.repeats = 4;
                break;
            case FIREBALL:
                this.damage = 4;
                this.range = 3;
                this.size = 2;
                this.repeats = 1;
                break;
            case HEAL:
                this.damage = -2;
                this.range = 2;
                this.size = 1;
                this.repeats = 1;
                this.luck = 1;
                break;
            case SMASH:
                this.damage = 6;
                this.range = 1;
                this.size = 1;
                this.repeats = 1;
                this.luck = 0.7;
                break;
            default:
                throw new IllegalArgumentException("Unknown Attack Type: " + type);
        }
    }

    /**
     * Returns the type of this attack.
     * 
     * @return the AttackType of this attack
     */
    public AttackType getType() {
        return type;
    }

    /**
     * Returns the base damage value of this attack.
     * 
     * @return the damage value (negative for healing attacks)
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Returns the maximum range of this attack in tiles.
     * 
     * @return the attack range
     */
    public int getRange() {
        return range;
    }

    /**
     * Returns the area of effect size of this attack in tiles.
     * 
     * @return the attack size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the number of times this attack repeats.
     * 
     * @return the number of attack repeats
     */
    public int getRepeats() {
        return repeats;
    }

    /**
     * Returns the name of this attack type.
     * 
     * @return the attack name as a String
     */
    public String getName() {
        return type.name();
    }

    /**
     * Returns the luck factor of this attack (probability modifier).
     * 
     * @return the luck value between 0.0 and 1.0
     */
    public double getLuck() {
        return luck;
    }
}