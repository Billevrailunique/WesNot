package controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import model.Attack;
import model.AutomatedPlayer;
import model.GameModel;
import model.PlayerModel;
import model.TileModel;
import model.TileModel.Terrains;
import model.Troop;
import view.GUI;
import view.GameView;
import view.MapView.MapPanel;

//TODO claim button display
//TODO

/**
 * The GameController class manages the core game logic and interactions between
 * models and views.
 * It handles player turns, troop movements, attacks, recruitment, and territory
 * claiming.
 */
public class GameController {
    private final GameModel gameModel;
    private final GameView gameView;
    public MapController mapController;
    private final GUI frame;
    private final Music musicPlayer = GUI.getMusicPlayer();
    private static final Terrains[] RECRUITMENT_TERRAINS = {
            Terrains.MAINDUNGEON, Terrains.DUNGEON0, Terrains.DUNGEON1,
            Terrains.DUNGEON2, Terrains.DUNGEON3, Terrains.DUNGEON4, Terrains.DUNGEON5,
            Terrains.VILLAGE, Terrains.VILLAGE0, Terrains.VILLAGE1, Terrains.VILLAGE2,
            Terrains.VILLAGE3, Terrains.VILLAGE4, Terrains.VILLAGE5 };
    private final Color actionButtonsColor = new Color(194, 40, 0);
    private final Font actionButtonsFont = new Font("Monofonto", Font.BOLD, 15);

    /**
     * Constructs a new GameController with the specified frame, number of players,
     * and seed.
     * 
     * @param frame          the parent GUI frame
     * @param numberOfPlayer the number of players in the game
     * @param seed           the random seed for game initialization
     */
    public GameController(GUI frame, int numberOfPlayer, int nbAi, long seed) {
        gameModel = new GameModel();
        mapController = new MapController(frame, gameModel, seed, numberOfPlayer, nbAi);
        initMouseListener();
        gameView = new GameView(frame, mapController.getView().getPanel(), gameModel);
        this.frame = frame;
        buttonsListener();
    }

    /**
     * Ends the current player's turn and advances to the next player or round.
     * Resets troop states and updates game information.
     */
    public void endTurn() {
        if (gameModel.getPlayers().size() - 1 == gameModel.getPlayerTurn()) {
            //System.out.println("new round");
            gameModel.addRound();
            gameModel.resetTurn();
            frame.getDialog().displayDialogue(gameModel.getRounds());
        } else {
            nextTurn();
            //System.out.println("next turn");
        }
        for (Troop troop : gameModel.getCurrentPlayer().getFaction()) {
            troop.setAttacked(false);
            troop.setMoved(false);
        }
        boolean gameOver = true;
        for (PlayerModel player : gameModel.getPlayers()) {
            if (!(player instanceof AutomatedPlayer) && !player.hasBeenEliminated()) {
                gameOver = false;
            }
        }
        if (gameOver) {
            gameView.gameOverView();
            return;

        }
        gameModel.setSelectedTroop(null);
        gameView.resetTroopInfo();
        gameView.clearTroopAimedsHPBar();
        gameView.clearTroopOwnedHPBar();
        gameModel.setInteractionMode(InteractionModes.IDLE);
        gameView.updateTroopAimedInfo(null);
        mapController.getMapView().mapPanel.centerViewPort();
        // Things to do when the turn is ended
    }

    public void nextTurn() {
        boolean isAI = gameModel.nextTurn();
        if (isAI) {
            endTurn();
            gameView.updateGameInfo();
            mapController.getMapView().mapPanel.updateMap();
            frame.revalidate();
            frame.repaint();
        }
    }

    /**
     * Initializes button listeners for game controls.
     * Sets up action listeners for home, end turn, cancel, and various interaction
     * buttons.
     */
    private void initButtonsListener() {
        MapModel mapModel = mapController.getMapModel();

        gameView.getHomeButton().addActionListener(e -> {
            frame.showSettings(true);
            frame.getDialog().getView().closeDialog();
            musicPlayer.playSFX("ButtonClicked");
            musicPlayer.play("MenuMusic");
        });

        gameView.getEndturn().addActionListener(e -> {
            endTurn();
            gameView.updateGameInfo();
            frame.revalidate();
            frame.repaint();

        });

        gameView.getCancelButton().addActionListener(l -> {
            gameView.clearActionVisual();
            gameModel.setInteractionMode(InteractionModes.IDLE);
            gameView.setTooltipText("select an action (you can also select a different troop)");
            gameView.setInteractionList(true, true);/*
                                                     * gameModel.getSelectedTile().getTroop().getType() ==
                                                     * Troop.TroopType.LEADER
                                                     * && Arrays.asList(RECRUITMENT_TERRAINS)
                                                     * .contains(gameModel.getSelectedTile().getTerrain())
                                                     * && gameModel.getSelectedTile().getPlayerID() ==
                                                     * gameModel.getPlayerTurn(),
                                                     * gameModel.getSelectedTile().getTroop().getType() ==
                                                     * Troop.TroopType.LEADER
                                                     * && gameModel.getSelectedTile().isClaimable());
                                                     */
            //System.out.println(gameModel.getSelectedTile().getTroop().getType() == Troop.TroopType.LEADER);
            gameView.showInteractionMenu();
        });

        gameView.getChangeDirButton(mapModel.showedChunk).addActionListener(l -> {
            mapController.changeChunk(mapController.getMapModel().getDir(), gameModel.getSelectedTroop());
            gameView.clearActionVisual();
            gameModel.setInteractionMode(InteractionModes.IDLE);
            gameView.setTooltipText("select an action (you can also select a different troop)");
            SwingUtilities.invokeLater(() -> {
                initMouseListener();
                mapController.getView().mapPanel.centerViewPort();
            });
        });

        gameView.getEnterCaverneButton().addActionListener(l -> {
            mapController.changeChunk(mapController.getMapModel().getDir(), gameModel.getSelectedTroop());
            gameView.clearActionVisual();
            gameModel.setInteractionMode(InteractionModes.IDLE);
            gameView.setTooltipText("select an action (you can also select a different troop)");
            SwingUtilities.invokeLater(() -> {
                initMouseListener();
                mapController.getView().mapPanel.centerViewPort();
            });
        });

        gameView.getEnterDungeonButton().addActionListener(l -> {
            mapController.changeChunk(mapController.getMapModel().getDir(), gameModel.getSelectedTroop());
            gameView.clearActionVisual();
            gameModel.setInteractionMode(InteractionModes.IDLE);
            gameView.setTooltipText("select an action (you can also select a different troop)");
            SwingUtilities.invokeLater(() -> {
                initMouseListener();
                mapController.getView().mapPanel.centerViewPort();
            });
        });

        gameView.getExitUndergroundButton(mapModel.showedChunk).addActionListener(l -> {
            mapController.changeChunk(mapController.getMapModel().getDir(), gameModel.getSelectedTroop());
            gameView.clearActionVisual();
            gameModel.setInteractionMode(InteractionModes.IDLE);
            gameView.setTooltipText("select an action (you can also select a different troop)");
            SwingUtilities.invokeLater(() -> {
                initMouseListener();
                mapController.getView().mapPanel.centerViewPort();
            });
        });

        gameView.getAttackButton().addActionListener(e -> {
            gameView.clearActionVisual();

            for (Attack attack : gameModel.getSelectedTroop().getAttacks()) {
                JButton button = new JButton(attack.getName());
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.setForeground(actionButtonsColor); // Couleur du texte
                button.setFont(actionButtonsFont);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setFocusable(false);
                gameView.getActionVisual().add(button);
                gameView.getActionVisual().add(Box.createVerticalStrut(10));
                button.addActionListener(l -> {
                    gameModel.setInteractionMode(InteractionModes.ATTACKING);
                    gameModel.setSelectedAttack(attack);
                    gameView.setTooltipText("select whom you would like to attack");
                });
            }
            gameView.getActionVisual().add(gameView.getCancelButton());
            gameView.getActionVisual().add(Box.createVerticalStrut(10));
            gameView.setTooltipText("select an attack type");
            gameView.getActionVisual().revalidate();
            gameView.getActionVisual().repaint();
        });

        gameView.getMoveButton().addActionListener(e -> {
            gameModel.setInteractionMode(InteractionModes.MOVING);
            mapModel.move(gameModel.getSelectedTroop(), gameModel.getSelectedTroop().getY(),
                    gameModel.getSelectedTroop().getX(), mapModel.showedChunk.coordInUnivers); // you might think that
                                                                                               // this does nothing but
                                                                                               // everything is
                                                                                               // calculated
            gameView.clearActionVisual();
            gameView.setTooltipText("select where you want to go");
            gameView.getActionVisual().add(gameView.getCancelButton());
            gameView.getActionVisual().add(Box.createVerticalStrut(10));

            gameView.getActionVisual().add(gameView.getChangeDirButton(mapModel.showedChunk));
            gameView.getActionVisual().add(Box.createVerticalStrut(10));

            gameView.getActionVisual().add(gameView.getEnterDungeonButton());
            gameView.getActionVisual().add(Box.createVerticalStrut(10));

            gameView.getActionVisual().add(gameView.getExitUndergroundButton(mapModel.showedChunk));
            gameView.getActionVisual().add(Box.createVerticalStrut(10));

            gameView.getActionVisual().add(gameView.getEnterCaverneButton());
            gameView.getActionVisual().add(Box.createVerticalStrut(10));

            //System.out.println("au moment où je clique sur move, dir vaut " +
            // mapModel.getDir().toString());

        });

        gameView.getRecruitButton().addActionListener(e -> {
            gameView.clearActionVisual();
            gameView.setTooltipText("Select a troop to recruit : ");
            for (TroopType t : TroopType.values()) {
                if (t == TroopType.LEADER
                        || Troop.getCost(t) > gameModel.getCurrentPlayer().getGold())
                    continue;
                JButton button = new JButton(t.toString() + " : " + Troop.getCost(t) + "$");
                button.addActionListener(l -> {
                    gameModel.setInteractionMode(InteractionModes.RECRUITING);
                    gameModel.setRecruit(t);
                    gameView.clearActionVisual();
                    gameView.setTooltipText("Select where you want to place your new recruit :");
                });
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.setForeground(actionButtonsColor); // Couleur du texte
                button.setFont(actionButtonsFont);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setFocusable(false);
                gameView.getActionVisual().add(button);
                gameView.getActionVisual().add(Box.createVerticalStrut(10));
            }
            gameView.getActionVisual().add(gameView.getCancelButton());
            gameView.getActionVisual().add(Box.createVerticalStrut(10));
        });
        gameView.getClaimButton().addActionListener(e -> {
            gameView.clearActionVisual();
            gameModel.setInteractionMode(InteractionModes.CLAIMING);
            interact(gameModel.getSelectedTile().getCoordinates().first(),
                    gameModel.getSelectedTile().getCoordinates().second());
            JButton validateButton = new JButton("claim");
            validateButton.setForeground(actionButtonsColor); // Couleur du texte
            validateButton.setFont(actionButtonsFont);
            validateButton.setBorderPainted(false);
            validateButton.setContentAreaFilled(false);
            validateButton.setFocusPainted(false);
            validateButton.setFocusable(false);
            validateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            validateButton.addActionListener(l -> {
                gameModel.getCurrentPlayer().addClaimedTile(gameModel.getSelectedTile());
                mapController.getMapModel().setNeighbourID(gameModel.getPlayerTurn(),
                        gameModel.getSelectedTile().getCoordinates().first(),
                        gameModel.getSelectedTile().getCoordinates().second());
                musicPlayer.playSFX("claimingSound");
                gameView.clearActionVisual();
                gameModel.setInteractionMode(InteractionModes.IDLE);
                gameView.setTooltipText("Select an action (you can also select another troop)");
                gameView.setInteractionList(
                        gameModel.getSelectedTile().getTroop().getType() == Troop.TroopType.LEADER
                                && Arrays.asList(RECRUITMENT_TERRAINS)
                                        .contains(gameModel.getSelectedTile().getTerrain())
                                && gameModel.getSelectedTile().getPlayerID() == gameModel.getPlayerTurn(),
                        gameModel.getSelectedTile().getTroop().getType() == Troop.TroopType.LEADER
                                && gameModel.getSelectedTile().isClaimable());
                gameView.showInteractionMenu();
            });
            gameView.getActionVisual().add(Box.createVerticalStrut(10));
            gameView.getActionVisual().add(validateButton);
            gameView.getActionVisual().add(Box.createVerticalStrut(10));
            gameView.getActionVisual().add(gameView.getCancelButton());
            gameView.getActionVisual().revalidate();
            gameView.getActionVisual().repaint();
        });

    }

    /**
     * Initializes mouse listeners for map interactions.
     * Handles tile selection and various interaction modes.
     */
    public final void initMouseListener() {
        MouseInputListener mouseListener;
        mouseListener = new MouseInputListener() {
            HexCell cell = mapController.getView().cell;
            MapPanel mapPanel = mapController.getView().mapPanel;

            @Override
            public void mouseClicked(MouseEvent e) {
                cell.setCellByPoint(e.getX(), e.getY()); // Adjust mouse position with offset
                int i = cell.getindexI();
                int j = cell.getindexJ();
                //System.out.println("\nclicked on (" + i + "," + j + ")\n");
                if (mapController.getMapModel().isInsideGrid(i, j)) {
                    interact(i, j);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
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
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
            }

        };
        mapController.getView().mapPanel.addMouseListener(mouseListener);
    }

    /**
     * Handles interactions with the game map based on current interaction mode.
     * 
     * @param i the x-coordinate of the interacted tile
     * @param j the y-coordinate of the interacted tile
     */
    public void interact(int i, int j) {
        MapModel mapModel = mapController.getMapModel();
        //System.out.println(mapModel.getMap()[j][i]);
        switch (gameModel.getInteractionMode()) {
            case IDLE:
                if (mapModel.getMap()[j][i].getTroop() != null) {
                    if (mapModel.getMap()[j][i].getTroop().getPlayerID() == gameModel
                            .getPlayerTurn()) {
                        gameModel.setSelectedTroop(mapModel.getMap()[j][i].getTroop());
                        gameModel.setSelectedTile(mapModel.getMap()[j][i]);
                        gameView.setInteractionList(
                                gameModel.getSelectedTile().getTroop().getType() == Troop.TroopType.LEADER
                                        && Arrays.asList(RECRUITMENT_TERRAINS)
                                                .contains(gameModel.getSelectedTile().getTerrain())
                                        && gameModel.getSelectedTile().getPlayerID() == gameModel.getPlayerTurn(),
                                mapModel.getMap()[j][i].getTroop().getType() == Troop.TroopType.LEADER
                                        && mapModel.getMap()[j][i].isClaimable());
                        gameView.setTooltipText("Select an action");
                        gameView.setInteractionList(
                                gameModel.getSelectedTile().getTroop().getType() == Troop.TroopType.LEADER
                                        && Arrays.asList(RECRUITMENT_TERRAINS)
                                                .contains(gameModel.getSelectedTile().getTerrain())
                                        && gameModel.getSelectedTile().getPlayerID() == gameModel.getPlayerTurn(),
                                gameModel.getSelectedTile().getTroop().getType() == Troop.TroopType.LEADER
                                        && gameModel.getSelectedTile().isClaimable());

                        /*System.out.println(
                                gameModel.getSelectedTile().getTroop().getType() == Troop.TroopType.LEADER
                                        && Arrays.asList(RECRUITMENT_TERRAINS)
                                                .contains(gameModel.getSelectedTile().getTerrain())
                                        && gameModel.getSelectedTile().getPlayerID() == gameModel.getPlayerTurn());
                        System.out.println(
                                gameModel.getSelectedTile().isClaimable());*/
                        gameView.showInteractionMenu();
                        gameView.updateTroopOwnedInfo();
                    } else {
                        gameView.setTooltipText(
                                "Voici les infos de cette troop ennemie ! Pour une action selectionné votre troop.");
                        gameView.updateTroopAimedInfo(mapModel.getMap()[j][i].getTroop());
                    }
                } else {
                    gameView.clearActionVisual();
                    gameView.setTooltipText("Il n'y a personne ici.");
                }
                break;
            case ATTACKING:
                if (!gameModel.getSelectedTroop().getAttacked()) {
                    if (mapModel.getMap()[j][i].getTroop() != null) {
                        Troop troopVictim = mapModel.getMap()[j][i].getTroop();
                        Attack attack = gameModel.getSelectedAttack();
                        Troop troopAgressor = gameModel.getSelectedTroop();
                        if (troopVictim != troopAgressor) {
                            if (troopVictim.getPlayerID() != troopAgressor.getPlayerID()) {
                                if (troopAgressor.reachable(troopVictim, attack)) {
                                    troopAgressor.attack(troopVictim, attack);
                                    if (!troopVictim.isAlive()) {
                                        gameModel.getPlayers().get(troopAgressor.getPlayerID()).getFaction()
                                                .remove(troopAgressor);
                                        mapModel.getMap()[j][i].setTroop(null);
                                        mapController.updateMap(j, i, 0);
                                    }
                                    musicPlayer.playSFX(attack.getType().toString());
                                    gameModel.getSelectedTroop().setAttacked(true);
                                    gameView.update(mapModel.getMap()[j][i].getTroop());
                                    gameView.clearActionVisual();
                                    gameView.setInteractionList(true, true);/*
                                                                             * gameModel.getSelectedTile().getTroop().
                                                                             * getType() == Troop.TroopType.LEADER
                                                                             * && Arrays.asList(RECRUITMENT_TERRAINS)
                                                                             * .contains(gameModel.getSelectedTile().
                                                                             * getTerrain())
                                                                             * &&
                                                                             * gameModel.getSelectedTile().getPlayerID()
                                                                             * == gameModel.getPlayerTurn(),
                                                                             * gameModel.getSelectedTile().getTroop().
                                                                             * getType() == Troop.TroopType.LEADER
                                                                             * &&
                                                                             * gameModel.getSelectedTile().isClaimable()
                                                                             * );
                                                                             */
                                    /*System.out.println(
                                            gameModel.getSelectedTile().getTroop().getType() == Troop.TroopType.LEADER
                                                    && Arrays.asList(RECRUITMENT_TERRAINS)
                                                            .contains(gameModel.getSelectedTile().getTerrain())
                                                    && gameModel.getSelectedTile().getPlayerID() == gameModel
                                                            .getPlayerTurn());
                                    System.out.println(
                                            gameModel.getSelectedTile().isClaimable());*/
                                    gameView.showInteractionMenu();
                                    gameModel.setInteractionMode(InteractionModes.IDLE);
                                    gameView.setTooltipText("Select an action (you can also select another troop)");
                                } else {
                                    gameView.setTooltipText("this troop is not reachable with this attack");
                                }
                            } else {
                                gameView.setTooltipText(
                                        "a troop cant attack someone in it's faction, select someone else");
                            }
                        } else {
                            gameView.setTooltipText("a troop cant attack itself, select someone else");
                        }
                    } else {
                        gameView.setTooltipText("there is noone here. select someone else");
                    }
                } else {
                    gameView.setTooltipText("This troop already attacked");
                }
                break;
            case RECRUITING:
                if (mapModel.getMap()[j][i].getTroop() == null) {
                    if (Arrays.asList(RECRUITMENT_TERRAINS).contains(mapModel.getMap()[j][i].getTerrain())
                            && mapModel.getMap()[j][i].getPlayerID() == gameModel.getPlayerTurn()) {
                        Troop recruit = new Troop(gameModel.getRecruit(), i, j, gameModel.getPlayerTurn(),
                                mapModel.showedChunk.coordInUnivers);
                        gameModel.getCurrentPlayer().getFaction().add(recruit);
                        mapModel.addTroop(recruit);
                        gameModel.getCurrentPlayer().subGold(Troop.getCost(recruit.getType()));
                        mapController.updateMap(j, i, 0);
                        gameModel.setInteractionMode(InteractionModes.IDLE);
                        gameView.setTooltipText("Select an action (you can also select another troop)");
                        gameView.clearActionVisual();
                        gameView.update(null);
                    } else {
                        gameView.setTooltipText("You cannot recruit on this tile.");
                    }
                } else {
                    gameView.setTooltipText(
                            "There already is a troop on this tile.");
                }
                break;
            case MOVING:
                if (!gameModel.getSelectedTroop().getMoved()) {
                    if (mapModel.getMap()[j][i].getTroop() != null) {
                        gameView.setTooltipText("there is someone here. select somewhere else");
                    } else {
                        Troop troop = gameModel.getSelectedTroop();
                        TileModel[][] tiles = mapModel.showedChunk.map;
                        List<Pair<Integer, Integer>> path = PathFinder.getPath(tiles,
                                new Pair<>(troop.getX(), troop.getY()), new Pair<>(i, j));
                        int cost = PathFinder.costFromPath(tiles, path);
                        // debug:
                        /*
                         * System.out.println("Debugging from GameController:");
                         * System.out.println("Movement has been triggered. Troop speed: " +
                         * troop.getSpeed()
                         * + ", Path cost: " + cost);
                         * PathFinder.printHexPath(tiles, path);
                         */

                        if (cost == -1) {
                            gameView.setTooltipText(
                                    "No path found between those two points (likely a bug in pathfinding)");
                        } else if (cost > troop.getSpeed()) {
                            gameView.setTooltipText(
                                    "That tile is too far away, select somewhere else. Distance to tile: " + cost);
                        } else {
                            mapModel.move(gameModel.getSelectedTroop(), j, i, mapModel.showedChunk.coordInUnivers);
                            gameModel.getSelectedTroop().setMoved(true);
                            mapController.updateMap(j, i, troop.getFOWUpdateRange());
                            gameModel.getCurrentPlayer().addScore(cost * 10);
                            gameView.updateGameInfo();
                            gameModel.setSelectedTroop(null);
                            gameView.resetTroopInfo();
                            gameModel.setInteractionMode(InteractionModes.IDLE);
                            gameView.setTooltipText("Select an action (you can also select another troop)");
                        }
                    }
                } else {
                    gameView.setTooltipText("This troop already moved");
                }
                break;
            case CLAIMING:
                if (gameModel.getSelectedTroop().getType() == Troop.TroopType.LEADER) {
                    if (mapModel.getMap()[j][i].isClaimable()) {
                        if (mapModel.getMap()[j][i].getPlayerID() == -1) {
                            gameView.setTooltipText("Claiming this area will give you "
                                    + mapModel.getMap()[j][i].getValue() + " gold each turn.");

                        } else {
                            gameView.setTooltipText("This area was already claimed !");
                        }
                    } else if (mapModel.getMap()[j][i].isNearlyClaimable()) {
                        gameView.setTooltipText("You must be at the center of this area to claim it !");
                    } else {
                        gameView.setTooltipText("You can only claim Villages and vainquished foe's Dungeons !");
                    }
                } else {
                    gameView.setTooltipText("Only your leader can claim territory.");
                }
                break;

            default:
                break;
        }
    }

    /**
     * Returns the GameView associated with this controller.
     * 
     * @return the GameView instance
     */
    public GameView getGameView() {
        return gameView;
    }

    /**
     * Returns the GameModel associated with this controller.
     * 
     * @return the GameModel instance
     */
    public GameModel getGameModel() {
        return gameModel;
    }

}