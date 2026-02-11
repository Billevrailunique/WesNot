/**
 * The Settings class manages game configuration parameters including:
 * - Audio volume settings
 * - Keyboard control mappings
 * - Gameplay toggles (e.g., Fog of War)
 * 
 * <p>All settings are maintained as static properties, making them globally accessible
 * throughout the application. Default values are initialized in the constructor.</p>
 */
package model;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Settings {
    private static float musicVolume;
    private static float sfxVolume;
    private static int up_key;
    private static int down_key;
    private static int left_key;
    private static int right_key;
    private static int centerViewPort_key;
    private static int nextRound_key;
    private static int pause_key;
    private static ArrayList<Integer> mvtKeyCode = new ArrayList<>();
    public static boolean FOW = true;

    private static int realPlayers = 1;
    private static int aiPlayers = 1;

    /**
     * Initializes default settings:
     * - Music and SFX volume at 50%
     * - Arrow keys for movement
     * - Enter key for viewport centering
     * - 'R' key for round advancement
     * - Fog of War enabled by default
     */
    public Settings() {
        musicVolume = 0.5f;
        sfxVolume = 0.5f;
        up_key = KeyEvent.VK_UP;
        down_key = KeyEvent.VK_DOWN;
        left_key = KeyEvent.VK_LEFT;
        right_key = KeyEvent.VK_RIGHT;
        centerViewPort_key = KeyEvent.VK_ENTER;
        nextRound_key = KeyEvent.VK_R;
        pause_key = KeyEvent.VK_ESCAPE;
        mvtKeyCode.add(up_key);
        mvtKeyCode.add(down_key);
        mvtKeyCode.add(left_key);
        mvtKeyCode.add(right_key);
    }

    // Audio Settings Getters

    /**
     * Gets the current music volume level
     * 
     * @return float value between 0.0 (silent) and 1.0 (full volume)
     */
    public static float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Gets the current sound effects volume level
     * 
     * @return float value between 0.0 (silent) and 1.0 (full volume)
     */
    public static float getSfxVolume() {
        return sfxVolume;
    }

    // Control Mapping Getters

    /**
     * Gets the keycode for upward movement
     * 
     * @return integer keycode (e.g., KeyEvent.VK_UP)
     */
    public static int getUpKeyCode() {
        return up_key;
    }

    /**
     * Gets the keycode for downward movement
     * 
     * @return integer keycode (e.g., KeyEvent.VK_DOWN)
     */
    public static int getDownKeyCode() {
        return down_key;
    }

    /**
     * Gets the keycode for leftward movement
     * 
     * @return integer keycode (e.g., KeyEvent.VK_LEFT)
     */
    public static int getLeftKeyCode() {
        return left_key;
    }

    /**
     * Gets the keycode for rightward movement
     * 
     * @return integer keycode (e.g., KeyEvent.VK_RIGHT)
     */
    public static int getRightKeyCode() {
        return right_key;
    }

    /**
     * Gets the keycode for viewport centering
     * 
     * @return integer keycode (e.g., KeyEvent.VK_ENTER)
     */
    public static int getCenterViewPortKeyCode() {
        return centerViewPort_key;
    }

    /**
     * Gets the keycode for advancing to the next round
     * 
     * @return integer keycode (e.g., KeyEvent.VK_R)
     */
    public static int getNextRoundKeyCode() {
        return nextRound_key;
    }

    public static int getPauseKeyCode() {
        return pause_key;
    }

    /**
     * Gets the list of all movement keycodes
     * 
     * @return ArrayList containing the four directional keycodes
     */
    public static ArrayList<Integer> getMovementKeys() {
        return mvtKeyCode;
    }

    // Gameplay Setting Getters

    /**
     * Gets the current Fog of War (FOW) state
     * 
     * @return true if Fog of War is enabled, false otherwise
     */
    public static boolean getFOW() {
        return FOW;
    }

    // Setters

    /**
     * Sets the music volume level
     * 
     * @param musicVolume float value between 0.0 (silent) and 1.0 (full volume)
     */
    public static void setMusicVolume(float musicVolume) {
        Settings.musicVolume = musicVolume;
    }

    /**
     * Sets the sound effects volume level
     * 
     * @param sfxVolume float value between 0.0 (silent) and 1.0 (full volume)
     */
    public static void setSfxVolume(float sfxVolume) {
        Settings.sfxVolume = sfxVolume;
    }

    /**
     * Sets the keycode for upward movement
     * 
     * @param keyCode integer keycode (e.g., KeyEvent.VK_W)
     */
    public static void setUpKey(char keyCode) {
        up_key = keyCode;
    }

    /**
     * Sets the keycode for downward movement
     * 
     * @param keyCode integer keycode (e.g., KeyEvent.VK_S)
     */
    public static void setDownKey(char keyCode) {
        down_key = keyCode;
    }

    /**
     * Sets the keycode for leftward movement
     * 
     * @param keyCode integer keycode (e.g., KeyEvent.VK_A)
     */
    public static void setLeftKey(char keyCode) {
        left_key = keyCode;
    }

    /**
     * Sets the keycode for rightward movement
     * 
     * @param keyCode integer keycode (e.g., KeyEvent.VK_D)
     */
    public static void setRightKey(char keyCode) {
        right_key = keyCode;
    }

    /**
     * Sets the keycode for viewport centering
     * 
     * @param keyCode integer keycode (e.g., KeyEvent.VK_SPACE)
     */
    public static void setCenterViewPortKey(char keyCode) {
        centerViewPort_key = keyCode;
    }

    /**
     * Sets the keycode for advancing to the next round
     * 
     * @param keyCode integer keycode (e.g., KeyEvent.VK_N)
     */
    public static void setNextRoundKey(char keyCode) {
        nextRound_key = keyCode;
    }

    /**
     * Enables or disables the Fog of War feature
     * 
     * @param b true to enable Fog of War, false to disable
     */
    public static void setFOW(boolean b) {
        FOW = b;
    }

    public static int getRealPlayers() {
        return realPlayers;
    }

    public static void setRealPlayers(int value) {
        realPlayers = value;
    }

    public static int getAiPlayers() {
        return aiPlayers;
    }

    public static void setAiPlayers(int value) {
        aiPlayers = value;
    }
}