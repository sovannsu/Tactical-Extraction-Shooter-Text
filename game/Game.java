package game;

import java.util.HashMap;
import java.util.Map;

/**
 * Game — the controller and spine of the application.
 *
 * Holds the world, the player, and the current mode. Every public method is one
 * player action (one "turn"); the view calls them on button clicks and then
 * reads getMode() / getPlayer() to refresh.
 */
public class Game {

    /** Probability that a given encounter is a PMC rather than a scav.
     *  Single tuning knob for how often the tougher enemy shows up. */
    private static final double PMC_SPAWN_CHANCE = 0.30;

    // -- Combat tuning knobs (skeleton values; tune freely) --
    private static final double HEADSHOT_CHANCE       = 0.20; // fraction of hits that strike the head
    private static final double HEADSHOT_MULTIPLIER   = 2.5;  // damage multiplier on a headshot
    private static final double BLOCKED_DAMAGE_FACTOR = 0.30; // fraction that leaks through armor that STOPS a round

    // -- World + persistent state --
    private final Map<String, Room> world = new HashMap<>();
    private final Player player;
    private final GameOutput out;

    private Mode mode = Mode.EXPLORING;

    // Combat state -- formerly locals inside Combat.fight()
    private Enemy  currentEnemy;
    private double enemyAim;

    public Game(String playerName, GameOutput out) {
        this.out    = out;
        this.player = new Player((playerName == null || playerName.isEmpty())
                                 ? "Operative" : playerName);
        buildWorld();
    }

    // -- Queries the view uses to refresh itself --
    public Mode   getMode()        { return mode; }
    public Player getPlayer()      { return player; }
    public Enemy  getCurrentEnemy(){ return currentEnemy; }
    public Room   currentRoom()    { return world.get(player.getCurrentRoom()); }

    // -- Intro --
    public void start() {
        out.println("Welcome, " + player.getName() + ".");
        out.println("You've been inserted at the western checkpoint. Extraction lies");
        out.println("somewhere on the far side of the industrial zone. Find it alive.\n");
        describeRoom(currentRoom());
    }

    // =========================================================================
    //  EXPLORE actions
    // =========================================================================

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
            beginCombat(rollEncounter());
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

    /** Decide who the player just walked into.
     *  Scavs are the staple; PMCs are the rarer, deadlier mercenaries. */
    private Enemy rollEncounter() {
        if (Math.random() < PMC_SPAWN_CHANCE) {
            return Enemy.createPMC();
        }
        return Enemy.createScav();
    }

    // =========================================================================
    //  COMBAT actions  (each = one iteration of the old while-loop in fight())
    // =========================================================================

    public void combatShoot() {
        if (mode != Mode.IN_COMBAT) return;

        Ammo round = player.shoot();
        if (round == null) {
            out.println("\nCLICK. Magazine empty -- you need to reload!\n");
        } else {
            Hit hit = resolveHit(round,
                currentEnemy.getHelmetArmorClass(), currentEnemy.getBodyArmorClass());
            currentEnemy.setTotalHealth(Math.max(0, currentEnemy.getTotalHealth() - hit.damage));
            out.println(shotLine(">>", "You fire the " + round.getName(), hit, round));

            if (currentEnemy.getTotalHealth() <= 0) {
                out.println(">> " + currentEnemy.getName() + " is down!\n");
                endCombat();
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
            out.println("\nNo reserve ammo left -- you're dry!\n");
        }
        enemyReturnFire(enemyAim * 1.2);   // exposed during reload
        afterEnemyFire();
    }

    public void combatFlee() {
        if (mode != Mode.IN_COMBAT) return;

        if (Math.random() < 0.5) {
            out.println("\nYou break contact and fall back!\n");
            endCombat();
        } else {
            out.println("\nYou try to flee but the enemy cuts off your retreat!\n");
            enemyReturnFire(enemyAim);
            afterEnemyFire();
        }
    }

    // -- Combat lifecycle helpers --

    private void beginCombat(Enemy enemy) {
        this.currentEnemy = enemy;
        this.enemyAim     = enemy.getAim() > 0 ? enemy.getAim() : defaultAim(enemy);
        this.mode         = Mode.IN_COMBAT;

        out.println("\n============================================");
        out.println("  CONTACT! " + enemy.getName() + " opens fire!");
        out.println("  Type:    " + enemy.getEnemyType() + " | Faction: " + enemy.getFaction());
        out.println("  Weapon:  " + weaponLine(enemy));
        out.println("  HP:      " + (int) enemy.getTotalHealth());
        out.println("============================================\n");
        out.println(player.statusString());
        out.println("Enemy HP: " + (int) enemy.getTotalHealth());
    }

    private void afterEnemyFire() {
        if (!player.isAlive()) {
            out.println("\n==============================");
            out.println("   YOU HAVE BEEN ELIMINATED.");
            out.println("==============================\n");
            mode = Mode.DEAD;
            return;
        }
        out.println("\n" + player.statusString());
        out.println("Enemy HP: " + (int) currentEnemy.getTotalHealth());
    }

    private void endCombat() {
        Room room = currentRoom();
        room.clearEnemy();   // NOTE: clears on flee too, matching the POC behaviour
        currentEnemy = null;
        mode = Mode.EXPLORING;
        out.println("Area secure. You catch your breath.\n");
        describeRoom(room);
        checkWin();
    }

    private void checkWin() {
        if (player.getCurrentRoom().equals("Extraction Point")) {
            out.println("\n========================================");
            out.println("   EXTRACTION SUCCESSFUL. You live.");
            out.println("========================================");
            mode = Mode.WON;
        }
    }

    // -- Combat math --

    // -- Unified hit resolution (used by BOTH player and enemy shots) --

    /** The outcome of one shot: how much damage, and the context the log needs. */
    private static final class Hit {
        final double  damage;
        final boolean headshot;
        final boolean penetrated; // did the round defeat the armor on the struck zone?
        final boolean armored;    // was the struck zone actually armored?
        Hit(double damage, boolean headshot, boolean penetrated, boolean armored) {
            this.damage     = damage;
            this.headshot   = headshot;
            this.penetrated = penetrated;
            this.armored    = armored;
        }
    }

    /**
     * Resolve a single shot against a target with the given head/body armor
     * classes. Rolls head vs. body, applies the headshot multiplier, and checks
     * the round's penetration against the armor on the struck zone.
     *
     * With no gear equipped (armorClass 0) the armor branch is inert and this
     * just produces flesh damage plus the headshot roll — which is exactly the
     * current state until the helmet/armor catalogue is filled in.
     */
    private Hit resolveHit(Ammo round, int helmetClass, int bodyArmorClass) {
        boolean headshot   = Math.random() < HEADSHOT_CHANCE;
        int     armorClass = headshot ? helmetClass : bodyArmorClass;
        boolean armored    = armorClass > 0;

        double damage = round.getFleshDamage() * round.getProjectiles();

        // High-penetration rounds carry a small bonus (preserved from the old
        // calcDamageToEnemy). Now applies to both directions of fire.
        if (round.getPenetrationPower() > 20) {
            double bonus = ((round.getPenetrationPower() - 20) / 10.0) * 0.05;
            damage *= (1 + bonus);
        }

        if (headshot) damage *= HEADSHOT_MULTIPLIER;

        boolean penetrated = true;
        if (armored) {
            // PLACEHOLDER rule until real armor classes are defined: a round
            // defeats the armor if its penetration meets armorClass * 10.
            // Replace this comparison when the real class system lands.
            penetrated = round.getPenetrationPower() >= armorClass * 10;
            if (!penetrated) damage *= BLOCKED_DAMAGE_FACTOR;
        }

        return new Hit(damage, headshot, penetrated, armored);
    }

    /** Build a one-line combat log entry, with headshot / armor flavour. */
    private String shotLine(String arrow, String action, Hit hit, Ammo round) {
        StringBuilder sb = new StringBuilder(arrow + " " + action);
        if (hit.headshot) sb.append(" -- HEADSHOT!");
        if (hit.armored)  sb.append(hit.penetrated ? " Armor pierced!" : " Armor holds!");
        sb.append(String.format(" (%.0f dmg, pen %d)", hit.damage, round.getPenetrationPower()));
        return sb.toString();
    }

    private void enemyReturnFire(double aimModifier) {
        double hitChance = Math.min(1.0, Math.max(0.0, aimModifier));
        if (Math.random() > hitChance) {
            out.println("<< " + currentEnemy.getName() + " fires -- misses!\n");
            return;
        }

        Gun gun = currentEnemy.getPrimary() != null
                ? currentEnemy.getPrimary()
                : currentEnemy.getSecondary();
        if (gun == null) {
            out.println("<< " + currentEnemy.getName() + " has no weapon -- does nothing.\n");
            return;
        }

        Ammo round = gun.getAmmunition();
        if (round == null) {
            out.println("<< " + currentEnemy.getName() + " fires wildly -- misses!\n");
            return;
        }

        Hit hit = resolveHit(round, player.getHelmetClass(), player.getBodyArmorClass());
        player.applyDamage(hit.damage);
        out.println(shotLine("<<", currentEnemy.getName() + " fires " + round.getName(), hit, round));
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

    // -- Room description --

    private void describeRoom(Room room) {
        out.println("\n-- " + room.getName() + " --------------------------------");
        out.println(room.getDescription());
        out.println("\nExits:");
        out.println(room.exitsString());
        if (room.hasEnemy()) {
            out.println("\n[!] Something doesn't feel right here...");
        }
    }

    // =========================================================================
    //  World builder -- a 4x5 industrial zone (20 rooms)
    //
    //    Watchtower -- Admin Office -- Container Yard -- Crane Platform -- Sniper Nest
    //        |              |               |                |                |
    //    Checkpoint --  Warehouse  --  Loading Bay  --  Rail Sidings  --  Pump Station
    //        |              |               |                |                |
    //   Trailer Park -- Collapsed Off. -- Motor Pool  --   Fuel Depot  -- Generator Shed
    //        |              |               |                |                |
    //    Dormitory -- Sewer Junction -- Boiler Room  -- Construction -- Extraction Point
    //
    //  Start: Checkpoint (NW).  Goal: Extraction Point (SE).
    //  Two routes reach extraction -- via Generator Shed (N) or Construction (W).
    // =========================================================================

    private void buildWorld() {
        // -- Row 0 (north) --
        Room watchtower = new Room("Watchtower",
            "A skeletal steel observation tower, half its ladders rusted away. The\n" +
            "wind moans through the gantry. Spent casings litter the platform above.",
            "You climb through the tower's shattered base.",
            "Customs", "Day");

        Room admin = new Room("Admin Office",
            "The old customs administration block. Filing cabinets lie gutted, their\n" +
            "papers fused into grey pulp by years of rain. A safe stands open and empty.",
            "Glass crunches underfoot as you enter the office.",
            "Customs", "Day");

        Room containerYard = new Room("Container Yard",
            "Shipping containers stacked four high form steel canyons. Faded customs\n" +
            "seals hang broken from their doors. Anything could be waiting around a corner.",
            "You slip between two towering container stacks.",
            "Customs", "Day");

        Room crane = new Room("Crane Platform",
            "A gantry crane looms overhead, its cab long abandoned. The catwalk gives a\n" +
            "clear line of sight across the yard -- and exposes you to anyone watching.",
            "Metal grating rings under your boots on the crane deck.",
            "Customs", "Day");

        Room sniperNest = new Room("Sniper Nest",
            "A blown-out upper room with a commanding view of the whole eastern flank.\n" +
            "A bipod's scuff marks still mar the windowsill. Whoever held this left fast.",
            "You creep into the gutted firing position.",
            "Customs", "Day");

        // -- Row 1 --
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
            "A chain-link fence separates this area from an overgrown yard.",
            "Cold air hits you under the loading canopy.",
            "Customs", "Day");

        Room railSidings = new Room("Rail Sidings",
            "Disused railway tracks vanish into weeds. A derailed freight car lies on\n" +
            "its side, doors sprung open, its cargo long since picked clean.",
            "You step over a rusted rail onto the sidings.",
            "Customs", "Day");

        Room pumpStation = new Room("Pump Station",
            "A squat brick pumping station, its machinery seized solid. Stagnant water\n" +
            "pools across the floor, and pipes drip somewhere in the dark.",
            "The door scrapes open into damp, echoing gloom.",
            "Customs", "Day");

        // -- Row 2 --
        Room trailerPark = new Room("Trailer Park",
            "A cluster of derelict construction trailers up on cinder blocks. Curtains\n" +
            "twitch in the breeze through broken windows. Someone lived here, once.",
            "You pick your way between the leaning trailers.",
            "Customs", "Day");

        Room collapsedOffice = new Room("Collapsed Office",
            "What was once an administrative building. The upper floor has caved in,\n" +
            "leaving a maze of broken furniture and exposed rebar. Scav graffiti covers\n" +
            "every surface. Loot may be hidden here -- or trouble.",
            "You squeeze through a busted-out window.",
            "Customs", "Day");

        Room motorPool = new Room("Motor Pool",
            "A vehicle depot of cannibalised trucks up on jacks, hoods gaping. Tool\n" +
            "boards hang empty on the walls. The reek of old diesel is everywhere.",
            "You duck under a half-raised roller door.",
            "Customs", "Day");

        Room fuelDepot = new Room("Fuel Depot",
            "Towering fuel tanks ringed by containment berms. Valve wheels are frozen\n" +
            "with rust, and the ground is black where something burned long ago.",
            "You move into the shadow of the great tanks.",
            "Customs", "Day");

        Room generatorShed = new Room("Generator Shed",
            "A corrugated shed housing dead industrial generators. Severed cables snake\n" +
            "across the floor. A faint draft from the far wall hints at the way out.",
            "You step over coiled cabling into the shed.",
            "Customs", "Day");

        // -- Row 3 (south) --
        Room dormitory = new Room("Dormitory",
            "Two-storey worker dormitories, bunks overturned and lockers prised open.\n" +
            "A child's drawing is still pinned to one wall -- an unsettling note here.",
            "Floorboards creak as you enter the dormitory hall.",
            "Customs", "Day");

        Room sewerJunction = new Room("Sewer Junction",
            "A flooded drainage junction beneath the plant. Water laps at your shins and\n" +
            "the air is thick with rot. Tunnels branch off into total darkness.",
            "You wade into the dripping junction.",
            "Customs", "Day");

        Room boilerRoom = new Room("Boiler Room",
            "The plant's old boiler house, all riveted iron and dead gauges. Pilot lights\n" +
            "long extinguished. Every footstep booms off the cold metal.",
            "Heat-warped doors part as you step inside.",
            "Customs", "Day");

        Room constructionSite = new Room("Construction Site",
            "Half-finished concrete skeletons rise from a sea of rebar and rubble.\n" +
            "Scaffolding sways overhead. The extraction road runs just past the far fence.",
            "You pick across the broken ground of the build site.",
            "Customs", "Day");

        Room extraction = new Room("Extraction Point",
            "A dirt road leading out of the industrial zone. A faded orange flare marks\n" +
            "the pickup spot.",
            "You reach the extraction point.",
            "Customs", "Day");
        extraction.clearEnemy(); // extraction always clear

        // -- Exits --
        // Row 0
        watchtower.addExit("south", "Checkpoint");
        watchtower.addExit("east",  "Admin Office");

        admin.addExit("west",  "Watchtower");
        admin.addExit("south", "Warehouse");
        admin.addExit("east",  "Container Yard");

        containerYard.addExit("west",  "Admin Office");
        containerYard.addExit("south", "Loading Bay");
        containerYard.addExit("east",  "Crane Platform");

        crane.addExit("west",  "Container Yard");
        crane.addExit("south", "Rail Sidings");
        crane.addExit("east",  "Sniper Nest");

        sniperNest.addExit("west",  "Crane Platform");
        sniperNest.addExit("south", "Pump Station");

        // Row 1
        checkpoint.addExit("north", "Watchtower");
        checkpoint.addExit("east",  "Warehouse");
        checkpoint.addExit("south", "Trailer Park");

        warehouse.addExit("north", "Admin Office");
        warehouse.addExit("west",  "Checkpoint");
        warehouse.addExit("east",  "Loading Bay");
        warehouse.addExit("south", "Collapsed Office");

        loadingBay.addExit("north", "Container Yard");
        loadingBay.addExit("west",  "Warehouse");
        loadingBay.addExit("east",  "Rail Sidings");
        loadingBay.addExit("south", "Motor Pool");

        railSidings.addExit("north", "Crane Platform");
        railSidings.addExit("west",  "Loading Bay");
        railSidings.addExit("east",  "Pump Station");
        railSidings.addExit("south", "Fuel Depot");

        pumpStation.addExit("north", "Sniper Nest");
        pumpStation.addExit("west",  "Rail Sidings");
        pumpStation.addExit("south", "Generator Shed");

        // Row 2
        trailerPark.addExit("north", "Checkpoint");
        trailerPark.addExit("east",  "Collapsed Office");
        trailerPark.addExit("south", "Dormitory");

        collapsedOffice.addExit("north", "Warehouse");
        collapsedOffice.addExit("west",  "Trailer Park");
        collapsedOffice.addExit("east",  "Motor Pool");
        collapsedOffice.addExit("south", "Sewer Junction");

        motorPool.addExit("north", "Loading Bay");
        motorPool.addExit("west",  "Collapsed Office");
        motorPool.addExit("east",  "Fuel Depot");
        motorPool.addExit("south", "Boiler Room");

        fuelDepot.addExit("north", "Rail Sidings");
        fuelDepot.addExit("west",  "Motor Pool");
        fuelDepot.addExit("east",  "Generator Shed");
        fuelDepot.addExit("south", "Construction Site");

        generatorShed.addExit("north", "Pump Station");
        generatorShed.addExit("west",  "Fuel Depot");
        generatorShed.addExit("south", "Extraction Point");

        // Row 3
        dormitory.addExit("north", "Trailer Park");
        dormitory.addExit("east",  "Sewer Junction");

        sewerJunction.addExit("north", "Collapsed Office");
        sewerJunction.addExit("west",  "Dormitory");
        sewerJunction.addExit("east",  "Boiler Room");

        boilerRoom.addExit("north", "Motor Pool");
        boilerRoom.addExit("west",  "Sewer Junction");
        boilerRoom.addExit("east",  "Construction Site");

        constructionSite.addExit("north", "Fuel Depot");
        constructionSite.addExit("west",  "Boiler Room");
        constructionSite.addExit("east",  "Extraction Point");

        extraction.addExit("north", "Generator Shed");
        extraction.addExit("west",  "Construction Site");

        // -- Register --
        for (Room r : new Room[] {
                watchtower, admin, containerYard, crane, sniperNest,
                checkpoint, warehouse, loadingBay, railSidings, pumpStation,
                trailerPark, collapsedOffice, motorPool, fuelDepot, generatorShed,
                dormitory, sewerJunction, boilerRoom, constructionSite, extraction }) {
            world.put(r.getName(), r);
        }
    }
}