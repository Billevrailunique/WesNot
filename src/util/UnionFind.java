package util;

import java.util.HashMap;
import java.util.Map;

public class UnionFind<T> {
    private final Map<T, T> parent = new HashMap<>();

    // Initialisation : chaque élément est son propre parent
    public void makeSet(T x) {
        parent.putIfAbsent(x, x);
    }

    public void add(T item) {
        parent.putIfAbsent(item, item);
    }

    // Trouve le représentant de l'ensemble de x avec compression de chemin
    public T find(T x) {
        if (!parent.containsKey(x)) {
            parent.put(x, x);
        }
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent.get(x))); // Compression de chemin
        }
        return parent.get(x);
    }

    // Fusionne les ensembles de x et y
    public void union(T x, T y) {
        T rootX = find(x);
        T rootY = find(y);
        if (!rootX.equals(rootY)) {
            parent.put(rootX, rootY); // ou l'inverse
        }
    }

    // Vérifie si deux éléments sont dans le même ensemble
    public boolean connected(T x, T y) {
        return find(x).equals(find(y));
    }
}
