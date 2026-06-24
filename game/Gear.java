package game;

/**
 * Gear — a single piece of equipment. For now this is the SKELETON of the
 * armor system: enough structure for the combat resolver to consult, with the
 * actual catalogue of helmets and vests to be filled in later.
 *
 * The one stat that drives combat is armorClass: 0 means no protection, and
 * higher numbers are harder to penetrate. A round defeats a piece by comparing
 * its penetration power against the armor's class (see Game.resolveHit). When
 * you build the real helmet/armor set, you just construct Gear objects with the
 * right class and durability and assign them to the player and enemies — the
 * resolver already knows how to use them.
 */
public class Gear {

    /** Where on the body this piece sits. Drives which shots it protects. */
    public enum Slot { HELMET, BODY_ARMOR, RIG, HEADSET, BACKPACK }

    private final String name;
    private final Slot   slot;
    private final int    armorClass;     // 0 = unarmored; 1..6+ = increasing protection
    private final double maxDurability;
    private double       durability;     // drops as the piece soaks hits

    public Gear(String name, Slot slot, int armorClass, double maxDurability) {
        this.name          = name;
        this.slot          = slot;
        this.armorClass    = armorClass;
        this.maxDurability = maxDurability;
        this.durability    = maxDurability;
    }

    /** An empty slot — no protection. Use this as the default "nothing equipped". */
    public static Gear none(Slot slot) {
        return new Gear("None", slot, 0, 0);
    }

    // -- Accessors --
    public String  getName()          { return name; }
    public Slot    getSlot()          { return slot; }
    public int     getArmorClass()    { return armorClass; }
    public double  getDurability()    { return durability; }
    public double  getMaxDurability() { return maxDurability; }
    public boolean isArmor()          { return armorClass > 0; }

    /**
     * Wear the piece down when it takes a hit. Skeleton rule: caller passes a
     * flat amount. A richer rule (scale by the round's penetration / damage,
     * lose class as durability falls) can replace this later without touching
     * the combat resolver.
     */
    public void degrade(double amount) {
        durability = Math.max(0, durability - amount);
    }
}