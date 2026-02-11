package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

/**
 * A custom dialog box implementation for displaying in-game conversations.
 * <p>
 * Features include:
 * <ul>
 *   <li>Text display area with custom font</li>
 *   <li>Speaker images and name tags</li>
 *   <li>Navigation controls</li>
 *   <li>Transparent overlay effect</li>
 * </ul>
 * The dialog appears at the bottom of the screen with accompanying speaker visuals.
 * </p>
 */
public class DialogView {
    private JFrame frame;
    private JDialog box;
    private JLabel text;
    private JButton next;
    private JButton skip;
    private JWindow windowImage;
    private JWindow windowLabel;
    private JLabel labelImage;
    private JLabel labelName;
    private ArrayList<ImageIcon> alreadyLoadedSpeaker;

    /**
     * Constructs a new DialogView attached to the specified frame.
     * 
     * @param frame The parent frame for this dialog
     */
    public DialogView(JFrame frame) {
        this.frame = frame;
        initializeSpeakerCache();
        initializeDialogComponents();
        initializeSpeakerWindows();
        positionWindows();
    }

    /**
     * Initializes the cache for speaker images.
     */
    private void initializeSpeakerCache() {
        alreadyLoadedSpeaker = new ArrayList<>();
        ImageIcon defaultNpc = new ImageIcon("ressource/images/NPC/defaultNpc.jpeg");
        defaultNpc.setDescription("defaultNpc");
        alreadyLoadedSpeaker.add(defaultNpc);
    }

    /**
     * Sets up the main dialog components.
     */
    private void initializeDialogComponents() {
        box = new JDialog(frame);
        box.setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        box.setSize((int) screenSize.getWidth(), (int) (screenSize.getHeight() * 0.20));
        box.setUndecorated(true);
        box.setBackground(Color.BLACK);
        box.setOpacity(0.55f);
        box.setLocation(0, (int) (screenSize.getHeight() - (screenSize.getHeight() * 0.20)));

        text = new JLabel("", SwingConstants.CENTER);
        setCustomFont(text, "UnifrakturCook-Bold", 20);
        
        next = new JButton("next");
        next.setFocusable(false);
        next.setSize(new Dimension(75, 25));

        skip = new JButton("skip");
        skip.setFocusable(false);
        skip.setSize(new Dimension(75, 25));

        JPanel btnPane = new JPanel();
        btnPane.setSize(new Dimension(150,25));
        btnPane.add(next);
        box.add(btnPane, BorderLayout.NORTH);
        box.add(text, BorderLayout.CENTER);
    }

    /**
     * Initializes the speaker image and name windows.
     */
    private void initializeSpeakerWindows() {
        // Image window setup
        windowImage = new JWindow(frame);
        windowImage.setLayout(new BorderLayout());
        windowImage.setBackground(new Color(0, 0, 0, 0));

        // Name window setup
        windowLabel = new JWindow(frame);
        windowLabel.setLayout(new BorderLayout());
        windowLabel.setBackground(new Color(0, 0, 0));

        labelName = new JLabel();
        labelName.setPreferredSize(new Dimension(150, 30));
        labelName.setHorizontalAlignment(SwingConstants.CENTER);
        setCustomFont(labelName, "Old English Text MT Regular", 20);

        windowLabel.add(labelName, BorderLayout.CENTER);
        windowLabel.pack();

        // Set default speaker image
        Image scaledImage = alreadyLoadedSpeaker.get(0).getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
        labelImage = new JLabel(new ImageIcon(scaledImage));
        windowImage.add(labelImage, BorderLayout.CENTER);
        windowImage.pack();
    }

    /**
     * Positions the speaker windows relative to the dialog.
     */
    private void positionWindows() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int imageY = (int) (screenSize.getHeight() - (screenSize.getHeight() * 0.20) - windowLabel.getHeight());
        windowLabel.setLocation(0, imageY);

        imageY = (int) (screenSize.getHeight() - (screenSize.getHeight() * 0.20) - windowLabel.getHeight() - windowImage.getHeight());
        windowImage.setLocation(0, imageY);
    }

    /**
     * Sets the action listener for the next button.
     * 
     * @param listener The action listener to be called when next is clicked
     */
    public void setNextButtonListener(ActionListener listener) {
        next.addActionListener(listener);
    }

    public void setSkipButtonListener(ActionListener listener){
        skip.addActionListener(listener);
    }

    /**
     * Gets the text label component for direct manipulation.
     * 
     * @return The JLabel displaying dialog text
     */
    public JLabel getTextLabel() {
        return this.text;
    }

    /**
     * Displays the dialog box and associated windows.
     */
    public void showDialog() {
        box.setVisible(true);
        windowImage.setVisible(true);
        windowLabel.setVisible(true);
    }

    /**
     * Closes and disposes of all dialog components.
     */
    public void closeDialog() {
        box.dispose();
        windowImage.setVisible(false);
        windowImage.dispose();
        windowLabel.setVisible(false);
        windowLabel.dispose();
    }

    /**
     * Updates the displayed speaker information.
     * 
     * @param speaker The name of the current speaker
     */
    public void updateSpeaker(String speaker) {
        labelName.setText(speaker);

        // Check cached images first
        for (ImageIcon speakerIcon : alreadyLoadedSpeaker) {
            if (speakerIcon.getDescription().equals(speaker)) {
                updateSpeakerImage(speakerIcon);
                refreshWindows();
                return;
            }
        }

        // Load new image if not cached
        ImageIcon newSpeaker = new ImageIcon("ressource/images/NPC/" + speaker + ".png");
        if (newSpeaker.getIconWidth() <= 1) {
            // Fallback to default if loading fails
            updateSpeakerImage(alreadyLoadedSpeaker.get(0));
        } else {
            newSpeaker.setDescription(speaker);
            alreadyLoadedSpeaker.add(newSpeaker);
            updateSpeakerImage(newSpeaker);
        }

        refreshWindows();
    }

    /**
     * Updates the speaker image display.
     * 
     * @param icon The ImageIcon to display
     */
    private void updateSpeakerImage(ImageIcon icon) {
        labelImage.setIcon(new ImageIcon(
                icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
    }

    /**
     * Applies a custom font to a label.
     * 
     * @param label The label to modify
     * @param fontName The font filename (without extension)
     * @param size The font size
     */
    private void setCustomFont(JLabel label, String fontName, int size) {
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, 
                    new File("ressource/" + fontName + ".ttf"));
            customFont = customFont.deriveFont(Font.PLAIN, size);
            label.setFont(customFont);
        } catch (Exception e) {
            System.err.println("Error loading custom font: " + e.getMessage());
            label.setFont(new Font("Serif", Font.PLAIN, size));
        }
    }

    /**
     * Refreshes the speaker windows to reflect changes.
     */
    private void refreshWindows() {
        windowImage.getContentPane().removeAll();
        windowImage.add(labelImage, BorderLayout.CENTER);
        windowImage.revalidate();
        windowImage.repaint();

        windowLabel.getContentPane().removeAll();
        windowLabel.add(labelName, BorderLayout.CENTER);
        windowLabel.revalidate();
        windowLabel.repaint();

        windowImage.setVisible(true);
        windowLabel.setVisible(true);
    }
}