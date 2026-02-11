package controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.*;
import model.MainMenuModel;
import util.*;
import view.GUI;
import view.MainMenuView;

/**
 * The MainMenuController class handles the main menu interactions and
 * navigation.
 * It manages the menu buttons, their actions, and coordinates between the main
 * menu view and model.
 */
public class MainMenuController {

    private final GUI frame;
    private JButton campagneButton;
    private JButton exitButton;
    private JButton continueButton;
    private JButton settingsButton;

    private final MainMenuModel model;
    private final MainMenuView view;
    private ArrayList<JButton> buttons;
    private final Music musicPlayer = GUI.getMusicPlayer();
    private final Color buttonColor = new Color(193,40,0);

    /**
     * Constructs a new MainMenuController with the specified GUI frame.
     * Initializes the model, view, and sets up the main menu components.
     * 
     * @param frame The parent GUI frame that contains this controller
     */
    public MainMenuController(GUI frame) {
        model = new MainMenuModel();

        String imagePath = model.getImagePath();

        view = new MainMenuView(frame, imagePath);

        this.frame = frame;

        init();
    }

    /**
     * Initializes the main menu components.
     * Creates and arranges all menu buttons vertically in the center of the panel.
     * Sets up button properties and adds them to the view.
     */
    public final void init() {
        buttons = new ArrayList<>(); // contain all buttons

        campagneButton = new JButton("New Game");
        campagneButton.setFocusable(false);
        exitButton = new JButton("Quit Game");
        exitButton.setFocusable(false);
        continueButton = new JButton("Continue");
        continueButton.setFocusable(false);
        settingsButton = new JButton("Settings");
        buttons.add(continueButton);
        buttons.add(campagneButton);
        buttons.add(settingsButton);
        buttons.add(exitButton);
        view.getPanel().add(Box.createVerticalGlue());
        view.getPanel().add(Box.createVerticalStrut(50));
        for (JButton b : buttons) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setAlignmentY(Component.CENTER_ALIGNMENT);
            b.setForeground(buttonColor); // Couleur du texte
            b.setFont(new Font("Monofonto", Font.BOLD, 25));
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            view.getPanel().add(b);
        }
        view.getPanel().add(Box.createVerticalGlue());
        buttonsListener();

        view.getPanel().revalidate();
        view.getPanel().repaint();
    }

    /**
     * Sets up action listeners for all main menu buttons.
     * Handles button click events for:
     * - Continue: Resumes existing game with saved state
     * - Quit Game: Exits the application
     * - Campagne: Starts a new campaign game
     */
    public void buttonsListener() {
        continueButton.addActionListener(e -> {
            musicPlayer.playSFX("ButtonClicked");
            musicPlayer.play("GameMusic");
            frame.setGame(true);
        });
        exitButton.addActionListener(e -> {
            frame.kill();
        });
        campagneButton.addActionListener(e -> {
            musicPlayer.playSFX("ButtonClicked");
            musicPlayer.play("GameMusic");
            frame.setGame(false);
        });
        settingsButton.addActionListener(e -> {
            musicPlayer.playSFX("ButtonClicked");
            frame.showSettings(false);
        });
    }

    /**
     * Returns the list of all menu buttons.
     * 
     * @return ArrayList containing all JButton instances in the menu
     */
    public ArrayList<JButton> getButtons() {
        return buttons;
    }

    /**
     * Returns the MainMenuView associated with this controller.
     * 
     * @return The MainMenuView instance
     */
    public MainMenuView getView() {
        return view;
    }

}