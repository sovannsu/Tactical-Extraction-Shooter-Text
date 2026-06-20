package game;

/**
 * Immutable value object holding the fixed stats for one weapon.
 * Used exclusively by Gun's static lookup table.
 *
 * FIX: the constructor was spelled `GunData` while the class is `Gundata`.
 * A Java constructor must match the class name EXACTLY; the mismatched version
 * was read as a method with a missing return type, so `new Gundata(...)` had
 * no constructor to call. Renamed to `Gundata`.
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