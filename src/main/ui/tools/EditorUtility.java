package ui.tools;

import java.util.Scanner;

public class EditorUtility {
    private static int titleLength = 40;
    private static Scanner scanner = new Scanner(System.in);

    // EFFECTS: asks the user for input and returns the input string
    public static String askString(String askText) {
        System.out.println(askText);
        return scanner.nextLine();
    }

    // EFFECTS: asks the user for input and returns the input int
    public static int askInt(String askText) {
        System.out.println(askText);
        int i = scanner.nextInt();
        scanner.nextLine();
        return i;
    }

    // EFFECTS: prints the title with borders
    public static void displayTitle(String title) {
        int titleDash = (titleLength - title.length()) / 2;
        String displayText = "";
        displayText += dashes(titleDash);
        displayText += title;
        displayText += dashes(titleDash);
        if (displayText.length() % 2 != 0) {
            displayText += "-";
        }
        System.out.println(displayText);
    }

    // EFFECTS: returns string of count dashes
    private static String dashes(int count) {
        String dashes = "";
        for (int i = 0; i < count; i++) {
            dashes += "-";
        }
        return dashes;
    }

    // EFFECTS: prints closing title
    public static void displayClose() {
        displayTitle("");
    }
}
