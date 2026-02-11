package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.JFrame;
import util.*;
import view.DialogView;

/**
 * The DialogManager class handles the display and management of dialogue
 * sequences in the game.
 * It reads dialogue from a file, manages the timing of text display, and
 * controls the dialogue view.
 */
public class DialogManager {
    private DialogView view;
    public ArrayList<Integer> turns;
    private BufferedReader br;
    private Task t;
    private int scene;
    private int ligne;
    private int colonne;
    private ArrayList<Integer> readLines = new ArrayList<>();
    private ArrayList<ArrayList<Dialogue>> messages;
    private boolean inTimeline;
    private String s1 = "<html><div style='width: 900px;'><span style='font-family: Arial;color: black; font-weight: bold'>";
    private String s2 = "</span></div></html>";

    /**
     * Constructs a new DialogManager with the specified file path and parent frame.
     * 
     * @param filepath the path to the dialogue text file
     * @param frame    the parent JFrame for the dialogue view
     */
    public DialogManager(String filepath, JFrame frame) {
        view = new DialogView(frame);
        turns = new ArrayList<>();
        messages = new ArrayList<>();
        scene = 0;
        ligne = 0;
        colonne = 0;
        turns.add(1);
        turns.add(5);
        try {
            br = new BufferedReader(new FileReader(filepath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadDialogs();
        t = new Task(1 / 2, () -> {
            view.getTextLabel().setText(s1
                    + messages.get(scene).get(ligne).line.substring(0, colonne + 1)
                    + s2);
            view.updateSpeaker(messages.get(scene).get(ligne).speaker);
            colonne++;

            if (colonne != messages.get(scene).get(ligne).line.length()) {
                Timeline.add(t);
            } else {
                inTimeline = false;
            }
        });
        view.setNextButtonListener(new NextButtonListener());
        view.setSkipButtonListener(new SkipButtonListener());
    }

    /**
     * Loads dialogues from the text file and organizes them by scene.
     * Each line in the file should be formatted as "dialogueID#speaker#text".
     */
    public final void loadDialogs() {
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] s = line.split("#");
                Dialogue d = new Dialogue(Integer.parseInt(s[0]), s[1], s[2]);

                if (messages.isEmpty() || d.dialogueID != messages.getLast().get(0).dialogueID) {
                    messages.add(new ArrayList<>());
                }
                messages.getLast().add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ActionListener for the Next button in the dialogue view.
     * Handles advancing to the next line or scene of dialogue.
     */
    private class NextButtonListener implements ActionListener {
        /**
         * Advances the dialogue when the Next button is clicked.
         * 
         * @param e the ActionEvent triggered by the button click
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (messages.get(scene).get(ligne).line.length() == colonne) {
                if (messages.get(scene).size() - 1 > ligne) {
                    colonne = 0;
                    ligne++;
                    if (!inTimeline) {
                        Timeline.add(t);
                        inTimeline = true;
                    }
                } else {
                    readLines.add(scene);
                    colonne = 0;
                    ligne = 0;
                    scene++;
                    view.closeDialog();
                }
            } else {
                view.getTextLabel().setText(s1 + messages.get(scene).get(ligne).line
                        + s2);
                colonne = messages.get(scene).get(ligne).line.length() - 1;
            }
        }
    }

    private class SkipButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //System.out.println("skip");
            readLines.add(scene);
            colonne = 0;
            ligne = 0;
            scene++;
            view.closeDialog();
        }
    }

    /**
     * Displays the dialogue for the specified turn if it exists in the turns list.
     * 
     * @param turn the game turn number to display dialogue for
     */
    public void displayDialogue(int turn) {
        if (turns.contains(turn) && !readLines.contains(turns.indexOf(turn))) {
            this.colonne = 0;
            this.ligne = 0;
            this.scene = turns.indexOf(turn);
            if (!inTimeline) {
                Timeline.add(t);
                inTimeline = true;
            }
            view.showDialog();
        }
    }

    /**
     * Returns the DialogView associated with this manager.
     * 
     * @return the DialogView instance
     */
    public DialogView getView() {
        return view;
    }

    /**
     * Inner class representing a single dialogue entry.
     */
    private class Dialogue {
        /** The ID of the dialogue scene */
        int dialogueID;
        /** The name of the speaker */
        String speaker;
        /** The dialogue text */
        String line;

        /**
         * Constructs a new Dialogue entry.
         * 
         * @param id      the dialogue scene ID
         * @param speaker the name of the speaker
         * @param line    the dialogue text
         */
        private Dialogue(int id, String speaker, String line) {
            this.dialogueID = id;
            this.speaker = speaker;
            this.line = line;
        }
    }
}