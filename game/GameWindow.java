package game;

import javax.swing.*;
import java.awt.*;

/**
 * GameWindow — the Swing view. It owns NO game logic. Its entire job is:
 *
 *   1. Lay out three regions (log, status panel, button bar).
 *   2. Turn button clicks into Game action calls.
 *   3. Repaint the status panel from the Player after every action.
 *   4. Swap the button bar between "explore" and "combat" based on getMode().
 *
 * The whole interaction model is one helper — act(): run a Game method, then
 * refresh(). That's the push-based loop: a click is one turn. Compare this to
 * ConsoleTest's read loop — same controller, different front-end.
 */
public class GameWindow {

    private final JFrame frame = new JFrame("Tactical Extraction Shooter — TEST");

    // CENTER: the narrative log. This is what GameOutput writes to.
    private final JTextArea log = new JTextArea();

    // EAST: live status readouts
    private final JProgressBar healthBar    = new JProgressBar();
    private final JLabel       roomLabel    = new JLabel();
    private final JLabel       modeLabel    = new JLabel();
    private final JLabel       weaponLabel  = new JLabel();
    private final JLabel       magLabel     = new JLabel();
    private final JLabel       reserveLabel = new JLabel();
    private final JLabel       gearLabel    = new JLabel();   // stub until Gear is wired

    // SOUTH: button bar that flips between three sets of controls
    private final CardLayout buttonCards = new CardLayout();
    private final JPanel     buttonPanel = new JPanel(buttonCards);

    private Game game;   // assigned in launch(), before any click can fire

    public GameWindow() {
        buildUI();
    }

    // ── Startup ───────────────────────────────────────────────────────────────

    public void launch() {
        String name = JOptionPane.showInputDialog(null,
            "Enter your operative's name:", "New Session",
            JOptionPane.QUESTION_MESSAGE);
        if (name == null) name = "";   // cancelled → Game defaults to "Operative"

        // The output sink: append each line to the log and scroll to the bottom.
        GameOutput out = this::appendLog;
        game = new Game(name, out);

        frame.setVisible(true);
        game.start();   // writes the intro + first room into the log via `out`
        refresh();      // paint initial status, show the explore buttons
    }

    // ── UI construction ─────────────────────────────────────────────────────────

    private void buildUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);   // centre on screen
        frame.setLayout(new BorderLayout(8, 8));

        // CENTER — log. Monospaced so the ASCII banners and separators line up.
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        log.setMargin(new Insets(8, 8, 8, 8));
        frame.add(new JScrollPane(log), BorderLayout.CENTER);

        frame.add(buildStatusPanel(), BorderLayout.EAST);

        buildButtonPanel();
        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JComponent buildStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.setPreferredSize(new Dimension(240, 0));

        panel.add(header("STATUS"));
        healthBar.setStringPainted(true);
        healthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        panel.add(healthBar);
        panel.add(Box.createVerticalStrut(6));
        panel.add(roomLabel);
        panel.add(modeLabel);
        panel.add(Box.createVerticalStrut(14));

        panel.add(header("WEAPON"));
        panel.add(weaponLabel);
        panel.add(magLabel);
        panel.add(reserveLabel);
        panel.add(Box.createVerticalStrut(14));

        panel.add(header("EQUIPMENT"));
        panel.add(gearLabel);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void buildButtonPanel() {
        // Explore controls
        JPanel explore = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        explore.add(dirButton("North", "north"));
        explore.add(dirButton("South", "south"));
        explore.add(dirButton("East",  "east"));
        explore.add(dirButton("West",  "west"));
        explore.add(actionButton("Examine", () -> game.examine()));
        explore.add(actionButton("Status",  () -> game.status()));

        // Combat controls
        JPanel combat = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        combat.add(actionButton("Shoot",  () -> game.combatShoot()));
        combat.add(actionButton("Reload", () -> game.combatReload()));
        combat.add(actionButton("Flee",   () -> game.combatFlee()));

        // Game-over placeholder
        JPanel over = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        over.add(new JLabel("Session complete — close the window to exit."));

        buttonPanel.add(explore, "explore");
        buttonPanel.add(combat,  "combat");
        buttonPanel.add(over,    "gameover");
    }

    // ── The per-click cycle ─────────────────────────────────────────────────────

    /** Run a Game action, then repaint. This IS the push-based game loop. */
    private void act(Runnable gameAction) {
        gameAction.run();
        refresh();
    }

    private void refresh() {
        Player p = game.getPlayer();

        int hp  = (int) p.getCurrentHealth();
        int max = (int) p.getMaxHealth();
        healthBar.setMaximum(max);
        healthBar.setValue(hp);
        healthBar.setString("HP  " + hp + " / " + max);

        roomLabel.setText("Location: " + p.getCurrentRoom());
        modeLabel.setText("Mode: " + game.getMode());

        weaponLabel.setText("Weapon: " + p.getWeapon().getName());
        magLabel.setText("Mag: " + p.getAmmoCount() + " / " + p.getWeapon().getCapacity());
        reserveLabel.setText("Reserve: " + p.getReserveAmmo());

        gearLabel.setText("<Gear system pending>");

        // Show the right button set for the current mode.
        switch (game.getMode()) {
            case IN_COMBAT     -> buttonCards.show(buttonPanel, "combat");
            case EXPLORING     -> buttonCards.show(buttonPanel, "explore");
            case DEAD, WON     -> buttonCards.show(buttonPanel, "gameover");
        }
    }

    // ── Small helpers ───────────────────────────────────────────────────────────

    private JButton dirButton(String label, String direction) {
        return actionButton(label, () -> game.move(direction));
    }

    private JButton actionButton(String label, Runnable gameAction) {
        JButton b = new JButton(label);
        b.addActionListener(e -> act(gameAction));
        return b;
    }

    private JLabel header(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void appendLog(String line) {
        log.append(line + "\n");
        log.setCaretPosition(log.getDocument().getLength());   // auto-scroll
    }

    // ── Entry point ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // All Swing work happens on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { /* fall back to default L&F */ }
            new GameWindow().launch();
        });
    }
}