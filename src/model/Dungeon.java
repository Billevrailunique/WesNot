package model;

import java.util.ArrayList;
import java.util.List;
import model.TileModel.Terrains;
import util.Node;
import util.Pair;
import util.PathFinder;
import util.RandomTaskManager;

/**
 * Represents a dungeon generator using the Binary Space Partitioning (BSP)
 * algorithm.
 * The dungeon consists of interconnected rooms with corridors between them.
 */
public class Dungeon {

    private int h = 6; // Minimum size for rooms

    private Chunk chunk;

    /**
     * Constructs a Dungeon generator for the given chunk.
     *
     * @param chunk The game chunk where the dungeon will be generated
     */
    public Dungeon(Chunk chunk) {
        this.chunk = chunk;
    }

    /**
     * Checks if a rectangular area is big enough to be split further.
     *
     * @param a Top-left x coordinate
     * @param b Top-left y coordinate
     * @param c Bottom-right x coordinate
     * @param d Bottom-right y coordinate
     * @return true if the area is big enough to split, false otherwise
     */
    public boolean bigEnoug(int a, int b, int c, int d) {
        return (c - a >= h) && (d - b >= h);
    }

    /**
     * Binary Space Partitioning algorithm to recursively split the dungeon space.
     *
     * @param noeud The current node representing a space to split
     */
    private void bsp(
            Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> noeud) {
        boolean isVert = RandomTaskManager.nextBoolean();
        boolean stopGauche = false;
        boolean stopDroite = false;
        int posCoupe = -1;
        Pair<Integer, Integer> g_topleft = new Pair<>(noeud.getContent().first().first(),
                noeud.getContent().first().second()),
                g_bottomright = new Pair<>(null, null),
                d_topleft = new Pair<Integer, Integer>(null, null),
                d_bottomright = new Pair<Integer, Integer>(noeud.getContent().second().first(),
                        noeud.getContent().second().second());
        if (isVert) {
            int mean = (noeud.getContent().first().first() + noeud.getContent().second().first()) / 2;
            int variance = mean * mean / 4;
            posCoupe = RandomTaskManager.normalDis(mean, variance, noeud.getContent().first().first(),
                    noeud.getContent().second().first());

            if (bigEnoug(g_topleft.first(), g_topleft.second(), posCoupe, d_bottomright.second())) {
                g_bottomright.setFirst(posCoupe);
                g_bottomright.setSecond(d_bottomright.second());
            } else {
                stopGauche = true;
            }
            if (bigEnoug(posCoupe, g_topleft.second(), d_bottomright.first(), d_bottomright.second())) {
                d_topleft.setFirst(posCoupe);
                d_topleft.setSecond(g_topleft.second());
            } else
                stopDroite = true;
        } else {
            int mean = (noeud.getContent().first().second() + noeud.getContent().second().second()) / 2;
            int variance = mean * mean / 4;
            posCoupe = RandomTaskManager.normalDis(mean, variance, noeud.getContent().first().second(),
                    noeud.getContent().second().second());

            if (bigEnoug(g_topleft.first(), g_topleft.second(), d_bottomright.first(), posCoupe)) {
                g_bottomright.setFirst(d_bottomright.first());
                g_bottomright.setSecond(posCoupe);
            } else {
                stopGauche = true;
            }
            if (bigEnoug(g_topleft.first(), posCoupe, d_bottomright.first(), d_bottomright.second())) {
                d_topleft.setFirst(g_topleft.first());
                d_topleft.setSecond(posCoupe);
            } else {
                stopDroite = true;
            }
        }

        if (!stopGauche) {
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> carré_g = new Pair<>(g_topleft, g_bottomright);
            Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> gauche = new Node<>();
            gauche.setContent(carré_g);
            bsp(gauche);
            noeud.setGauche(gauche);
        }
        if (!stopDroite) {
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> carré_d = new Pair<>(d_topleft, d_bottomright);
            Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> droite = new Node<>();
            droite.setContent(carré_d);
            bsp(droite);
            noeud.setDroite(droite);
        }
    }

    /**
     * Converts the BSP tree into actual dungeon rooms and corridors.
     *
     * @param tree The BSP tree representing the dungeon structure
     */
    public void drawDungonMap(Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> tree) {
        if (tree == null) {
            return;
        }

        if (tree.getDroite() == null && tree.getGauche() == null) {
            Pair<Integer, Integer> top_left = tree.getContent().first(), bottom_right = tree.getContent().second();
            int moy = (tree.getContent().first().first() + tree.getContent().second().first()) / 2;
            int mean = (moy + tree.getContent().first().first()) / 2;
            int variance = mean * mean / 4;
            int x1 = RandomTaskManager.normalDis(mean, variance, tree.getContent().first().first() + 1,
                    moy - h / 3);

            moy = (tree.getContent().first().second() + tree.getContent().second().second()) / 2;
            mean = (moy + tree.getContent().first().second()) / 2;
            variance = mean * mean / 4;
            int y1 = RandomTaskManager.normalDis(mean, variance, tree.getContent().first().second() + 1,
                    moy - h / 3);

            moy = (tree.getContent().first().second() + tree.getContent().second().second()) / 2;
            mean = (moy + tree.getContent().second().second()) / 2;
            variance = mean * mean / 4;
            int y2 = RandomTaskManager.normalDis(mean, variance, moy + h / 3,
                    tree.getContent().second().second() - 1);

            moy = (tree.getContent().first().first() + tree.getContent().second().first()) / 2;
            mean = (moy + tree.getContent().second().first()) / 2;
            variance = mean * mean / 4;
            int x2 = RandomTaskManager.normalDis(mean, variance, moy + h / 3,
                    tree.getContent().second().first() - 1);

            tree.setContent(new Pair<>(new Pair<>(x1, y1), new Pair<>(x2, y2)));

            for (int i = top_left.first(); i <= bottom_right.first(); i++) {
                for (int j = top_left.second(); j <= bottom_right.second(); j++) {
                    if (i >= x1 && i <= x2 && j >= y1 && j <= y2) {
                        chunk.mapTerrains[j][i] = Terrains.SOL;
                    } else {
                        chunk.mapTerrains[j][i] = Terrains.MUR;
                    }
                }
            }
            return;
        }

        Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> gauche = tree.getGauche(), droite = tree.getDroite();
        drawDungonMap(gauche);
        drawDungonMap(droite);
        drawCorridor(tree);
    }

    /**
     * Draws corridors between rooms in the BSP tree.
     *
     * @param tree The BSP tree node containing rooms to connect
     */
    private void drawCorridor(Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> tree) {
        if (tree == null) {
            return;
        }

        if (tree.getGauche() == null || tree.getDroite() == null) {
            return;
        }

        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> _tree1 = tree.getGauche().getContent(),
                _tree2 = tree.getDroite().getContent();

        int a1 = _tree1.first().first();
        int b1 = _tree1.first().second();
        int c1 = _tree1.second().first();
        int d1 = _tree1.second().second();
        int a2 = _tree2.first().first();
        int b2 = _tree2.first().second();
        int c2 = _tree2.second().first();
        int d2 = _tree2.second().second();

        ArrayList<Pair<Integer, Integer>> chemins = new ArrayList<>();
        List<Pair<Integer, Integer>> path = new ArrayList<>();

        if (a2 <= c1 && c2 >= a1) {
            ArrayList candidats = tree.cmpTree(tree.getGauche(), false, false);
            Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> rect1_rect2 = distanceMin(
                    candidats, tree.getDroite());

            _tree1 = rect1_rect2.first();
            _tree2 = rect1_rect2.second();
            a1 = _tree1.first().first();
            b1 = _tree1.first().second();
            c1 = _tree1.second().first();
            d1 = _tree1.second().second();
            a2 = _tree2.first().first();
            b2 = _tree2.first().second();
            c2 = _tree2.second().first();
            d2 = _tree2.second().second();

            int corridorPos = a2;
            if (a2 <= c1 && c2 >= a1) {
                int start = a2 <= a1 ? a1 : a2;
                int end = c2 <= c1 ? c2 : c1;

                corridorPos = RandomTaskManager.nextInt(start, end);

                if (Caverne.getTileAround(corridorPos, b2, Terrains.SOL,
                        chunk) == 0
                        || Caverne.getTileAround(corridorPos, d1, Terrains.SOL,
                                chunk) == 0) {
                    int[][] truc = { { corridorPos - 1, b2 }, { corridorPos + 1, b2 }, { corridorPos - 1, d1 },
                            { corridorPos + 1, d1 } };
                    for (int[] coord : truc) {
                        if (Caverne.getTileAround(coord[0], coord[1], Terrains.SOL,
                                chunk) != 0) {
                            corridorPos = coord[0];
                            break;
                        }
                    }
                }

                for (int i = 0; i <= Math.max((b2 - d1), (d1 - b2)); i++) {
                    chemins.add(new Pair<>(corridorPos, i + Math.min(d1, b2)));
                }

            } else if (c2 < a1 && b2 > d1) {
                path.clear();
                Pair<Integer, Integer> startCoord = new Pair<>(a1, d1), endCoord = new Pair<>(c2, b2);
                path = PathFinder.getCorridors(chunk.map, startCoord, endCoord);

                for (Pair<Integer, Integer> coord : path) {
                    chemins.add(coord);
                }

            } else if (a2 > c1 && b2 > d1) {
                path.clear();
                Pair<Integer, Integer> startCoord = new Pair<>(c1, d1), endCoord = new Pair<>(a2, b2);
                path = PathFinder.getCorridors(chunk.map, startCoord, endCoord);

                for (Pair<Integer, Integer> coord : path) {
                    chemins.add(coord);
                }

            } else if (b2 <= d1 && d2 >= b1) {
                int start = b2 <= b1 ? b1 : b2;
                int end = d2 <= d1 ? d2 : d1;

                corridorPos = RandomTaskManager.nextInt(start, end);

                if (Caverne.getTileAround(c1, corridorPos, Terrains.SOL,
                        chunk) == 0
                        || Caverne.getTileAround(a2, corridorPos, Terrains.SOL,
                                chunk) == 0) {
                    int[][] truc = { { c1, corridorPos - 1 }, { c1, corridorPos + 1 }, { a2, corridorPos - 1 },
                            { a2, corridorPos + 1 } };
                    for (int[] coord : truc) {
                        if (Caverne.getTileAround(coord[0], coord[1], Terrains.SOL,
                                chunk) != 0) {
                            corridorPos = coord[1];
                            break;
                        }
                    }
                }
                for (int i = 0; i <= Math.max(c1 - a2, a2 - c1); i++) {
                    chemins.add(new Pair<Integer, Integer>(c1 + i, corridorPos));
                }
            }
        } else if (b2 <= d1 && d2 >= b1) {
            ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> candidats = tree.cmpTree(tree.getGauche(),
                    false, true);
            Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> rect1_rect2 = distanceMin(
                    candidats, tree.getDroite());

            _tree1 = rect1_rect2.first();
            _tree2 = rect1_rect2.second();

            a1 = _tree1.first().first();
            b1 = _tree1.first().second();
            c1 = _tree1.second().first();
            d1 = _tree1.second().second();
            a2 = _tree2.first().first();
            b2 = _tree2.first().second();
            c2 = _tree2.second().first();
            d2 = _tree2.second().second();
            int corridorPos;
            if (b2 <= d1 && d2 >= b1) {
                int start = b2 <= b1 ? b1 : b2;
                int end = d2 <= d1 ? d2 : d1;

                corridorPos = RandomTaskManager.nextInt(start, end);

                if (Caverne.getTileAround(c1, corridorPos, Terrains.SOL,
                        chunk) == 0
                        || Caverne.getTileAround(a2, corridorPos, Terrains.SOL,
                                chunk) == 0) {
                    int[][] truc = { { c1, corridorPos - 1 }, { c1, corridorPos + 1 }, { a2, corridorPos - 1 },
                            { a2, corridorPos + 1 } };
                    for (int[] coord : truc) {
                        if (Caverne.getTileAround(coord[0], coord[1], Terrains.SOL,
                                chunk) != 0) {
                            corridorPos = coord[1];
                            break;
                        }
                    }
                }

                for (int i = 0; i <= Math.max(c1 - a2, a2 - c1); i++) {
                    chemins.add(new Pair<Integer, Integer>(c1 + i, corridorPos));
                }

            } else if (a2 > c1 && d2 < b1) {
                path.clear();
                Pair<Integer, Integer> startCoord = new Pair<>(c1, b1), endCoord = new Pair<>(a2, d2);

                path = PathFinder.getCorridors(chunk.map, startCoord, endCoord);

                for (Pair<Integer, Integer> coord : path) {
                    chemins.add(coord);
                }

            } else if (c2 < a1 && b2 > d1) {
                path.clear();
                Pair<Integer, Integer> startCoord = new Pair<>(c2, b2), endCoord = new Pair<>(a1, d1);

                path = PathFinder.getCorridors(chunk.map, startCoord, endCoord);

                for (Pair<Integer, Integer> coord : path) {
                    chemins.add(coord);
                }

            } else if (a2 > c1 && b2 > d1) {
                path.clear();
                Pair<Integer, Integer> startCoord = new Pair<>(c1, d1), endCoord = new Pair<>(a2, b2);

                path = PathFinder.getCorridors(chunk.map, startCoord, endCoord);

                for (Pair<Integer, Integer> coord : path) {
                    chemins.add(coord);

                }

            }

        } else if (a2 > c1 && d2 < b1) {
            ArrayList candidats = tree.cmpTree(tree.getGauche(), false, true);
            Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> rect1_rect2 = distanceMin(
                    candidats, tree.getDroite());
            tree.getContent();

            _tree1 = rect1_rect2.first();
            _tree2 = rect1_rect2.second();

            a1 = _tree1.first().first();
            b1 = _tree1.first().second();
            c1 = _tree1.second().first();
            d1 = _tree1.second().second();
            a2 = _tree2.first().first();
            b2 = _tree2.first().second();
            c2 = _tree2.second().first();
            d2 = _tree2.second().second();

            path.clear();
            Pair<Integer, Integer> startCoord = new Pair<>(c1, b1), endCoord = new Pair<>(a2, d2);

            path = PathFinder.getCorridors(chunk.map, startCoord, endCoord);

            for (Pair<Integer, Integer> coord : path) {
                chemins.add(coord);
            }

        } else if (c2 < a1 && b2 > d1) {
            ArrayList candidats = tree.cmpTree(tree.getGauche(), false, false);
            Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> rect1_rect2 = distanceMin(
                    candidats, tree.getDroite());

            _tree1 = rect1_rect2.first();
            _tree2 = rect1_rect2.second();

            a1 = _tree1.first().first();
            b1 = _tree1.first().second();
            c1 = _tree1.second().first();
            d1 = _tree1.second().second();
            a2 = _tree2.first().first();
            b2 = _tree2.first().second();
            c2 = _tree2.second().first();
            d2 = _tree2.second().second();

            path.clear();
            Pair<Integer, Integer> endCoord = new Pair<>(a1, d1), startCoord = new Pair<>(c2, b2);

            path = PathFinder.getCorridors(chunk.map, startCoord, endCoord);

            for (Pair<Integer, Integer> coord : path) {
                chemins.add(coord);
            }

        } else if (a2 > c1 && b2 > d1) {
            ArrayList candidats = tree.cmpTree(tree.getGauche(), false, true);
            Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> rect1_rect2 = distanceMin(
                    candidats, tree.getDroite());

            _tree1 = rect1_rect2.first();
            _tree2 = rect1_rect2.second();

            a1 = _tree1.first().first();
            b1 = _tree1.first().second();
            c1 = _tree1.second().first();
            d1 = _tree1.second().second();
            a2 = _tree2.first().first();
            b2 = _tree2.first().second();
            c2 = _tree2.second().first();
            d2 = _tree2.second().second();

            path.clear();
            Pair<Integer, Integer> startCoord = new Pair<Integer, Integer>(c1, d1),
                    endCoord = new Pair<Integer, Integer>(a2, b2);

            path = PathFinder.getCorridors(chunk.map, startCoord, endCoord);

            for (Pair<Integer, Integer> coord : path) {
                chemins.add(coord);

            }

        }

        tree.setContent(new Pair<>(new Pair<>(Math.min(a1, a2), Math.min(b1, b2)),
                new Pair<>(Math.max(c1, c2), Math.max(d1, d2))));

        for (Pair<Integer, Integer> tuile : chemins) {
            if (chunk.isInsideGrid(tuile.first(), tuile.second())) {
                chunk.mapTerrains[tuile.second()][tuile.first()] = Terrains.SOL;
            }
        }
        return;
    }

    /**
     * Fills the entire dungeon with walls terrain.
     */
    public void fillwithwall() {
        for (int i = 0; i < chunk.GRID_HEIGHT; i++) {
            for (int j = 0; j < chunk.GRID_WIDTH; j++) {
                chunk.map[i][j] = new TileModel(Terrains.MUR, null);
                chunk.mapTerrains[i][j] = Terrains.MUR;
            }
        }
    }

    /**
     * Places the dungeon exit in a random room.
     *
     * @param tree The BSP tree representing the dungeon
     */
    public void placeExit(Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> tree) {
        Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> dungeon = tree;
        while (dungeon.getGauche() != null | dungeon.getDroite() != null) {
            if (dungeon.getGauche() != null) {
                dungeon = dungeon.getGauche();
            } else {
                dungeon = dungeon.getDroite();
            }
        }
        int i = RandomTaskManager.nextInt(dungeon.getContent().first().first(),
                dungeon.getContent().second().first() - 1);
        int j = RandomTaskManager.nextInt(dungeon.getContent().first().second(),
                dungeon.getContent().second().second() - 1);

        chunk.mapTerrains[j][i] = Terrains.EXIT;
        chunk.map[j][i] = new TileModel(Terrains.EXIT, null);
        chunk.setLandingPoint(new Pair<>(i, j));
    }

    /**
     * Places a reward in a random room.
     *
     * @param tree The BSP tree representing the dungeon
     */
    public void placeReward(Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> tree) {
        Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> dungeon = tree;
        while (dungeon.getDroite() != null | dungeon.getGauche() != null) {
            if (dungeon.getDroite() != null) {
                dungeon = dungeon.getDroite();
            } else {
                dungeon = dungeon.getGauche();
            }
        }
        int i = RandomTaskManager.nextInt(dungeon.getContent().first().first(),
                dungeon.getContent().second().first() - 1);
        int j = RandomTaskManager.nextInt(dungeon.getContent().first().second(),
                dungeon.getContent().second().second() - 1);
        if (chunk.mapTerrains[j][i] == Terrains.VILLAGE0) {
            placeReward(dungeon);
            return;
        }
        chunk.mapTerrains[j][i] = Terrains.REWARD;
        chunk.map[j][i] = new TileModel(Terrains.REWARD, null);
    }

    /**
     * Calculates the distance between two rectangles (rooms).
     *
     * @param rect1 The first rectangle
     * @param rect2 The second rectangle
     * @return The distance between the two rectangles
     */
    private static double distance(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> rect1,
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> rect2) {
        int x1Min = rect1.first().first();
        int y1Min = rect1.first().second();
        int x1Max = rect1.second().first();
        int y1Max = rect1.second().second();

        int x2Min = rect2.first().first();
        int y2Min = rect2.first().second();
        int x2Max = rect2.second().first();
        int y2Max = rect2.second().second();

        // Distance horizontale (0 si chevauchement)
        int dx = Math.max(0, Math.max(x1Min - x2Max, x2Min - x1Max));
        // Distance verticale (0 si chevauchement)
        int dy = Math.max(0, Math.max(y1Min - y2Max, y2Min - y1Max));

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Recursively searches for the closest room in the tree.
     *
     * @param roomFixe The fixed room to compare against
     * @param tree     The BSP tree to search
     * @param min      The current minimum distance
     * @param roomMin  The current closest room
     * @return A pair containing the closest room and its distance
     */
    private static Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Double> distanceMinRecursive(
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> roomFixe,
            Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> tree,
            double min,
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> roomMin) {

        if (tree == null)
            return new Pair<>(roomMin, min);

        if (tree.getGauche() == null && tree.getDroite() == null) {
            double d = distance(roomFixe, tree.getContent());
            if (d < min)
                return new Pair<>(tree.getContent(), d);
            else
                return new Pair<>(roomMin, min);
        }

        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> bestRoom = roomMin;
        double bestDist = min;

        if (tree.getGauche() != null) {
            Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Double> left = distanceMinRecursive(roomFixe,
                    tree.getGauche(), bestDist, bestRoom);
            if (left.second() < bestDist) {
                bestRoom = left.first();
                bestDist = left.second();
            }
        }

        if (tree.getDroite() != null) {
            Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Double> right = distanceMinRecursive(roomFixe,
                    tree.getDroite(), bestDist, bestRoom);
            if (right.second() < bestDist) {
                bestRoom = right.first();
                bestDist = right.second();
            }
        }

        return new Pair<>(bestRoom, bestDist);
    }

    /**
     * Finds the pair of closest rooms between a list and a tree.
     *
     * @param candidats The list of candidate rooms
     * @param tree      The BSP tree to search
     * @return A pair of the closest rooms
     */
    public static Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> distanceMin(
            ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> candidats,
            Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> tree) {

        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> bestCandidat = null;
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> bestFromTree = null;
        double minDistance = Double.MAX_VALUE;

        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> candidat : candidats) {
            Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Double> res = distanceMinRecursive(candidat,
                    tree, minDistance, null);

            if (res.second() < minDistance) {
                bestCandidat = candidat;
                bestFromTree = res.first();
                minDistance = res.second();
            }
        }

        return new Pair<>(bestCandidat, bestFromTree);
    }

    /**
     * Initializes the dungeon map by generating rooms and corridors.
     */
    public void initMap() {
        Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> initial = new Node<>();

        Pair<Integer, Integer> top_left = new Pair<>(0, 0),
                bottom_right = new Pair<>(chunk.GRID_WIDTH - 1, chunk.GRID_HEIGHT - 1);

        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> rect = new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(
                top_left, bottom_right);

        fillwithwall();

        initial.setContent(rect);

        bsp(initial);

        drawDungonMap(initial);
        placeExit(initial);
        placeReward(initial);
    }
}
