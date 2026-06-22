package pvz;

/**
 * GameEngine controls the main game loop (Swing Timer) and drives the
 * game state updates by calling GameWorld.update(dt). It delegates
 * UI responsibilities back to GamePanel where needed.
 */
public class GameEngine {
    private final GamePanel panel;
    private final GameWorld world;
    private final javax.swing.Timer timer;

    public GameEngine(GamePanel panel, GameWorld world) {
        this.panel = panel;
        this.world = world;
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
        if (panel.getState() != GamePanel.State.PLAYING) {
            panel.repaint();
            return;
        }

        long now = System.currentTimeMillis();
        double dt = (now - panel.getLastUpdate()) / 1000.0;
        if (dt > 0.1) dt = 0.1;
        panel.setLastUpdate(now);

        GameWorld.UpdateResult res = world.update(dt);
        if (res == GameWorld.UpdateResult.LOSE) {
            if (panel.getState() != GamePanel.State.LOSE) {
                panel.setState(GamePanel.State.LOSE);
                stop();
                panel.repaint();
            }
        } else if (res == GameWorld.UpdateResult.WIN) {
            if (panel.getState() != GamePanel.State.WIN) {
                panel.setState(GamePanel.State.WIN);
                stop();
                panel.repaint();
            }
        } else {
            panel.repaint();
        }
    }
}