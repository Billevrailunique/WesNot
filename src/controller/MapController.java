package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JScrollPane;
import model.GameModel;
import model.MapModel;
import model.MapModel.Directions;
import model.Settings;
import model.Troop;
import util.Music;
import view.GUI;
import view.MapView;
import model.MapModel;

/**
 * The MapController class manages the game map interactions and navigation.
 * It handles map scrolling, chunk changing, fog of war (FOW) toggling,
 * and coordinates between the map view and model.
 */
public class MapController {
    private MapView mapView;
    private MapModel mapModel;
    public JScrollPane scrollPane;
    private KeyListener keyListener;
    private Music musicPlayer = GUI.getMusicPlayer();

    /**
     * Displays the game map in the associated view.
     */
    public void displayMap() {
        mapView.displayMap();
    }

    /**
     * Returns the MapView associated with this controller.
     * 
     * @return the MapView instance
     */
    public MapView getView() {
        return view;
    }

    /**
     * Initializes the key listener for map navigation.
     * Handles:
     * - Enter key: Centers the viewport
     * - 'R' key: Fast-forwards 3 turns
     * - Arrow keys: Map scrolling
     */
    public final void initKeyListener() {
        keyListener = new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                //
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println("pressed " + e.getKeyCode());
                if (shouldStartScrolling(e)) {
                    mapView.startScrolling(e);
                } else {
                    if (e.getKeyCode() == Settings.getCenterViewPortKeyCode()) {
                        mapView.mapPanel.centerViewPort();
                    }
                    if (e.getKeyCode() == Settings.getNextRoundKeyCode()) {
                        for (int i = 0; i < 3; i++) {
                            mapView.frame.gameController.endTurn();
                        }
                        mapView.frame.gameController.getGameView().updateGameInfo();
                        mapView.frame.revalidate();
                        mapView.frame.repaint();
                    }
                    if (e.getKeyCode() == Settings.getPauseKeyCode()) {
                        mapView.frame.showSettings(true);
                        mapView.frame.getDialog().getView().closeDialog();
                        musicPlayer.playSFX("ButtonClicked");
                        musicPlayer.play("MenuMusic");
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (shouldStopScrolling(e)) {
                    mapView.stopScrolling(e);
                }
            }

        };
    }

    /**
     * Constructs a new MapController with the specified frame, players, and seed.
     * 
     * @param frame     the parent GUI frame
     * @param gameModel gameModel object which mapModel will share players with
     * @param seed      the random seed for map generation
     */
    public MapController(GUI frame, GameModel gameModel, long seed, int nbPlayer, int nbAi) {
        this.mapModel = new MapModel(gameModel, nbPlayer, nbAi);
        // mapModel.printMap();
        this.mapView = new MapView(frame, mapModel);
        this.scrollPane = mapView.scrollPane;
        initKeyListener();
        mapView.frame.addKeyListener(keyListener);
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }

    /**
     * Returns the MapView instance.
     * 
     * @return the MapView instance
     */
    public MapView getMapView() {
        return mapView;
    }

    /**
     * Returns the MapModel instance.
     * 
     * @return the MapModel instance
     */
    public MapModel getMapModel() {
        return mapModel;
    }

    /**
     * Updates the map view at specified coordinates with given visibility range.
     * 
     * @param row   the row coordinate to update
     * @param col   the column coordinate to update
     * @param range the visibility range around the coordinates
     */
    public void updateMap(int row, int col, int range) {
        mapModel.updateFOW(row, col, range);
        mapView.mapPanel.updateMap();
    }

    /**
     * Changes the currently displayed map chunk in the specified direction.
     * 
     * @param dir    the direction to change chunks
     * @param leader the troop leading the chunk change (typically the player's
     *               leader)
     */
    public void changeChunk(Directions dir, Troop leader) {
        mapModel.changeChunk(dir, leader);
        this.scrollPane = mapView.scrollPane;
        mapView.updateScrollPane();
        mapView.frame.removeKeyListener(keyListener);
        mapView.frame.setVisible(true);
        initKeyListener();
        mapView.frame.addKeyListener(keyListener);
        mapView.frame.setFocusable(true);
        mapView.frame.requestFocusInWindow();
    }

    /**
     * Determines if a key press should start map scrolling.
     * 
     * @param e the KeyEvent to check
     * @return true if the key is an arrow key, false otherwise
     */
    private boolean shouldStartScrolling(KeyEvent e) {
        return Settings.getMovementKeys().contains(e.getKeyCode());
    }

    /**
     * Determines if a key release should stop map scrolling.
     * 
     * @param e the KeyEvent to check
     * @return true if the key matches the current scroll direction, false otherwise
     */
    private boolean shouldStopScrolling(KeyEvent e) {
        return e.getKeyCode() == mapView.scrollDirection;
    }
}