package game;

public class Enemy {
	private String name; // Textual name of enemy
	private String enemyType; // Specific enemy type (Scav, PMC, Boss)
	private String faction; // Overall faction
	private boolean group; // Solo or group
	private int followers; // Number of followers if in a group
	private boolean raiders; // Scavenger raiders or not
	private boolean boss; // Boss type or not
	private boolean rogues; // Rogues or not
	private boolean goons; // Goons or not
	private double totalHealth; // Combined head and body health pool
	private double headHealth; // Head health pool
	private double bodyHealth; // Body health pool
	private double aim; // Aim modifier based on enemy difficulty 
	// (Worst aim to best: Scavengers, PMCs/Raiders, Rogues/Boss Guards, Bosses)
	/*
	private Gear[] equipment; // List of gear per person
	private Gear headset; // Headset - index 0 of gear
	private Gear helmet; // Helmet - index 1 of gear
	private Gear vest; // Vest - index 2 of gear
	private Gear unarmoredRig; // Unarmored rig - index 3 of gear
	private Gear armoredRig; // Armored rig - index 2 and 3 of gear (Covers vest and unarmored rig)
	private Gear backpack; // Backpack - index 4 of gear
	private Gun[] guns; // List of weapons per person
	*/
	private Gun primary; // Primary weapon - index 0 of guns
	private String primaryName; // Name of primary weapon
	private Gun secondary; // Secondary weapon if equipped - index 1 of guns
	private String secondaryName; // Name of secondary weapon if equipped
	private static final String[] scavWeapons = { "PM", "SR-1MP", "MP-443", // Pistols indexes 0-2
			"TOZ-106", "MP-133", "MP-153", "Saiga-12K", // Shotguns indexes 3-6
			"PP-91", "Saiga-9", "STM-9", "PPSh-41", // SMGs indexes 7-10
			"AKS-74U", "AK-74", // Assault rifles indexes 11-12
			"SKS", "VPO-101", "VPO-136", "ADAR 2-15", // Assault carbines indexes 13-16
			"VPO-215", "Mosin" }; // Snipers indexes 17-18
	private static final String[] BEARWeapons = { "PM", "SR-1MP", "MP-443", // Pistols indexes 0-2
			"MP-133", "MP-153", "Saiga-12K", "KS-23M", // Shotguns indexes 3-6
			"PP-91", "PP19-01", "STM-9", "PPSh-41", // SMGs indexes 7-10
			"AKS-74U", "AK-74", "AK-104", "AKM", "ASh-12", "AS VAL", // Assault rifles indexes 11-16
			"SKS", "VPO-101", "VPO-136", "ADAR 2-15", "SVDS", // Assault carbines and DMRs indexes 17-21
			"VPO-215", "Mosin", "SV-98", "T-5000" }; // Snipers indexes 22-25
	private static final String[] USECWeapons = { "Glock 17", "FN 5-7", "M9A3", // Pistols indexes 0-2
			"M870", "M590A1", "M3 Super 90", // Shotguns indexes 3-5
			"MP5", "MP7A2", "MP9", "MPX", "FN P90", "UMP", "Vector", // SMGs indexes 6-12
			"MCX", "M4A1", "MDR", "SCAR-L", "SA-58", // Assault rifles indexes 13-17
			"TX-15", "RFB", "SR-25", "M1A", "G28", "RSASS", // Assault carbines and DMRs indexes 18-23
			"M700", "DVL-10", "AXMC" }; // Snipers indexes 24-26
	private static final String[] raiderWeapons = { "AS-VAL", "KBP 9A-91", "AKS-74U", "AK-74M", "AK-12", "AK-104", "AK-105", // RUS weaponry indexes 0-6
			"MP7A2", "FN P90", "MP5", "MCX", "MDR", "M4A1", "AUG A3", "SA-58" }; // NATO weaponry indexes 7-14
	private static final String[] rogueWeapons = { "MCX Spear", "MDR", "M4A1", "HK 416A5", "AUG A3", "SA-58" }; // NATO weaponry indexes 0-5
	/*
	 * Various accessor and modifier methods for above instance variables.
	 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnemyType() {
		return enemyType;
	}

	public void setEnemyType(String type) {
		this.enemyType = type;
	}

	public String getFaction() {
		return faction;
	}

	public void setFaction(String faction) {
		this.faction = faction;
	}

	public Boolean getRaiders() {
		return raiders;
	}

	public void setRaiders(Boolean raiders) {
		this.raiders = raiders;
	}

	public Boolean getRogues() {
		return rogues;
	}

	public void setRogues(Boolean rogues) {
		this.rogues = rogues;
	}

	public Boolean getGoons() {
		return goons;
	}

	public void setGoons(Boolean goons) {
		this.goons = goons;
	}

	public Boolean getBoss() {
		return boss;
	}

	public void setBoss(Boolean boss) {
		this.boss = boss;
	}

	public Boolean getGroup() {
		return group;
	}

	public void setGroup(Boolean group) {
		this.group = group;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public double getTotalHealth() {
		return totalHealth;
	}

	public void setTotalHealth(double health) {
		this.totalHealth = health;
	}

	public double getHeadHealth() {
		return headHealth;
	}

	public void setHeadHealth(double health) {
		this.headHealth = health;
	}

	public double getBodyHealth() {
		return bodyHealth;
	}

	public void setBodyHealth(double health) {
		this.bodyHealth = health;
	}

	public double getAim() {
		return aim;
	}

	public void setAim(double aim) {
		this.aim = aim;
	}

	public String getPrimaryName() {
		return primaryName;
	}

	public void setPrimaryName(String weapon) {
		this.primaryName = weapon;
	}

	public String getSecondaryName() {
		return secondaryName;
	}

	public void setSecondaryName(String weapon) {
		this.secondaryName = weapon;
	}

	public Gun getPrimary() {
		return primary;
	}

	public void setPrimary(Gun primary) {
		this.primary = primary;
	}

	public Gun getSecondary() {
		return secondary;
	}

	public void setSecondary(Gun secondary) {
		this.secondary = secondary;
	}

	/** Armor class protecting the head; 0 = unarmored.
	 *  Enemies carry no armor yet — wire this to real helmet gear once the
	 *  enemy loadout system is built. */
	public int getHelmetArmorClass() {
		return 0;
	}

	/** Armor class protecting the body; 0 = unarmored.
	 *  Wire this to real vest / rig gear once enemy loadouts are built. */
	public int getBodyArmorClass() {
		return 0;
	}

	/*
	 * Overall method for creating enemies for the player to fight. Creates an array
	 * of Enemy types for the current enemy encounter.
	 */
	public Enemy[] createEnemies(String map, String time) {
		Enemy[] enemies = new Enemy[4];
		int random = (int) (Math.random() * (20)) + 1;
		if (random < 11) { // Majority of enemies fought will be scavengers (scavs)
			scavGroup(enemies);
		} else if (random > 10 && random < 16) { // Second largest population of enemies will be PMCs
			PMCSquad(enemies);
		} else { // Bosses, rogues, or raiders depending on map and time
			specialEnemies(enemies, map, time);
		}
		return enemies;
	}

	public void scavGroup(Enemy[] group) {
		Enemy first = new Enemy();
		first.setEnemyType("Scav");
		first.setBoss(false);
		int random = (int) (Math.random() * 5) + 1;
		if (random > 2) { // Three-fifths chance of a group of scavs (duo, trio, or squad)
			first.setGroup(true);
			random = (int) (Math.random() * 3) + 2;
			for (int i = 0; i < random; i++) {
				group[i] = createScav();
				group[i].setGroup(true);
			}
		} else {
			group[0] = createScav();
			group[0].setGroup(false);
		}
	}

	public static Enemy createScav() {
		Enemy scav = new Enemy();
		scav.setName("The scav");
		scav.setEnemyType("Scav");
		scav.setFaction("Scavengers");
		scav.setTotalHealth(440);
		scav.setHeadHealth(35);
		scav.setBodyHealth(405);
		scav.setRaiders(false);
		scav.setRogues(false);
		scav.setGoons(false);
		scav.setBoss(false);
		scav.setGroup(false);
		scav.setFollowers(0);
		scav.setAim(0.30); // Scavs have poor aim
		// One-in-eight chance the scav only has a pistol (indexes 0-2);
		// otherwise a non-pistol (indexes 3-18).
		if ((int) (Math.random() * 8) == 0) { // Pistol scav
			int idx = (int) (Math.random() * 3); // 0-2
			scav.setPrimaryName("");
			scav.setSecondaryName(scavWeapons[idx]);
			scav.setSecondary(new Gun(scavWeapons[idx]));
		} else { // Non-pistol scav
			int idx = (int) (Math.random() * 16) + 3; // 3-18
			scav.setPrimaryName(scavWeapons[idx]);
			scav.setPrimary(new Gun(scavWeapons[idx]));
			scav.setSecondaryName("");
		}
		return scav;
	}

	public void PMCSquad(Enemy[] squad) {
		Enemy first = new Enemy();
		first.setEnemyType("PMC");
		first.setBoss(false);
		int random = (int) (Math.random() * 5) + 1;
		if (random > 4) { // One-fifth chance of a group of PMCs (duo, trio, or squad)
			first.setGroup(true);
			random = (int) (Math.random() * 3) + 2;
			if (random == 1) { // BEAR squad
				random = (int) (Math.random() * 4) + 1;
				for (int i = 0; i < random; i++) {
					squad[i] = createBEAR();
					squad[i].setGroup(true);
				}
			} else { // USEC squad
				random = (int) (Math.random() * 3) + 2;
				for (int i = 0; i < random; i++) {
					squad[i] = createUSEC();
					squad[i].setGroup(true);
				}
			}
		} else {
			random = (int) (Math.random() * 2) + 1;
			if (random == 1) { // Solo BEAR
				squad[0] = createBEAR();
				squad[0].setGroup(false);
			} else { // Solo USEC
				squad[0] = createUSEC();
				squad[0].setGroup(false);
			}
		}
	}

	public static Enemy createBEAR() {
		Enemy bear = new Enemy();
		bear.setName("The BEAR");
		bear.setEnemyType("PMC");
		bear.setFaction("BEAR");
		bear.setTotalHealth(440);
		bear.setHeadHealth(35);
		bear.setBodyHealth(405);
		bear.setRaiders(false);
		bear.setRogues(false);
		bear.setGoons(false);
		bear.setBoss(false);
		bear.setGroup(false);
		bear.setFollowers(0);
		int random = (int) (Math.random() * 24) + 3;
		if (random != 26) { // BEAR non-pistols indexes 3-25
			bear.setPrimaryName(BEARWeapons[random]);
			bear.setPrimary(new Gun(BEARWeapons[random]));
			bear.setSecondaryName("");
		} else { // BEAR pistols indexes 0-2
			bear.setPrimaryName("");
			random = (int) (Math.random() * 3);
			bear.setSecondaryName(BEARWeapons[random]);
			bear.setSecondary(new Gun(BEARWeapons[random]));
		}
		return bear;
	}

	public static Enemy createUSEC() {
		Enemy usec = new Enemy();
		usec.setName("The USEC");
		usec.setEnemyType("PMC");
		usec.setFaction("USEC");
		usec.setTotalHealth(440);
		usec.setHeadHealth(35);
		usec.setBodyHealth(405);
		usec.setRaiders(false);
		usec.setRogues(false);
		usec.setGoons(false);
		usec.setBoss(false);
		usec.setGroup(false);
		usec.setFollowers(0);
		int random = (int) (Math.random() * 25) + 3;
		if (random != 27) { // USEC non-pistols indexes 3-26
			usec.setPrimaryName(USECWeapons[random]);
			usec.setPrimary(new Gun(USECWeapons[random]));
			usec.setSecondaryName("");
		} else { // USEC pistols indexes 0-2
			usec.setPrimaryName("");
			random = (int) (Math.random() * 3);
			usec.setSecondaryName(USECWeapons[random]);
			usec.setSecondary(new Gun(USECWeapons[random]));
		}
		return usec;
	}

	/** A PMC encounter: equally likely BEAR or USEC. Both are tougher than scavs —
	 *  better aim (0.55 vs 0.30) and better weapon pools. */
	public static Enemy createPMC() {
		return (Math.random() < 0.5) ? createBEAR() : createUSEC();
	}
	public void specialEnemies(Enemy[] enemies, String map, String time) {
		Enemy first = new Enemy();
		first.setEnemyType("Boss");
		first.setBoss(true);
		if (time.equals("Day")) {
			switch (map) {
			case "Customs":
				int random = (int) (Math.random() * (20)) + 1;
				if (random > 15) { // One-quarter chance of the Goons
					createGoons(enemies);
				} else { // Reshala
					bossGroup(enemies, map, time);
				}
				break;
			case "Woods":
				random = (int) (Math.random() * (20)) + 1;
				if (random > 15) { // One-quarter chance of the Goons
					createGoons(enemies);
				} else { // Shturman
					bossGroup(enemies, map, time);
				}
				break;
			case "Shoreline":
				random = (int) (Math.random() * (20)) + 1;
				if (random > 15) { // One-quarter chance of the Goons
					createGoons(enemies);
				} else { // Sanitar
					bossGroup(enemies, map, time);
				}
				break;
			case "Lighthouse":
				random = (int) (Math.random() * (20)) + 1;
				if (random < 6) { // One-quarter chance of the Goons
					createGoons(enemies);
				} else if (random < 16) { // One-half chance of a squad of Rogues
					rogueSquad(enemies);
				} else { // One-quarter chance of Zryachiy
					bossGroup(enemies, map, time);
				}
				break;
			case "Reserve":
				random = (int) (Math.random() * (20)) + 1;
				if (random < 16) { // Three-quarters chance of Raiders
					raiderSquad(enemies);
				} else { // One-quarter chance of Glukhar
					bossGroup(enemies, map, time);
				}
				break;
			case "Labs":
				raiderSquad(enemies);
			}
		} else if (time.equals("Night")) {

		}
	}

	public void raiderSquad(Enemy[] squad) {
		for (int i = 0; i < 4; i++) {
			squad[i] = createRaider();
		}
	}

	public Enemy createRaider() {
		Enemy raider = new Enemy();
		raider.setName("The raider");
		raider.setEnemyType("Raiders");
		raider.setFaction("Scavengers");
		raider.setTotalHealth(752);
		raider.setHeadHealth(42);
		raider.setBodyHealth(710);
		raider.setRaiders(false);
		raider.setRogues(true);
		raider.setGoons(false);
		raider.setBoss(true);
		raider.setGroup(true);
		raider.setFollowers(0);
		int random = (int) (Math.random() * 15);
		raider.setPrimaryName(raiderWeapons[random]);
		raider.setPrimary(new Gun(raiderWeapons[random]));
		return raider;
	}

	public void rogueSquad(Enemy[] squad) {
		for (int i = 0; i < 4; i++) {
			squad[i] = createRogue();
		}
	}

	public Enemy createRogue() {
		Enemy rogue = new Enemy();
		rogue.setName("The rogue");
		rogue.setEnemyType("Boss");
		rogue.setFaction("Rogues");
		rogue.setTotalHealth(780);
		rogue.setHeadHealth(40);
		rogue.setBodyHealth(740);
		rogue.setRaiders(false);
		rogue.setRogues(true);
		rogue.setGoons(false);
		rogue.setBoss(true);
		rogue.setGroup(true);
		rogue.setFollowers(0);
		int random = (int) (Math.random() * 6);
		rogue.setPrimaryName(rogueWeapons[random]);
		rogue.setPrimary(new Gun(rogueWeapons[random]));
		return rogue;
	}

	public void bossGroup(Enemy[] group, String map, String time) {
		Enemy boss = new Enemy();
		boss.setEnemyType("Boss");
		boss.setFaction("Scavengers");
		boss.setBoss(true);
		if (time.equals("Day")) { // Daytime bosses
			if (map.equals("Factory")) { // Tagilla
				boss.setName("Tagilla");
				boss.setTotalHealth(1220);
				boss.setHeadHealth(100);
				boss.setBodyHealth(1120);
				boss.setRaiders(false);
				boss.setRogues(false);
				boss.setGoons(false);
				boss.setGroup(false);
				boss.setFollowers(0);
				int random = (int) (Math.random() * 2) + 1;
				if (random == 1) {
					boss.setPrimaryName("Saiga-12K"); // 12 Gauge
					boss.setPrimary(new Gun("Saiga-12K"));
					boss.setSecondaryName("Dead Blow Hammer");
				} else {
					boss.setPrimaryName("AKS-74U"); // 5.45x39mm
					boss.setPrimary(new Gun("AKS-74U"));
					boss.setSecondaryName("Dead Blow Hammer");
				}
			}
			if (map.equals("Interchange")) { // Killa
				boss.setName("Killa");
				boss.setTotalHealth(890);
				boss.setHeadHealth(70);
				boss.setBodyHealth(820);
				boss.setRaiders(false);
				boss.setRogues(false);
				boss.setGoons(false);
				boss.setGroup(false);
				boss.setFollowers(0);
				boss.setPrimaryName("RPK-16"); // 5.45x39mm
				boss.setPrimary(new Gun("RPK-16"));
			}
			if (map.equals("Reserve")) { // Glukhar
				boss.setName("Glukhar");
				boss.setTotalHealth(1010);
				boss.setHeadHealth(70);
				boss.setBodyHealth(940);
				boss.setRaiders(false);
				boss.setRogues(false);
				boss.setGoons(false);
				boss.setGroup(true);
				boss.setFollowers(3);
				boss.setPrimaryName("ASh-12"); // 12.7x55mm
				boss.setPrimary(new Gun("ASh-12"));
			}
			if (map.equals("Streets")) { // Kaban or Kollontay
				int random = (int) (Math.random() * (20)) + 1;
				if (random < 11) { // Kaban
					boss.setName("Kaban");
					boss.setTotalHealth(1300);
					boss.setHeadHealth(85);
					boss.setBodyHealth(1215);
					boss.setRaiders(false);
					boss.setRogues(false);
					boss.setGoons(false);
					boss.setGroup(true);
					boss.setFollowers(3);
					random = (int) (Math.random() * 2) + 1;
					if (random == 1) {
						boss.setPrimaryName("PKM"); // 7.62x54R
						boss.setPrimary(new Gun("PKM"));
					} else {
						boss.setPrimaryName("PKP"); // 7.62x54R
						boss.setPrimary(new Gun("PKP"));
					}
				} else { // Kollontay
					boss.setName("Kollontay");
					boss.setTotalHealth(1055);
					boss.setHeadHealth(65);
					boss.setBodyHealth(990);
					boss.setRaiders(false);
					boss.setRogues(false);
					boss.setGoons(false);
					boss.setGroup(true);
					boss.setFollowers(3);
					if (random == 1) {
						boss.setPrimaryName("RPD"); // 7.62x39mm
						boss.setPrimary(new Gun("RPD"));
						boss.setSecondaryName("PR-Taran Baton");
					} else {
						boss.setPrimaryName("RPDN"); // 7.62x39mm
						boss.setPrimary(new Gun("RPDN"));
						boss.setSecondaryName("PR-Taran Baton");
					}
				}
			}
			if (map.equals("Customs")) { // Reshala
				boss.setName("Reshala");
				boss.setTotalHealth(812);
				boss.setHeadHealth(62);
				boss.setBodyHealth(750);
				boss.setRaiders(false);
				boss.setRogues(false);
				boss.setGoons(false);
				boss.setGroup(true);
				boss.setFollowers(3);
				boss.setPrimaryName("AK-101"); // 5.56x45mm
				boss.setPrimary(new Gun("AK-101"));
				boss.setSecondaryName("Golden TT-33"); // 7.62x25mm
				boss.setSecondary(new Gun("Golden TT-33"));
			}
			if (map.equals("Woods")) { // Shturman
				boss.setName("Shturman");
				boss.setTotalHealth(812);
				boss.setHeadHealth(62);
				boss.setBodyHealth(750);
				boss.setRaiders(false);
				boss.setRogues(false);
				boss.setGoons(false);
				boss.setGroup(true);
				boss.setFollowers(2);
				boss.setPrimaryName("SVDS"); // 7.62x54R
				boss.setPrimary(new Gun("SVDS"));
				boss.setSecondaryName("Red Rebel Icepick");
			}
			if (map.equals("Shoreline")) { // Sanitar
				boss.setName("Sanitar");
				boss.setTotalHealth(812);
				boss.setHeadHealth(62);
				boss.setBodyHealth(750);
				boss.setRaiders(false);
				boss.setRogues(false);
				boss.setGoons(false);
				boss.setGroup(true);
				boss.setFollowers(3);
				boss.setPrimaryName("VSS Vintorez"); // 9x39mm
				boss.setPrimary(new Gun("VSS Vintorez"));
			}
			if (map.equals("Lighthouse")) { // Zryachiy or Rogues
				int random = (int) (Math.random() * (20)) + 1;
				if (random < 5) { // Zryachiy
					boss.setName("Zryachiy");
					boss.setTotalHealth(812);
					boss.setHeadHealth(62);
					boss.setBodyHealth(750);
					boss.setRaiders(false);
					boss.setRogues(false);
					boss.setGoons(false);
					boss.setGroup(true);
					boss.setFollowers(2);
					boss.setPrimaryName("SVDS"); // 7.62x54R
					boss.setPrimary(new Gun("SVDS"));
				}
			}
		} else { // Nighttime bosses and cultists

		}
		if (group[0].getGroup()) {
			for (int i = 1; i < (boss.getFollowers() + 1); i++) {
				group[i] = createGuard();
			}
		}
	}

	public Enemy createGuard() { // Followers of the Boss
		Enemy guard = new Enemy();
		guard.setName("The boss's guard");
		guard.setEnemyType("Scav");
		guard.setFaction("Scavengers");
		guard.setTotalHealth(580);
		guard.setHeadHealth(40);
		guard.setBodyHealth(540);
		guard.setRaiders(false);
		guard.setRogues(false);
		guard.setGoons(false);
		guard.setBoss(false);
		guard.setGroup(true);
		guard.setFollowers(0);
		int random = (int) (Math.random() * 2) + 1;
		if (random == 1) { // USEC weaponry
			random = (int) (Math.random() * 25) + 3;
			guard.setPrimaryName(USECWeapons[random]);
			guard.setPrimary(new Gun(USECWeapons[random]));
			guard.setSecondaryName("");
		} else { // BEAR weaponry
			random = (int) (Math.random() * 24) + 3;
			guard.setPrimaryName(BEARWeapons[random]);
			guard.setPrimary(new Gun(BEARWeapons[random]));
			guard.setSecondaryName("");
		}
		return guard;
	}

	public Enemy[] createGoons(Enemy[] goons) { // The Goons
		Enemy knight = new Enemy();
		knight.setName("Knight");
		knight.setEnemyType("Boss");
		knight.setFaction("Rogues");
		knight.setTotalHealth(1120);
		knight.setHeadHealth(80);
		knight.setBodyHealth(1040);
		knight.setRaiders(false);
		knight.setRogues(false);
		knight.setGoons(true);
		knight.setBoss(true);
		knight.setGroup(true);
		knight.setFollowers(0);
		knight.setPrimaryName("Mk47 Mutant"); // 7.62x39mm
		knight.setPrimary(new Gun("Mk47 Mutant"));
		knight.setSecondaryName("Glock 18C"); // 9x19mm
		knight.setSecondary(new Gun("Glock 18C"));
		goons[0] = knight;
		Enemy bigPipe = new Enemy();
		bigPipe.setName("Big Pipe");
		bigPipe.setEnemyType("Boss");
		bigPipe.setFaction("Rogues");
		bigPipe.setTotalHealth(910);
		bigPipe.setHeadHealth(70);
		bigPipe.setBodyHealth(840);
		bigPipe.setRaiders(false);
		bigPipe.setRogues(false);
		bigPipe.setGoons(true);
		bigPipe.setBoss(true);
		bigPipe.setGroup(true);
		bigPipe.setFollowers(0);
		bigPipe.setPrimaryName("MCX"); // .300
		bigPipe.setPrimary(new Gun("MCX"));
		bigPipe.setSecondaryName("M32A1 MSGL"); // 40x46mm
		bigPipe.setSecondary(new Gun("M32A1 MSGL"));
		goons[1] = bigPipe;
		Enemy birdeye = new Enemy();
		birdeye.setName("Birdeye");
		birdeye.setEnemyType("Boss");
		birdeye.setFaction("Rogues");
		birdeye.setTotalHealth(795);
		birdeye.setHeadHealth(70);
		birdeye.setBodyHealth(725);
		birdeye.setRaiders(false);
		birdeye.setRogues(false);
		birdeye.setGoons(true);
		birdeye.setBoss(true);
		birdeye.setGroup(true);
		birdeye.setFollowers(0);
		birdeye.setPrimaryName("RSASS"); // 7.62x51mm
		birdeye.setPrimary(new Gun("RSASS"));
		birdeye.setSecondaryName("M9A3"); // 9x19mm
		birdeye.setSecondary(new Gun("M9A3"));
		goons[2] = birdeye;
		return goons;
	}

	public void shoot() {

	}

	public void cover() {

	}

	public void suppress() {

	}

	public void reload() {

	}

	public void die() {

	}
}