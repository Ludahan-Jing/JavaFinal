package pvz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import pvz.state.*;

public class GamePanel extends JPanel {

    // ── Game state (State Pattern) ─────────────────────────────────────────────
    private GameState gameState = new MenuState(this);

    // ── Game model (moved to GameWorld) ─────────────────────────────────────────
    private final GameWorld world = new GameWorld();

    // ── Timing / UI ────────────────────────────────────────────────────────────
    private long lastUpdate;
    private long gameStartTime;
    private long pauseStart;
    private long totalPauseTime;

    // ── Input ──────────────────────────────────────────────────────────────────
    private PlantType selectedPlant = null;
    private int       hoverCol = -1, hoverRow = -1;
    private int       selectedTileCol = -1, selectedTileRow = -1;
    private Rectangle removeBtnBounds = null;
    private Rectangle endBtnRestartBounds = null;
    private Rectangle endBtnMenuBounds = null;
    private Rectangle pauseBtnContinueBounds = null;
    private Rectangle pauseBtnRestartBounds = null;
    private Rectangle pauseBtnEndBounds = null;
    private Point     mousePos = new Point();

    // ── Game engine (controls timer & update loop) ─────────────────────────────
    private GameEngine engine;

    // ── Window (merged from GameWindow) ────────────────────────────────────────
    private JFrame frame;

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(new Color(80, 140, 60));
        setFocusable(true);

        GameInputListener inputListener = new GameInputListener(this, world);
        addMouseListener(inputListener);
        addMouseMotionListener(inputListener);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e);
            }
        });

        engine = new GameEngine(this);
        initWindow();
    }

    private void initWindow() {
        frame = new JFrame("🌻 植物大戰殭屍 - Java Edition");
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    public JFrame getFrame() {
        return frame;
    }

    public GameState getGameState() { return gameState; }
    public void changeState(GameState newState) {
        this.gameState = newState;
        repaint();
    }
    public GameWorld getWorld() { return world; }
    public GameEngine getEngine() { return engine; }
    public synchronized long getLastUpdate() { return lastUpdate; }
    public synchronized void setLastUpdate(long v) { lastUpdate = v; }
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
    public Rectangle getPauseBtnContinueBounds() { return pauseBtnContinueBounds; }
    public Rectangle getPauseBtnRestartBounds() { return pauseBtnRestartBounds; }
    public Rectangle getPauseBtnEndBounds() { return pauseBtnEndBounds; }
    public void stopEngine() { if (engine != null) engine.stop(); }

    public void prepareEndScreenButtons() {
        int bx = Constants.WINDOW_WIDTH / 2 - Constants.END_BTN_HALF_WIDTH;
        endBtnRestartBounds = new Rectangle(bx, Constants.END_BTN_RESTART_Y, Constants.END_BTN_W, Constants.END_BTN_H);
        endBtnMenuBounds = new Rectangle(bx, Constants.END_BTN_MENU_Y, Constants.END_BTN_W, Constants.END_BTN_H);
    }

    public void startGame() {
        world.start();
        lastUpdate     = System.currentTimeMillis();
        gameStartTime  = System.currentTimeMillis();
        totalPauseTime = 0;
        selectedPlant  = null;
        endBtnRestartBounds = null;
        endBtnMenuBounds = null;
        pauseBtnContinueBounds = null;
        pauseBtnRestartBounds = null;
        pauseBtnEndBounds = null;
        changeState(new PlayingState(this));
        engine.start();
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gameState.draw(g);
    }

    public void handleKey(KeyEvent e) {
        gameState.handleKey(e);
    }

    public void markPauseStart() {
        pauseStart = System.currentTimeMillis();
    }

    public void preparePauseMenuButtons() {
        int bx = Constants.WINDOW_WIDTH / 2 - Constants.END_BTN_HALF_WIDTH;
        pauseBtnContinueBounds = new Rectangle(bx, Constants.PAUSE_BTN_FIRST_Y, Constants.END_BTN_W, Constants.END_BTN_H);
        pauseBtnRestartBounds  = new Rectangle(bx, Constants.PAUSE_BTN_FIRST_Y + Constants.PAUSE_BTN_GAP, Constants.END_BTN_W, Constants.END_BTN_H);
        pauseBtnEndBounds      = new Rectangle(bx, Constants.PAUSE_BTN_FIRST_Y + Constants.PAUSE_BTN_GAP * 2, Constants.END_BTN_W, Constants.END_BTN_H);
    }

    public void resumeFromPause() {
        totalPauseTime += System.currentTimeMillis() - pauseStart;
        lastUpdate = System.currentTimeMillis();
        pauseBtnContinueBounds = null;
        pauseBtnRestartBounds = null;
        pauseBtnEndBounds = null;
        changeState(new PlayingState(this));
    }

    public void endGameFromPauseAsLose() {
        if (engine != null) engine.stop();
        prepareEndScreenButtons();
        pauseBtnContinueBounds = null;
        pauseBtnRestartBounds = null;
        pauseBtnEndBounds = null;
        changeState(new LoseState(this));
    }

    public void goToMenu() {
        stopEngine();
        changeState(new MenuState(this));
    }
}
