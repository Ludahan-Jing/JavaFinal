package pvz;

import pvz.state.PlayingState;

/**
 * GameEngine controls the main game loop (Swing Timer) and drives the
 * game state updates by delegating to the current GameState.update(dt).
 */
public class GameEngine {
    private final GamePanel panel;
    private final javax.swing.Timer timer;

    public GameEngine(GamePanel panel) {
        this.panel = panel;
        // ~60 FPS
        this.timer = new javax.swing.Timer(16, e -> tick());
    }

    public void start() {
        // ensure lastUpdate is set by panel (startGame sets it), but set to now as fallback
        if (panel.getLastUpdate() == 0) panel.setLastUpdate(System.currentTimeMillis());
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void tick() {
        double dt = 0;
        if (panel.getGameState() instanceof PlayingState) {
            long now = System.currentTimeMillis();
            dt = (now - panel.getLastUpdate()) / 1000.0;
            if (dt > 0.1) dt = 0.1;
            panel.setLastUpdate(now);
        }

        panel.getGameState().update(dt);
        panel.repaint();
    }
}
