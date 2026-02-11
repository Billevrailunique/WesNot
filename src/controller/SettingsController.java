/**
 * The SettingsController class manages the application settings and acts as the mediator between
 * the Settings model and SettingsView. It handles:
 * - Volume adjustments for music and sound effects
 * - Navigation between game states
 * - Communication with the music player
 * 
 * <p>This controller maintains synchronization between user interface changes and actual
 * game settings, ensuring immediate feedback when adjustments are made.</p>
 */
package controller;

import model.Settings;
import util.Music;
import view.*;

public class SettingsController {
    private Settings settings;
    private SettingsView view;
    private final Music musicPlayer = GUI.getMusicPlayer();
    public GUI frame;

    /**
     * Constructs a SettingsController with the specified GUI frame
     * @param frame The main application frame that contains this controller
     */
    public SettingsController(GUI frame) {
        this.frame = frame;
        settings = new Settings();
        view = new SettingsView(this);
        initButtonListener();
        applyVolumes();
    }

    /**
     * Initializes event listeners for all navigation buttons:
     * - Menu button returns to main menu
     * - Resume button continues the current game
     * - Quit button terminates the application
     * 
     * <p>All buttons trigger appropriate sound effects when clicked.</p>
     */
    private void initButtonListener() {
        view.getMenuButton().addActionListener(e -> {
            musicPlayer.playSFX("ButtonClicked");
            frame.mainMenuOn();
            frame.getDialog().getView().closeDialog();
            musicPlayer.play("MenuMusic");
        });
        
        view.getResumeButton().addActionListener(e -> {
            musicPlayer.playSFX("ButtonClicked");
            frame.setGame(GUI.loadSave);
            musicPlayer.play("GameMusic");
        });
        
        view.getQuitButton().addActionListener(e -> {
            musicPlayer.playSFX("ButtonClicked");
            frame.kill();
        });
    }

    /**
     * Displays the settings panel with optional resume button
     * @param resumeButton true to show the resume button, false to hide it
     */
    public void displaySettings(boolean resumeButton) {
        view.displayComponents(resumeButton);
    }

    /**
     * Sets the music volume and updates both settings and audio player
     * @param volume The new volume level (0.0 to 1.0)
     */
    public void setMusicVolume(float volume) {
        Settings.setMusicVolume(volume);
        musicPlayer.changeMusicVolume(volume);
    }

    /**
     * Sets the sound effects volume and updates both settings and audio player
     * @param volume The new volume level (0.0 to 1.0)
     */
    public void setSfxVolume(float volume) {
        Settings.setSfxVolume(volume);
        musicPlayer.changeSFXVolume(volume);
    }

    /**
     * Gets the current music volume setting
     * @return float value between 0.0 (silent) and 1.0 (full volume)
     */
    public float getMusicVolume() {
        return Settings.getMusicVolume();
    }

    /**
     * Gets the current sound effects volume setting
     * @return float value between 0.0 (silent) and 1.0 (full volume)
     */
    public float getSfxVolume() {
        return Settings.getSfxVolume();
    }

    /**
     * Applies the current volume settings to the music player
     */
    private void applyVolumes() {
        musicPlayer.changeMusicVolume(Settings.getMusicVolume());
        musicPlayer.changeSFXVolume(Settings.getSfxVolume());
    }

    /**
     * Gets the Settings model instance
     * @return The Settings model being controlled
     */
    public Settings getModel() {
        return settings;
    }

    /**
     * Gets the SettingsView instance
     * @return The view component being controlled
     */
    public SettingsView getView() {
        return view;
    }
}