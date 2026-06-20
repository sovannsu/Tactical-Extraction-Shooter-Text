package game;

/**
 * A sink for game narration. The game logic no longer calls System.out
 * directly — it calls out.println(...), and the *caller* decides where that
 * goes:
 *
 *   Terminal / console:  GameOutput out = System.out::println;
 *   Swing window:        GameOutput out = line -> textArea.append(line + "\n");
 *
 * Same game logic, two destinations. This is a functional interface, so a
 * lambda or method reference satisfies it.
 */
@FunctionalInterface
public interface GameOutput {
    void println(String line);
}