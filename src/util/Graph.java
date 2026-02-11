package util;

import java.util.*;

public class Graph<T> {
    public static class Edge<T> implements Comparable<Edge<T>> {
        public T from, to;
        public Integer weight;

        public Edge(T from, T to, Integer weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge<T> other) {
            return Integer.compare(this.weight, other.weight);
        }

        @Override
        public String toString() {
            return "(" + from + " <-> " + to + ", poids=" + weight + ")";
        }
    }

    private final List<T> sommets;
    private final List<Edge<T>> arretes;
    private final List<List<Integer>> adj;

    public Graph() {
        sommets = new ArrayList<>();
        arretes = new ArrayList<>();
        adj = new ArrayList<>();
    }

    public void addSommet(T sommet) {
        sommets.add(sommet);
        for (List<Integer> row : adj) {
            row.add(null); // Nouvelle colonne
        }
        List<Integer> newRow = new ArrayList<>(Collections.nCopies(sommets.size(), null));
        adj.add(newRow); // Nouvelle ligne
    }

    public void link(T a, T b, int poids) {
        int idA = sommets.indexOf(a);
        int idB = sommets.indexOf(b);

        if (idA == -1 || idB == -1)
            throw new IllegalArgumentException("Sommet non trouvé");
        if (adj.get(idA).get(idB) != null)
            throw new IllegalStateException("Arête déjà existante");

        adj.get(idA).set(idB, poids);
        adj.get(idB).set(idA, poids);
        arretes.add(new Edge<>(a, b, poids));
    }

    public void setPoid(T a, T b, int poids) {
        int idA = sommets.indexOf(a);
        int idB = sommets.indexOf(b);
        if (idA == -1 || idB == -1)
            throw new IllegalArgumentException("Sommet non trouvé");

        if (adj.get(idA).get(idB) != null) {
            adj.get(idA).set(idB, poids);
            adj.get(idB).set(idA, poids);
            for (Edge<T> e : arretes) {
                if ((e.from.equals(a) && e.to.equals(b)) || (e.from.equals(b) && e.to.equals(a))) {
                    e.weight = poids;
                    break;
                }
            }
        } else {
            link(a, b, poids);
        }
    }

    public List<T> getSommets() {
        return sommets;
    }

    public List<Edge<T>> getArretes() {
        return arretes;
    }

    public Integer getPoid(T a, T b) {
        int idA = sommets.indexOf(a);
        int idB = sommets.indexOf(b);
        return (idA >= 0 && idB >= 0) ? adj.get(idA).get(idB) : null;
    }
}
