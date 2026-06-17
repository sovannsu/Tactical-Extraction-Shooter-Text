package game;

import java.util.HashMap;
import java.util.Map;

/**
 * A single location in the game world.
 * Rooms are connected to each other by named directions (e.g. "north", "east").
 * Each room may have a description and may spawn an enemy encounter.
 */
public class Room {
    private String name;
    private String description;       // What the player sees on EXAMINE
    private String atmosphere;        // Short flavour line shown on entry
    private Map<String, String> exits; // Direction -> Room name
    private boolean hasEnemy;         // Does this room currently have a threat?
    private String map;               // Which map this room belongs to (for enemy generation)
    private String time;              // Day / Night

    public Room(String name, String description, String atmosphere, String map, String time) {
        this.name = name;
        this.description = description;
        this.atmosphere = atmosphere;
        this.map = map;
        this.time = time;
        this.exits = new HashMap<>();
        // Roughly one-third of rooms have an active enemy on entry
        this.hasEnemy = (Math.random() < 0.40);
    }

    // --- Accessors ---

    public String getName() { return name; }

    public String getDescription() { return description; }

    public String getAtmosphere() { return atmosphere; }

    public String getMap() { return map; }

    public String getTime() { return time; }

    public boolean hasEnemy() { return hasEnemy; }

    public void clearEnemy() { hasEnemy = false; }

    public Map<String, String> getExits() { return exits; }

    public void addExit(String direction, String roomName) {
        exits.put(direction.toLowerCase(), roomName);
    }

    /**
     * Returns a formatted string listing all exits.
     */
    public String exitsString() {
        if (exits.isEmpty()) return "  No visible exits.";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : exits.entrySet()) {
            sb.append("  [").append(e.getKey().toUpperCase()).append("] -> ").append(e.getValue()).append("\n");
        }
        return sb.toString().stripTrailing();
    }
}