package pvz.state;

import java.awt.*;
import java.awt.event.KeyEvent;
import pvz.Constants;
import pvz.GamePanel;
import pvz.Plant.Sunflower;

public class MenuState implements GameState {
    private final GamePanel panel;

    public MenuState(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void update(double dt) {
        // Menu has no simulation to advance
    }

    @Override
    public void draw(Graphics2D g) {
        DrawUtils.drawSky(g);
        DrawUtils.drawGrassLawn(g);

        g.setFont(new Font("Dialog", Font.BOLD, 56));
        String title = "🌻 植物大戰殭屍";
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(title);
        g.setColor(new Color(0, 0, 0, 100));
        g.drawString(title, (Constants.WINDOW_WIDTH - tw) / 2 + 3, 180 + 3);
        g.setColor(new Color(80, 200, 80));
        g.drawString(title, (Constants.WINDOW_WIDTH - tw) / 2, 180);

        drawDecorativePlants(g);

        DrawUtils.drawButton(g, Constants.WINDOW_WIDTH / 2 - 100, 250, 200, 55,
                new Color(60, 180, 60), new Color(40, 140, 40), "開始遊戲", Color.WHITE, 22);

        g.setFont(new Font("Dialog", Font.PLAIN, 15));
        g.setColor(new Color(240, 240, 240));
        String[] tips = {
            "• 點擊底部植物欄選擇植物，再點擊草坪種植",
            "• 收集陽光以購買植物（點擊太陽收集）",
            "• 向日葵可持續產生陽光",
            "• 豌豆射手可攻擊殭屍",
            "• 堅果牆可阻擋殭屍",
            "• 寒冰射手會減慢殭屍速度",
            "• 櫻桃炸彈會在2秒後爆炸，傷害範圍內的所有殭屍",
            "• 共 10 波殭屍，全部消滅即可獲勝！"
        };
        int ty = 340;
        for (String tip : tips) {
            int tw2 = g.getFontMetrics().stringWidth(tip);
            g.drawString(tip, (Constants.WINDOW_WIDTH - tw2) / 2, ty);
            ty += 22;
        }
    }

    @Override
    public void handleKey(KeyEvent e) {
        // No keyboard actions on the main menu
    }

    private void drawDecorativePlants(Graphics2D g) {
        Sunflower sf = new Sunflower(0, 0);
        sf.draw(g);
        g.setColor(new Color(255, 220, 0));
        for (int i = 0; i < 8; i++) {
            double a = Math.toRadians(i * 45);
            g.fillOval((int)(80 + Math.cos(a) * 18) - 8, (int)(230 + Math.sin(a) * 18) - 8, 16, 16);
        }
        g.setColor(new Color(140, 80, 20));
        g.fillOval(68, 218, 24, 24);
        g.setColor(new Color(255, 220, 0));
        for (int i = 0; i < 8; i++) {
            double a = Math.toRadians(i * 45);
            g.fillOval((int)(820 + Math.cos(a) * 18) - 8, (int)(230 + Math.sin(a) * 18) - 8, 16, 16);
        }
        g.setColor(new Color(140, 80, 20));
        g.fillOval(808, 218, 24, 24);
    }
}
