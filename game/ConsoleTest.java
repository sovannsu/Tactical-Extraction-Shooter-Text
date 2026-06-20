package game;

import java.util.Scanner;

/**
 * ConsoleTest — a TEMPORARY driver that lets you play the new Game controller
 * from the console, before the Swing window exists.
 *
 * This is the structure-first verification step: if the game plays correctly
 * here, the controller is sound, and the GUI becomes pure wiring. Notice how
 * thin this is — it just reads a line and calls the matching Game method based
 * on the mode. Your Swing button handlers will look almost identical, minus the
 * read loop (a click replaces a typed line).
 *
 * Run from the directory CONTAINING the game/ folder:
 *     javac game/*.java
 *     java game.ConsoleTest
 *
 * You can delete this file once the window is working — or keep it around as a
 * fast, GUI-free way to test game logic.
 */
public class ConsoleTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your operative's name: ");
        String name = scanner.nextLine().trim();

        // The output sink: a method reference straight to the console.
        // The GUI will pass a different one-liner that appends to a JTextArea.
        GameOutput out = System.out::println;

        Game game = new Game(name, out);
        game.start();

        while (game.getMode() != Mode.DEAD && game.getMode() != Mode.WON) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("quit") || input.equals("exit")) break;

            switch (game.getMode()) {
                case EXPLORING -> {
                    switch (input) {
                        case "examine", "look" -> game.examine();
                        case "status", "stat"  -> game.status();
                        case "n", "north"      -> game.move("north");
                        case "s", "south"      -> game.move("south");
                        case "e", "east"       -> game.move("east");
                        case "w", "west"       -> game.move("west");
                        default -> System.out.println("Try: n/s/e/w, examine, status, quit");
                    }
                }
                case IN_COMBAT -> {
                    switch (input) {
                        case "s", "shoot"  -> game.combatShoot();
                        case "r", "reload" -> game.combatReload();
                        case "f", "flee"   -> game.combatFlee();
                        default -> System.out.println("In combat: s)hoot  r)eload  f)lee");
                    }
                }
                default -> { /* DEAD / WON handled by the while condition */ }
            }
        }

        System.out.println("\nSession ended. Stay frosty.");
        scanner.close();
    }
}