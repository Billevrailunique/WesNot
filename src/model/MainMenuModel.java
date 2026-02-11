package model;

import java.awt.*;

/**
 * The MainMenuModel class represents the data and properties for the main menu screen.
 * It manages the menu dimensions, title, and background image path.
 */
public class MainMenuModel {

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private String pageName = "WesNot";
    private int window_width = screenSize.width;
    private int window_height = screenSize.height;
    private String imagePath = "ressource/images/backgrounds/background.png";

    /**
     * Constructs a new MainMenuModel with default values.
     * Automatically sets dimensions based on the current screen size.
     */
    public MainMenuModel() {
    }

    /**
     * Returns the title of the main menu page.
     * 
     * @return the page title as a String
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Returns the height of the menu window.
     * 
     * @return the window height in pixels
     */
    public int getWindow_height() {
        return window_height;
    }

    /**
     * Returns the width of the menu window.
     * 
     * @return the window width in pixels
     */
    public int getWindow_width() {
        return window_width;
    }

    /**
     * Returns the path to the background image.
     * 
     * @return the file path to the background image
     */
    public String getImagePath() {
        return imagePath;
    }
}