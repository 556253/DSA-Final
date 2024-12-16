/**
 * @author Jay Um
 * @author Kai Quan Chua
 *
 * The Main class initializes and starts the Tetris game with two players running simultaneously.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Main {

    /**
     * The label displaying the scores of the two players and the leader.
     */
    private static JLabel scoreLabel;

    /**
     * Main entry point for the program. Initializes the window and both Tetris game instances.
     * @param args Command line arguments (not used in this program).
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris: Two Players");

        scoreLabel = new JLabel("Player 1: 0 | Player 2: 0 | Leader: Tied", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Tetris tetris1 = new Tetris(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, score -> updateScores(1, score));
        Tetris tetris2 = new Tetris(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, score -> updateScores(2, score));

        JPanel container = new JPanel();
        container.setLayout(new GridLayout(1, 2, 10, 0));
        container.add(tetris1);
        container.add(tetris2);

        frame.setLayout(new BorderLayout());
        frame.add(scoreLabel, BorderLayout.NORTH);
        frame.add(container, BorderLayout.CENTER);

        frame.setSize(1000, 700);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * The score of Player 1.
     */
    private static int player1Score = 0;

    /**
     * The score of Player 2.
     */
    private static int player2Score = 0;


    /**
     * Updates the scores for the players and updates the label displaying the score and leader.
     * @param player The player number (1 or 2).
     * @param score The new score for the player.
     */
    private static void updateScores(int player, int score) {
        if (player == 1) {
            player1Score = score;
        } else if (player == 2) {
            player2Score = score;
        }

        String leader;
        if (player1Score > player2Score) {
            leader = "Player 1";
        } else if (player2Score > player1Score) {
            leader = "Player 2";
        } else {
            leader = "Tied";
        }

        scoreLabel.setText(String.format("Player 1: %d | Player 2: %d | Leader: %s", player1Score, player2Score, leader));
    }
}
