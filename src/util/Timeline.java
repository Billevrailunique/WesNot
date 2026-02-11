package util;

import java.util.LinkedList;
import javax.swing.*;

public class Timeline {
    private static final LinkedList<Task> tasklist = new LinkedList<>(); // la liste de tache
    private static final LinkedList<Task> toAdd = new LinkedList<>();    
    private static final int interval = 125; // Intervalle en millisecondes 4 fois par seconde
    private static boolean isRunning = false;
    private static final Timer timer = new Timer(interval, e -> run());

    /**
     * run is the main function. She is call every $interval ms and manage every
     * task
     */
    public static void run() {
        LinkedList<Task> toRemove = new LinkedList<>();
        for (Task runnable : tasklist) {
            if (runnable.getTime() == 0) {
                runnable.getTask().run();
                toRemove.add(runnable);
            } else {
                runnable.decreaseTime();
            }
        }
        // Evite les modifications en même temps qu'une lecture
        for (Task rm : toRemove) {
            tasklist.remove(rm);
        }
        // Evite les modifications en même temps qu'une lecture
        for (Task add : toAdd) {
            tasklist.add(add);
        }
        toAdd.clear();
    }

    public static void add(Task task) {
        // gestion en différer pour ne pas "casser" les noeuds de la liste iterative
        toAdd.add(task);
        // si le timer n'a pas été lancer
        if (!isRunning) {
            timer.start();
            isRunning = true;
        }
    }

    public static int getIntervale() {
        return interval;
    }

}