package game;

/**
 * Immutable value object holding the fixed stats for one weapon.
 * Used exclusively by Gun's static lookup table — nothing else should
 * need to instantiate this directly.
 */
public class Gundata {
    public final String caliber;
    public final int    capacity;
    public final String range;

    public Gundata(String caliber, int capacity, String range) {
        this.caliber  = caliber;
        this.capacity = capacity;
        this.range    = range;
    }
}