package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import model.Chunk;
import model.GameModel;
import model.PlayerModel;
import model.Troop;

public class GameView {
    private final JPanel gameViewPane;
    private final JPanel center;
    private final JPanel top;
    private final JPanel right;
    private final JButton homeButton;
    private final JButton endturn;
    private final JPanel actionVisual;
    private final JPanel gameInfo;
    private final JPanel playerNameInfo;
    private final JPanel roundInfo;
    private final JPanel goldInfo;
    private final JPanel troopNumberInfo;
    private final JPanel troopOwnedPanel;
    private final JPanel troopOwnedName;
    private final JPanel troopOwnedIcon;
    private final JPanel troopOwnedInfo;
    private final JPanel troopAimedPanel;
    private final JPanel troopAimedName;
    private final JPanel troopAimedIcon;
    private final JPanel troopAimedInfo;
    private final JLabel scoreLabel;
    private final JLabel playerInfoLabel;
    private final JLabel roundInfoLabel;
    private final JLabel goldInfoLabel;
    private final JLabel numberOfTroopLabel;
    private final JProgressBar troopOwnedHPBar;
    private final JProgressBar troopAimedHPBar;

    private Boolean rec;
    private Boolean claim;
    private final JPanel scoreInfo;
    private final Color textColor = new Color(194, 157, 0);
    private final Color actionButtonColor = new Color(193, 40, 0);
    private final Color panelBackgroundColor = new Color(55, 33, 14);
    private final Font topBarFont = new Font("Monofonto", Font.PLAIN, 15);
    private final Image topbarBackground = new ImageIcon("ressource/images/backgrounds/topBarBackground.png")
            .getImage();

    JButton attackButton;
    JButton recruitButton;
    JButton moveButton;
    JButton cancelButton;
    JButton changeDirButton;
    JButton claimTerritoryButton;
    JButton enterDungeonButton;
    JButton exitUndergroundButton;
    JButton enterCaverneButton;
    JLabel claimReward;
    JLabel claimedTile;
    JTextArea tooltipText;

    public static String direction = "dungeon";

    private GameModel model;

    public GameView(JFrame frame, JScrollPane map, GameModel model) {
        this.model = model;

        // INITIALISATION OF PANELS

        gameViewPane = new JPanel(new BorderLayout());
        gameViewPane.setBackground(Color.LIGHT_GRAY);

        top = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(topbarBackground, 0, 0, getWidth(), getHeight(), this);
            }
        };
        homeButton = new JButton("Pause");
        homeButton.setFocusable(false);
        homeButton.setBorder(BorderFactory.createLineBorder(textColor, 3));
        homeButton.setContentAreaFilled(false);
        homeButton.setFocusPainted(false);
        homeButton.setFocusable(false);
        homeButton.setForeground(textColor);
        homeButton.setFont(new Font("Monofonto", Font.BOLD, 15));
        gameInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerNameInfo = new JPanel(new BorderLayout());
        roundInfo = new JPanel(new BorderLayout());
        goldInfo = new JPanel(new BorderLayout());
        troopNumberInfo = new JPanel(new BorderLayout());
        scoreInfo = new JPanel();

        right = new JPanel(new BorderLayout());

        troopAimedPanel = new JPanel(new BorderLayout());
        troopOwnedPanel = new JPanel(new BorderLayout());

        troopInfo = new JPanel(new BorderLayout());
        actionVisual = new JPanel();
        actionVisual.setBackground(panelBackgroundColor);
        troopOwnedName = new JPanel();
        troopOwnedName.setBackground(panelBackgroundColor);
        troopOwnedIcon = new JPanel();
        troopOwnedIcon.setBackground(panelBackgroundColor);
        troopAimedIcon = new JPanel();
        troopAimedIcon.setBackground(panelBackgroundColor);
        troopAimedName = new JPanel();
        troopAimedName.setBackground(panelBackgroundColor);
        troopOwnedInfo = new JPanel();
        troopAimedInfo = new JPanel();
        troopOwnedHPBar = new JProgressBar();
        troopAimedHPBar = new JProgressBar();
        endturn = new JButton("Terminer le tour");
        endturn.setFocusable(false);

        attackButton = new JButton("attack");
        recruitButton = new JButton("recruit");
        moveButton = new JButton("move");
        cancelButton = new JButton("cancel");
        claimTerritoryButton = new JButton("claim");

        tooltipText = new JTextArea("Select a troop");

        center = map;

        // SETTING THEM

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        center = map;
        gameViewPane.add(center, BorderLayout.CENTER);
        gameInfo.setBorder(BorderFactory.createLineBorder(textColor, 3));
        gameInfo.setOpaque(false);

        // top
        top.setPreferredSize(new Dimension(screenSize.width, screenSize.height / 16));
        top.setBackground(panelBackgroundColor);

        playerNameInfo.setPreferredSize(new Dimension(135, screenSize.height / 16));
        goldInfo.setPreferredSize(new Dimension(100, screenSize.height / 16));
        roundInfo.setPreferredSize(new Dimension(75, screenSize.height / 16));
        troopNumberInfo.setPreferredSize(new Dimension(100, screenSize.height / 16));
        playerNameInfo.setOpaque(false);
        goldInfo.setOpaque(false);
        roundInfo.setOpaque(false);
        troopNumberInfo.setOpaque(false);
        scoreInfo.setOpaque(false);
        troopInfo.setPreferredSize(new Dimension(screenSize.width / 8, 500));
        troopInfo.setBackground(panelBackgroundColor);

        // right
        right.setPreferredSize(new Dimension(screenSize.width / 8, screenSize.height));
        actionVisual.setBorder(BorderFactory.createLineBorder(textColor, 3));
        actionVisual.setPreferredSize(new Dimension(screenSize.width / 8, 200));

        troopOwnedHPBar.setStringPainted(true);
        troopAimedHPBar.setStringPainted(true);
        troopOwnedHPBar.setString("/HP");
        troopAimedHPBar.setString("/HP");
        troopOwnedHPBar.setForeground(new Color(10, 100, 20));
        troopAimedHPBar.setForeground(new Color(10, 100, 20));
        troopOwnedPanel.setPreferredSize(new Dimension(screenSize.width / 8, 300));
        troopOwnedInfo.setBackground(panelBackgroundColor);
        troopOwnedInfo.setOpaque(true);
        troopAimedPanel.setPreferredSize(new Dimension(screenSize.width / 8, 300));
        troopAimedInfo.setBackground(panelBackgroundColor);
        troopAimedInfo.setOpaque(true);
        troopOwnedIcon.setPreferredSize(new Dimension(screenSize.width / 8, 200));
        troopAimedIcon.setPreferredSize(new Dimension(screenSize.width / 8, 200));
        troopOwnedInfo.setPreferredSize(new Dimension(screenSize.width / 8, 50));
        troopAimedInfo.setPreferredSize(new Dimension(screenSize.width / 8, 50));

        troopOwnedIcon.setBackground(panelBackgroundColor);
        troopOwnedIcon.setOpaque(true);
        troopAimedIcon.setBackground(panelBackgroundColor);
        troopAimedIcon.setOpaque(true);
        troopOwnedInfo.setBackground(panelBackgroundColor);
        troopOwnedInfo.setOpaque(true);
        troopAimedInfo.setBackground(panelBackgroundColor);
        troopAimedInfo.setOpaque(true);

        troopOwnedHPBar.setBackground(Color.DARK_GRAY);
        troopAimedHPBar.setBackground(Color.DARK_GRAY);

        tooltipText.setEditable(false);
        tooltipText.setWrapStyleWord(true);
        tooltipText.setLineWrap(true);
        tooltipText.setOpaque(false);
        tooltipText.setBackground(new Color(0, 0, 0, 0));
        tooltipText.setAlignmentX(Component.CENTER_ALIGNMENT);
        tooltipText.setFocusable(false);

        attackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        attackButton.setFocusable(false);
        recruitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        recruitButton.setFocusable(false);
        moveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        moveButton.setFocusable(false);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        claimTerritoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ADDING ELEMENTS

        // top
        playerInfoLabel = new JLabel("Joueur:" + model.getPlayers().get(model.getPlayerTurn()).getName());
        playerInfoLabel.setFont(topBarFont);
        playerInfoLabel.setForeground(textColor);
        playerNameInfo.add(playerInfoLabel,
                BorderLayout.CENTER);
        playerNameInfo.add(new JLabel(new ImageIcon("ressource/icons/player.png")), BorderLayout.WEST);

        roundInfoLabel = new JLabel("Tour: " + model.getRounds());
        roundInfoLabel.setFont(topBarFont);
        roundInfoLabel.setForeground(textColor);
        roundInfo.add(roundInfoLabel, BorderLayout.CENTER);

        goldInfoLabel = new JLabel("Or: " + model.getPlayers().get(model.getPlayerTurn()).getGold());
        goldInfoLabel.setFont(topBarFont);
        goldInfoLabel.setForeground(textColor);
        goldInfo.add(goldInfoLabel, BorderLayout.CENTER);
        goldInfo.add(new JLabel(new ImageIcon("ressource/icons/piece.png")), BorderLayout.WEST);

        numberOfTroopLabel = new JLabel(
                "Troupes: " + model.getPlayers().get(model.getPlayerTurn()).getFaction().size());
        numberOfTroopLabel.setFont(topBarFont);
        numberOfTroopLabel.setForeground(textColor);
        troopNumberInfo.add(numberOfTroopLabel,
                BorderLayout.CENTER);

        scoreLabel = new JLabel("Score: " + model.getPlayers().get(model.getPlayerTurn()).getScore());
        scoreLabel.setFont(topBarFont);
        scoreLabel.setForeground(textColor);
        scoreInfo.add(scoreLabel);

        gameInfo.add(playerNameInfo);
        gameInfo.add(roundInfo);
        gameInfo.add(goldInfo);
        gameInfo.add(troopNumberInfo);
        gameInfo.add(scoreInfo);

        top.add(homeButton, BorderLayout.WEST);
        top.add(gameInfo, BorderLayout.CENTER);

        ArrayList<JButton> actionButtons = new ArrayList<>();

        attackButton = new JButton("attack");
        recruitButton = new JButton("recruit");
        moveButton = new JButton("move");
        cancelButton = new JButton("cancel");
        changeDirButton = new JButton("");
        enterCaverneButton = new JButton("enter in caverne");
        claimTerritoryButton = new JButton("claim");
        enterDungeonButton = new JButton("Enter in dungeon");
        exitUndergroundButton = new JButton("back to the surface");

        // actionButtons.add(endturn);
        actionButtons.add(attackButton);
        actionButtons.add(recruitButton);
        actionButtons.add(moveButton);
        actionButtons.add(cancelButton);
        actionButtons.add(changeDirButton);
        actionButtons.add(claimTerritoryButton);
        actionButtons.add(enterDungeonButton);
        actionButtons.add(exitUndergroundButton);

        for (JButton button : actionButtons) {
            // Enlever la bordure
            button.setBorderPainted(false);

            // Enlever le fond
            button.setContentAreaFilled(false);

            // Enlever la mise en avant au focus
            button.setFocusPainted(false);

            // Set la couleur du text
            button.setForeground(actionButtonColor);

            // Set le font
            button.setFont(new Font("Monofonto", Font.BOLD, 15));
        }

        // Set la couleur des contours
        endturn.setBorder(BorderFactory.createLineBorder(textColor, 3));

        // Enlever le fond
        endturn.setContentAreaFilled(false);

        // Enlever la mise en avant au focus
        endturn.setFocusPainted(false);

        // Set la couleur du text
        endturn.setForeground(textColor);

        // Set le font
        endturn.setFont(new Font("Monofonto", Font.BOLD, 15));

        tooltipText = new JTextArea("Select a troop");
        tooltipText.setFont(new Font("Monofonto", Font.ITALIC, 15));
        tooltipText.setEditable(false);
        tooltipText.setWrapStyleWord(true);
        tooltipText.setLineWrap(true);
        tooltipText.setOpaque(false);
        tooltipText.setBackground(new Color(0, 0, 0, 0));
        tooltipText.setForeground(actionButtonColor);
        tooltipText.setAlignmentX(Component.CENTER_ALIGNMENT);
        tooltipText.setAlignmentY(Component.CENTER_ALIGNMENT);
        tooltipText.setFocusable(false);

        attackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        attackButton.setFocusable(false);
        recruitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        recruitButton.setFocusable(false);
        moveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        moveButton.setFocusable(false);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setFocusable(false);
        changeDirButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeDirButton.setFocusable(false);
        enterCaverneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        enterCaverneButton.setFocusable(false);
        claimTerritoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        claimTerritoryButton.setFocusable(false);
        enterDungeonButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        enterDungeonButton.setFocusable(false);
        exitUndergroundButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitUndergroundButton.setFocusable(false);

        // actionVisual.add(Box.createVerticalStrut(30));
        actionVisual.add(Box.createVerticalGlue());
        actionVisual.add(tooltipText);
        actionVisual.add(Box.createVerticalGlue());

        troopOwnedInfo.add(troopOwnedHPBar);
        troopAimedInfo.add(troopAimedHPBar);
        troopOwnedPanel.setPreferredSize(new Dimension(screenSize.width / 8, 260));
        troopAimedPanel.setPreferredSize(new Dimension(screenSize.width / 8, 260));

        actionVisual.setPreferredSize(new Dimension(screenSize.width / 8, 180));

        troopOwnedPanel.add(troopOwnedName, BorderLayout.NORTH);
        troopOwnedPanel.add(troopOwnedIcon, BorderLayout.CENTER);
        troopOwnedPanel.add(troopOwnedInfo, BorderLayout.SOUTH);
        troopAimedPanel.add(troopAimedName, BorderLayout.NORTH);
        troopAimedPanel.add(troopAimedIcon, BorderLayout.CENTER);
        troopAimedPanel.add(troopAimedInfo, BorderLayout.SOUTH);
        troopInfo.add(actionVisual, BorderLayout.CENTER);
        troopInfo.add(troopOwnedPanel, BorderLayout.NORTH);
        troopInfo.add(troopAimedPanel, BorderLayout.SOUTH);
        troopInfo.setBorder(BorderFactory.createLineBorder(textColor, 7));

        right.setBackground(panelBackgroundColor);
        right.add(troopInfo, BorderLayout.CENTER);
        right.add(endturn, BorderLayout.SOUTH);

        // final adds
        gameViewPane.add(top, BorderLayout.NORTH);
        gameViewPane.add(right, BorderLayout.EAST);
    }

    public void update(Troop ennemieTroop) {
        updateGameInfo();
        updateTroopOwnedInfo();
        updateTroopAimedInfo(ennemieTroop);
    }

    public void updateGameInfo() {
        playerNameInfo.removeAll();
        roundInfo.removeAll();
        goldInfo.removeAll();
        troopNumberInfo.removeAll();
        scoreInfo.removeAll();

        playerInfoLabel.setText("Joueur:" + model.getPlayers().get(model.getPlayerTurn()).getName());
        roundInfoLabel.setText("Tour: " + model.getRounds());
        goldInfoLabel.setText("Or: " + model.getPlayers().get(model.getPlayerTurn()).getGold());
        numberOfTroopLabel.setText("Troupes: " + model.getPlayers().get(model.getPlayerTurn()).getFaction().size());
        scoreLabel.setText("Score: " + model.getPlayers().get(model.getPlayerTurn()).getScore());

        playerNameInfo.add(playerInfoLabel, BorderLayout.CENTER);
        playerNameInfo.add(new JLabel(new ImageIcon("ressource/icons/player.png")), BorderLayout.WEST);
        roundInfo.add(roundInfoLabel, BorderLayout.CENTER);
        goldInfo.add(goldInfoLabel, BorderLayout.CENTER);
        goldInfo.add(new JLabel(new ImageIcon("ressource/icons/piece.png")), BorderLayout.WEST);
        troopNumberInfo.add(numberOfTroopLabel, BorderLayout.CENTER);
        scoreInfo.add(scoreLabel);

    }

    public void updateTroopOwnedInfo() {
        troopOwnedName.removeAll();
        troopOwnedIcon.removeAll();
        if (model.getSelectedTroop() != null) {
            ImageIcon originalIcon = new ImageIcon("ressource/images/troops/"
                    + model.getSelectedTroop().getType().toString() + model.getPlayerTurn() + ".png");

            Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);

            ImageIcon resizedIcon = new ImageIcon(scaledImage);
            JLabel icon = new JLabel((resizedIcon));
            icon.setPreferredSize(troopOwnedIcon.getPreferredSize());
            troopOwnedIcon.add(icon);
            troopOwnedHPBar.setMaximum(Troop.getDefaultHealth(model.getSelectedTroop().getType()));
            troopOwnedHPBar.setValue(model.getSelectedTroop().getHealth());
            troopOwnedHPBar.setString(troopOwnedHPBar.getValue() + "/" + troopOwnedHPBar.getMaximum() + " HP");
        } else {
            troopOwnedHPBar.setString("/HP");
        }
        JLabel troopName = model.getSelectedTroop() != null
                ? new JLabel("(vous) : " + model.getSelectedTroop().getName())
                : new JLabel();
        troopName.setForeground(textColor);

        troopOwnedName.add(troopName);

        troopOwnedHPBar.revalidate();
        troopOwnedHPBar.repaint();
        troopOwnedName.revalidate();
        troopOwnedName.repaint();
        troopOwnedIcon.revalidate();
        troopOwnedIcon.repaint();

    }

    public void updateTroopAimedInfo(Troop ennemieTroop) {
        troopAimedName.removeAll();
        troopAimedIcon.removeAll();
        if (ennemieTroop != null) {
            ImageIcon originalIcon = new ImageIcon("ressource/images/troops/" + ennemieTroop.getType().toString()
                    + ennemieTroop.getPlayerID() + ".png");

            Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);

            ImageIcon resizedIcon = new ImageIcon(scaledImage);
            JLabel icon = new JLabel((resizedIcon));

            icon.setPreferredSize(troopOwnedIcon.getPreferredSize());
            troopAimedIcon.add(icon);
            troopAimedHPBar.setMaximum(Troop.getDefaultHealth(ennemieTroop.getType()));
            troopAimedHPBar.setValue(ennemieTroop.getHealth());
            troopAimedHPBar.setString(troopAimedHPBar.getValue() + "/" + troopAimedHPBar.getMaximum() + " HP");
        } else {
            troopAimedHPBar.setString("/HP");
        }
        JLabel troopName = model.getSelectedTroop() != null
                ? new JLabel("(vous) : " + model.getSelectedTroop().getName())
                : new JLabel();
        troopName.setForeground(textColor);

        troopOwnedName.add(troopName);
        troopAimedHPBar.revalidate();
        troopAimedHPBar.repaint();
        troopAimedName.revalidate();
        troopAimedName.repaint();
        troopAimedIcon.revalidate();
        troopAimedIcon.repaint();

    }

    public void clearActionVisual() {
        actionVisual.removeAll();
        actionVisual.revalidate();
        actionVisual.repaint();
    }

    public void clearTroopOwnedName() {
        troopOwnedName.removeAll();
        troopOwnedName.add(new JLabel("(vous) : "));
        troopOwnedName.revalidate();
        troopOwnedName.repaint();
    }

    public void clearTroopAimedName() {
        troopAimedName.removeAll();
        troopAimedName.add(new JLabel("(ennemie) : "));
        troopAimedName.revalidate();
        troopAimedName.repaint();
    }

    public void clearTroopOwnedHPBar() {
        troopOwnedHPBar.setValue(0);
        troopOwnedHPBar.setString("/HP");
    }

    public void clearTroopAimedsHPBar() {
        troopAimedHPBar.setValue(0);
        troopAimedHPBar.setString("/HP");
    }

    public void showInteractionMenu() {
        actionVisual.removeAll();
        actionVisual.setLayout(new BoxLayout(actionVisual, BoxLayout.Y_AXIS));
        actionVisual.add(tooltipText);
        actionVisual.add(Box.createVerticalGlue());
        actionVisual.add(attackButton);
        if (rec) {
            actionVisual.add(Box.createVerticalStrut(5));
            actionVisual.add(recruitButton);
        }
        if (claim) {
            actionVisual.add(Box.createVerticalStrut(5));
            actionVisual.add(claimTerritoryButton);
        }
        actionVisual.add(Box.createVerticalStrut(5));
        actionVisual.add(moveButton);
        actionVisual.add(Box.createVerticalGlue());

        actionVisual.revalidate();
        actionVisual.repaint();
    }

    public void resetTroopInfo() {
        clearActionVisual();
        clearTroopOwnedName();
        clearTroopAimedName();
        updateTroopAimedInfo(null);
        updateTroopOwnedInfo();
        setTooltipText("Select a troop");
    }

    public JButton getHomeButton() {
        return homeButton;
    }

    public JButton getEndturn() {
        return endturn;
    }

    public JButton getEnterDungeonButton() {
        if (direction.equals("dungeon")) {
            enterDungeonButton.setVisible(true);
        } else {
            enterDungeonButton.setVisible(false);
        }
        return enterDungeonButton;
    }

    public JButton getExitUndergroundButton(Chunk source) {
        if (source.type.equals("dungeon") || source.type.equals("caverne") && direction.equals("non")) {
            exitUndergroundButton.setVisible(true);
        } else {
            exitUndergroundButton.setVisible(false);
        }
        return exitUndergroundButton;
    }

    public JButton getAttackButton() {
        return attackButton;
    }

    public JButton getRecruitButton() {
        return recruitButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getChangeDirButton(Chunk chunk) {
        if (!(direction.equals("") || direction.equals("non") || direction.equals("dungeon")
                || direction.equals("caverne")) && chunk.type.equals("surface")) {
            changeDirButton.setText("aller " + direction);
            changeDirButton.setVisible(true);
            return changeDirButton;
        }
        changeDirButton.setVisible(false);
        return changeDirButton;
    }

    public JButton getEnterCaverneButton() {
        //System.out.println("in getEnterCaverneButton");
        //System.out.println(direction);
        if (direction.equals("caverne")) {
            enterCaverneButton.setVisible(true);
        } else {
            enterCaverneButton.setVisible(false);
        }
        return enterCaverneButton;
    }

    public JButton getMoveButton() {
        return moveButton;
    }

    public JButton getClaimButton() {
        return claimTerritoryButton;
    }

    public JPanel getActionVisual() {
        return actionVisual;
    }

    public JTextArea getTooltipText() {
        return tooltipText;
    }

    public void setTooltipText(String s) {
        tooltipText.setText(s);
        tooltipText.setVisible(true);
        actionVisual.add(tooltipText);
        actionVisual.revalidate();
        actionVisual.repaint();
    }

    public void hideTooltipText() {
        tooltipText.setVisible(false);
    }

    public JPanel getPane() {
        return gameViewPane;
    }

    public void setInteractionList(boolean recruitment, boolean claim) {
        this.rec = recruitment;
        this.claim = claim;
    }

    public void gameOverView(){
        //System.out.println("GAME OVER");
        JLabel text = new JLabel("GAME OVER !!!");
        text.setFont(new Font("Calibri",Font.BOLD,100));
        text.setForeground(Color.RED);
        JPanel panel = new JPanel();
        panel.add(text);
        String score="<html>";
        for (PlayerModel player : model.getPlayers()) {

            score += "<p>"+ player.getName()+ " : " + player.getScore()+ "</p>";
        }
        score += "</html>";
        JLabel textScore = new JLabel(score);
        textScore.setFont(new Font("Calibri",Font.BOLD,25));
        textScore.setForeground(Color.WHITE);
        panel.add(textScore);
        panel.setPreferredSize(new Dimension(500,500));
        panel.setBackground(Color.BLACK);
        gameViewPane.remove(center);
        gameViewPane.add(panel, BorderLayout.CENTER);
        right.remove(endturn);
        clearActionVisual();
        resetTroopInfo();


        
    }

}
