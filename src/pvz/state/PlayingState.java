package pvz.state;

import java.awt.*;
import java.awt.event.KeyEvent;
import pvz.*;
import pvz.PlantType;

public class PlayingState implements GameState {
    private final GamePanel panel;

    public PlayingState(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void update(double dt) {
        GameWorld.UpdateResult res = panel.getWorld().update(dt);
        if (res == GameWorld.UpdateResult.LOSE) {
            panel.changeState(new LoseState(panel));
            panel.stopEngine();
            panel.prepareEndScreenButtons();
        } else if (res == GameWorld.UpdateResult.WIN) {
            panel.changeState(new WinState(panel));
            panel.stopEngine();
            panel.prepareEndScreenButtons();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        drawGameScene(panel, g);
    }

    @Override
    public void handleKey(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            panel.changeState(new PausedState(panel));
        }
    }

    /** Shared by paused and end-game states that overlay the frozen playfield. */
    static void drawGameScene(GamePanel panel, Graphics2D g) {
        var world = panel.getWorld();
        DrawUtils.drawSky(g);
        DrawUtils.drawGrassLawn(g);
        drawGrid(panel, g);
        drawSelectedHighlight(panel, g);
        drawShop(panel, g);
        drawHUD(panel, g);

        for (int r = 0; r < Constants.ROWS; r++)
            for (int c = 0; c < Constants.COLS; c++)
                if (world.grid[r][c] != null) world.grid[r][c].draw(g);

        drawRemoveButton(panel, g);

        for (Pea p : world.peas) p.draw(g);
        for (var z : world.zombies) z.draw(g);
        for (Sun s : world.suns) s.draw(g);
        for (Explosion ex : world.explosions) ex.draw(g);
        for (FloatText ft : world.floatTexts) ft.draw(g);

        drawHoverPreview(panel, g);
        drawDangerZone(g);
    }

    private static void drawGrid(GamePanel panel, Graphics2D g) {
        var world = panel.getWorld();
        g.setColor(new Color(0, 0, 0, 40));
        g.setStroke(new BasicStroke(1));
        for (int r = 0; r <= Constants.ROWS; r++) {
            int y = Constants.GRID_Y + r * Constants.CELL_H;
            g.drawLine(Constants.GRID_X, y, Constants.GRID_X + Constants.COLS * Constants.CELL_W, y);
        }
        for (int c = 0; c <= Constants.COLS; c++) {
            int x = Constants.GRID_X + c * Constants.CELL_W;
            g.drawLine(x, Constants.GRID_Y, x, Constants.GRID_Y + Constants.ROWS * Constants.CELL_H);
        }

        PlantType selectedPlant = panel.getSelectedPlant();
        int hoverCol = panel.getHoverCol();
        int hoverRow = panel.getHoverRow();
        if (hoverCol >= 0 && hoverRow >= 0 && selectedPlant != null) {
            int hx = Constants.GRID_X + hoverCol * Constants.CELL_W;
            int hy = Constants.GRID_Y + hoverRow * Constants.CELL_H;
            boolean canPlace = world.grid[hoverRow][hoverCol] == null && world.sunCount >= selectedPlant.cost;
            g.setColor(canPlace ? new Color(255, 255, 100, 80) : new Color(255, 60, 60, 80));
            g.fillRect(hx, hy, Constants.CELL_W, Constants.CELL_H);
            g.setColor(canPlace ? new Color(255, 255, 0, 160) : new Color(255, 0, 0, 160));
            g.setStroke(new BasicStroke(2));
            g.drawRect(hx, hy, Constants.CELL_W, Constants.CELL_H);
            g.setStroke(new BasicStroke(1));
        }
    }

    private static void drawShop(GamePanel panel, Graphics2D g) {
        var world = panel.getWorld();
        PlantType selectedPlant = panel.getSelectedPlant();

        g.setColor(new Color(40, 80, 30));
        g.fillRoundRect(120, 8, 640, 110, 16, 16);
        g.setColor(new Color(80, 160, 60));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(120, 8, 640, 110, 16, 16);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(30, 60, 20));
        g.fillRoundRect(4, 8, 110, 110, 12, 12);
        g.setColor(new Color(255, 220, 30));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(4, 8, 110, 110, 12, 12);
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(255, 230, 30));
        g.fillOval(18, 18, 36, 36);
        for (int i = 0; i < 8; i++) {
            double a = Math.toRadians(i * 45);
            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine((int)(36 + Math.cos(a) * 18), (int)(36 + Math.sin(a) * 18),
                       (int)(36 + Math.cos(a) * 25), (int)(36 + Math.sin(a) * 25));
            g.setStroke(new BasicStroke(1));
        }
        g.setColor(new Color(255, 220, 30));
        g.setFont(new Font("Dialog", Font.BOLD, 22));
        String sunStr = String.valueOf(world.sunCount);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(sunStr, 59 - fm.stringWidth(sunStr) / 2, 90);
        g.setFont(new Font("Dialog", Font.PLAIN, 11));
        g.setColor(new Color(200, 200, 180));
        g.drawString("陽光", 42, 108);

        PlantType[] types = PlantType.values();
        for (int i = 0; i < types.length; i++) {
            PlantType pt = types[i];
            int ix = Constants.SHOP_START_X + i * (Constants.SHOP_ITEM_W + 8);
            int iy = Constants.SHOP_Y + 5;
            boolean selected  = selectedPlant == pt;
            boolean affordable = world.sunCount >= pt.cost;
            Color cardBg = selected ? new Color(255, 240, 100) : affordable ? new Color(60, 100, 50) : new Color(40, 60, 35);
            g.setColor(cardBg);
            g.fillRoundRect(ix, iy, Constants.SHOP_ITEM_W, Constants.SHOP_ITEM_H, 10, 10);
            g.setColor(selected ? new Color(220, 160, 0) : affordable ? new Color(100, 180, 80) : new Color(60, 80, 50));
            g.setStroke(new BasicStroke(selected ? 3 : 1.5f));
            g.drawRoundRect(ix, iy, Constants.SHOP_ITEM_W, Constants.SHOP_ITEM_H, 10, 10);
            g.setStroke(new BasicStroke(1));

            drawMiniPlant(g, pt, ix + Constants.SHOP_ITEM_W / 2, iy + 32, affordable);

            g.setFont(new Font("Dialog", Font.BOLD, 10));
            g.setColor(affordable ? Color.WHITE : new Color(120, 120, 100));
            String name = pt.name;
            FontMetrics fm2 = g.getFontMetrics();
            g.drawString(name, ix + (Constants.SHOP_ITEM_W - fm2.stringWidth(name)) / 2, iy + 63);

            g.setFont(new Font("Dialog", Font.BOLD, 12));
            g.setColor(affordable ? new Color(255, 230, 50) : new Color(150, 120, 60));
            String cost = "☀" + pt.cost;
            FontMetrics fm3 = g.getFontMetrics();
            g.drawString(cost, ix + (Constants.SHOP_ITEM_W - fm3.stringWidth(cost)) / 2, iy + 80);

            if (!affordable) {
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRoundRect(ix, iy, Constants.SHOP_ITEM_W, Constants.SHOP_ITEM_H, 10, 10);
            }
        }

        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Dialog", Font.BOLD, 14));
        String waveStr = "第 " + Math.min(world.wave, world.maxWaves) + "/" + world.maxWaves + " 波";
        g.drawString(waveStr, 780, 40);
        g.setFont(new Font("Dialog", Font.PLAIN, 13));
        g.drawString("消滅: " + world.zombiesKilled, 780, 60);
        g.setFont(new Font("Dialog", Font.PLAIN, 12));
        g.drawString("[ESC] 暫停", 780, 95);
    }

    private static void drawMiniPlant(Graphics2D g, PlantType pt, int cx, int cy, boolean color) {
        Color c  = color ? pt.color     : pt.color.darker().darker();
        Color c2 = color ? pt.darkColor : pt.darkColor.darker().darker();
        switch (pt) {
            case SUNFLOWER -> {
                g.setColor(c);
                for (int i = 0; i < 6; i++) {
                    double a = Math.toRadians(i * 60);
                    g.fillOval((int)(cx + Math.cos(a) * 10) - 5, (int)(cy + Math.sin(a) * 10) - 5, 10, 10);
                }
                g.setColor(c2);
                g.fillOval(cx - 8, cy - 8, 16, 16);
            }
            case PEASHOOTER, SNOWPEA -> {
                g.setColor(c);
                g.fillOval(cx - 11, cy - 12, 22, 22);
                g.setColor(c2);
                g.fillRoundRect(cx + 8, cy - 4, 14, 8, 4, 4);
            }
            case WALLNUT -> {
                g.setColor(c);
                g.fillOval(cx - 13, cy - 16, 26, 32);
                g.setColor(c2);
                g.drawOval(cx - 13, cy - 16, 26, 32);
            }
            case CHERRYBOMB -> {
                g.setColor(c);
                g.fillOval(cx - 14, cy - 8, 14, 14);
                g.fillOval(cx,      cy - 8, 14, 14);
                g.setColor(c2);
                g.setStroke(new BasicStroke(2));
                g.drawLine(cx - 7, cy - 8, cx - 9, cy - 18);
                g.drawLine(cx + 7, cy - 8, cx + 9, cy - 18);
                g.setStroke(new BasicStroke(1));
            }
        }
    }

    private static void drawHUD(GamePanel panel, Graphics2D g) {
        PlantType selectedPlant = panel.getSelectedPlant();
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRect(0, Constants.WINDOW_HEIGHT - 30, Constants.WINDOW_WIDTH, 30);
        g.setFont(new Font("Dialog", Font.PLAIN, 13));
        g.setColor(new Color(220, 220, 200));
        if (selectedPlant != null) {
            g.drawString("已選擇: " + selectedPlant.name + "（花費 " + selectedPlant.cost + " 陽光）  右鍵取消選擇", 10, Constants.WINDOW_HEIGHT - 10);
        } else {
            g.drawString("點擊頂部植物欄選擇植物，再點擊草坪種植。點擊太陽收集陽光！", 10, Constants.WINDOW_HEIGHT - 10);
        }
    }

    private static void drawHoverPreview(GamePanel panel, Graphics2D g) {
        PlantType selectedPlant = panel.getSelectedPlant();
        if (selectedPlant == null) return;
        Point mousePos = panel.getMousePos();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        drawMiniPlant(g, selectedPlant, mousePos.x, mousePos.y - 20, true);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private static void drawRemoveButton(GamePanel panel, Graphics2D g) {
        int selectedTileCol = panel.getSelectedTileCol();
        int selectedTileRow = panel.getSelectedTileRow();
        if (selectedTileCol < 0 || selectedTileRow < 0) {
            panel.setRemoveBtnBounds(null);
            return;
        }
        var world = panel.getWorld();
        if (world.grid[selectedTileRow][selectedTileCol] == null) {
            panel.setSelectedTile(-1, -1);
            panel.setRemoveBtnBounds(null);
            return;
        }
        int tx = Constants.GRID_X + selectedTileCol * Constants.CELL_W;
        int ty = Constants.GRID_Y + selectedTileRow * Constants.CELL_H;
        int bw = 20, bh = 20;
        int bx = tx + Constants.CELL_W - bw - 6;
        int by = ty + 6;
        g.setColor(new Color(200, 50, 50, 220));
        g.fillRoundRect(bx, by, bw, bh, 6, 6);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawLine(bx + 4, by + 4, bx + bw - 5, by + bh - 5);
        g.drawLine(bx + bw - 5, by + 4, bx + 4, by + bh - 5);
        g.setStroke(new BasicStroke(1));
        panel.setRemoveBtnBounds(new Rectangle(bx, by, bw, bh));
    }

    private static void drawSelectedHighlight(GamePanel panel, Graphics2D g) {
        int selectedTileCol = panel.getSelectedTileCol();
        int selectedTileRow = panel.getSelectedTileRow();
        if (selectedTileCol < 0 || selectedTileRow < 0) return;
        if (panel.getHoverCol() != selectedTileCol || panel.getHoverRow() != selectedTileRow) return;
        int tx = Constants.GRID_X + selectedTileCol * Constants.CELL_W;
        int ty = Constants.GRID_Y + selectedTileRow * Constants.CELL_H;
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        g.setColor(new Color(255, 255, 255));
        g.fillRect(tx, ty, Constants.CELL_W, Constants.CELL_H);
        g.setComposite(old);
    }

    private static void drawDangerZone(Graphics2D g) {
        g.setColor(new Color(220, 30, 30, 120));
        g.setStroke(new BasicStroke(3));
        g.drawLine(Constants.GRID_X, Constants.GRID_Y,
                   Constants.GRID_X, Constants.GRID_Y + Constants.ROWS * Constants.CELL_H);
        g.setStroke(new BasicStroke(1));
    }
}
