package util;

import javax.swing.*;

import view.GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Cette classe représente un panneau d'interface utilisateur qui agit comme une
 * fenêtre superposée ("overlay").
 * Elle est autonome et combine à la fois la vue et le contrôleur, sans modèle
 * associé.
 *
 * 
 * Le panneau superposé est conçu pour afficher un contenu semi-transparent
 * qui peut être fermé à l'aide d'un bouton situé dans l'en-tête.
 * Il est initialisé avec un cadre parent (GUI) et un composant de contenu.
 * 
 */
public class OverlayPanel extends JPanel {
    private final JPanel headerPanel;
    private final JButton closeButton;
    private Component contentPanel;

    /**
     * Constructeur pour initialiser un panneau superposé.
     *
     * @param parentFrame le cadre principal de l'application.
     * @param content     le composant à afficher dans le panneau superposé.
     */
    public OverlayPanel(GUI parentFrame, Component content) {
        // Initialisation
        contentPanel = content;
        parentFrame.isOverlayOn = true;

        // Taille et disposition du panneau principal
        setSize(parentFrame.getWidth(), parentFrame.getHeight());
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 150)); // Fond semi-transparent

        // Panneau d'en-tête
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(parentFrame.getWidth() / 2, 40));
        headerPanel.setBackground(new Color(50, 50, 50)); // Couleur de l'en-tête

        // Bouton "X" pour fermer le panneau superposé
        closeButton = new JButton("X");
        closeButton.setFocusable(false);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setForeground(Color.WHITE);

        // Ajouter une action au bouton pour fermer le panneau superposé
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.hideOverlayPanel();
            }
        });

        // Ajouter le bouton "X" au bord droit de l'en-tête
        headerPanel.add(closeButton, BorderLayout.EAST);

        // Ajouter un titre centré
        JLabel title = new JLabel(content.getClass().getSimpleName());
        title.setForeground(Color.WHITE); // Texte blanc
        headerPanel.add(title, BorderLayout.WEST);

        // Configurer le panneau de contenu
        contentPanel.setPreferredSize(new Dimension(parentFrame.getWidth() / 2, parentFrame.getHeight() / 2));
        contentPanel.setBackground(Color.LIGHT_GRAY);

        // Ajouter headerPanel et contentPanel au panneau principal
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Méthode pour modifier dynamiquement le contenu affiché dans le panneau
     * superposé.
     *
     * @param newPanel le nouveau panneau à afficher.
     */
    public void setContentPanel(Component newPanel) {
        remove(contentPanel); // Supprimer l'ancien contenu
        contentPanel = newPanel; // Mettre à jour avec le nouveau contenu
        add(contentPanel, BorderLayout.CENTER); // Ajouter au centre
        revalidate();
        repaint();
    }
}
