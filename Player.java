package game;

public class Player {
    private String name;
    private double maxHealth;
    private double currentHealth;
    private Gun weapon;
    private int ammoCount;       // Rounds remaining in current magazine
    private int reserveAmmo;     // Extra magazines worth of ammo
    private String currentRoom;  // Key into the Room map

    public Player(String name) {
        this.name = name;
        this.maxHealth = 440;
        this.currentHealth = 440;
        // Start with a basic AKM - reliable and mid-tier
        this.weapon = new Gun("AKM");
        this.ammoCount = weapon.getCapacity();
        this.reserveAmmo = weapon.getCapacity() * 3; // 3 spare mags
        this.currentRoom = "Checkpoint";
    }

    // --- Accessors ---

    public String getName() { return name; }

    public double getMaxHealth() { return maxHealth; }

    public double getCurrentHealth() { return currentHealth; }

    public Gun getWeapon() { return weapon; }

    public int getAmmoCount() { return ammoCount; }

    public int getReserveAmmo() { return reserveAmmo; }

    public String getCurrentRoom() { return currentRoom; }

    public void setCurrentRoom(String room) { this.currentRoom = room; }

    // --- Combat helpers ---

    public boolean isAlive() {
        return currentHealth > 0;
    }

    /**
     * Player fires one shot. Returns the Ammo object that fired, or null if
     * the magazine is empty.
     */
    public Ammo shoot() {
        if (ammoCount <= 0) return null;
        ammoCount--;
        return weapon.getAmmunition();
    }

    /**
     * Reload from reserve. Returns true if reload was possible.
     */
    public boolean reload() {
        if (reserveAmmo <= 0) return false;
        int needed = weapon.getCapacity() - ammoCount;
        int taken = Math.min(needed, reserveAmmo);
        ammoCount += taken;
        reserveAmmo -= taken;
        return true;
    }

    /**
     * Apply incoming damage. Simple model: if penetrationPower >= 20 the round
     * punches through light cover and deals full damage; otherwise 60% damage
     * (simulating soft cover / partial protection a player might have).
     */
    public void takeDamage(Ammo round) {
        double dmg;
        if (round.getPenetrationPower() >= 20) {
            dmg = round.getFleshDamage();
        } else {
            dmg = round.getFleshDamage() * 0.6;
        }
        currentHealth = Math.max(0, currentHealth - dmg);
    }

    public String statusString() {
        return String.format("[HP: %.0f/%.0f]  [%s | Mag: %d/%d | Reserve: %d]",
            currentHealth, maxHealth,
            weapon.getName(), ammoCount, weapon.getCapacity(), reserveAmmo);
    }
}