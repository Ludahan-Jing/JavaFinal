package pvz;

import java.awt.Rectangle;

/**
 * Single source of truth for the screen-space bounds of every clickable
 * button in the game (menu start button, pause-menu buttons, end-screen
 * buttons).
 *
 * Previously these Rectangles were independently recomputed in up to four
 * different places (drawing code, ESC key handler, mouse click handler),
 * which made it easy for draw bounds and click bounds to drift out of
 * sync. Both GamePanel (for drawing) and GameInputListener (for hit
 * testing) should read from here instead of building their own Rectangles.
 *
 * All methods are pure functions of Constants — bounds never change at
 * runtime, so there's no need to cache or lazily-null them out.
 */
public final class UiLayout {
    private UiLayout() {}

    public static Rectangle menuStartButton() {
        int bx = Constants.WINDOW_WIDTH / 2 - Constants.MENU_BTN_HALF_WIDTH;
        return new Rectangle(bx, Constants.MENU_BTN_Y, Constants.MENU_BTN_W, Constants.MENU_BTN_H);
    }

    public static Rectangle endScreenRestartButton() {
        int bx = Constants.WINDOW_WIDTH / 2 - Constants.END_BTN_HALF_WIDTH;
        return new Rectangle(bx, Constants.END_BTN_RESTART_Y, Constants.END_BTN_W, Constants.END_BTN_H);
    }

    public static Rectangle endScreenMenuButton() {
        int bx = Constants.WINDOW_WIDTH / 2 - Constants.END_BTN_HALF_WIDTH;
        return new Rectangle(bx, Constants.END_BTN_MENU_Y, Constants.END_BTN_W, Constants.END_BTN_H);
    }

    public static Rectangle pauseContinueButton() {
        int bx = Constants.WINDOW_WIDTH / 2 - Constants.END_BTN_HALF_WIDTH;
        return new Rectangle(bx, Constants.PAUSE_BTN_FIRST_Y, Constants.END_BTN_W, Constants.END_BTN_H);
    }

    public static Rectangle pauseRestartButton() {
        int bx = Constants.WINDOW_WIDTH / 2 - Constants.END_BTN_HALF_WIDTH;
        return new Rectangle(bx, Constants.PAUSE_BTN_FIRST_Y + Constants.PAUSE_BTN_GAP,
                Constants.END_BTN_W, Constants.END_BTN_H);
    }

    public static Rectangle pauseEndButton() {
        int bx = Constants.WINDOW_WIDTH / 2 - Constants.END_BTN_HALF_WIDTH;
        return new Rectangle(bx, Constants.PAUSE_BTN_FIRST_Y + Constants.PAUSE_BTN_GAP * 2,
                Constants.END_BTN_W, Constants.END_BTN_H);
    }
}