/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.1, Apr 2017
 */

// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {

    private JLabel expander = new JLabel("Use Expander");
    private JLabel shrinker = new JLabel("Shrinkers Left: 5");
    private JLabel takeFive = new JLabel("Use TakeFive");

    public void run() {
        final JFrame frame = new JFrame("Sarah's Dots");
        frame.setLocation(100, 100);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);

        expander.setBorder(new EmptyBorder(0,7,0,7));
        shrinker.setBorder(new EmptyBorder(0,7,0,7));
        takeFive.setBorder(new EmptyBorder(0,7,0,7));

        expander.setForeground(Color.red);
        shrinker.setForeground(Color.red);
        takeFive.setForeground(Color.red);

        final GameBoard board = new GameBoard(expander, shrinker, takeFive);
        frame.add(board, BorderLayout.CENTER);

        status_panel.add(expander);
        status_panel.add(shrinker);
        status_panel.add(takeFive);

        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        final JButton pause = new JButton("Pause");
        pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.pause();
            }
        });
        control_panel.add(pause);

        final JButton restart = new JButton("Restart");
        restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.reset();
            }
        });
        control_panel.add(restart);

        final JButton bestMove = new JButton("Best Move");
        bestMove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.getBestMove();
            }
        });
        control_panel.add(bestMove);

        frame.addKeyListener(board);
        board.requestFocusInWindow();

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }
}