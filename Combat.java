package game;

/**
 * Handles a single combat encounter between the Player and one Enemy.
 * Combat is turn-based:
 *   1. Player shoots (or reloads, or tries to flee).
 *   2. Enemy returns fire if still alive.
 *   3. Repeat until one side is down or the player flees.
 *
 * The enemy's aim modifier (0.0 – 1.0) controls how often their shots connect.
 * Scavs get a low aim value so they miss frequently; bosses get a high one.
 */
public class Combat {

    /**
     * Run a full fight. Returns true if the player survived, false if they died.
     * All output goes to System.out so Terminal.java stays clean.
     */
    public static boolean fight(Player player, Enemy enemy, java.util.Scanner scanner) {
        System.out.println("\n════════════════════════════════════════════");
        System.out.println("  CONTACT! " + enemy.getName() + " opens fire!");
        System.out.println("  Type:    " + enemy.getEnemyType() + " | Faction: " + enemy.getFaction());
        System.out.println("  Weapon:  " + weaponLine(enemy));
        System.out.println("  HP:      " + (int) enemy.getTotalHealth());
        System.out.println("════════════════════════════════════════════\n");

        // Scavs aim poorly (~30% hit rate). Set a baseline if aim wasn't assigned.
        double enemyAim = enemy.getAim() > 0 ? enemy.getAim() : defaultAim(enemy);
        double enemyHealth = enemy.getTotalHealth();
        boolean playerFled = false;

        while (enemy.getTotalHealth() > 0 && player.isAlive() && !playerFled) {
            System.out.println(player.statusString());
            System.out.println("Enemy HP: " + (int) enemy.getTotalHealth());
            System.out.println("\nWhat do you do?");
            System.out.println("  [S]hoot   [R]eload   [F]lee");
            System.out.print("> ");

            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "s", "shoot" -> {
                    // --- Player attacks ---
                    Ammo round = player.shoot();
                    if (round == null) {
                        System.out.println("\nCLICK. Magazine empty — you need to reload!\n");
                    } else {
                        double dmg = calcDamageToEnemy(round, enemy);
                        enemy.setTotalHealth(Math.max(0, enemy.getTotalHealth() - dmg));
                        System.out.printf("%n>> You fire the %s (%.0f dmg, pen %d).%n",
                            round.getName(), dmg, round.getPenetrationPower());

                        if (enemy.getTotalHealth() <= 0) {
                            System.out.println(">> " + enemy.getName() + " is down!\n");
                            break;
                        } else {
                            System.out.printf(">> Enemy HP remaining: %.0f%n%n", enemy.getTotalHealth());
                        }
                    }

                    // --- Enemy returns fire ---
                    enemyShoot(enemy, player, enemyAim);
                }

                case "r", "reload" -> {
                    if (player.reload()) {
                        System.out.println("\nYou reload. Mag topped off.\n");
                    } else {
                        System.out.println("\nNo reserve ammo left — you're dry!\n");
                    }
                    // Enemy still fires during your reload — you're exposed
                    enemyShoot(enemy, player, enemyAim * 1.2); // slightly better chance to hit
                }

                case "f", "flee" -> {
                    // 50 / 50 chance to break contact
                    if (Math.random() < 0.5) {
                        System.out.println("\nYou break contact and fall back!\n");
                        playerFled = true;
                    } else {
                        System.out.println("\nYou try to flee but the enemy cuts off your retreat!\n");
                        enemyShoot(enemy, player, enemyAim);
                    }
                }

                default -> System.out.println("Unknown command. Try S, R, or F.\n");
            }
        }

        if (!player.isAlive()) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║   YOU HAVE BEEN ELIMINATED.  ║");
            System.out.println("╚══════════════════════════════╝\n");
            return false;
        }

        if (!playerFled && enemy.getTotalHealth() <= 0) {
            System.out.println("Contact neutralised. Stay frosty.\n");
        }

        return true; // player alive, whether they killed or fled
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------

    /**
     * Calculates damage the player's round deals to the enemy.
     * Enemies are considered unarmoured for the POC, so flesh damage applies
     * directly. High penetration rounds get a small bonus to represent
     * punching through any incidental cover.
     */
    private static double calcDamageToEnemy(Ammo round, Enemy enemy) {
        double base = round.getFleshDamage() * round.getProjectiles();
        // Bonus: every 10 penetration power above 20 adds 5% damage
        if (round.getPenetrationPower() > 20) {
            double bonus = ((round.getPenetrationPower() - 20) / 10.0) * 0.05;
            base *= (1 + bonus);
        }
        return base;
    }

    /**
     * Enemy fires at the player. Hit chance is driven by the aim modifier.
     * The enemy uses their primary weapon if available, else secondary.
     */
    private static void enemyShoot(Enemy enemy, Player player, double aimModifier) {
        // Clamp aim to [0, 1]
        double hitChance = Math.min(1.0, Math.max(0.0, aimModifier));
        if (Math.random() > hitChance) {
            System.out.println("<< " + enemy.getName() + " fires — misses!\n");
            return;
        }

        Gun enemyGun = enemy.getPrimary() != null ? enemy.getPrimary() : enemy.getSecondary();
        if (enemyGun == null) {
            System.out.println("<< " + enemy.getName() + " has no weapon — does nothing.\n");
            return;
        }

        Ammo round = enemyGun.getAmmunition();
        if (round == null) {
            System.out.println("<< " + enemy.getName() + " fires wildly — misses!\n");
            return;
        }

        player.takeDamage(round);
        System.out.printf("<< %s fires %s — HITS! You take %.0f damage.%n%n",
            enemy.getName(), round.getName(),
            round.getFleshDamage() * round.getProjectiles());
    }

    /** Default aim values when Enemy.aim was never explicitly set. */
    private static double defaultAim(Enemy enemy) {
        return switch (enemy.getEnemyType()) {
            case "Scav"    -> 0.30;
            case "PMC"     -> 0.55;
            case "Raiders" -> 0.60;
            case "Boss"    -> 0.70;
            default        -> 0.35;
        };
    }

    private static String weaponLine(Enemy enemy) {
        String w = "";
        if (enemy.getPrimaryName() != null && !enemy.getPrimaryName().isEmpty())
            w += enemy.getPrimaryName();
        if (enemy.getSecondaryName() != null && !enemy.getSecondaryName().isEmpty())
            w += (w.isEmpty() ? "" : " / ") + enemy.getSecondaryName();
        return w.isEmpty() ? "Unknown" : w;
    }
}