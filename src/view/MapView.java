package view;

import model.MapModel;
import util.HexCell;
import model.TileModel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import model.Chunk;
import model.MapModel;
import model.Settings;
import model.TileModel.Terrains;
import util.HexCell;

public class MapView {
    public final GUI frame;
    private MapController mapController;
    public MapModel mapModel;
    public MapPanel mapPanel;
    public JScrollPane scrollPane;
    private Timer scrollTimer;
    public int scrollDirection;
    private GameController gameController;
    private final HashMap<String, Image> TerrainImages;
    private final HashMap<String, Image> TroopImages;
    private final int scrollSpeed = 20;

    private static final int RADIUS = 70;
    private static final int ON = 1;
    private static final int OFF = 2;
    public HexCell cell = new HexCell(RADIUS);
    int[] cornersX = new int[HexCell.NUM_CORNERS];
    int[] cornersY = new int[HexCell.NUM_CORNERS];

    public static int[][] grid = {
            { 0, 0, 0, 0, 0 },
            { 0, OFF, OFF, OFF, 0 },
            { OFF, OFF, OFF, OFF, OFF },
            { 0, 0, OFF, 0, 0 }
    };

    private static final int GRID_HEIGHT = grid.length;
    private static final int GRID_WIDTH = grid[0].length;

    public MapView(GUI frame) {
        this.frame = frame;
        this.mapModel = mapmodel;
        this.mapPanel = new MapPanel(mapmodel.showedChunk);
        this.scrollPane = new JScrollPane(mapPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(194, 157, 0), 3));
        this.TerrainImages = new HashMap<>();
        this.TroopImages = new HashMap<>();
        SwingUtilities.invokeLater(() -> {
            mapController = frame.gameController.mapController;
            scrollTimer = new Timer(0, e -> {
                mapPanel.scroll();
            });
            scrollTimer.setRepeats(true);
            gameController = frame.gameController;
            gameController.getGameView().getPane().add(scrollPane, BorderLayout.CENTER);
        });
    }

    public void updateScrollPane() {
        mapPanel.setVisible(false);

        gameController.getGameView().getPane().remove(scrollPane);

        mapPanel = new MapPanel(mapModel.showedChunk);

        this.scrollPane = new JScrollPane(mapModel.showedChunk.pane);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        gameController.getGameView().getPane().add(scrollPane, BorderLayout.CENTER);
        gameController.getGameView().getPane().revalidate();
        gameController.getGameView().getPane().repaint();

        // rebuid tilemodel
        for (int i = 0; i < mapModel.showedChunk.GRID_WIDTH; i++) {
            for (int j = 0; j < mapModel.showedChunk.GRID_HEIGHT; j++) {
                mapModel.builTileModel(i, j);
            }
        }
        mapModel.initValueFOW();
        // définir où on spawn
        // mapModel.updateFOW(cordSpawn.first(), cordSpawn.second())
        mapPanel.setVisible(true);
        //System.out.println("updateScrollPane() a été appelé pour le chunk de coord "
        // + mapModel.showedChunk.coordInUnivers.toString());
    }

    public void displayMap() {
        //System.out.println("displaying map");

        Dimension viewSize = scrollPane.getViewport().getView().getSize();
        Dimension extentSize = scrollPane.getViewport().getExtentSize();
        cell.setCellIndex(gameController.getGameModel().getPlayers().get(0).getLeader().getX(),
                gameController.getGameModel().getPlayers().get(0).getLeader().getY());

        int viewX = Math.max(0,
                Math.min(cell.getCenterX() - extentSize.width / 2, viewSize.width - extentSize.width));
        int viewY = Math.max(0, Math.min(cell.getCenterY() - extentSize.height / 2,
                viewSize.height - extentSize.height));

        scrollPane.getHorizontalScrollBar().setValue(viewX);
        scrollPane.getVerticalScrollBar().setValue(viewY);

        mapPanel.setVisible(true);
    }

    public void hideMap() {
        mapPanel.setVisible(false);
    }

    public JScrollPane getPanel() {
        return scrollPane;
    }

    public void startScrolling(KeyEvent e) {
        scrollDirection = e.getKeyCode();
        scrollTimer.start();
    }

    public void stopScrolling(KeyEvent e) {
        scrollTimer.stop();
    }

    public class MapPanel extends JPanel {
        int[] cornersX = new int[HexCell.NUM_CORNERS];
        int[] cornersY = new int[HexCell.NUM_CORNERS];
        private Chunk chunk;

        public MapPanel(Chunk chunk) {
            this.chunk = chunk;
            int n = chunk.GRID_WIDTH;
            int k = chunk.GRID_HEIGHT;
            int totalHeight = k * cell.HEIGHT + cell.HEIGHT / 2;
            int totalWidth = (2 * n - (n / 2)) * cell.RADIUS + cell.RADIUS / 2;
            this.setPreferredSize(new Dimension(totalWidth, totalHeight));
            setBackground(Color.BLACK);
            setOpaque(true);
            setVisible(false);
            setFocusable(true);

            chunk.pane = this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawTerrains(g);
            // drawHexagons(g);
        }

        public void drawHexagons(Graphics g) {
            for (int k = 0; k < chunk.GRID_HEIGHT; k++) {
                for (int l = 0; l < chunk.GRID_WIDTH; l++) {
                    cell.setCellIndex(l, k);
                    cell.computeCorners(cornersX, cornersY);
                    g.setColor((grid[j][i] == ON) ? map[i][j].getColor() : Color.BLUE);
                    g.fillPolygon(cornersX, cornersY, HexCell.NUM_CORNERS);
                    g.setColor(Color.BLACK);
                    g.drawPolygon(cornersX, cornersY, HexCell.NUM_CORNERS);
                }
            }
        }

        /**
         * Retrieves the image associated with a given terrain type.
         * If the image is already cached, it returns the cached image.
         * Otherwise, it loads the image from disk, resizes it, stores it in the cache,
         * and returns it.
         *
         * @param t The terrain type whose image is needed.
         * @return The corresponding image of the terrain, or null if an error occurs.
         */
        private Image getTerrainImages(String t) {
            Image result = TerrainImages.get(t);
            if (result != null) {
                return result;
            } else {
                try {
                    BufferedImage image = ImageIO.read(new File("ressource/images/terrains/" + t + ".png"));
                    Image resizedImage = image.getScaledInstance(cell.WIDTH, cell.HEIGHT, Image.SCALE_DEFAULT);
                    TerrainImages.put(t, resizedImage);
                    return resizedImage;
                } catch (IOException e) {
                    //System.out.println("Impossible de trouver l'image " + t);
                }
            }
            return null;
        }

        /**
         * Retrieves the image associated with a given troop type.
         * If the image is already cached, it returns the cached image.
         * Otherwise, it loads the image from disk, resizes it, stores it in the cache,
         * and returns it.
         *
         * @param t The troop type whose image is needed.
         * @return The corresponding image of the troop, or null if an error occurs or
         *         the troop type is null.
         */
        private Image getTroopImages(String t) {
            if (!t.equals("")) {
                Image result = TroopImages.get(t);
                if (result != null) {
                    return result;
                } else {
                    try {
                        BufferedImage image = ImageIO.read(new File("ressource/images/troops/" + t + ".png"));
                        Image resizedImage = image.getScaledInstance(cell.WIDTH, cell.HEIGHT, Image.SCALE_DEFAULT);
                        TroopImages.put(t, resizedImage);
                        return resizedImage;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        /**
         * Draws the terrain and troop images on the given Graphics object.
         * It iterates through the map grid, computing the correct positions,
         * and draws the corresponding images for each terrain and troop (if present).
         *
         * @param g The Graphics object used for rendering the images.
         */
        public void drawTerrains(Graphics g) {
            for (int k = 0; k < chunk.GRID_HEIGHT; k++) {
                for (int l = 0; l < chunk.GRID_WIDTH; l++) {
                    if (isInsideViewPort(k, l))
                        drawTile(g, k, l);
                }
            }
        }

        private boolean isInsideViewPort(int row, int col) {
            if (scrollPane.getViewport() == null) {
                return true;
            }
            Point p = scrollPane.getViewport().getViewPosition();
            int width = scrollPane.getViewport().getView().getWidth();
            int height = scrollPane.getViewport().getView().getHeight();

            cell.setCellByPoint(p.x, p.y);
            int iMin = cell.getindexI();
            int jMin = cell.getindexJ();

            cell.setCellByPoint(p.x + width, p.y + height);
            int iMax = cell.getindexI();
            int jMax = cell.getindexJ();

            return iMin - 1 <= col && col <= iMax + 1 && jMin - 1 <= row && row <= jMax + 1;

        }

        /**
         * Updates the map by repainting discovered cells.
         */
        public void updateMap() {
            for (int k = 0; k < chunk.GRID_HEIGHT; k++) {
                for (int l = 0; l < chunk.GRID_WIDTH; l++) {
                    if (mapModel.isDiscovered(k, l)) {
                        cell.setCellIndex(l, k);
                        repaint(cell.getX(), cell.getY(), cell.WIDTH, cell.HEIGHT);
                    }
                }
            }

        }

        public void centerViewPort() {
            //System.out.println("center view port");
            if (gameController.getGameModel().getCurrentPlayer().getLeader()
                    .getCoordChunk() == gameController.mapController.getMapModel().showedChunk.coordInUnivers) {
                Dimension viewSize = scrollPane.getViewport().getView().getSize();
                Dimension extentSize = scrollPane.getViewport().getExtentSize();
                cell.setCellIndex(gameController.getGameModel().getCurrentPlayer().getLeader().getX(),
                        gameController.getGameModel().getCurrentPlayer().getLeader().getY());

                int viewX = Math.max(0,
                        Math.min(cell.getCenterX() - extentSize.width / 2, viewSize.width - extentSize.width));
                int viewY = Math.max(0, Math.min(cell.getCenterY() - extentSize.height / 2,
                        viewSize.height - extentSize.height));

                scrollPane.getHorizontalScrollBar().setValue(viewX);
                scrollPane.getVerticalScrollBar().setValue(viewY);
                scrollPane.revalidate();
                scrollPane.repaint();
            }
        }

        /**
         * Draws a tile on the map at the specified row and column.
         * 
         * @param g   the Graphics object used for drawing
         * @param row the row index of the tile
         * @param col the column index of the tile
         */
        public void drawTile(Graphics g, int row, int col) {
            cell.setCellIndex(col, row);
            cell.computeCorners(cornersX, cornersY);

            if (mapModel.isDiscovered(row, col) || !Settings.getFOW()) {
                g.drawImage(
                        getTerrainImages(
                                mapModel.getMap()[row][col].getTerrain().toString() + getNeighbourRiver(col, row)),
                        cell.getX(), cell.getY(), this);

                if (mapModel.getMap()[row][col].getTroop() != null) {
                    if (mapModel.getMap()[row][col].getTroop().isAlive()) {
                        g.drawImage(
                                getTroopImages(mapModel.getMap()[row][col].getTroop().getType().toString()
                                        + (mapModel.getMap()[row][col].getTroop().getPlayerID()%3)),
                                cell.getX(), cell.getY(), this);
                    }
                }
            } else {
                g.drawImage(getTerrainImages("FOW"), cell.getX(), cell.getY(), this);
            }
        }

        /**
         * Determines the river image identifier based on neighboring river tiles.
         * 
         * @param l the column index of the tile
         * @param k the row index of the tile
         * @return a string representing the river image identifier, or an empty string
         *         if not applicable
         */
        private String getNeighbourRiver(int l, int k) {
            cell.setCellIndex(l, k);

            if (mapModel.getTerrains()[k][l] == Terrains.RIVER
                    || mapModel.getTerrains()[k][l] == Terrains.BRIDGE
                    || mapModel.getTerrains()[k][l] == Terrains.SOURCE) {

                String idRivIm = "";
                int[] indexTop = { 5, 0, 1 };
                int[] indexBot = { 2, 3, 4 };
                LinkedList<Integer> indexPosTop = new LinkedList<>();
                LinkedList<Integer> indexPosBot = new LinkedList<>();

                for (int i : indexTop) {
                    if (chunk.isInsideGrid(cell.getNeighborJ(i), cell.getNeighborI(i))) {
                        if (mapModel.getMap()[cell.getNeighborJ(i)][cell.getNeighborI(i)]
                                .getTerrain() == Terrains.RIVER
                                || mapModel.getMap()[cell.getNeighborJ(i)][cell.getNeighborI(i)]
                                        .getTerrain() == Terrains.BRIDGE
                                || mapModel.getMap()[cell.getNeighborJ(i)][cell.getNeighborI(i)]
                                        .getTerrain() == Terrains.SOURCE) {
                            indexPosTop.add(i);
                        }
                    }
                }

                idRivIm += !indexPosTop.isEmpty() ? Collections.max(indexPosTop) : "";

                for (int i : indexBot) {
                    if (chunk.isInsideGrid(cell.getNeighborJ(i), cell.getNeighborI(i))) {
                        if (mapModel.getMap()[cell.getNeighborJ(i)][cell.getNeighborI(i)]
                                .getTerrain() == Terrains.RIVER
                                || mapModel.getMap()[cell.getNeighborJ(i)][cell.getNeighborI(i)]
                                        .getTerrain() == Terrains.BRIDGE) {
                            indexPosBot.add(i);
                        }
                    }
                }

                idRivIm += !indexPosBot.isEmpty() ? correctIndex(indexPosBot) : "";
                if (mapModel.getTerrains()[k][l] == Terrains.SOURCE) {
                    return "" + correctIndex(indexPosBot);
                }

                return idRivIm.length() >= 1 ? idRivIm : "";
            } else {
                return "";
            }
        }

        /**
         * Determines the correct index for river image identification.
         * 
         * @param l a list of neighboring river tile indices
         * @return the corrected river index
         */
        private static int correctIndex(LinkedList<Integer> l) {
            if (l.contains(2)) {
                return 2;
            } else if (l.contains(4)) {
                return 4;
            } else {
                return 3;
            }
        }

        public void scroll() {
            //System.out.println("scroll called");
            if (scrollDirection == KeyEvent.VK_UP || scrollDirection == KeyEvent.VK_DOWN) {
                int currentPos = scrollPane.getVerticalScrollBar().getValue();
                boolean down = scrollDirection == KeyEvent.VK_DOWN;
                scrollPane.getVerticalScrollBar().setValue(currentPos + (down ? scrollSpeed : -scrollSpeed));
            } else {
                int currentPos = scrollPane.getHorizontalScrollBar().getValue();
                boolean right = scrollDirection == KeyEvent.VK_RIGHT;
                scrollPane.getHorizontalScrollBar().setValue(currentPos + (right ? scrollSpeed : -scrollSpeed));
            }
        }
    }
}
