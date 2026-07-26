package game;

/**
 * ArmorTest — THROWAWAY diagnostic. Three checks, no live fights required:
 *
 *   1) Spawn 20 PMCs and print the helmet/vest ratings the factories rolled.
 *   2) Roll a large sample and report the distribution (sanity-check the odds).
 *   3) Sweep the player's AKM rounds (7.62x39) across every rating tier so you
 *      see holds / strains / pierced side by side.
 *
 * The band table below MIRRORS Game.ARMOR_BAND_DELTAS / ARMOR_BAND_MULTS, which
 * are private to Game. If you retune the curve there, mirror it here — or just
 * delete this file, it's only a checking tool.
 *
 * Run:  javac game\*.java ;  java game.ArmorTest
 */
public class ArmorTest {

    // -- Mirror of Game's band table (keep in sync if you retune) --
    private static final int[]    BAND_DELTAS = { -20, -10,   0,   10 };
    private static final double[] BAND_MULTS  = { 0.10, 0.25, 0.50, 0.85, 1.00 };
    private static final double   PIERCE = 0.85, STRAIN = 0.50;

    private static double mult(int pen, int rating) {
        int delta = pen - rating;
        for (int i = 0; i < BAND_DELTAS.length; i++)
            if (delta <= BAND_DELTAS[i]) return BAND_MULTS[i];
        return BAND_MULTS[BAND_MULTS.length - 1];
    }

    /** Format one cell the way the combat log reads it. */
    private static String cell(int pen, int rating) {
        if (rating == 0) return "flesh (1.00)";        // unarmored zone bypasses the curve
        double m = mult(pen, rating);
        String w = (m >= PIERCE) ? "pierced" : (m >= STRAIN) ? "strains" : "holds";
        return String.format("%.2fx %s", m, w);
    }

    public static void main(String[] args) {
        // 1) What armor do PMCs actually roll?
        System.out.println("=== 20 PMC spawns (rolled ratings) ===");
        for (int i = 0; i < 20; i++) {
            Enemy e = Enemy.createPMC();
            System.out.printf("  %-5s  helmet %2d   vest %2d%n",
                e.getFaction(), e.getHelmetArmorClass(), e.getBodyArmorClass());
        }

        // 2) Distribution over a big sample.
        int n = 2000, helmeted = 0;
        int[] vestCount = new int[64];
        for (int i = 0; i < n; i++) {
            Enemy e = Enemy.createPMC();
            if (e.getHelmetArmorClass() > 0) helmeted++;
            vestCount[e.getBodyArmorClass()]++;
        }
        System.out.printf("%n=== distribution over %d PMCs ===%n", n);
        System.out.printf("  helmeted : %4.1f%%  (expected ~60%%)%n", 100.0 * helmeted / n);
        for (int r = 0; r < vestCount.length; r++)
            if (vestCount[r] > 0)
                System.out.printf("  vest %2d  : %4.1f%%  (expected ~50%% each for 20/30)%n",
                    r, 100.0 * vestCount[r] / n);

        // 3) Pen sweep: the AKM's 7.62x39 rounds vs each rating tier.
        int[]    pens    = { 15, 30, 35, 47, 58 };
        String[] names   = { "HP", "T-45M1", "PS GZH", "BP GZH", "MAI AP" };
        int[]    ratings = { 0, 10, 20, 30, 40 };

        System.out.println("\n=== 7.62x39 rounds vs armor ratings ===");
        StringBuilder head = new StringBuilder(String.format("%-14s", "round \\ rate"));
        for (int r : ratings) head.append(String.format("| rate %-8d", r));
        System.out.println(head);
        for (int i = 0; i < pens.length; i++) {
            StringBuilder row = new StringBuilder(
                String.format("%-14s", names[i] + " (" + pens[i] + ")"));
            for (int r : ratings) row.append(String.format("| %-11s ", cell(pens[i], r)));
            System.out.println(row);
        }
    }
}