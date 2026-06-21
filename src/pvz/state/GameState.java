package pvz.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public interface GameState {
    void update(double dt);
    void draw(Graphics2D g);
    void handleKey(KeyEvent e);
}
