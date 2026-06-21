package pvz.state;

import java.awt.*;
import pvz.Constants;
import pvz.GamePanel;

/** Shared drawing utilities used across multiple game states. */
final class DrawUtils {
    private DrawUtils() {}

    static void drawSky(Graphics2D g) {
        GradientPaint sky = new GradientPaint(0, 0, new Color(135, 206, 235), 0, 120, new Color(180, 230, 180));
        g.setPaint(sky);
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, 120);
    }

    static void drawGrassLawn(Graphics2D g) {
        g.setColor(new Color(120, 80, 40));
        g.fillRect(Constants.GRID_X, Constants.GRID_Y,
                Constants.COLS * Constants.CELL_W, Constants.ROWS * Constants.CELL_H);

        for (int r = 0; r < Constants.ROWS; r++) {
            for (int c = 0; c < Constants.COLS; c++) {
                int x = Constants.GRID_X + c * Constants.CELL_W;
                int y = Constants.GRID_Y + r * Constants.CELL_H;
                Color grass = (r + c) % 2 == 0 ? new Color(90, 160, 60) : new Color(80, 150, 55);
                g.setColor(grass);
                g.fillRect(x, y, Constants.CELL_W, Constants.CELL_H);
            }
        }
    }

    static void drawButton(Graphics2D g, int x, int y, int w, int h,
                           Color fill, Color border, String text, Color textColor, int fontSize) {
        g.setColor(fill);
        g.fillRoundRect(x, y, w, h, 14, 14);
        g.setColor(border);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 14, 14);
        g.setStroke(new BasicStroke(1));
        g.setFont(new Font("Dialog", Font.BOLD, fontSize));
        g.setColor(textColor);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, x + (w - fm.stringWidth(text)) / 2, y + h / 2 + fm.getAscent() / 2 - 2);
    }

    static void drawEndScreen(GamePanel panel, Graphics2D g, boolean win) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setFont(new Font("Dialog", Font.BOLD, 56));
        String title = win ? "🎉 你贏了！" : "💀 遊戲結束";
        g.setColor(win ? new Color(100, 255, 100) : new Color(255, 80, 80));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (Constants.WINDOW_WIDTH - fm.stringWidth(title)) / 2, 230);

        g.setFont(new Font("Dialog", Font.PLAIN, 22));
        g.setColor(new Color(220, 220, 200));
        String sub = win ? "所有殭屍已被消滅！你的花園得救了！" : "殭屍衝進了你的花園...";
        fm = g.getFontMetrics();
        g.drawString(sub, (Constants.WINDOW_WIDTH - fm.stringWidth(sub)) / 2, 285);

        var world = panel.getWorld();
        String stat = "消滅殭屍: " + world.zombiesKilled + " 個  |  到達第 " + Math.min(world.wave, world.maxWaves) + " 波";
        g.setFont(new Font("Dialog", Font.PLAIN, 18));
        fm = g.getFontMetrics();
        g.drawString(stat, (Constants.WINDOW_WIDTH - fm.stringWidth(stat)) / 2, 325);

        int bx = Constants.WINDOW_WIDTH / 2 - Constants.END_BTN_HALF_WIDTH;
        drawButton(g, bx, Constants.END_BTN_RESTART_Y, Constants.END_BTN_W, Constants.END_BTN_H,
            new Color(60, 160, 60), new Color(40, 120, 40), "再玩一次", Color.WHITE, 22);
        drawButton(g, bx, Constants.END_BTN_MENU_Y, Constants.END_BTN_W, Constants.END_BTN_H,
            new Color(100, 60, 160), new Color(70, 40, 120), "返回主選單", Color.WHITE, 22);
    }
}
