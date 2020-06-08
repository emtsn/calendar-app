package utilities;

import java.awt.*;

public final class ColorUtility {
    public static final Color COLOR_1 = new Color(255, 89, 94, 255);
    public static final Color COLOR_2 = new Color(255, 202, 58,255);
    public static final Color COLOR_3 = new Color(138, 201, 38,255);
    public static final Color COLOR_4 = new Color(25, 130, 196,255);
    public static final Color COLOR_5 = new Color(106, 76, 147,255);

    public static final Color[] DEFAULT_COLORS = new Color[]{
            COLOR_1, COLOR_2, COLOR_3, COLOR_4, COLOR_5 };
    public static final String[] DEFAULT_COLOR_STRINGS = new String[]{
            "Red", "Yellow", "Green", "Blue", "Purple" };

    // EFFECTS: returns a string for the color
    public static String colorToString(Color color) {
        for (int i = 0; i < DEFAULT_COLORS.length; i++) {
            if (color.equals(DEFAULT_COLORS[i])) {
                return DEFAULT_COLOR_STRINGS[i];
            }
        }
        return "Color";
    }

    // EFFECTS: returns a color from the string, returns defaultColor if none match
    public static Color stringToColor(String string, Color defaultColor) {
        for (int i = 0; i < DEFAULT_COLOR_STRINGS.length; i++) {
            if (string.equals(DEFAULT_COLOR_STRINGS[i])) {
                return DEFAULT_COLORS[i];
            }
        }
        return defaultColor;
    }
}
