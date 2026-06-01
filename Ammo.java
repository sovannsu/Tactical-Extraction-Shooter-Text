package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ammo {
	private String name;
	private String size;
	private int projectiles; // Projectiles per shot fired, changes for grenades and some shotgun shells
	private int fleshDamage; // Hits unarmored body parts
	private int penetrationPower; // Damage modifier when bullet hits armored body parts

	private static final Map<String, Ammo>         AMMO_TABLE   = new HashMap<>();
	private static final Map<String, List<String>> CALIBER_POOL = new HashMap<>();

	static {
		// ── 9x18mm ───────────────────────────────────────────────────────────
		register("PM SP7 GZH", "9x18mm", 1, 77, 2);
		register("PM PPE GZH", "9x18mm", 1, 61, 7);
		register("PM PBM GZH", "9x18mm", 1, 40, 28);

		// ── 9x19mm ───────────────────────────────────────────────────────────
		register("RIP", "9x19mm", 1, 102, 2);
		register("Quakemaker", "9x19mm", 1, 85, 8);
		register("PST GZH", "9x19mm", 1, 54, 20);
		register("PBP GZH", "9x19mm", 1, 52, 39);

		// ── 9x21mm ───────────────────────────────────────────────────────────
		register("PE GZH", "9x21mm", 1, 80, 15);
		register("BT GZH", "9x21mm", 1, 49, 23);
		register("7N42", "9x21mm", 1, 45, 38);

		// ── 9x39mm ───────────────────────────────────────────────────────────
		register("SP-5 GS", "9x39mm", 1, 71, 28);
		register("SP-6 GS", "9x39mm", 1, 60, 48);
		register("BP GS", "9x39mm", 1, 58, 54);

		// ── .45 ACP ──────────────────────────────────────────────────────────
		register(".45 RIP", ".45 ACP", 1, 130, 3);
		register("ACP Match FMJ", ".45 ACP", 1, 72, 25);
		register("ACP AP", ".45 ACP", 1, 66, 38);

		// ── 4.6x30mm ─────────────────────────────────────────────────────────
		register("Action SX", "4.6x30mm", 1, 65, 18);
		register("FMJ SX", "4.6x30mm", 1, 43, 40);
		register("AP SX", "4.6x30mm", 1, 35, 53);

		// ── 5.7x28mm ─────────────────────────────────────────────────────────
		register("SS198LF", "5.7x28mm", 1, 70, 17);
		register("L191 Tracer", "5.7x28mm", 1, 53, 33);
		register("SS190", "5.7x28mm", 1, 49, 37);

		// ── 12 Gauge ─────────────────────────────────────────────────────────
		register("Magnum Buckshot", "12 Gauge", 8, 50, 2);
		register("Flechette", "12 Gauge", 8, 25, 31);
		register("12G RIP", "12 Gauge", 1, 265, 2);
		register("AP-20 Slug", "12 Gauge", 1, 164, 37);

		// ── 20 Gauge ─────────────────────────────────────────────────────────
		register("5.6 Buckshot", "20 Gauge", 8, 26, 1);
		register("Poleva-6u Slug", "20 Gauge", 1, 135, 17);

		// ── 23x75mm (4 Gauge) ────────────────────────────────────────────────
		register("Shrapnel-10 Buckshot", "23x75mm", 8, 87, 11);
		register("Barrikada Slug", "23x75mm", 1, 192, 39);
		register("Zveda Flashbang Round", "23x75mm", 1, 0, 0);

		// ── .366 TKM ─────────────────────────────────────────────────────────
		register("TKM Geksa", ".366 TKM", 1, 110, 14);
		register("TKM EKO", ".366 TKM", 1, 73, 30);
		register("TKM AP-M", ".366 TKM", 1, 90, 42);

		// ── 5.45x39mm ────────────────────────────────────────────────────────
		register("PRS GS", "5.45x39mm", 1, 70, 13);
		register("PS GS", "5.45x39mm", 1, 53, 28);
		register("BT GS", "5.45x39mm", 1, 48, 37);
		register("7N40", "5.45x39mm", 1, 52, 42);
		register("BP GS", "5.45x39mm", 1, 46, 45);
		register("BS GS", "5.45x39mm", 1, 44, 55);
		register("PPBS GS Igolnik", "5.45x39mm",  1, 37, 62);

		// ── 5.56x45mm ────────────────────────────────────────────────────────
		register("Warmageddon", "5.56x45mm", 1, 88, 3);
		register("M856", "5.56x45mm", 1, 64, 18);
		register("M855", "5.56x45mm", 1, 57, 31);
		register("M856A1", "5.56x45mm", 1, 52, 38);
		register("M855A1", "5.56x45mm", 1, 47, 45);
		register("M995", "5.56x45mm", 1, 42, 53);
		register("SSA AP", "5.56x45mm", 1, 38, 57);

		// ── 7.62x25mm ────────────────────────────────────────────────────────
		register("TT LRNPC", "7.62x25mm", 1, 66, 7);
		register("TT AKBS", "7.62x25mm", 1, 64, 12);
		register("TT PST GZH", "7.62x25mm", 1, 50, 25);

		// ── 7.62x39mm ────────────────────────────────────────────────────────
		register("HP", "7.62x39mm", 1, 87, 15);
		register("T-45M1 GZH", "7.62x39mm", 1, 64, 30);
		register("PS GZH", "7.62x39mm", 1, 57, 35);
		register("BP GZH", "7.62x39mm", 1, 58, 47);
		register("MAI AP", "7.62x39mm", 1, 47, 58);

		// ── 7.62x51mm ────────────────────────────────────────────────────────
		register("Ultra Nosler", "7.62x51mm", 1, 107, 15);
		register("M80", "7.62x51mm", 1, 80, 41);
		register("M61", "7.62x51mm", 1, 70, 64);
		register("M993", "7.62x51mm", 1, 67, 70);

		// ── 7.62x54mmR ───────────────────────────────────────────────────────
		register("HP BT Tracer", "7.62x54mmR", 1, 102, 23);
		register("T-46M GZH", "7.62x54mmR", 1, 82, 41);
		register("SNB GZH", "7.62x54mmR", 1, 75, 62);
		register("BS GS", "7.62x54mmR", 1, 72, 70);

		// ── .300 Blackout ────────────────────────────────────────────────────
		register("Blackout Whisper", ".300 Blackout", 1, 90, 15);
		register("Blackout BCP FMJ", ".300 Blackout", 1, 60, 30);
		register("Blackout CBJ", ".300 Blackout", 1, 58, 43);
		register("Blackout AP", ".300 Blackout", 1, 51, 48);

		// ── 6.8x51mm ─────────────────────────────────────────────────────────
		register("SIG FMJ", "6.8x51mm", 1, 80, 36);
		register("SIG Hybrid", "6.8x51mm", 1, 72, 47);

		// ── 12.7x55mm ────────────────────────────────────────────────────────
		register("PS12A", "12.7x55mm", 1, 165, 10);
		register("PS12", "12.7x55mm", 1, 115, 28);
		register("PS12B", "12.7x55mm", 1, 102, 46);

		// ── .338 Lapua Magnum ────────────────────────────────────────────────
		register("TAC-X", ".338 Lapua", 1, 196, 18);
		register("UCW", ".338 Lapua", 1, 142, 32);
		register("FMJ", ".338 Lapua", 1, 122, 47);
		register("AP", ".338 Lapua", 1, 115, 79);

		// ── 40mm Grenade Launcher ────────────────────────────────────────────
		register("M381 HE", "40mm", 10, 199, 1);
		register("M433 HEDP", "40mm", 15, 199, 1);
		register("M576 MP-APERS", "40mm", 15, 160, 5);
		register("VOG-25", "40mm", 15, 199, 0);
	}

	private static void register(String name, String caliber,int projectiles, int fleshDmg, int pen) {
		Ammo a = new Ammo();
		a.name = name;
		a.size = caliber;
		a.projectiles = projectiles;
		a.fleshDamage = fleshDmg;
		a.penetrationPower = pen;
		AMMO_TABLE.put(name, a);
		CALIBER_POOL.computeIfAbsent(caliber, k -> new ArrayList<>()).add(name);
	}

	public static Ammo randomForCaliber(String caliber) {
		List<String> pool = CALIBER_POOL.get(caliber);
		if (pool == null || pool.isEmpty()) {
			System.err.println("Warning: no ammo registered for caliber '" + caliber + "'");
			return new Ammo();
		}
		String chosen = pool.get((int) (Math.random() * pool.size()));
		return AMMO_TABLE.get(chosen);
	}

	public static Ammo get(String name) {
		Ammo result = AMMO_TABLE.get(name);
		if (result == null) {
			System.err.println("Warning: unknown ammo name '" + name + "'");
			return new Ammo();
		}
		return result;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}
	public void   setSize(String size) {
		 this.size = size;

	}

	public int  getProjectiles() {
		return projectiles;
	}
	public void setProjectiles(int projectiles) {
		this.projectiles = projectiles;
	}

	public int  getFleshDamage() {
		return fleshDamage;
	}
	public void setFleshDamage(int fleshDamage) {
		this.fleshDamage = fleshDamage;

	}

	public int  getPenetrationPower() { 
		return penetrationPower;
	}
	public void setPenetrationPower(int pen) {
		this.penetrationPower = pen;
	}
}