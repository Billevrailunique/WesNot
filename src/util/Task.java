package util;


/**
 * La classe Task représente une tâche à exécuter après un certain délai.
 * Elle contient un temps d'attente et une action à effectuer une fois ce délai
 * écoulé.
 */
public class Task {
    private int time; // Temps restant avant l'exécution de la tâche (en unité de l'intervalle de
                      // temps du jeu)
    private final Runnable task; // L'action à exécuter après le délai

    /**
     * Constructeur de la classe Task.
     * Initialise une tâche avec un délai en secondes et l'action à exécuter.
     *
     * @param time Le délai avant d'exécuter la tâche, en secondes.
     * @param task L'action à exécuter.
     */
    public Task(double time, Runnable task) {
        // Convertit le temps en secondes en fonction de l'intervalle de temps du jeu
        this.time = (int) (time / (Math.pow(10, -3) * Timeline.getIntervale()));
        this.task = task;
    }

    /**
     * Récupère l'action associée à la tâche.
     *
     * @return L'action à exécuter.
     */
    public Runnable getTask() {
        return this.task;
    }

    /**
     * Récupère le temps restant avant l'exécution de la tâche.
     *
     * @return Le temps restant avant l'exécution, en unités de l'intervalle de
     *         temps du jeu.
     */
    public int getTime() {
        return time;
    }

    /**
     * Définit un nouveau temps restant avant l'exécution de la tâche.
     * Le temps est donné en secondes, et il est converti en unités de l'intervalle
     * de temps du jeu.
     *
     * @param time Le nouveau temps restant, en secondes.
     */
    public void setTime(int time) {
        // Convertit le temps en secondes en fonction de l'intervalle de temps du jeu
        this.time = (int) (time / (Math.pow(10, -3) * Timeline.getIntervale()));
    }

    /**
     * Réduit le temps restant avant l'exécution de la tâche de 1 unité.
     */
    public void decreaseTime() {
        time--;
    }

    /**
     * Récupère le temps restant avant l'exécution de la tâche, en secondes.
     *
     * @return Le temps restant en secondes.
     */
    public int getTimeInSecond() {
        return (int) (time * (Math.pow(10, -3) * Timeline.getIntervale()));
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de la tâche.
     *
     * @return Une chaîne représentant la tâche, incluant le temps restant.
     */
    @Override
    public String toString() {
        return time + " ";
    }
}