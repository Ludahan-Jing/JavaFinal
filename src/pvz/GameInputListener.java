package pvz;

import java.awt.*;
import java.awt.event.*;
import pvz.Plant.*;

/**
 * Handles all mouse and keyboard-driven input for the game.
 *
 * Owns UiSelectionState directly — it mutates hover/selection state
 * without routing through GamePanel getter/setter pairs. GamePanel is
 * only used for state-machine transitions (startGame, pauseMenu, etc.)
 * and repaint() triggers, which are the two things that genuinely belong
 * to the Swing host component.
 */
public class GameInputListener implements MouseListener, MouseMotionListener {
    private final GamePanel         panel;
    private final GameWorld         world;
    private final UiSelectionState  ui;

    public GameInputListener(GamePanel panel, GameWorld world, UiSelectionState ui) {
        this.panel = panel;
        this.world = world;
        this.ui    = ui;
    }

    // ── mouseClicked: state-machine routing (menu / pause / end-screen) ─────────

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        GamePanel.State state = panel.getState();

        if (state == GamePanel.State.MENU) {
            if (UiLayout.menuStartButton().contains(mx, my)) {
                panel.startGame();
            }
            return;
        }

        if (state == GamePanel.State.PAUSED) {
            if      (UiLayout.pauseContinueButton().contains(mx, my)) panel.resumeFromPause();
            else if (UiLayout.pauseRestartButton().contains(mx, my))  panel.startGame();
            else if (UiLayout.pauseEndButton().contains(mx, my))      panel.endGameFromPauseAsLose();
            return;
        }

        if (state == GamePanel.State.WIN || state == GamePanel.State.LOSE) {
            if (UiLayout.endScreenRestartButton().contains(mx, my)) {
                panel.startGame();
            } else if (UiLayout.endScreenMenuButton().contains(mx, my)) {
                panel.setState(GamePanel.State.MENU);
                panel.stopEngine();
                panel.repaint();
            }
            return;
        }
    }

    // ── mousePressed: gameplay interactions ─────────────────────────────────────

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        GamePanel.State state = panel.getState();

        if (state == GamePanel.State.PAUSED) return;
        if (state != GamePanel.State.PLAYING) return;

        // Right-click: deselect
        if (e.getButton() == MouseEvent.BUTTON3) {
            ui.setSelectedPlant(null);
            return;
        }

        // Remove-button clicked
        Rectangle rb = ui.getRemoveBtnBounds();
        if (rb != null && rb.contains(mx, my)) {
            int sr = ui.getSelectedTileRow();
            int sc = ui.getSelectedTileCol();
            if (sr >= 0 && sc >= 0 && world.grid[sr][sc] != null) {
                world.removePlant(sr, sc);
                world.addFloatText("已移除植物", mx, my - 10, new Color(255, 200, 100), 16, 800);
            }
            ui.setSelectedTile(-1, -1);
            ui.setRemoveBtnBounds(null);
            return;
        }

        // Sun collection
        for (Sun s : world.suns) {
            if (s.getBounds().contains(mx, my)) {
                s.collected = true;
                world.sunCount += Constants.SUN_VALUE;
                world.addFloatText("+25 ☀", mx, my, new Color(255, 230, 50), 18, 900);
                return;
            }
        }

        // Shop clicks
        PlantType[] types = PlantType.values();
        for (int i = 0; i < types.length; i++) {
            int ix = Constants.SHOP_START_X + i * (Constants.SHOP_ITEM_W + 8);
            int iy = Constants.SHOP_Y + 5;
            if (mx >= ix && mx <= ix + Constants.SHOP_ITEM_W && my >= iy && my <= iy + Constants.SHOP_ITEM_H) {
                if (world.sunCount >= types[i].cost) {
                    ui.setSelectedPlant(types[i]);
                }
                return;
            }
        }

        // Grid placement / remove-overlay
        int clickedCol = (mx - Constants.GRID_X) / Constants.CELL_W;
        int clickedRow = (my - Constants.GRID_Y) / Constants.CELL_H;
        boolean onGrid = clickedCol >= 0 && clickedCol < Constants.COLS
                      && clickedRow >= 0 && clickedRow < Constants.ROWS;

        PlantType sel = ui.getSelectedPlant();
        if (sel != null && onGrid) {
            if (world.grid[clickedRow][clickedCol] == null && world.sunCount >= sel.cost) {
                world.grid[clickedRow][clickedCol] = sel.create(clickedCol, clickedRow);
                world.sunCount -= sel.cost;
                world.addFloatText("-" + sel.cost + " ☀", mx, my - 20,
                        new Color(220, 180, 50), 16, 800);
                // Cherry bomb deselects after placement; others stay selected for rapid planting
                if (sel == PlantType.CHERRYBOMB) ui.setSelectedPlant(null);
            }
        } else if (onGrid) {
            if (world.grid[clickedRow][clickedCol] != null) {
                ui.setSelectedTile(clickedCol, clickedRow);
            } else {
                ui.setSelectedTile(-1, -1);
                ui.setRemoveBtnBounds(null);
            }
        }
    }

    // ── mouseMoved: hover tracking ───────────────────────────────────────────────

    @Override
    public void mouseMoved(MouseEvent e) {
        ui.setMousePos(e.getPoint());
        int mx = e.getX(), my = e.getY();

        int newCol = (mx - Constants.GRID_X) / Constants.CELL_W;
        int newRow = (my - Constants.GRID_Y) / Constants.CELL_H;

        if (newCol < 0 || newCol >= Constants.COLS || newRow < 0 || newRow >= Constants.ROWS) {
            ui.setHoverColRow(-1, -1);
            // cancel tile overlay when mouse leaves grid
            if (ui.getSelectedTileCol() >= 0) {
                ui.setSelectedTile(-1, -1);
                ui.setRemoveBtnBounds(null);
            }
            return;
        }

        ui.setHoverColRow(newCol, newRow);

        // Cancel overlay if mouse moved off the tile that was selected
        if (ui.getSelectedTileCol() >= 0
                && (ui.getHoverCol() != ui.getSelectedTileCol()
                    || ui.getHoverRow() != ui.getSelectedTileRow())) {
            ui.setSelectedTile(-1, -1);
            ui.setRemoveBtnBounds(null);
        }
    }

    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}
    @Override public void mouseDragged(MouseEvent e)  {}
    @Override public void mouseReleased(MouseEvent e) {}
}