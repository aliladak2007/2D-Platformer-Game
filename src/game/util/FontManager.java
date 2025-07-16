package game.util;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code FontManager} class is responsible for loading and managing
 * custom fonts for the game. It caches and provides methods to derive fonts
 * at different sizes and styles.
 * <p>
 * This utility class implements the Singleton pattern with static methods and
 * a private cache. It preloads commonly used fonts at startup and provides
 * convenient methods to access them throughout the application.
 * </p>
 * <p>
 * Key features:
 * <ul>
 *   <li>Font caching to improve performance</li>
 *   <li>Automatic font registration with the system</li>
 *   <li>Fallback to system fonts if custom fonts fail to load</li>
 *   <li>Convenience methods for common font operations</li>
 * </ul>
 * </p>
 * <p>
 * Preloaded fonts:
 * <ul>
 *   <li>"default" - A pixel-style font (myPixelFont.ttf)</li>
 *   <li>"emulogic" - An arcade-style font (Emulogic.ttf)</li>
 * </ul>
 * </p>
 */
public class FontManager {
    /**
     * A cache that stores loaded fonts by their alias names.
     * <p>
     * This static map prevents redundant font loading and improves performance
     * by storing font references for quick retrieval.
     * </p>
     */
    private static final Map<String, Font> fontCache = new HashMap<>();

    /**
     * Static initializer block that preloads commonly used fonts when the class is first loaded.
     * <p>
     * This ensures that the default pixel font and emulogic font are immediately
     * available for use throughout the application without explicit loading.
     * </p>
     */
    static {
        // Preload default pixel font
        loadFont("myPixelFont.ttf", "default");
        loadFont("Emulogic.ttf", "emulogic");
    }

    /**
     * Loads a font from the data folder and stores it with a given alias.
     * <p>
     * This method:
     * <ol>
     *   <li>Attempts to create a font from the specified TTF file</li>
     *   <li>Registers the font with the local graphics environment</li>
     *   <li>Stores the font in the cache using the provided alias</li>
     * </ol>
     * </p>
     * <p>
     * If the font loading fails, an error message is printed to the standard error stream,
     * but no exception is thrown to prevent application crashes.
     * </p>
     *
     * @param filename Name of the .ttf file located in the data folder.
     *                 The file is expected to be in the "data/" directory.
     * @param alias    Logical name to retrieve the font later.
     *                 This alias is used as the key in the font cache.
     */
    public static void loadFont(String filename, String alias) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File("data/" + filename));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            fontCache.put(alias, font);
        } catch (Exception e) {
            System.err.println("Error loading font " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Returns a derived font using the specified alias, style, and size.
     * <p>
     * This method retrieves a base font from the cache using the provided alias,
     * then derives a new font with the requested style and size. If the requested
     * font is not found in the cache, a fallback Courier New font is returned.
     * </p>
     * <p>
     * Font styles are defined in the {@link Font} class:
     * <ul>
     *   <li>{@link Font#PLAIN} - Plain font style</li>
     *   <li>{@link Font#BOLD} - Bold font style</li>
     *   <li>{@link Font#ITALIC} - Italic font style</li>
     * </ul>
     * These can be combined with bitwise OR, e.g., {@code Font.BOLD | Font.ITALIC}
     * </p>
     *
     * @param alias Font alias name, such as "default" or "emulogic".
     * @param style Font style (e.g., {@link Font#BOLD}, {@link Font#ITALIC}).
     * @param size  Font size in points.
     * @return The derived font, or fallback Courier New if the requested font is not found.
     */
    public static Font getFont(String alias, int style, float size) {
        Font font = fontCache.get(alias);
        if (font != null) {
            return font.deriveFont(style, size);
        } else {
            return new Font("Courier New", style, (int) size); // fallback
        }
    }

    /**
     * Convenience method for retrieving a font with plain style.
     * <p>
     * This is equivalent to calling {@code getFont(alias, Font.PLAIN, size)}.
     * </p>
     *
     * @param alias Font alias name.
     * @param size  Font size in points.
     * @return The derived font with plain style, or fallback if not found.
     */
    public static Font getFont(String alias, float size) {
        return getFont(alias, Font.PLAIN, size);
    }

    /**
     * Shortcut for getting the default font (myPixelFont) with specified style and size.
     * <p>
     * This is equivalent to calling {@code getFont("default", style, size)}.
     * </p>
     *
     * @param style Font style (e.g., {@link Font#BOLD}, {@link Font#ITALIC}).
     * @param size  Font size in points.
     * @return The default font with the specified style and size.
     */
    public static Font getDefaultFont(int style, float size) {
        return getFont("default", style, size);
    }

    /**
     * Shortcut for getting the default font (myPixelFont) with plain style.
     * <p>
     * This is equivalent to calling {@code getDefaultFont(Font.PLAIN, size)}.
     * </p>
     *
     * @param size Font size in points.
     * @return The default font with plain style and the specified size.
     */
    public static Font getDefaultFont(float size) {
        return getDefaultFont(Font.PLAIN, size);
    }
}