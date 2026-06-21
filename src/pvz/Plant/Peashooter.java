package pvz.Plant;

import java.awt.*;
import pvz.PlantType;
import pvz.Constants;
import pvz.GameWorld;
import pvz.Pea;

public class Peashooter extends Plant {
    public Peashooter(int col, int row) {
        super(PlantType.PEASHOOTER, col, row, PlantType.PEASHOOTER.maxHp, Constants.PEA_DMG, 'P');
    }

    @Override
    public void update(GameWorld world, long now) {
        if (world.hasZombieInRow(row, col) && now - lastShootTime > Constants.SHOOT_INTERVAL) {
            lastShootTime = now;
            double px = cx() + 20;
            double py = cy();
            world.peas.add(new Pea(px, py, row, false));
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

        // Body
        g.setColor(new Color(60, 180, 60));
        g.fillOval(cx - 18, cy - 20, 36, 40);
        g.setColor(new Color(30, 140, 30));
        g.drawOval(cx - 18, cy - 20, 36, 40);
        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(cx - 10, cy - 15, 10, 10);
        g.fillOval(cx + 0,  cy - 15, 10, 10);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 7, cy - 12, 5, 5);
        g.fillOval(cx + 3, cy - 12, 5, 5);
        // Mouth / barrel
        g.setColor(new Color(20, 120, 20));
        g.fillRoundRect(cx + 12, cy - 5, 22, 12, 6, 6);
        g.setColor(Color.BLACK);
        g.drawRoundRect(cx + 12, cy - 5, 22, 12, 6, 6);
        // Leaf
        g.setColor(new Color(80, 200, 80));
        int[] lx = {cx - 5, cx - 25, cx - 10};
        int[] ly = {cy + 10, cy + 20, cy + 25};
        g.fillPolygon(lx, ly, 3);

        drawHpBar(g);
    }
}