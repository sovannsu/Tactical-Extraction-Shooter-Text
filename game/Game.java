package game;

import java.util.HashMap;
import java.util.Map;

/**
 * Game — the controller and the new spine of the application.
 *
 * This class absorbs everything the two old blocking loops used to own:
 *   - from Terminal.main():   the world map, the player, world-building,
 *                             room description, movement, the win check
 *   - from Combat.fight():    the per-turn combat logic and its helpers
 *
 * The crucial change from the terminal build: combat state that used to be
 * LOCAL VARIABLES inside fight() (the current enemy, the enemy's aim) are now
 * FIELDS on this object. In a push-based model, no method is "running" between
 * two button clicks, so anything that must survive between clicks has to live
 * on the object, not on a method's stack.
 *
 * Every public action method follows the same contract:
 *   1. Ignore the click if we're not in the right mode (guards stray input).
 *   2. Do exactly ONE step of work (no loops).
 *   3. Update `mode` if the situation changed.
 *   4. Narrate via `out`.
 * The view then reads getMode() / getPlayer() to refresh itself.
 */
public class Game {

    // ── World + persistent state ─────────────────────────────────────────────
    private final Map<String, Room> world = new HashMap<>();
    private final Player player;
    private final GameOutput out;

    private Mode mode = Mode.EXPLORING;

    // Combat state — formerly locals inside Combat.fight()
    private Enemy  currentEnemy;
    private double enemyAim;

    public Game(String playerName, GameOutput out) {
        this.out    = out;
        this.player = new Player((playerName == null || playerName.isEmpty())
                                 ? "Operative" : playerName);
        buildWorld();
    }

    // ── Queries the view uses to refresh itself ──────────────────────────────
    public Mode   getMode()        { return mode; }
    public Player getPlayer()      { return player; }
    public Enemy  getCurrentEnemy(){ return currentEnemy; }
    public Room   currentRoom()    { return world.get(player.getCurrentRoom()); }

    // ── Intro ────────────────────────────────────────────────────────────────
    public void start() {
        out.println("Welcome, " + player.getName() + ".");
        out.println("Your extraction point is on the far side of the industrial zone.");
        out.println("Reach it alive.\n");
        describeRoom(currentRoom());
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  EXPLORE actions
    // ═════════════════════════════════════════════════════════════════════════

    /** Move in a direction ("north"/"south"/"east"/"west").
     *  Buttons pass the full direction, so the old `move w` shorthand gap
     *  simply doesn't exist here. */
    public void move(String direction) {
        if (mode != Mode.EXPLORING) return;

        Room current = currentRoom();
        Map<String, String> exits = current.getExits();
        if (!exits.containsKey(direction)) {
            out.println("You can't go that way.");
            return;
        }

        Room next = world.get(exits.get(direction));
        if (next == null) {
            out.println("That path leads nowhere (bug: room not found).");
            return;
        }

        player.setCurrentRoom(next.getName());
        out.println("\nYou move " + direction + " into " + next.getName() + ".");
        out.println(next.getAtmosphere());

        if (next.hasEnemy()) {
            beginCombat(Enemy.createScav());   // POC: scav encounters only, as before
        } else {
            out.println("The area seems quiet. For now.\n");
            describeRoom(next);
            checkWin();
        }
    }

    public void examine() {
        if (mode != Mode.EXPLORING) return;
        describeRoom(currentRoom());
    }

    public void status() {
        out.println("\n" + player.statusString());
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  COMBAT actions  (each = one iteration of the old while-loop in fight())
    // ═════════════════════════════════════════════════════════════════════════

    public void combatShoot() {
        if (mode != Mode.IN_COMBAT) return;

        Ammo round = player.shoot();
        if (round == null) {
            out.println("\nCLICK. Magazine empty — you need to reload!\n");
            // NOTE: as in the terminal build, an empty trigger-pull still draws
            // enemy fire — you wasted your action while exposed.
        } else {
            double dmg = calcDamageToEnemy(round, currentEnemy);
            currentEnemy.setTotalHealth(Math.max(0, currentEnemy.getTotalHealth() - dmg));
            out.println(String.format(">> You fire the %s (%.0f dmg, pen %d).",
                round.getName(), dmg, round.getPenetrationPower()));

            if (currentEnemy.getTotalHealth() <= 0) {
                out.println(">> " + currentEnemy.getName() + " is down!\n");
                endCombat();          // enemy down — they do NOT return fire
                return;
            }
            out.println(String.format(">> Enemy HP remaining: %.0f",
                currentEnemy.getTotalHealth()));
        }

        enemyReturnFire(enemyAim);
        afterEnemyFire();
    }

    public void combatReload() {
        if (mode != Mode.IN_COMBAT) return;

        if (player.reload()) {
            out.println("\nYou reload. Mag topped off.\n");
        } else {
            out.println("\nNo reserve ammo left — you're dry!\n");
        }
        // Enemy fires during your reload — slightly better chance to hit you.
        enemyReturnFire(enemyAim * 1.2);
        afterEnemyFire();
    }

    public void combatFlee() {
        if (mode != Mode.IN_COMBAT) return;

        if (Math.random() < 0.5) {
            out.println("\nYou break contact and fall back!\n");
            endCombat();   // survived by fleeing
        } else {
            out.println("\nYou try to flee but the enemy cuts off your retreat!\n");
            enemyReturnFire(enemyAim);
            afterEnemyFire();
        }
    }

    // ── Combat lifecycle helpers ─────────────────────────────────────────────

    private void beginCombat(Enemy enemy) {
        this.currentEnemy = enemy;
        this.enemyAim     = enemy.getAim() > 0 ? enemy.getAim() : defaultAim(enemy);
        this.mode         = Mode.IN_COMBAT;

        out.println("\n════════════════════════════════════════════");
        out.println("  CONTACT! " + enemy.getName() + " opens fire!");
        out.println("  Type:    " + enemy.getEnemyType() + " | Faction: " + enemy.getFaction());
        out.println("  Weapon:  " + weaponLine(enemy));
        out.println("  HP:      " + (int) enemy.getTotalHealth());
        out.println("════════════════════════════════════════════\n");
        out.println(player.statusString());
        out.println("Enemy HP: " + (int) enemy.getTotalHealth());
    }

    /** Called after the player survives the turn but the fight continues —
     *  shows the state they'll act on next click. Also catches player death. */
    private void afterEnemyFire() {
        if (!player.isAlive()) {
            out.println("\n╔══════════════════════════════╗");
            out.println("║   YOU HAVE BEEN ELIMINATED.  ║");
            out.println("╚══════════════════════════════╝\n");
            mode = Mode.DEAD;
            return;
        }
        out.println("\n" + player.statusString());
        out.println("Enemy HP: " + (int) currentEnemy.getTotalHealth());
    }

    /** Player survived AND the fight is over (kill or successful flee). */
    private void endCombat() {
        Room room = currentRoom();
        // NOTE: the terminal build cleared the room's enemy on ANY survival —
        // including fleeing. Preserved here for 1:1 behaviour. If you'd rather
        // a fled enemy stay a threat, only clear on a kill.
        room.clearEnemy();
        currentEnemy = null;
        mode = Mode.EXPLORING;
        out.println("Area secure. You catch your breath.\n");
        describeRoom(room);
        checkWin();
    }

    private void checkWin() {
        if (player.getCurrentRoom().equals("Extraction Point")) {
            out.println("\n╔══════════════════════════════════════╗");
            out.println("║   EXTRACTION SUCCESSFUL. You live.   ║");
            out.println("╚══════════════════════════════════════╝");
            mode = Mode.WON;
        }
    }

    // ── Combat math (lifted from Combat.java, unchanged in spirit) ────────────

    private double calcDamageToEnemy(Ammo round, Enemy enemy) {
        double base = round.getFleshDamage() * round.getProjectiles();
        if (round.getPenetrationPower() > 20) {
            double bonus = ((round.getPenetrationPower() - 20) / 10.0) * 0.05;
            base *= (1 + bonus);
        }
        return base;
    }

    private void enemyReturnFire(double aimModifier) {
        double hitChance = Math.min(1.0, Math.max(0.0, aimModifier));
        if (Math.random() > hitChance) {
            out.println("<< " + currentEnemy.getName() + " fires — misses!\n");
            return;
        }

        Gun gun = currentEnemy.getPrimary() != null
                ? currentEnemy.getPrimary()
                : currentEnemy.getSecondary();
        if (gun == null) {
            out.println("<< " + currentEnemy.getName() + " has no weapon — does nothing.\n");
            return;
        }

        Ammo round = gun.getAmmunition();
        if (round == null) {
            out.println("<< " + currentEnemy.getName() + " fires wildly — misses!\n");
            return;
        }

        player.takeDamage(round);
        // (double) cast guards against the %.0f-with-int crash from the POC.
        double shown = round.getFleshDamage() * (double) round.getProjectiles();
        out.println(String.format("<< %s fires %s — HITS! You take %.0f damage.",
            currentEnemy.getName(), round.getName(), shown));
    }

    private double defaultAim(Enemy enemy) {
        return switch (enemy.getEnemyType()) {
            case "Scav"    -> 0.30;
            case "PMC"     -> 0.55;
            case "Raiders" -> 0.60;
            case "Boss"    -> 0.70;
            default        -> 0.35;
        };
    }

    private String weaponLine(Enemy enemy) {
        String w = "";
        if (enemy.getPrimaryName() != null && !enemy.getPrimaryName().isEmpty())
            w += enemy.getPrimaryName();
        if (enemy.getSecondaryName() != null && !enemy.getSecondaryName().isEmpty())
            w += (w.isEmpty() ? "" : " / ") + enemy.getSecondaryName();
        return w.isEmpty() ? "Unknown" : w;
    }

    // ── Room description (lifted from Terminal.java) ──────────────────────────

    private void describeRoom(Room room) {
        out.println("\n── " + room.getName() + " ──────────────────────────────");
        out.println(room.getDescription());
        out.println("\nExits:");
        out.println(room.exitsString());
        if (room.hasEnemy()) {
            out.println("\n[!] Something doesn't feel right here...");
        }
    }

    // ── World builder (lifted from Terminal.java, unchanged) ──────────────────

    private void buildWorld() {
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
}