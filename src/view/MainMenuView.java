package view;

import java.awt.*;
import javax.swing.*;

public class MainMenuView extends JPanel {

    private GUI frame;
    private JPanel mainPanel;
    private Image backgroundImage;

    public MainMenuView(GUI frame, String imagePath) {

        this.frame = frame;

        // load background image
        backgroundImage = new ImageIcon(imagePath).getImage();
        // create pane with image
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JFrame getFrame() {
        return frame;
    }
}
