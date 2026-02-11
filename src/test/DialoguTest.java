package test;

import controller.DialogManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DialoguTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("test dialogue");
        JPanel panel = new JPanel();

        panel.setBackground(Color.BLUE);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        try {
            DialogManager dialogue = new DialogManager("ressource/dialog.txt", frame);
            dialogue.loadDialogs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
