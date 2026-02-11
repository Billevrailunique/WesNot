package model;

import java.util.*;
import model.TileModel.Terrains;
import util.Pair;
import util.PathFinder;
import util.RandomTaskManager;
import util.UnionFind;
import util.Graph.Edge;

public class Caverne {

    private Chunk chunk;
    private final int density = 35;
    private final int nbTurn = 7;
    private final int nbInitialPoint = 30;
    private static final ArrayList<Pair<Integer, Integer>> VOISINS = new ArrayList<>(
            Arrays.asList(
                    new Pair<>(0, 1),
                    new Pair<>(-1, 0),
                    new Pair<>(1, 0),
                    new Pair<>(0, -1),
                    new Pair<>(-1, 1),
                    new Pair<>(1, -1)));
    private ArrayList<Pair<Integer, Integer>> initialPoints;

    private final int height;
    private final int width;
    private boolean[][] checked;

    public Caverne(Chunk chunk) {
        this.chunk = chunk;
        height = chunk.GRID_HEIGHT;
        width = chunk.GRID_WIDTH;
        checked = new boolean[width][height];
    }

    private boolean overpopulated(int i, int j, boolean lastTurn) {
        int nbWall = getTileAround(i, j, Terrains.MOUNTAIN0, chunk);
        boolean isCurrentlyWall = chunk.mapTerrains[i][j] == Terrains.MOUNTAIN0;

        if (!lastTurn)
            return nbWall >= 4;
        return isCurrentlyWall ? nbWall >= 2 : nbWall >= 5;
    }

    private void initialisation() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                chunk.mapTerrains[i][j] = RandomTaskManager.nextInt(0, 99) > density ? Terrains.MOUNTAIN0
                        : Terrains.PLAIN0;

                chunk.map[i][j] = new TileModel(chunk.mapTerrains[i][j], null);
            }
        }
    }

    private void wallingEdges() {
        for (int i = 0; i < width; i++) {
            chunk.mapTerrains[i][0] = Terrains.MOUNTAIN0;
            chunk.mapTerrains[i][height - 1] = Terrains.MOUNTAIN0;
        }
        for (int j = 0; j < height; j++) {
            chunk.mapTerrains[0][j] = Terrains.MOUNTAIN0;
            chunk.mapTerrains[width - 1][j] = Terrains.MOUNTAIN0;
        }
    }

    public static int getTileAround(int i, int j, Terrains type, Chunk chunk) {
        int count = 0;
        for (Pair<Integer, Integer> v : VOISINS) {
            int ni = i + v.first();
            int nj = j + v.second();
            if (chunk.isInsideGrid(ni, nj) && chunk.mapTerrains[nj][ni] == type)
                count++;
        }
        return count;
    }

    private void gen() {
        for (int n = 0; n < nbTurn; n++) {
            wallingEdges();
            Terrains[][] newCavtab = new Terrains[width][height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    newCavtab[i][j] = overpopulated(i, j, (n >= nbTurn - 2)) ? Terrains.MOUNTAIN0 : Terrains.PLAIN0;
                }
            }
            chunk.mapTerrains = newCavtab;
        }
    }

    private Set<Pair<Integer, Integer>> floodfil(Pair<Integer, Integer> point) {
        Set<Pair<Integer, Integer>> border = new HashSet<>();
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();

        if (chunk.mapTerrains[point.first()][point.second()] != Terrains.PLAIN0)
            return border;

        queue.offer(point);
        checked[point.first()][point.second()] = true;

        while (!queue.isEmpty()) {
            Pair<Integer, Integer> current = queue.poll();
            boolean isBorder = false;
            for (Pair<Integer, Integer> v : VOISINS) {
                int ni = current.first() + v.first();
                int nj = current.second() + v.second();
                if (chunk.isInsideGrid(ni, nj) && chunk.mapTerrains[ni][nj] != Terrains.PLAIN0)
                    isBorder = true;
            }
            if (isBorder)
                border.add(current);
            for (Pair<Integer, Integer> v : VOISINS) {
                int ni = current.first() + v.first();
                int nj = current.second() + v.second();
                if (chunk.isInsideGrid(ni, nj) && !checked[ni][nj] && chunk.mapTerrains[ni][nj] == Terrains.PLAIN0) {
                    checked[ni][nj] = true;
                    queue.offer(new Pair<>(ni, nj));
                }
            }
        }
        return border;
    }

    private ArrayList<Set<Pair<Integer, Integer>>> floodfil() {
        initialPoints = new ArrayList<>();
        for (int i = 0; i < nbInitialPoint; i++) {
            initialPoints
                    .add(new Pair<>(RandomTaskManager.nextInt(1, width - 2), RandomTaskManager.nextInt(1, height - 2)));
        }

        ArrayList<Set<Pair<Integer, Integer>>> zones = new ArrayList<>();
        for (Pair<Integer, Integer> point : initialPoints) {
            Set<Pair<Integer, Integer>> zone = floodfil(point);
            if (!zone.isEmpty() && zone.size() > 5)
                zones.add(zone);
        }
        return zones;
    }

    private Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Integer> bruteForceDig(
            Set<Pair<Integer, Integer>> zoneA, Set<Pair<Integer, Integer>> zoneB) {
        int dmin = Integer.MAX_VALUE;
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> points = new Pair<>(null, null);

        for (Pair<Integer, Integer> a : zoneA) {
            for (Pair<Integer, Integer> b : zoneB) {
                int d = (int) Math.sqrt(Math.pow(a.first() - b.first(), 2) + Math.pow(a.second() - b.second(), 2));
                if (d < dmin) {
                    dmin = d;
                    points = new Pair<>(a, b);
                    if (d < 3)
                        return new Pair<>(points, d);
                }
            }
        }
        return new Pair<>(points, dmin);
    }

    private void connectWithKruskal(ArrayList<Edge<Pair<Integer, Integer>>> edges) {
        Collections.sort(edges);

        UnionFind<Pair<Integer, Integer>> uf = new UnionFind<>();
        for (Edge<Pair<Integer, Integer>> e : edges) {
            uf.add(e.from);
            uf.add(e.to);
        }

        for (Edge<Pair<Integer, Integer>> e : edges) {
            if (!uf.connected(e.from, e.to)) {
                uf.union(e.from, e.to);
                digTunnel(e.from, e.to);
            }
        }
    }

    private void digTunnel(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
        List<Pair<Integer, Integer>> path = PathFinder.getPath(chunk.map, from, to);

        ArrayList<Pair<Integer, Integer>> chemins = new ArrayList<>();
        Pair<Integer, Integer> tuile = new Pair<Integer, Integer>(from.first(), from.second());

        for (Pair<Integer, Integer> coord : path) {
            chemins.add(coord);
        }

        for (Pair<Integer, Integer> point : chemins) {
            if (chunk.isInsideGrid(point.first(), point.second())) {
                chunk.mapTerrains[point.first()][point.second()] = Terrains.PLAIN0;
            }
        }
    }

    public void initMap() {
        initialisation();
        gen();
        ArrayList<Set<Pair<Integer, Integer>>> areas = floodfil();

        ArrayList<Edge<Pair<Integer, Integer>>> edges = new ArrayList<>();
        for (int i = 0; i < areas.size(); i++) {
            for (int j = 0; j < i; j++) {
                var result = bruteForceDig(areas.get(i), areas.get(j));
                edges.add(new Edge<>(result.first().first(), result.first().second(), result.second()));
            }
        }

        connectWithKruskal(edges);
        // printMap();
        placeExit();
    }

    private void placeExit() {
        Pair<Integer, Integer> pos = new Pair<Integer, Integer>(0, 0);
        while (chunk.mapTerrains[pos.second()][pos.first()] != Terrains.PLAIN0) {
            pos.setFirst(RandomTaskManager.nextInt(0, width - 1));
            pos.setSecond(RandomTaskManager.nextInt(0, height - 1));
        }
        chunk.mapTerrains[pos.second()][pos.first()] = Terrains.EXIT;
        chunk.setLandingPoint(pos);
    }

    public final void printMap() {
        for (Terrains[] row : chunk.mapTerrains) {
            for (Terrains cell : row) {
                //System.out.print(cell.toString().charAt(0) + " ");
            }
            //System.out.println();
        }
    }
}
