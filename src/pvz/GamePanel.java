package pvz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {

    // ── Game state ─────────────────────────────────────────────────────────────
    public enum State { MENU, PLAYING, PAUSED, WIN, LOSE }
    private State state = State.MENU;

    // ── Game model ─────────────────────────────────────────────────────────────
    private final GameWorld world = new GameWorld();

    // ── UI selection/hover state (shared by input listener and renderer) ───────
    private final UiSelectionState uiState = new UiSelectionState();

    // ── Rendering (all drawXxx logic lives here now) ────────────────────────────
    private final GameRenderer renderer = new GameRenderer(world, uiState);

    // ── Timing ─────────────────────────────────────────────────────────────────
    private long lastUpdate;
    private long gameStartTime;
    private long pauseStart;
    private long totalPauseTime;

    // ── Game engine (controls timer & update loop) ─────────────────────────────
    private GameEngine engine;

    // ── Window ─────────────────────────────────────────────────────────────────
    private JFrame frame;

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(new Color(80, 140, 60));
        // allow keyboard focus so ESC and other keys work
        setFocusable(true);
        // attach extracted input listener
        GameInputListener inputListener = new GameInputListener(this, world, uiState);
        addMouseListener(inputListener);
        addMouseMotionListener(inputListener);

        // Keyboard input
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e);
            }
        });

        // engine created after world is initialized
        engine = new GameEngine(this, world);
        initWindow();
    }

    private void initWindow() {
        frame = new JFrame("🌻 植物大戰殭屍 - Java Edition");
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    public JFrame getFrame() {
        return frame;
    }

    // Exposed for GameEngine to inspect/control the state and timing
    public State getState() { return state; }
    public void setState(State s) { this.state = s; }
    public GameWorld getWorld() { return world; }
    public synchronized long getLastUpdate() { return lastUpdate; }
    public synchronized void setLastUpdate(long v) { lastUpdate = v; }
    public void stopEngine() { if (engine != null) engine.stop(); }

    // ── Start / Reset ──────────────────────────────────────────────────────────
    public void startGame() {
        world.start();
        lastUpdate     = System.currentTimeMillis();
        gameStartTime  = System.currentTimeMillis();
        totalPauseTime = 0;
        uiState.clearSelectedPlant();
        state          = State.PLAYING;
        engine.start();
        // ensure this panel has keyboard focus so ESC is received
        javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }

    // Game updates are driven by GameEngine -> see src/pvz/GameEngine.java

    // ── Painting ───────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        renderer.render((Graphics2D) g0, state);
    }

    // ── Keyboard ───────────────────────────────────────────────────────────────
    public void handleKey(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (state == State.PLAYING) {
                enterPauseMenu();
            } else if (state == State.PAUSED) {
                resumeFromPause();
            }
        }
    }

    // Helper methods for the input listener (and ESC key) to control pause/resume flows
    public void enterPauseMenu() {
        state = State.PAUSED;
        pauseStart = System.currentTimeMillis();
        repaint();
    }

    public void resumeFromPause() {
        state = State.PLAYING;
        totalPauseTime += System.currentTimeMillis() - pauseStart;
        lastUpdate = System.currentTimeMillis();
        repaint();
    }

    public void endGameFromPauseAsLose() {
        state = State.LOSE;
        if (engine != null) engine.stop();
        repaint();
    }
}