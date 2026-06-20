package game;

/**
 * Explicit game state. In the terminal build, "what mode am I in" was implicit —
 * it was simply *where execution happened to be* (main()'s loop vs. inside
 * Combat.fight()). A push-based GUI can't rely on that, so the mode becomes a
 * value the view can query to decide which buttons to show.
 */
public enum Mode {
    EXPLORING,   // free to move / examine — show explore buttons
    IN_COMBAT,   // a fight is active      — show combat buttons (shoot/reload/flee)
    DEAD,        // player eliminated      — game over
    WON          // reached extraction     — game over
}