package game;

public class EnemyEncounter {
	private String map; // Map location
	private String time; // Day or night
	private Enemy[] enemies; // Array version of enemy/enemies
	private Enemy currentEnemy; // Currently focused enemy
	private Enemy secondaryEnemy1; // Secondary enemy if any
	private Enemy secondaryEnemy2; // Secondary enemy if any
	private Enemy secondaryEnemy3; // Secondary enemy if any
	/*
	 * Various accessor and modifier methods for above instance variables.
	 */

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Enemy[] getEnemies() {
		return enemies;
	}

	public void setEnemies(Enemy[] enemies) {
		this.enemies = enemies;
	}

	public Enemy getCurrentEnemy() {
		return currentEnemy;
	}

	public void setCurrentEnemy(Enemy currentEnemy) {
		this.currentEnemy = currentEnemy;
	}

	public Enemy getSecondaryEnemy1() {
		return secondaryEnemy1;
	}

	public void setSecondaryEnemy1(Enemy secondaryEnemy1) {
		this.secondaryEnemy1 = secondaryEnemy1;
	}

	public Enemy getSecondaryEnemy2() {
		return secondaryEnemy2;
	}

	public void setSecondaryEnemy2(Enemy secondaryEnemy2) {
		this.secondaryEnemy2 = secondaryEnemy2;
	}

	public Enemy getSecondaryEnemy3() {
		return secondaryEnemy3;
	}

	public void setSecondaryEnemy3(Enemy secondaryEnemy3) {
		this.secondaryEnemy3 = secondaryEnemy3;
	}

	public EnemyEncounter(String map, String time) {
		this.map = map;
		this.time = time;
		// enemies = createEnemies(map, time);
		currentEnemy = enemies[0];
		secondaryEnemy1 = enemies[1];
		secondaryEnemy2 = enemies[2];
		secondaryEnemy3 = enemies[3];
		if (currentEnemy.getEnemyType().equals("Scav")) { // Scav or raider encounter
			if (currentEnemy.getGroup()) { // Group of scavs or raiders

			} else { // Solo scav

			}
		} else if (currentEnemy.getEnemyType().equals("PMC")) { // PMC encounter
			if (currentEnemy.getGroup()) { // Group of PMCs

			} else { // Solo PMC

			}
		} else if (currentEnemy.getEnemyType().equals("Boss")) { // Boss encounter
			if (currentEnemy.getName().equals("The rogue")) {

			}

		}
	}

	public void fight(String map, String time) {

	}

	public void flee(String map, String time) {

	}

	public void enemyDeath() {

	}
}
