/**
 * @author Jay Um
 * @author Kai Quan Chua
 *
 * The Tetrimino class represents a single Tetrimino, which has a shape, position, and color.
 */

import java.awt.Color;

class Tetrimino {

    /** The shape of the Tetrimino (a 2D array representing the blocks). */
    private int[][] shape;

    /** The x-coordinate of the Tetrimino. */
    private int x;

    /** The y-coordinate of the Tetrimino. */
    private int y;

    /** The color of the Tetrimino. */
    private Color color;

    /** The predefined colors for the Tetrimino shapes. */
    private static final Color[] COLORS = {
            Color.CYAN,    // I shape
            Color.MAGENTA, // T shape
            Color.YELLOW,  // O shape
            Color.GREEN,   // S shape
            Color.RED,     // Z shape
            Color.BLUE,    // J shape
            Color.ORANGE   // L shape
    };

    /**
     * Constructor for creating a new Tetrimino.
     * @param shape The shape of the Tetrimino.
     * @param typeIndex The index corresponding to the Tetrimino type (used to set color).
     */
    public Tetrimino(int[][] shape, int typeIndex) {
        this.shape = shape;
        this.color = COLORS[typeIndex];
        this.x = 3;
        this.y = -shape.length;
    }

    /**
     * Gets the shape of the Tetrimino.
     * @return A 2D array representing the shape.
     */
    public int[][] getShape() {
        return shape;
    }

    /**
     * Gets the x-coordinate of the Tetrimino.
     * @return The x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the Tetrimino.
     * @return The y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the color of the Tetrimino.
     * @return The color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Rotates the Tetrimino 90 degrees clockwise.
     */
    public void rotate() {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotatedShape = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotatedShape[j][rows - 1 - i] = shape[i][j];
            }
        }
        shape = rotatedShape;
    }

    /**
     * Moves the Tetrimino down by one cell.
     */
    public void moveDown() {
        y++;
    }

    /**
     * Moves the Tetrimino left by one cell.
     */
    public void moveLeft() {
        x--;
    }

    /**
     * Moves the Tetrimino right by one cell.
     */
    public void moveRight() {
        x++;
    }
}
