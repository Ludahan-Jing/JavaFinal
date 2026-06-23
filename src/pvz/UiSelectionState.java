package pvz;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Holds transient UI selection/hover state: which plant is selected from
 * the shop, which grid tile the mouse is hovering, which deployed tile is
 * selected (for the remove button), and the current mouse position.
 *
 * This used to live as six separate fields directly on GamePanel with
 * fourteen public getter/setter pairs, purely so GameInputListener could
 * mutate them and the renderer could read them. Centralizing them here
 * means GamePanel no longer needs to expose its internals just to act as
 * a pass-through — GameInputListener owns this object directly, and the
 * renderer takes it as a plain read-only parameter.
 */
public class UiSelectionState {
    private PlantType selectedPlant = null;
    private int hoverCol = -1, hoverRow = -1;
    private int selectedTileCol = -1, selectedTileRow = -1;
    // Dynamic — tracks whichever grid tile is currently selected, recomputed
    // each frame by the renderer (unlike the static button bounds in UiLayout).
    private Rectangle removeBtnBounds = null;
    private Point mousePos = new Point();

    public PlantType getSelectedPlant() { return selectedPlant; }
    public void setSelectedPlant(PlantType p) { selectedPlant = p; }

    public int getHoverCol() { return hoverCol; }
    public int getHoverRow() { return hoverRow; }
    public void setHoverColRow(int c, int r) { hoverCol = c; hoverRow = r; }

    public int getSelectedTileCol() { return selectedTileCol; }
    public int getSelectedTileRow() { return selectedTileRow; }
    public void setSelectedTile(int c, int r) { selectedTileCol = c; selectedTileRow = r; }

    public Rectangle getRemoveBtnBounds() { return removeBtnBounds; }
    public void setRemoveBtnBounds(Rectangle r) { removeBtnBounds = r; }

    public Point getMousePos() { return mousePos; }
    public void setMousePos(Point p) { mousePos = p; }

    /** Resets shop selection only — called when a new game starts. */
    public void clearSelectedPlant() {
        selectedPlant = null;
    }
}