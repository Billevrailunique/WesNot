package util;

import java.util.Random;

/**
 * A thread-safe utility class for managing random number generation with additional functionality.
 * <p>
 * This class provides:
 * <ul>
 *   <li>Synchronized random number generation</li>
 *   <li>Custom distributions (including normal distribution within bounds)</li>
 *   <li>Seed management for reproducible randomness</li>
 *   <li>Range-bound integer generation</li>
 * </ul>
 * All methods are thread-safe through synchronization.
 * </p>
 */
public class RandomTaskManager {
    private static Random random;
    private static long seed;

    /**
     * Constructs a RandomTaskManager with a specific seed for reproducible results.
     * 
     * @param s The seed value to initialize the random number generator
     */
    public RandomTaskManager(long s) {
        seed = s;
        random = new Random(seed);
    }

    /**
     * Constructs a RandomTaskManager seeded with the current system time.
     */
    public RandomTaskManager() {
        this(System.currentTimeMillis());
    }

    /**
     * Generates a random integer within the specified range [min, max].
     * 
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return A random integer between min and max
     * @throws AssertionError if min >= max
     */
    public static synchronized int nextInt(int min, int max) {
        assert (min < max);
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Generates a normally distributed random integer within specified bounds.
     * <p>
     * The method ensures the returned value falls within [borneInf, bornSup] by
     * repeatedly sampling until a valid value is found (with a safety limit).
     * </p>
     * 
     * @param mean The center of the normal distribution
     * @param variance The spread of the distribution (must be > 0)
     * @param borneInf The lower bound (inclusive)
     * @param bornSup The upper bound (inclusive)
     * @return A normally distributed random integer within bounds
     * @throws AssertionError if variance <= 0, bounds are invalid, or infinite loop detected
     */
    public static synchronized int normalDis(int mean, int variance, int borneInf, int bornSup) {
        assert (variance > 0);
        assert (bornSup > borneInf + 1);
        assert (borneInf >= 0);
        
        int ans = -1;
        int count = 10000; // Safety counter to prevent infinite loops
        
        while (ans > bornSup || ans < borneInf) {
            double varAlea = random.nextGaussian();
            ans = Double.valueOf(variance * varAlea + mean).intValue();
            
            if (count <= 0) {
                System.err.println("Infinite loop detected in normalDis");
                System.err.println("Parameters:");
                System.err.println("Mean: " + mean);
                System.err.println("Variance: " + variance);
                System.err.println("Lower bound: " + borneInf);
                System.err.println("Upper bound: " + bornSup);
                System.exit(1);
            }
            count--;
        }
        return ans;
    }

    /**
     * Generates a random float between 0.0 (inclusive) and 1.0 (exclusive).
     * 
     * @return A random float value
     */
    public static synchronized float nextFloat() {
        return random.nextFloat();
    }

    /**
     * Generates a random boolean value.
     * 
     * @return A random true or false value
     */
    public static synchronized boolean nextBoolean() {
        return random.nextBoolean();
    }

    /**
     * Sets a new seed for the random number generator.
     * 
     * @param newSeed The new seed value
     */
    public static synchronized void setSeed(long newSeed) {
        seed = newSeed;
        random.setSeed(newSeed);
    }

    /**
     * Gets the current random number generator seed.
     * 
     * @return The current seed value
     */
    public static synchronized long getSeed() {
        return seed;
    }

    /**
     * Provides direct access to the underlying Random instance.
     * <p>
     * Note: Use with caution as direct operations on this instance
     * won't be automatically synchronized.
     * </p>
     * 
     * @return The underlying Random instance
     */
    public static synchronized Random getRandom() {
        return random;
    }
}