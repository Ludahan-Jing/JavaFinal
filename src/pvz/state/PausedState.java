package pvz.state;

import java.awt.*;
import java.awt.event.KeyEvent;
import pvz.Constants;
import pvz.GamePanel;

public class PausedState implements GameState {
    private final GamePanel panel;

    public PausedState(GamePanel panel) {
        this.panel = panel;
        panel.preparePauseMenuButtons();
        panel.markPauseStart();
    }

    @Override
    public void update(double dt) {
        // Game simulation is frozen while paused
    }

    @Override
    public void draw(Graphics2D g) {
        PlayingState.drawGameScene(panel, g);
        drawPauseOverlay(g);
    }

    @Override
    public void handleKey(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            panel.resumeFromPause();
        }
    }

    private void drawPauseOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        g.setFont(new Font("Dialog", Font.BOLD, 48));
        g.setColor(Color.WHITE);
        String txt = "⏸ 遊戲暫停";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(txt, (Constants.WINDOW_WIDTH - fm.stringWidth(txt)) / 2, 260);
        g.setFont(new Font("Dialog", Font.PLAIN, 20));
        g.setColor(new Color(200, 220, 200));
        String hint = "選擇一項操作";
        fm = g.getFontMetrics();
        g.drawString(hint, (Constants.WINDOW_WIDTH - fm.stringWidth(hint)) / 2, 300);

        int bx = Constants.WINDOW_WIDTH / 2 - Constants.END_BTN_HALF_WIDTH;
        if (panel.getPauseBtnContinueBounds() == null) {
            panel.preparePauseMenuButtons();
        }

        Rectangle cont = panel.getPauseBtnContinueBounds();
        Rectangle rst  = panel.getPauseBtnRestartBounds();
        Rectangle end  = panel.getPauseBtnEndBounds();
        Point mousePos = panel.getMousePos();

        Color contFill = cont.contains(mousePos) ? new Color(90, 210, 90) : new Color(60, 160, 60);
        DrawUtils.drawButton(g, cont.x, cont.y, cont.width, cont.height,
            contFill, new Color(40, 120, 40), "繼續遊戲", Color.WHITE, 20);

        Color rstFill = rst.contains(mousePos) ? new Color(255, 200, 80) : new Color(200, 140, 0);
        DrawUtils.drawButton(g, rst.x, rst.y, rst.width, rst.height,
            rstFill, new Color(150, 100, 10), "重新開始", Color.WHITE, 20);

        Color endFill = end.contains(mousePos) ? new Color(240, 100, 100) : new Color(200, 60, 60);
        DrawUtils.drawButton(g, end.x, end.y, end.width, end.height,
            endFill, new Color(140, 30, 30), "結束遊戲", Color.WHITE, 20);
    }
}
