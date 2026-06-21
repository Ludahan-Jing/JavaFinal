package pvz.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import pvz.GamePanel;

public class WinState implements GameState {
    private final GamePanel panel;

    public WinState(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void update(double dt) {
        // Game has ended; no further simulation
    }

    @Override
    public void draw(Graphics2D g) {
        PlayingState.drawGameScene(panel, g);
        DrawUtils.drawEndScreen(panel, g, true);
    }

    @Override
    public void handleKey(KeyEvent e) {
        // No keyboard actions on the end screen
    }
}
