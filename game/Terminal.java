package game;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//import game.Player;

/**
 * Terminal — entry point and main game loop for the TEST proof-of-concept.
 *
 * Out-of-combat commands:
 *   move <dir> / go <dir>   — move to an adjacent room
 *   north/south/east/west   — shorthand movement (also n/s/e/w)
 *   examine / look          — describe the current room
 *   status / stat           — show health, weapon, ammo
 *   help                    — list commands
 *   quit / exit             — end the session
 *
 * In-combat commands (handled in Combat.java):  S)hoot  R)eload  F)lee
 */
public class Terminal {

    // The world: room name -> Room object
    private static final Map<String, Room> world = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printBanner();

        System.out.print("Enter your operative's name: ");
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) playerName = "Operative";

        Player player = new Player(playerName);
        buildWorld();

        System.out.println("\nWelcome, " + player.getName() + ".");
        System.out.println("Your extraction point is on the far side of the industrial zone.");
        System.out.println("Reach it alive. Type HELP for commands.\n");

        Room current = world.get(player.getCurrentRoom());
        describeRoom(current);

        // ── Main game loop ───────────────────────────────────────────────────
        while (player.isAlive()) {

            // Win condition: reached the extraction point
            if (player.getCurrentRoom().equals("Extraction Point")) {
                System.out.println("\n╔══════════════════════════════════════╗");
                System.out.println("║   EXTRACTION SUCCESSFUL. You live.   ║");
                System.out.println("╚══════════════════════════════════════╝");
                break;
            }

            System.out.print("\n> ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) continue;

            String[] parts = raw.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1].toLowerCase() : "";

            current = world.get(player.getCurrentRoom()); // refresh

            switch (cmd) {
                case "help" -> printHelp();
                case "examine", "look" -> describeRoom(current);
                case "status", "stat" -> System.out.println("\n" + player.statusString());

                case "move", "go" -> {
                    if (arg.isEmpty()) System.out.println("Move where? Try: move north");
                    else handleMove(player, current, arg, scanner);
                }

                case "north", "south", "east", "west", "n", "s", "e", "w" ->
                    handleMove(player, current, expandDirection(cmd), scanner);

                case "quit", "exit" -> {
                    System.out.println("\nSession ended. Stay frosty.\n");
                    scanner.close();
                    return;
                }

                default -> System.out.println("Unknown command. Type HELP for a list.");
            }
        }

        if (!player.isAlive()) {
            System.out.println("\nGame over. The zone claims another operative.");
        }

        scanner.close();
    }

    // ── Movement & encounter trigger ─────────────────────────────────────────

    private static void handleMove(Player player, Room current, String direction, Scanner scanner) {
        Map<String, String> exits = current.getExits();
        if (!exits.containsKey(direction)) {
            System.out.println("You can't go that way.");
            return;
        }

        Room nextRoom = world.get(exits.get(direction));
        if (nextRoom == null) {
            System.out.println("That path leads nowhere (bug: room not found).");
            return;
        }

        player.setCurrentRoom(nextRoom.getName());
        System.out.println("\nYou move " + direction + " into " + nextRoom.getName() + ".");
        System.out.println(nextRoom.getAtmosphere());

        if (nextRoom.hasEnemy()) {
            Enemy enemy = Enemy.createScav();
            boolean survived = Combat.fight(player, enemy, scanner);
            if (survived) {
                nextRoom.clearEnemy();
                System.out.println("Area secure. You catch your breath.\n");
                describeRoom(nextRoom);
            }
            // If the player died, the main loop's isAlive() check ends the game.
        } else {
            System.out.println("The area seems quiet. For now.\n");
            describeRoom(nextRoom);
        }
    }

    // ── Room description ─────────────────────────────────────────────────────

    private static void describeRoom(Room room) {
        System.out.println("\n── " + room.getName() + " ──────────────────────────────");
        System.out.println(room.getDescription());
        System.out.println("\nExits:");
        System.out.println(room.exitsString());
        if (room.hasEnemy()) {
            System.out.println("\n[!] Something doesn't feel right here...");
        }
    }

    // ── World builder ────────────────────────────────────────────────────────
    //
    //   Checkpoint --E--> Warehouse --E--> Loading Bay --E--> Extraction Point
    //                        |
    //                        S
    //                        |
    //                  Collapsed Office

    private static void buildWorld() {
        Room checkpoint = new Room("Checkpoint",
            "A crumbling concrete guardhouse at the edge of the industrial zone.\n" +
            "Sandbags and rusted barbed wire line the windows. An overturned security\n" +
            "desk rots in the corner. The smell of cordite still hangs in the air.",
            "You step through a gap in the perimeter wall.",
            "Customs", "Day");
        checkpoint.clearEnemy(); // starting room always safe

        Room warehouse = new Room("Warehouse",
            "A massive corrugated-steel building. Rows of broken shelving stretch into\n" +
            "the shadows. Crates stamped with Cyrillic text are stacked against the far\n" +
            "wall. A rusted forklift sits abandoned near the loading dock.",
            "The heavy door groans as you push it open.",
            "Customs", "Day");

        Room loadingBay = new Room("Loading Bay",
            "Concrete docks, now empty. Skid marks and old oil stains cover the floor.\n" +
            "A chain-link fence separates this area from an overgrown yard. The\n" +
            "extraction road is visible through the wire — almost there.",
            "Cold air hits you under the loading canopy.",
            "Customs", "Day");

        Room extraction = new Room("Extraction Point",
            "A dirt road leading out of the industrial zone. A faded orange flare marks\n" +
            "the pickup spot.",
            "You reach the extraction point.",
            "Customs", "Day");
        extraction.clearEnemy(); // extraction always clear

        Room office = new Room("Collapsed Office",
            "What was once an administrative building. The upper floor has caved in,\n" +
            "leaving a maze of broken furniture and exposed rebar. Scav graffiti covers\n" +
            "every surface. Loot may be hidden here — or trouble.",
            "You squeeze through a busted-out window.",
            "Customs", "Day");

        checkpoint.addExit("east", "Warehouse");

        warehouse.addExit("west", "Checkpoint");
        warehouse.addExit("east", "Loading Bay");
        warehouse.addExit("south", "Collapsed Office");

        loadingBay.addExit("west", "Warehouse");
        loadingBay.addExit("east", "Extraction Point");

        office.addExit("north", "Warehouse");

        world.put("Checkpoint",       checkpoint);
        world.put("Warehouse",        warehouse);
        world.put("Loading Bay",      loadingBay);
        world.put("Extraction Point", extraction);
        world.put("Collapsed Office", office);
    }

    // ── UI helpers ───────────────────────────────────────────────────────────

    private static void printBanner() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  TACTICAL EXTRACTION SHOOTER — TEXT EDITION   ║");
        System.out.println("║                PROOF OF CONCEPT               ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    private static void printHelp() {
        System.out.println("\n── COMMANDS ─────────────────────────────────────");
        System.out.println("  examine / look           describe current area");
        System.out.println("  move <dir> / go <dir>    move in a direction");
        System.out.println("  north / south / east / west   (or n/s/e/w)");
        System.out.println("  status / stat            show health & weapon");
        System.out.println("  quit / exit              end the session");
        System.out.println("  [In combat:  S)hoot   R)eload   F)lee ]");
        System.out.println("─────────────────────────────────────────────────");
    }

    private static String expandDirection(String cmd) {
        return switch (cmd) {
            case "n" -> "north";
            case "s" -> "south";
            case "e" -> "east";
            case "w" -> "west";
            default  -> cmd;
        };
    }
}