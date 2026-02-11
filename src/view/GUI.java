package view;

import controller.GameController;
import controller.MainMenuController;
import controller.SettingsController;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import util.Music;

public class GUI extends JFrame {

    private MainMenuController mainMenuController;
    private int layer = 0;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final JLayeredPane layeredPane;
    private GameController gameController;
    private static final Music musicPlayer = new Music();
    public final DialogManager dialogue;
    private static SettingsController settingsController;

    public boolean isOverlayOn = false;
    private OverlayPanel overlayPanel;
    public static long seed;

    private static ArrayList<String> data;
    public static String SAVE_PATH = "ressource/save/save.txt";

    public static boolean loadSave = false;
    public boolean FOW;

    public GUI() {

        // create main window
        super("WesNot");
        cardLayout = new CardLayout(); // Initialisation of the CardLayout
        mainPanel = new JPanel(cardLayout);
        layeredPane = new JLayeredPane();

        // global random object
        data = loadSave();
        seed = System.currentTimeMillis(); // ligne inutile ?
        RandomTaskManager rd = new RandomTaskManager(seed);

        // full screen
        // GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        // GraphicsDevice gd = ge.getDefaultScreenDevice();
        // if (gd.isFullScreenSupported()) {
        // gd.setFullScreenWindow(this);
        // } else {
        // this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // }
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        /*
         * this.addWindowListener(new WindowAdapter() {
         * 
         * @Override
         * public void windowClosing(WindowEvent e){
         * kill();
         * }
         * });
         */
        this.setLocationRelativeTo(null);

        // dimension
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        this.setSize(screenSize);
        layeredPane.setPreferredSize(screenSize);
        layeredPane.setBounds(0, 0, screenSize.width, screenSize.height);
        this.add(layeredPane);

        mainPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());

        layeredPane.add(mainPanel, Integer.valueOf(layer++));

        mainMenuController = new MainMenuController(this);
        mainPanel.add(mainMenuController.getView().getPanel(), "Menu");

        this.setVisible(true); // Rendre la fenêtre visible
        this.revalidate();
        this.repaint();

        dialogue = new DialogManager("ressource/dialogue/dialog.txt", this);
        settingsController = new SettingsController(this);
    }

    /**
     * Initialisation du panneau superposé (overlay).
     * Il sera utilisé pour afficher des informations supplémentaires au-dessus de
     * la vue principale.
     */
    private void initOverlayPanel() {
        overlayPanel = new OverlayPanel(this, new JPanel()); // Créer un nouveau panneau pour le superposé
        overlayPanel.setOpaque(false); // Rendre le panneau transparent
        overlayPanel.setSize(layeredPane.getWidth() / 2, layeredPane.getHeight() / 2); // Définir la taille du panneau
                                                                                       // superposé

        // Positionner le panneau superposé au centre de l'écran
        int x = (layeredPane.getWidth() - overlayPanel.getWidth()) / 2;
        int y = (layeredPane.getHeight() - overlayPanel.getHeight()) / 2;
        overlayPanel.setBounds(x, y, overlayPanel.getWidth(), overlayPanel.getHeight()); // Définir la position du
                                                                                         // panneau superposé

        // Le panneau superposé est invisible par défaut
        overlayPanel.setVisible(false); // Rendre le panneau superposé invisible au départ
    }

    public void addToLayeredPanel(Component a) {
        layeredPane.add(a, Integer.valueOf(layer++));
    }

    public void removeOfLayeredPanel(Component a) {
        layeredPane.remove(a);
    }

    public void showSettings(boolean resume) {
        if (!mainPanel.isAncestorOf(settingsController.getView().getPanel())) {
            mainPanel.add(settingsController.getView().getPanel(), "Settings");
        }
        settingsController.displaySettings(resume);
        cardLayout.show(mainPanel, "Settings");
        revalidate();
        repaint();
    }

    public void setGame(boolean b) {
        loadSave = b;
        if (b) {
            seed = Long.parseLong(GUI.getLoadedData().get(2));
            RandomTaskManager.setSeed(seed);
        } else {
            seed = System.currentTimeMillis(); // 9 -> caverne pas loin
            RandomTaskManager.setSeed(seed);
        }
        if (gameController == null) {
            gameController = new GameController(this, settingsController.getModel().getRealPlayers(),
                    settingsController.getModel().getAiPlayers(), seed); // Créer un contrôleur de pour un
            // joueur
            mainPanel.add(gameController.getGameView().getPane(), "Game"); // Ajouter la vue du jeu au CardLayout
        }
        cardLayout.show(mainPanel, "Game");
        SwingUtilities.invokeLater(() -> gameController.mapController.displayMap());

        Timeline.add(new Task(1, () -> {
            try {
                dialogue.loadDialogs();
                dialogue.displayDialogue(gameController.getGameModel().getRounds());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public void mainMenuOn() {
        cardLayout.show(mainPanel, "Menu");
        hideOverlayPanel();
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    public static Music getMusicPlayer() {
        return musicPlayer;
    }

    public DialogManager getDialog() {
        return dialogue;
    }

    public SettingsController getSettings() {
        return settingsController;
    }

    /**
     * Méthode pour cacher le panneau superposé
     */
    public void hideOverlayPanel() {
        isOverlayOn = false; // Indiquer que le panneau superposé n'est plus actif
        overlayPanel.setVisible(false); // Rendre le panneau superposé invisible
        overlayPanel.repaint(); // Repeindre le panneau pour appliquer les changements
    }

    /**
     * Méthode pour afficher ou masquer le panneau superposé avec l'interface
     * utilisateur des machines, inventaire et craft
     * 
     * @param jp Le composant à afficher dans le panneau superposé
     */
    public void showOverlayPanel(Component jp) {
        isOverlayOn = true; // Indiquer que le panneau superposé est actif
        overlayPanel.removeAll(); // Retirer tous les composants du panneau superposé

        overlayPanel.setPreferredSize(jp.getPreferredSize()); // Définir la taille du panneau superposé en fonction du
                                                              // composant
        overlayPanel.setVisible(false); // Rendre le panneau superposé invisible avant l'ajout
        overlayPanel.setVisible(true); // Rendre le panneau superposé visible
        overlayPanel.setContentPanel(jp); // Ajouter le composant au panneau superposé
        overlayPanel.repaint(); // Repeindre le panneau pour appliquer les changements
    }

    // public static Random getRandom() {
    // return random;
    // }

    public void kill() {
        if (gameController != null) {
            gameController.mapController.getMapModel().cleanSave();
            gameController.mapController.getMapModel().writeToFile();
        }
        dispose();
        System.exit(0);
    }

    public static ArrayList<String> getLoadedData() {
        return data;
    }

    private ArrayList<String> loadSave() {
        String line;
        ArrayList<String> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_PATH))) {

            while ((line = reader.readLine()) != null) {
                if (!line.equals("###")) {
                    data.add(line);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur dans le chargement de la sauvegarde");
        }
        return data;
    }

}
