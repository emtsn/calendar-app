package utilities;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class ColorUtilityTest {

    @Test
    void testColorToString() {
        for (int i = 0; i < ColorUtility.DEFAULT_COLORS.length; i++) {
            assertEquals(ColorUtility.DEFAULT_COLOR_STRINGS[i],
                    ColorUtility.colorToString(ColorUtility.DEFAULT_COLORS[i]));
        }
        assertEquals("Color", ColorUtility.colorToString(new Color(1,1,1,1)));
    }

    @Test
    void testStringToColor() {
        for (int i = 0; i < ColorUtility.DEFAULT_COLOR_STRINGS.length; i++) {
            assertEquals(ColorUtility.DEFAULT_COLORS[i],
                    ColorUtility.stringToColor(ColorUtility.DEFAULT_COLOR_STRINGS[i], Color.BLACK));
        }
        assertEquals(Color.BLACK, ColorUtility.stringToColor("", Color.BLACK));
    }

}
