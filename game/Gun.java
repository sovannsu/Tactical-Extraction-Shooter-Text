package game;

import java.util.HashMap;
import java.util.Map;

/**
 * A weapon instance. All fixed per-model stats (caliber, capacity, range)
 * live in the static GUN_TABLE, populated once at class load. The constructor
 * is now a simple table lookup followed by a random ammo roll for that caliber.
 *
 * Adding a new weapon = one line in the static initialiser. No new methods,
 * no if-else branch, no ammo arrays.
 */
public class Gun {
    private String name;
    private String range;     // Effective range
    private int    capacity;  // Magazine capacity
    private String caliber;   // Caliber string (matches Ammo's CALIBER_POOL keys)
    private Ammo   ammunition; // The specific round currently loaded

    // ── Static lookup table: weapon name -> fixed stats ──────────────────────
    private static final Map<String, Gundata> GUN_TABLE = new HashMap<>();

    static {
        // 9x18mm
        GUN_TABLE.put("PM",           new Gundata("9x18mm",     8,  "Low"));
        GUN_TABLE.put("PP-91",        new Gundata("9x18mm",    20,  "Low"));

        // 9x19mm
        GUN_TABLE.put("MP-443",       new Gundata("9x19mm",    18,  "Low"));
        GUN_TABLE.put("Glock 17",     new Gundata("9x19mm",    17,  "Low"));
        GUN_TABLE.put("Glock 18C",    new Gundata("9x19mm",    17,  "Low"));
        GUN_TABLE.put("M9A3",         new Gundata("9x19mm",    17,  "Low"));
        GUN_TABLE.put("Saiga-9",      new Gundata("9x19mm",    20,  "Medium"));
        GUN_TABLE.put("STM-9",        new Gundata("9x19mm",    19,  "Medium"));
        GUN_TABLE.put("PP-19-01",     new Gundata("9x19mm",    30,  "Medium"));
        GUN_TABLE.put("MP5",          new Gundata("9x19mm",    30,  "Medium"));
        GUN_TABLE.put("MP9",          new Gundata("9x19mm",    25,  "Medium"));
        GUN_TABLE.put("MPX",          new Gundata("9x19mm",    30,  "Medium"));
        GUN_TABLE.put("Vector",       new Gundata("9x19mm",    31,  "Medium"));

        // 9x21mm
        GUN_TABLE.put("SR-1MP",       new Gundata("9x21mm",    18,  "Low"));

        // 9x39mm
        GUN_TABLE.put("KBP 9A-91",    new Gundata("9x39mm",    20,  "Medium"));
        GUN_TABLE.put("AS-VAL",       new Gundata("9x39mm",    20,  "Medium"));
        GUN_TABLE.put("VSS Vintorez", new Gundata("9x39mm",    20,  "Medium"));

        // .45 ACP
        GUN_TABLE.put("UMP",          new Gundata(".45 ACP",   25,  "Medium"));

        // 4.6x30mm
        GUN_TABLE.put("MP7A2",        new Gundata("4.6x30mm",  30,  "Medium"));

        // 5.7x28mm
        GUN_TABLE.put("FN 5-7",       new Gundata("5.7x28mm",  20,  "Medium"));
        GUN_TABLE.put("FN P90",       new Gundata("5.7x28mm",  50,  "Medium"));

        // 12 Gauge
        GUN_TABLE.put("MP-133",       new Gundata("12 Gauge",   6,  "Short"));
        GUN_TABLE.put("MP-153",       new Gundata("12 Gauge",   5,  "Short"));
        GUN_TABLE.put("Saiga-12K",    new Gundata("12 Gauge",   5,  "Short"));
        GUN_TABLE.put("M870",         new Gundata("12 Gauge",   7,  "Short"));
        GUN_TABLE.put("M590A1",       new Gundata("12 Gauge",   8,  "Short"));
        GUN_TABLE.put("M3 Super 90",  new Gundata("12 Gauge",  11,  "Short"));

        // 20 Gauge
        GUN_TABLE.put("TOZ-106",      new Gundata("20 Gauge",   3,  "Short"));

        // 23x75mm (4 Gauge)
        GUN_TABLE.put("KS-23M",       new Gundata("23x75mm",    3,  "Short"));

        // .366 TKM
        GUN_TABLE.put("VPO-215",      new Gundata(".366 TKM",   4,  "Long"));

        // 5.45x39mm
        GUN_TABLE.put("AKS-74U",      new Gundata("5.45x39mm", 30,  "Medium"));
        GUN_TABLE.put("AK-74",        new Gundata("5.45x39mm", 30,  "Long"));
        GUN_TABLE.put("AK-74M",       new Gundata("5.45x39mm", 30,  "Long"));
        GUN_TABLE.put("AK-105",       new Gundata("5.45x39mm", 30,  "Long"));
        GUN_TABLE.put("AK-12",        new Gundata("5.45x39mm", 30,  "Long"));
        GUN_TABLE.put("RPK-16",       new Gundata("5.45x39mm", 60,  "Long"));

        // 5.56x45mm
        GUN_TABLE.put("ADAR 2-15",    new Gundata("5.56x45mm", 20,  "Medium"));
        GUN_TABLE.put("TX-15",        new Gundata("5.56x45mm", 30,  "Long"));
        GUN_TABLE.put("AK-101",       new Gundata("5.56x45mm", 30,  "Long"));
        GUN_TABLE.put("M4A1",         new Gundata("5.56x45mm", 30,  "Long"));
        GUN_TABLE.put("Aug A3",       new Gundata("5.56x45mm", 30,  "Long"));
        GUN_TABLE.put("HK 416A5",     new Gundata("5.56x45mm", 30,  "Long"));

        // 7.62x25mm
        GUN_TABLE.put("Golden TT-33", new Gundata("7.62x25mm",  8,  "Short"));
        GUN_TABLE.put("PPSh-41",      new Gundata("7.62x25mm", 35,  "Medium"));

        // 7.62x39mm
        // NOTE: AK-104 resolved here (7.62x39mm). The original if-else chain
        // matched it under 5.56x45mm first, which was a bug — AK-104 is a
        // 7.62x39 rifle.
        GUN_TABLE.put("SKS",          new Gundata("7.62x39mm", 10,  "Long"));
        GUN_TABLE.put("VPO-136",      new Gundata("7.62x39mm", 10,  "Medium"));
        GUN_TABLE.put("AKM",          new Gundata("7.62x39mm", 30,  "Long"));
        GUN_TABLE.put("AK-104",       new Gundata("7.62x39mm", 30,  "Long"));
        GUN_TABLE.put("RPD",          new Gundata("7.62x39mm", 100, "Long"));
        GUN_TABLE.put("RPDN",         new Gundata("7.62x39mm", 100, "Long"));
        GUN_TABLE.put("Mk47 Mutant",  new Gundata("7.62x39mm", 30,  "Long"));

        // 7.62x51mm
        GUN_TABLE.put("VPO-101",      new Gundata("7.62x51mm", 10,  "Medium"));
        GUN_TABLE.put("MDR",          new Gundata("7.62x51mm", 30,  "Long"));
        GUN_TABLE.put("SCAR-L",       new Gundata("7.62x51mm", 30,  "Long"));
        GUN_TABLE.put("SA-58",        new Gundata("7.62x51mm", 30,  "Long"));
        GUN_TABLE.put("RFB",          new Gundata("7.62x51mm", 20,  "Long"));
        GUN_TABLE.put("SR-25",        new Gundata("7.62x51mm", 20,  "Long"));
        GUN_TABLE.put("M1A",          new Gundata("7.62x51mm", 20,  "Long"));
        GUN_TABLE.put("G28",          new Gundata("7.62x51mm", 10,  "Long"));
        GUN_TABLE.put("RSASS",        new Gundata("7.62x51mm", 20,  "Long"));
        GUN_TABLE.put("M700",         new Gundata("7.62x51mm",  5,  "Long"));
        GUN_TABLE.put("DVL-10",       new Gundata("7.62x51mm", 10,  "Long"));
        GUN_TABLE.put("T-5000",       new Gundata("7.62x51mm",  5,  "Long"));

        // 7.62x54mmR
        GUN_TABLE.put("Mosin",        new Gundata("7.62x54mmR", 5,  "Long"));
        GUN_TABLE.put("SV-98",        new Gundata("7.62x54mmR", 10, "Long"));
        GUN_TABLE.put("SVDS",         new Gundata("7.62x54mmR", 10, "Long"));
        GUN_TABLE.put("PKM",          new Gundata("7.62x54mmR", 100,"Long"));
        GUN_TABLE.put("PKP",          new Gundata("7.62x54mmR", 100,"Long"));

        // .300 Blackout
        GUN_TABLE.put("MCX",          new Gundata(".300 Blackout", 30, "Long"));

        // 6.8x51mm
        GUN_TABLE.put("MCX Spear",    new Gundata("6.8x51mm",  25,  "Long"));

        // 12.7x55mm
        GUN_TABLE.put("ASh-12",       new Gundata("12.7x55mm", 10,  "Medium"));

        // .338 Lapua
        GUN_TABLE.put("AXMC",         new Gundata(".338 Lapua", 10, "Long"));
    }

    /** Fallback used when a weapon name isn't in the table (typo or unmapped). */
    private static final Gundata DEFAULT_GUN = new Gundata("7.62x39mm", 30, "Long");

    // ── Constructor ──────────────────────────────────────────────────────────

    public Gun(String weapon) {
        Gundata data = GUN_TABLE.get(weapon);
        if (data == null) {
            System.err.println("Warning: unknown weapon '" + weapon
                + "' — falling back to default loadout.");
            data = DEFAULT_GUN;
        }
        this.name       = weapon;
        this.caliber    = data.caliber;
        this.capacity   = data.capacity;
        this.range      = data.range;
        this.ammunition = Ammo.randomForCaliber(data.caliber);
    }

    // ── Accessors / mutators ─────────────────────────────────────────────────

    public String getName()                    { return name; }
    public void   setName(String name)         { this.name = name; }

    public String getRange()                   { return range; }
    public void   setRange(String range)       { this.range = range; }

    public String getCaliber()                 { return caliber; }
    public void   setCaliber(String caliber)   { this.caliber = caliber; }

    public int  getCapacity()                  { return capacity; }
    public void setCapacity(int capacity)      { this.capacity = capacity; }

    public Ammo getAmmunition()                { return ammunition; }
    public void setAmmunition(Ammo ammunition) { this.ammunition = ammunition; }
}