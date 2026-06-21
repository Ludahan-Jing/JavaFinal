package pvz.Plant;

import java.awt.*;
import pvz.PlantType;
import pvz.Constants;

/**
 * Abstract Plant base class with template stats: baseHp, baseAttack, idChar
 * Concrete plant types should extend this and implement draw-specific behavior
 */
public abstract class Plant {
    public final PlantType type; // keeps shop/display info
    public final int col, row;
    public int hp;
    public final int baseHp;
    public final int baseAttack;
    public final char idChar; // short id for display (e.g. 'S','P','W','C')
    public long lastShootTime;
    public long lastSunTime;
    public long placedTime;
    public boolean exploding;      // cherry bomb
    public long   explodeStartTime;

    protected Plant(PlantType type, int col, int row, int baseHp, int baseAttack, char idChar) {
        this.type = type;
        this.col = col;
        this.row = row;
        this.baseHp = baseHp;
        this.baseAttack = baseAttack;
        this.idChar = idChar;
        this.hp = baseHp;
        this.placedTime = System.currentTimeMillis();
    }

    public boolean isDead() { return hp <= 0; }

    public int cx() { return Constants.GRID_X + col * Constants.CELL_W + Constants.CELL_W / 2; }
    public int cy() { return Constants.GRID_Y + row * Constants.CELL_H + Constants.CELL_H / 2; }

    public abstract void draw(Graphics2D g);

    /**
     * Per-tick behavior hook (shooting, sun production, fuse timers, etc).
     * Default is no-op — most plants (e.g. Wallnut) don't need this.
     * Subclasses with active behavior override this instead of GameWorld
     * branching on plant type.
     *
     * @param world the game world (gives access to peas/suns lists and helpers)
     * @param now   current time in millis, passed down so all plants use one timestamp
     */
    public void update(pvz.GameWorld world, long now) {
        // no-op by default
    }

    /**
     * Shared HP bar rendering, used by all plant subclasses.
     * Anchored to this plant's grid cell, same look as before per-subclass.
     */
    protected void drawHpBar(Graphics2D g) {
        int x = Constants.GRID_X + col * Constants.CELL_W;
        int y = Constants.GRID_Y + row * Constants.CELL_H;
        int w = Constants.CELL_W;
        int h = Constants.CELL_H;

        int barW = w - 10;
        int barH = 6;
        int bx   = x + 5;
        int by   = y + h - 12;

        g.setColor(Color.DARK_GRAY);
        g.fillRect(bx, by, barW, barH);
        float ratio = (float) hp / type.maxHp;
        g.setColor(ratio > 0.5f ? new Color(80, 200, 80)
                 : ratio > 0.25f ? new Color(220, 180, 0)
                 : new Color(200, 60, 60));
        g.fillRect(bx, by, (int) (barW * ratio), barH);
        g.setColor(Color.BLACK);
        g.drawRect(bx, by, barW, barH);
    }
}