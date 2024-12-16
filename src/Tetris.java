/**
 * @author Jay Um
 * @author Kai Quan Chua
 *
 * The Tetris class represents a single instance of the Tetris game,
 * with game logic, rendering, and player input handling.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tetris extends JPanel implements ActionListener {

    /** The width of the Tetris game board. */
    public final int BOARD_WIDTH = 10;

    /** The height of the Tetris game board. */
    public final int BOARD_HEIGHT = 20;

    /** The size of each cell on the board. */
    public final int CELL_SIZE = 30;

    /** The width of the info panel that displays score and other information. */
    public final int INFO_PANEL_WIDTH = 150;

    /** The current game board represented as a 2D boolean array. */
    private boolean[][] board;

    /** The current Tetrimino being controlled by the player. */
    private Tetrimino currentTetrimino;

    /** The next Tetrimino to appear after the current one is placed. */
    private Tetrimino nextTetrimino;

    /** Timer used for the game loop. */
    private Timer timer;

    /** The current score of the player. */
    private int score;

    /** Listener for updating the score in the Main class. */
    private final ScoreUpdateListener scoreUpdateListener;

    /** The predefined shapes of the Tetriminos. */
    private final int[][][] TETRIMINO_SHAPES = {
            { {1, 1, 1, 1} },           // I shape
            { {1, 1, 1}, {0, 1, 0} },   // T shape
            { {1, 1}, {1, 1} },         // O shape
            { {1, 1, 0}, {0, 1, 1} },   // S shape
            { {0, 1, 1}, {1, 1, 0} },   // Z shape
            { {1, 1, 1}, {0, 0, 1} },   // J shape
            { {1, 1, 1}, {1, 0, 0} }    // L shape
    };

    /** The key code for rotating the Tetrimino. */
    private final int rotateKey;

    /** The key code for moving the Tetrimino down. */
    private final int downKey;

    /** The key code for moving the Tetrimino left. */
    private final int leftKey;

    /** The key code for moving the Tetrimino right. */
    private final int rightKey;

    /**
     * Constructor for creating a new Tetris game instance.
     * @param rotateKey The key for rotating the Tetrimino.
     * @param downKey The key for moving the Tetrimino down.
     * @param leftKey The key for moving the Tetrimino left.
     * @param rightKey The key for moving the Tetrimino right.
     * @param scoreUpdateListener Listener for score updates.
     */
    public Tetris(int rotateKey, int downKey, int leftKey, int rightKey, ScoreUpdateListener scoreUpdateListener) {
        this.rotateKey = rotateKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.scoreUpdateListener = scoreUpdateListener;

        board = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        currentTetrimino = getRandomTetrimino();
        nextTetrimino = getRandomTetrimino();
        timer = new Timer(400, this);
        timer.start();
        score = 0;

        setFocusable(true);
        setupKeyBindings();
    }


    /**
     * Sets up the key bindings for player controls.
     */
    private void setupKeyBindings() {
        InputMap inputMap = this.getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(leftKey, 0), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(rightKey, 0), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke(downKey, 0), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(rotateKey, 0), "rotate");

        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveLeft();
                repaint();
            }
        });

        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRight();
                repaint();
            }
        });

        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveDown();
                repaint();
            }
        });

        actionMap.put("rotate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotate();
                repaint();
            }
        });
    }

    /**
     * Paints the game board and the current Tetrimino.
     * @param g The Graphics object used for drawing.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j]) {
                    g.setColor(Color.BLUE);
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    g.setColor(Color.WHITE);
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        int[][] shape = currentTetrimino.getShape();
        g.setColor(currentTetrimino.getColor());
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    g.fillRect((currentTetrimino.getX() + j) * CELL_SIZE,
                            (currentTetrimino.getY() + i) * CELL_SIZE,
                            CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect((currentTetrimino.getX() + j) * CELL_SIZE,
                            (currentTetrimino.getY() + i) * CELL_SIZE,
                            CELL_SIZE, CELL_SIZE);
                    g.setColor(currentTetrimino.getColor());
                }
            }
        }

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(BOARD_WIDTH * CELL_SIZE, 0, INFO_PANEL_WIDTH, BOARD_HEIGHT * CELL_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(BOARD_WIDTH * CELL_SIZE, 0, INFO_PANEL_WIDTH, BOARD_HEIGHT * CELL_SIZE);
        g.drawString("Score: " + score, BOARD_WIDTH * CELL_SIZE + 20, 30);
    }

    /**
     * Action performed during the game loop. Moves the current Tetrimino down or stops it if it can't move.
     * @param e The action event.
     */
    public void actionPerformed(ActionEvent e) {
        if (canMoveDown(currentTetrimino)) {
            currentTetrimino.moveDown();
        } else {
            stopTetrimino();
        }
        repaint();
    }

    /**
     * Gets a random Tetrimino shape from the predefined shapes.
     * @return A randomly chosen Tetrimino.
     */
    private Tetrimino getRandomTetrimino() {
        int index = (int) (Math.random() * TETRIMINO_SHAPES.length);
        return new Tetrimino(TETRIMINO_SHAPES[index], index);
    }

    /**
     * Checks if the current Tetrimino can move down.
     * @param tetrimino The Tetrimino to check.
     * @return True if the Tetrimino can move down, false otherwise.
     */
    private boolean canMoveDown(Tetrimino tetrimino) {
        int[][] shape = tetrimino.getShape();
        int x = tetrimino.getX();
        int y = tetrimino.getY();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int row = y + i + 1;
                    if (row >= BOARD_HEIGHT || (row >= 0 && board[row][x + j])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if the current Tetrimino can move left.
     * @param tetrimino The Tetrimino to check.
     * @return True if the Tetrimino can move left, false otherwise.
     */
    private boolean canMoveLeft(Tetrimino tetrimino) {
        int[][] shape = tetrimino.getShape();
        int x = tetrimino.getX();
        int y = tetrimino.getY();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int col = x + j - 1;
                    if (col < 0 || (y + i >= 0 && board[y + i][col])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if the current Tetrimino can move right.
     * @param tetrimino The Tetrimino to check.
     * @return True if the Tetrimino can move right, false otherwise.
     */
    private boolean canMoveRight(Tetrimino tetrimino) {
        int[][] shape = tetrimino.getShape();
        int x = tetrimino.getX();
        int y = tetrimino.getY();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int col = x + j + 1;
                    if (col >= BOARD_WIDTH || (y + i >= 0 && board[y + i][col])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Moves the current Tetrimino left if possible.
     */
    private void moveLeft() {
        if (canMoveLeft(currentTetrimino)) {
            currentTetrimino.moveLeft();
        }
    }

    /**
     * Moves the current Tetrimino right if possible.
     */
    private void moveRight() {
        if (canMoveRight(currentTetrimino)) {
            currentTetrimino.moveRight();
        }
    }

    /**
     * Moves the current Tetrimino down if possible.
     */
    private void moveDown() {
        if (canMoveDown(currentTetrimino)) {
            currentTetrimino.moveDown();
        } else {
            stopTetrimino();
        }
    }

    /**
     * Rotates the current Tetrimino.
     */
    private void rotate() {
        currentTetrimino.rotate();
        if (!isValidPosition(currentTetrimino)) {
            for (int i = 0; i < 3; i++) {
                currentTetrimino.rotate();
            }
        }
    }

    /**
     * Checks if the current Tetrimino is in a valid position on the board.
     * @param tetrimino The Tetrimino to check.
     * @return True if the Tetrimino is in a valid position, false otherwise.
     */
    private boolean isValidPosition(Tetrimino tetrimino) {
        int[][] shape = tetrimino.getShape();
        int x = tetrimino.getX();
        int y = tetrimino.getY();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    if (x + j < 0 || x + j >= BOARD_WIDTH || y + i >= BOARD_HEIGHT || (y + i >= 0 && board[y + i][x + j])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Stops the current Tetrimino and places it on the board.
     */
    private void stopTetrimino() {
        int[][] shape = currentTetrimino.getShape();
        int x = currentTetrimino.getX();
        int y = currentTetrimino.getY();

        /*  Algorithmic Efficiency:
            O(m*n) or O(n^2)

            There is a nested for loop which simply results in O(n^2) or O(n*m) since the two for loops
            run for a different number of times. The big Ω notation would be Ω(1) since the values can
            repeat only once in the best case scenario resulting in only 1 code execution.
        */
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    if (y + i < 0) {
                        gameOver();
                        return;
                    }
                    board[y + i][x + j] = true;
                }
            }
        }

        clearLines();

        currentTetrimino = nextTetrimino;
        nextTetrimino = getRandomTetrimino();
    }

    /**
     * Clears completed lines from the board.
     */
    private void clearLines() {
        int linesCleared = 0;

        /*  Algorithmic Efficiency:
            O(n^2) or O(BOARD_HEIGHT * BOARD_WIDTH)

            The first two nested for loops runs for the indicated number of tiles of height and width.
            This makes the program run for BOARD_HEIGHT * BOARD_WIDTH amount of times. These are final
            variables and therefore the big Ω notation is still BOARD_HEIGHT * BOARD_WIDTH. The triple
            nested loops have constant loop numbers based from the previous i value and BOARD_WIDTH and
            therefore does not contribute to the big o notation.
         */
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            boolean lineComplete = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (!board[i][j]) {
                    lineComplete = false;
                    break;
                }
            }
            if (lineComplete) {
                linesCleared++;
                for (int k = i; k > 0; k--) {
                    System.arraycopy(board[k - 1], 0, board[k], 0, BOARD_WIDTH);
                }
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    board[0][j] = false;
                }
            }
        }

        switch (linesCleared) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }

        if (scoreUpdateListener != null) {
            scoreUpdateListener.onScoreUpdated(score);
        }
    }

    /**
     * Listener interface for updating the score.
     */
    public interface ScoreUpdateListener {
        /**
         * Called when the score is updated.
         * @param score The new score.
         */
        void onScoreUpdated(int score);
    }

    /**
     * Ends the game and shows a "Game Over" message.
     */
    private void gameOver() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

}