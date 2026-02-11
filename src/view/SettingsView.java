/**
 * The SettingsView class provides a graphical interface for game settings configuration.
 * It allows users to adjust:
 * - Music and sound effect volumes
 * - Toggle Fog of War
 * - Navigate between game states (menu, quit, resume)
 * 
 * <p>The view features a custom background image and stylized UI components including
 * triangular slider controls and transparent buttons.</p>
 */
package view;

import controller.SettingsController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import model.Settings;

public class SettingsView {// TODO implémenter le chgt de control
    private JPanel panel;
    private SettingsController controller;
    private JSlider musicVolumeSlider;
    private JSlider sfxVolumeSlider;
    private JButton menuButton;
    private JButton quitButton;
    private JButton resumeButton;
    private JCheckBox fowCheckBox;
    private JCheckBox tutorial;
    private ArrayList<JComponent> componentFromGame = new ArrayList<>();
    private ArrayList<JComponent> componentFromMenu = new ArrayList<>();
    private Image backgroundImage;
    private final Color textColor = Color.WHITE;
    private final Color sliderColor = new Color(194, 157, 0);
    private final Font settingsFont = new Font("Monofonto", Font.BOLD, 25);

    private JComboBox<Integer> realPlayerDropdown;
    private JComboBox<Integer> aiPlayerDropdown;

    /**
     * Constructs a SettingsView with the specified controller
     * 
     * @param controller The SettingsController that manages settings logic
     */
    public SettingsView(SettingsController controller) {// TODO change color of labels and sliders
        this.controller = controller;
        backgroundImage = new ImageIcon("ressource/images/backgrounds/Settings.png").getImage();
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        initComponents();
    }

    /**
     * Initializes all UI components :
     * - Volume sliders
     * - Fog of War checkbox
     * - Navigation buttons (Menu, Quit, Resume)
     */
    private void initComponents() {

        resumeButton = new JButton("Resume");
        resumeButton.setForeground(textColor);
        resumeButton.setFont(settingsFont);
        resumeButton.setBorderPainted(false);
        resumeButton.setContentAreaFilled(false);
        resumeButton.setFocusPainted(false);

        componentFromGame.add(resumeButton);

        // Label pour la musique
        JLabel musicLabel = new JLabel("Volume Musique:");
        musicLabel.setFont(settingsFont);
        musicLabel.setForeground(textColor);

        // Slider pour la musique
        musicVolumeSlider = new JSlider(0, 100, (int) (controller.getMusicVolume() * 100));
        musicVolumeSlider.addChangeListener(e -> {
            float volume = musicVolumeSlider.getValue() / 100f;
            controller.setMusicVolume(volume);
        });
        musicVolumeSlider.setUI(new BasicSliderUI() {
            @Override
            public void paintThumb(Graphics g) {
                g.setColor(Color.BLACK); // couleur du thumb
                int centerX = thumbRect.x + thumbRect.width / 2;
                int centerY = thumbRect.y + thumbRect.height / 2;

                // Triangle pointé vers le bas
                int[] xPoints = { centerX - 7, centerX + 7, centerX };
                int[] yPoints = { centerY - 10, centerY - 10, centerY + 10 };

                g.fillPolygon(xPoints, yPoints, 3);
            }

            @Override
            public void paintTrack(Graphics g) {
                g.setColor(sliderColor); // couleur de la barre
                g.fillRect(trackRect.x, trackRect.y + trackRect.height / 2 - 2, trackRect.width, 4);
            }
        });
        musicVolumeSlider.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width / 3,
                musicVolumeSlider.getPreferredSize().height));
        musicVolumeSlider.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width / 3,
                musicVolumeSlider.getPreferredSize().height));
        musicVolumeSlider.setBackground(sliderColor);

        componentFromGame.add(musicLabel);
        componentFromGame.add(musicVolumeSlider);
        componentFromMenu.add(musicLabel);
        componentFromMenu.add(musicVolumeSlider);

        // Label pour les SFX
        JLabel sfxLabel = new JLabel("Volume SFX:");
        sfxLabel.setFont(settingsFont);
        sfxLabel.setForeground(textColor);

        // Slider pour les SFX
        sfxVolumeSlider = new JSlider(0, 100, (int) (controller.getSfxVolume() * 100));
        sfxVolumeSlider.addChangeListener(e -> {
            float volume = sfxVolumeSlider.getValue() / 100f;
            controller.setSfxVolume(volume);
        });
        sfxVolumeSlider.setUI(new BasicSliderUI() {
            @Override
            public void paintThumb(Graphics g) {
                g.setColor(Color.BLACK); // couleur du thumb
                int centerX = thumbRect.x + thumbRect.width / 2;
                int centerY = thumbRect.y + thumbRect.height / 2;

                // Triangle pointé vers le bas
                int[] xPoints = { centerX - 7, centerX + 7, centerX };
                int[] yPoints = { centerY - 10, centerY - 10, centerY + 10 };

                g.fillPolygon(xPoints, yPoints, 3);
            }

            @Override
            public void paintTrack(Graphics g) {
                g.setColor(sliderColor); // couleur de la barre
                g.fillRect(trackRect.x, trackRect.y + trackRect.height / 2 - 2, trackRect.width, 4);
            }
        });
        sfxVolumeSlider.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width / 3,
                musicVolumeSlider.getPreferredSize().height));
        sfxVolumeSlider.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width / 3,
                musicVolumeSlider.getPreferredSize().height));
        sfxVolumeSlider.setBackground(sliderColor);

        componentFromGame.add(sfxLabel);
        componentFromGame.add(sfxVolumeSlider);
        componentFromMenu.add(sfxLabel);
        componentFromMenu.add(sfxVolumeSlider);

        // CheckBox for FOW
        fowCheckBox = new JCheckBox("Fog of War") {
            @Override
            public void updateUI() {
                super.updateUI();
                setContentAreaFilled(false); // Désactive le remplissage du fond
                setBorderPainted(false); // Cache la bordure
            }
        };
        fowCheckBox.addItemListener(e -> {
            int state = e.getStateChange();
            if (state == ItemEvent.SELECTED) {
                Settings.setFOW(true);
            } else {
                Settings.setFOW(false);
            }
        });
        fowCheckBox.setSelected(true);
        fowCheckBox.setFont(settingsFont);
        fowCheckBox.setForeground(textColor);

        componentFromMenu.add(fowCheckBox);

        tutorial = new JCheckBox("Tutorial") {
            @Override
            public void updateUI() {
                super.updateUI();
                setContentAreaFilled(false); // Désactive le remplissage du fond
                setBorderPainted(false); // Cache la bordure
            }
        };
        tutorial.addItemListener(e -> {
            int state = e.getStateChange();
            if (state != ItemEvent.SELECTED) {
                controller.frame.dialogue.turns.remove(0);
            } else {
            }
        });
        tutorial.setSelected(true);
        tutorial.setFont(settingsFont);
        tutorial.setForeground(textColor);

        componentFromMenu.add(tutorial);
        componentFromGame.add(tutorial);

        // Boutons
        menuButton = new JButton("Menu");
        menuButton.setForeground(textColor);
        menuButton.setFont(settingsFont);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);

        quitButton = new JButton("Quit Game");
        quitButton.setForeground(textColor);
        quitButton.setFont(settingsFont);
        quitButton.setBorderPainted(false);
        quitButton.setContentAreaFilled(false);
        quitButton.setFocusPainted(false);

        // Dropdown for real players
        JLabel realPlayersLabel = new JLabel("Number of Real Players:");
        realPlayersLabel.setFont(settingsFont);
        realPlayersLabel.setForeground(textColor);

        Integer[] playerOptions = { 1, 2 };
        realPlayerDropdown = new JComboBox<>(playerOptions);
        realPlayerDropdown.setFont(settingsFont);
        realPlayerDropdown.setMaximumSize(new Dimension(200, 40));
        realPlayerDropdown.addActionListener(e -> {
            int selected = (Integer) realPlayerDropdown.getSelectedItem();
            Settings.setRealPlayers(selected); // You must define this in model.Settings
        });

        // Dropdown for AI players
        JLabel aiPlayersLabel = new JLabel("Number of AI Players:");
        aiPlayersLabel.setFont(settingsFont);
        aiPlayersLabel.setForeground(textColor);

        aiPlayerDropdown = new JComboBox<>(playerOptions);
        aiPlayerDropdown.setFont(settingsFont);
        aiPlayerDropdown.setMaximumSize(new Dimension(200, 40));
        aiPlayerDropdown.addActionListener(e -> {
            int selected = (Integer) aiPlayerDropdown.getSelectedItem();
            Settings.setAiPlayers(selected); // Also define this in model.Settings
        });

        componentFromMenu.add(realPlayersLabel);
        componentFromMenu.add(realPlayerDropdown);

        componentFromMenu.add(aiPlayersLabel);
        componentFromMenu.add(aiPlayerDropdown);

        componentFromGame.add(menuButton);
        componentFromMenu.add(menuButton);

        componentFromGame.add(quitButton);
        componentFromMenu.add(quitButton);

    }

    /**
     * Updates slider positions to reflect current volume settings
     */
    public void updateSliders() {
        musicVolumeSlider.setValue((int) (controller.getMusicVolume() * 100));
        sfxVolumeSlider.setValue((int) (controller.getSfxVolume() * 100));
    }

    /**
     * Displays all components with optional resume button
     * 
     * @param isFromGame Whether to show the resume button
     */
    public void displayComponents(boolean isFromGame) {
        panel.removeAll();
        panel.add(Box.createVerticalGlue());

        if (isFromGame) {
            for (JComponent comp : componentFromGame) {
                if (comp instanceof JCheckBox && controller.frame.gameController != null) {

                }
                comp.setAlignmentX(Component.CENTER_ALIGNMENT);
                comp.setAlignmentY(Component.CENTER_ALIGNMENT);
                panel.add(comp);
                panel.add(Box.createVerticalStrut(10));
            }
        } else {
            for (JComponent comp : componentFromMenu) {
                comp.setAlignmentX(Component.CENTER_ALIGNMENT);
                comp.setAlignmentY(Component.CENTER_ALIGNMENT);
                panel.add(comp);
                panel.add(Box.createVerticalStrut(10));
            }
        }

        panel.add(Box.createVerticalGlue());

        panel.revalidate();
        panel.repaint();
    }

    /**
     * Gets the main settings panel
     * 
     * @return JPanel containing all settings components
     */
    public JPanel getPanel() {
        return this.panel;
    }

    /**
     * Gets all UI components managed by this view
     * 
     * @return ArrayList of JComponents
     */
    public ArrayList<JComponent> getComponents() {
        return componentFromGame;
    }

    /**
     * Gets the menu navigation button
     * 
     * @return Menu button instance
     */
    public JButton getMenuButton() {
        return this.menuButton;
    }

    /**
     * Gets the resume game button
     * 
     * @return Resume button instance
     */
    public JButton getResumeButton() {
        return resumeButton;
    }

    /**
     * Gets the quit game button
     * 
     * @return Quit button instance
     */
    public JButton getQuitButton() {
        return quitButton;
    }

}
