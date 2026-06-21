package pvz.Plant;

import java.awt.*;
import pvz.PlantType;
import pvz.Constants;
import pvz.GameWorld;
import pvz.Sun;

public class Sunflower extends Plant {
    public Sunflower(int col, int row) {
        super(PlantType.SUNFLOWER, col, row, PlantType.SUNFLOWER.maxHp, 0, 'S');
    }

    @Override
    public void update(GameWorld world, long now) {
        if (now - lastSunTime > Constants.SF_SUN_INTERVAL) {
            lastSunTime = now;
            world.suns.add(new Sun(cx(), cy() - 30));
        }
    }

    @Override
    public void draw(Graphics2D g) {
        int x = Constants.GRID_X + col * Constants.CELL_W;
        int y = Constants.GRID_Y + row * Constants.CELL_H;
        int w = Constants.CELL_W;
        int h = Constants.CELL_H;
        int cx = x + w / 2;
        int cy = y + h / 2;

        // Petals
        g.setColor(new Color(255, 200, 0));
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            int px = (int)(cx + Math.cos(angle) * 20);
            int py = (int)(cy + Math.sin(angle) * 20);
            g.fillOval(px - 8, py - 8, 16, 16);
        }
        // Center
        g.setColor(new Color(140, 80, 20));
        g.fillOval(cx - 14, cy - 14, 28, 28);
        // Face
        g.setColor(Color.BLACK);
        g.fillOval(cx - 5, cy - 5, 4, 4);
        g.fillOval(cx + 1, cy - 5, 4, 4);
        g.drawArc(cx - 5, cy, 10, 6, 0, -180);
        // Stem
        g.setColor(new Color(60, 160, 60));
        g.setStroke(new BasicStroke(4));
        g.drawLine(cx, cy + 14, cx, cy + 40);
        g.setStroke(new BasicStroke(1));

        drawHpBar(g);
    }
}