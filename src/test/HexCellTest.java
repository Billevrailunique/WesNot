package test;

import javax.swing.*;
import util.*;

public class HexCellTest {
    private static final int RADIUS = 70;

    private static int[][] grid = new int[20][20];

    private static final int GRID_HEIGHT = grid.length;
    private static final int GRID_WIDTH = grid[0].length;

    private static HexGridPanel hexPanel = new HexGridPanel();
    private static JScrollPane scrollPane = new JScrollPane(hexPanel);

    private static void initGrid() {
        for (int i = 2; i < GRID_HEIGHT - 2; i++) {
            for (int j = 2; j < GRID_WIDTH - 2; j++) {
                grid[i][j] = 1;
            }
        }
    }

    private static class HexGridPanel extends JPanel implements MouseInputListener {
        private HexCell cell = new HexCell(RADIUS);
        int[] cornersX = new int[HexCell.NUM_CORNERS];
        int[] cornersY = new int[HexCell.NUM_CORNERS];

        private final Point MARGIN = new Point(80, 110); // Margin for auto-scrolling

        public HexGridPanel() {
            setPreferredSize(new Dimension((GRID_WIDTH + 1) * RADIUS * 3 / 2,
                    (int) ((GRID_HEIGHT + 1) * RADIUS * Math.sqrt(3))));
            setBackground(Color.WHITE);
            addMouseListener(this);
            addMouseMotionListener(this); // Add mouse motion listener for dragging
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawHexagons(g);
        }

        public void drawHexagons(Graphics g) {
            for (int k = 0; k < GRID_HEIGHT; k++) {
                for (int l = 0; l < GRID_WIDTH; l++) {
                    cell.setCellIndex(l, k);
                    cell.computeCorners(cornersX, cornersY);
                    g.setColor(new Color(10 * k, 0, 10 * l));
                    g.fillPolygon(cornersX, cornersY, HexCell.NUM_CORNERS);
                    g.setColor(Color.WHITE);
                    g.drawPolygon(cornersX, cornersY, HexCell.NUM_CORNERS);
                }
            }
        }

        private boolean isInsideGrid(int i, int j) {
            return 0 <= i && i < GRID_WIDTH && 0 <= j && j < GRID_HEIGHT;
        }

        private void toggleCell(int i, int j) {
            //System.out.println(i + ";" + j);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            cell.setCellByPoint(e.getX(), e.getY()); // Adjust mouse position with offset
            int i = cell.getindexI();
            int j = cell.getindexJ();
            if (isInsideGrid(i, j)) {
                toggleCell(i, j);
                repaint();
            }
        }

        public boolean pointIsInMargin(Point p) {
            int panelWidth = scrollPane.getViewport().getWidth();
            int panelHeight = scrollPane.getViewport().getHeight();
            return p.x < MARGIN.x || p.y < MARGIN.y || p.x > panelWidth - MARGIN.x || p.y > panelHeight - MARGIN.y;
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // Call autoscroll when the mouse moves over the panel
            Point p = e.getPoint();
            autoscroll(p);
        }

        public void autoscroll(Point p) {
            // Get the current viewport position
            Point currentPos = scrollPane.getViewport().getViewPosition();
            int initialX = currentPos.x;
            int initialY = currentPos.y;
            int panelWidth = scrollPane.getViewport().getWidth();
            int panelHeight = scrollPane.getViewport().getHeight();
            Dimension preferredSize = hexPanel.getPreferredSize(); // Get the preferred size of the hex grid panel

            // Adjust scroll position based on mouse location and margin
            if (p.x < initialX + MARGIN.x) {
                currentPos.x -= 10; // Scroll left
            } else if (p.x > panelWidth - MARGIN.x) {
                currentPos.x += 10; // Scroll right
            }

            if (p.y < initialY + MARGIN.y) {
                currentPos.y -= 10; // Scroll up
            } else if (p.y > panelHeight - MARGIN.y) {
                currentPos.y += 10; // Scroll down
            }

            // Prevent scrolling out of bounds using the preferred size of the hex grid
            // panel
            // Clamping the values to ensure we do not scroll past the bounds
            currentPos.x = Math.max(0, Math.min(currentPos.x, preferredSize.width - panelWidth));
            currentPos.y = Math.max(0, Math.min(currentPos.y, preferredSize.height - panelHeight));

                // Set the new scroll position to update the view
                scrollPane.getViewport().setViewPosition(currentPos);

            });
            refreshing.start();
        }

        public void scroll() {
            if (scrollDirection == KeyEvent.VK_UP || scrollDirection == KeyEvent.VK_DOWN) {
                int currentPos = scrollPane.getVerticalScrollBar().getValue();
                boolean down = scrollDirection == KeyEvent.VK_DOWN;
                scrollPane.getVerticalScrollBar().setValue(currentPos + (down ? 10 : -10));
            } else {
                int currentPos = scrollPane.getHorizontalScrollBar().getValue();
                boolean right = scrollDirection == KeyEvent.VK_RIGHT;
                scrollPane.getHorizontalScrollBar().setValue(currentPos + (right ? 10 : -10));
            }
        }

        public void startScrolling(KeyEvent e) {
            scrollDirection = e.getKeyCode();
            scrollTimer.start();
        }

        public void stopScrolling(KeyEvent e) {
            scrollTimer.stop();
        }

        private boolean shouldStartScrolling(KeyEvent e) {
            return e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT
                    || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN;
        }

        private boolean shouldStopScrolling(KeyEvent e) {
            return e.getKeyCode() == scrollDirection;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (shouldStartScrolling(e)) {
                // startScrolling(e);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (shouldStopScrolling(e)) {
                stopScrolling(e);
            }
        }
    }

    public static void main(String[] args) {
        initGrid();
        JFrame frame = new JFrame("Hex Grid Test");

        scrollPane.setAutoscrolls(true); // Enable autoscroll in the scroll pane

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.getContentPane().add(scrollPane);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
