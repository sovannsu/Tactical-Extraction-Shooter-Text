package game;

public class Player {
    private String name;
    private double maxHealth;
    private double currentHealth;
    private Gun weapon;
    private int ammoCount;       // Rounds remaining in current magazine
    private int reserveAmmo;     // Extra magazines worth of ammo
    private String currentRoom;  // Key into the Room map

    // -- Armor (skeleton) --
    // Default to empty slots (class 0 = unarmored). Swap in real Gear later.
    private Gear helmet;
    private Gear bodyArmor;

    public Player(String name) {
        this.name = name;
        this.maxHealth = 440;
        this.currentHealth = 440;
        // Start with a basic AKM - reliable and mid-tier
        this.weapon = new Gun("AKM");
        this.ammoCount = weapon.getCapacity();
        this.reserveAmmo = weapon.getCapacity() * 3; // 3 spare mags
        this.currentRoom = "Checkpoint";

        // Unarmored to start. Assign real Gear here (or via setters) once the
        // helmet/armor catalogue exists.
        this.helmet    = Gear.none(Gear.Slot.HELMET);
        this.bodyArmor = Gear.none(Gear.Slot.BODY_ARMOR);
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

    // --- Armor ---

    public Gear getHelmet()    { return helmet; }
    public Gear getBodyArmor() { return bodyArmor; }

    public void setHelmet(Gear helmet)       { this.helmet = helmet; }
    public void setBodyArmor(Gear bodyArmor) { this.bodyArmor = bodyArmor; }

    /** Armor class protecting the head; 0 if no helmet. */
    public int getHelmetClass() {
        return helmet != null ? helmet.getArmorClass() : 0;
    }

    /** Armor class protecting the body; 0 if no body armor. */
    public int getBodyArmorClass() {
        return bodyArmor != null ? bodyArmor.getArmorClass() : 0;
    }

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
     * Apply a precomputed amount of damage. The hit (head/body, armor, headshot
     * multiplier) is resolved by Game.resolveHit, which keeps the player and the
     * enemies running through one shared damage model.
     */
    public void applyDamage(double amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }

    /**
     * @deprecated Superseded by Game.resolveHit + applyDamage. Kept only so the
     * legacy Combat.java still compiles; delete once Combat.java is removed.
     */
    @Deprecated
    public void takeDamage(Ammo round) {
        double dmg = (round.getPenetrationPower() >= 20)
            ? round.getFleshDamage()
            : round.getFleshDamage() * 0.6;
        currentHealth = Math.max(0, currentHealth - dmg);
    }

    public String statusString() {
        return String.format("[HP: %.0f/%.0f]  [%s | Mag: %d/%d | Reserve: %d]",
            currentHealth, maxHealth,
            weapon.getName(), ammoCount, weapon.getCapacity(), reserveAmmo);
    }
}