/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.1, Apr 2017
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * GameBoard
 *
 * This class holds the primary game logic for how different objects interact with one another. Take
 * time to understand how the timer interacts with the different methods and how it repaints the GUI
 * on every tick().
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private Dot[][] board;
    private Path path;
    private boolean shrinkOn = false;
    private boolean expandOn = false;
    private boolean takeFiveOn = false;
    private boolean hasExpanded = false;
    private boolean hasTakenFive = false;
    private int shrinkCounter = 5;
    private JLabel expander;
    private JLabel shrinker;
    private JLabel takeFive;
    private boolean isTimed;
    private int movesCount = 30;
    private int time = 60;
    public boolean playing = true;
    private int score = 0;
    private Timer gameTimer;
    private BufferedWriter bw;
    private boolean hasPrompted = false;

    // Game constants
    public static final int COURT_WIDTH = 380;
    public static final int COURT_HEIGHT = 400;
    public static final Color[] colorSet = new Color[]{Color.RED, Color.ORANGE, Color.yellow, Color.green, Color.CYAN,
            Color.magenta};

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 25;
    public static final int TIMEUPDATE = 1000;

    public GameBoard(JLabel expander, JLabel shrinker, JLabel takeFive) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        Timer timer = new Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });

        gameTimer = new Timer(TIMEUPDATE, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTime();
            }
        });

        resetDialog();

        timer.start();
        setFocusable(true);
        addKeyListener(this);

        path = new Path();
        board = new Dot[6][6];
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                board[i][j] = makeRandomDot(i, j);
            }
        }

        addMouseListener(this);
        addMouseMotionListener(this);

        this.expander = expander;
        this.shrinker = shrinker;
        this.takeFive = takeFive;

        try {
            bw = new BufferedWriter(new FileWriter("highscores.sarah", true));
        }
        catch(IOException e) {

        }
    }

    public Dot makeRandomDot(int i, int j) {
        int isClear = (int)(Math.random()*7);
        Dot d;
        if (isClear == 0) {
            d = new Dot(20 + 60*i, 45 + 60*j, true, Color.GRAY);
        }
        else if (isClear == 1) {
            d = new Dot(20 + 60*i, 45 + 60*j, false, Color.RED);
        }
        else if (isClear == 2) {
            d = new Dot(20 + 60*i, 45 + 60*j, false, Color.ORANGE);
        }
        else if (isClear == 3) {
            d = new Dot(20 + 60 * i, 45 + 60*j, false, Color.YELLOW);
        }

        else if (isClear == 4) {
            d = new Dot(20 + 60*i, 45 + 60*j, false, Color.GREEN);
        }
        else if (isClear == 5){
            d = new Dot(20 + 60*i, 45 + 60*j, false, Color.CYAN);
        }
        else {
            d = new Dot(20 + 60*i, 45 + 60*j, false, Color.MAGENTA);
        }
        return d;
    }

    public void fall(Color c) {
        for (int i = 0; i < 6; i++) {
            for (int j = 5; j > -1; j--) {
                if (board[i][j].getColor().equals(Color.WHITE)) {
                    getLastDot(i, j);
                }

            }
        }
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (board[i][j].getColor().equals(Color.WHITE)) {
                    score++;
                    board[i][j] = makeRandomDot(i, j);
                    while (board[i][j].getColor().equals(c)) {
                        board[i][j] = makeRandomDot(i, j);
                    }
                }
            }
        }
    }

    public void getLastDot(int col, int row) {
        for (int i = row - 1; i > -1; i--) {
            if (!board[col][i].getColor().equals(Color.WHITE)) {
                board[col][row].setColor(board[col][i].getColor());
                board[col][row].setClarity(board[col][i].isClearDot());
                board[col][i].setColor(Color.WHITE);
                return;
            }
        }
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {
        Color c = Color.WHITE;
        if (path.isClosed() && path.getColor().equals(Color.GRAY)) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    board[i][j].setColor(Color.WHITE);
                }
            }
            if (!isTimed) {
                movesCount--;
            }
        }
        else if (path.isClosed()) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j].getColor().equals(path.getColor())) {
                        c = path.getColor();
                        board[i][j].setColor(Color.WHITE);
                    }
                }
            }
            if (!isTimed) {
                movesCount--;
            }
        }
        else {
            int inc = 0;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (path.getLength() >= 2 && path.hasDot(board[i][j])) {
                        board[i][j].setColor(Color.WHITE);
                        inc++;
                    }
                }
            }
            if (inc > 0 && !isTimed) {
                movesCount--;
            }
        }
        fall(c);
        path = new Path();
        isDonePlaying();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        requestFocusInWindow();

        if (takeFiveOn && !hasTakenFive && playing) {
            hasTakenFive = true;
            if (isTimed) {
                time += 5;
            } else {
                movesCount+= 5;
            }
            takeFive.setText("TakeFive Used");
            takeFive.setForeground(Color.red);
        }

        if (expandOn && !hasExpanded && !shrinkOn && playing) {
            int xIndex = -1;
            int yIndex = -1;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if(board[i][j].isInside(e.getX(), e.getY())) {
                        xIndex = i;
                        yIndex = j;
                        hasExpanded = true;
                        expandOn = false;
                        expander.setText("Expander Used");
                        expander.setForeground(Color.red);
                    }
                }
            }
            if (xIndex != -1) {
                Color c = board[xIndex][yIndex].getColor();
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[i].length; j++) {
                        if (board[i][j].getColor().equals(c)) {
                            board[i][j].setColor(Color.WHITE);
                        }
                    }
                }
                movesCount--;
                isDonePlaying();
                fall(c);
            }
        }

        if (shrinkOn && shrinkCounter > 0 && !expandOn && playing) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if(board[i][j].isInside(e.getX(), e.getY())) {
                        board[i][j].setColor(Color.WHITE);
                        shrinkCounter--;
                        shrinker.setText("Shrinkers Left: " + shrinkCounter);
                        if (shrinkCounter == 0) {
                            shrinker.setForeground(Color.red);
                            shrinkOn = false;
                        }
                        movesCount--;
                        isDonePlaying();
                    }
                }
            }
            fall(Color.WHITE);
        }
    }

    public void mouseDragged(MouseEvent e) {
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if (board[i][j].isInside(e.getX(), e.getY())) {
                    if (path.getLength() >= 2 && path.getElem(path.getLength() - 2) == board[i][j]) {
                        path.removeElem(path.getLength() - 2);
                        path.removeElem(path.getLength() - 1);
                    }
                    if (!path.isClosed()) {
                        if (path.getLength() == 0) {
                            path.addDot(board[i][j]);
                        } else {
                            int xDiff = Math.abs(path.lastElement().getXIndex() - board[i][j].getXIndex());
                            int yDiff = Math.abs(path.lastElement().getYIndex() - board[i][j].getYIndex());
                            if (xDiff + yDiff == 1) {
                                path.addDot(board[i][j]);
                            }
                        }
                    }
                }

            }
        }
    }

    public void	mouseMoved(MouseEvent e) {

    }

    public void keyPressed(KeyEvent e) {

        }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S) {
            if (shrinkCounter >= 1) {
                if (shrinkOn) {
                    shrinker.setForeground(Color.red);
                } else {
                    shrinker.setForeground(Color.green);
                }
            }
            shrinkOn = !shrinkOn;
        } else if (e.getKeyCode() == KeyEvent.VK_E) {
            if (!hasExpanded) {
                if (expandOn) {
                    expander.setForeground(Color.red);
                } else {
                    expander.setForeground(Color.green);
                }
            }
            expandOn = !expandOn;
        } else if (e.getKeyCode() == KeyEvent.VK_T) {
            if (!hasTakenFive) {
                if (takeFiveOn) {
                    takeFive.setForeground(Color.red);
                } else {
                    takeFive.setForeground(Color.green);
                }
                takeFiveOn = !takeFiveOn;
            }
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    void tick() {
        repaint();
    }

    void updateTime() {
        if (time >= 1) {
            time--;
        }
        isDonePlaying();
    }

    public void pause() {
        gameTimer.stop();
        JOptionPane pausedScreen = new JOptionPane("<html><h3>Welcome to the Pause Menu!</h3>Hey, so I see you've " +
                "stepped away from the game. I know <br> you need some space, but you can always come back...</html>");
        JDialog dialog = pausedScreen.createDialog(null, "Game is Paused");

        final JButton unpause = new JButton("Unpause");
        unpause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                gameTimer.start();
            }
        });

        final JButton instructions = new JButton("How to Play");
        instructions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                final JFrame instructFrame = new JFrame("Instructions");
                instructFrame.setLocation(500, 100);
                final JPanel instructPanel = new JPanel();
                final JLabel instructText = new JLabel(
                        "<html><h1>Welcome to Dots!</h1>"
                                + "Dots is a beautifully addictive puzzle game about connecting the dots.<br><br>"
                                + "Connect the most dots possible to high-score.<br><br>"
                                + "<h2>How to Play</h2>"
                                + "<ol> <li> Choose a Mode </li>"
                                + "<ul> <li> Choose <b>Moves</b> mode to play Dots with 30 moves. </li>"
                                + "<li> Choose <b>Timed</b> mode to play Dots for 60 seconds. </li> </ul>"
                                + "<li> Connect the Dots </li>"
                                + "<ul> <li> Click and drag to connect adjacent, same-color dots in a path. </li>"
                                + "<li> Gray dots can be connected to a path of any-colored dots. </li>"
                                + "<li> Release mouse to remove path from the board. </li>"
                                + "<li> Above dots fall into the empty spots left behind by removed dots. </li> </ul>"
                                + "<li> Make Squares </li>"
                                + "<ul> <li> When colored dots are connected in a closed path, all same-colored dots " +
                                "are removed from the board. </li>"
                                + "<li> When only gray dots are connected in a closed path, all dots are cleared " +
                                "from the board. </li> </ul>"
                                + "<h2>Power Ups</h2>"
                                + "<ol> <li> Shrinkers" +
                                " <ul><li>Click the \"s\" button to turn on shrinkers.</li>" +
                                "<li>Click a dot to remove it from the board.</li></ul></li>"
                                + "<li> Expanders" +
                                " <ul><li>Click the \"e\" button to turn on expanders.</li>" +
                                "<li>Click a dot to remove it from the board.</li></ul></li>" +
                                "<li> Take Five" +
                                " <ul><li>Click the \"t\" button to turn on take five.</li>" +
                                "<li>In <b>Moves</b> mode, take five adds five moves.</li>" +
                                "<li>In <b>Timed</b> mode, take five adds five seconds.</li></ul></ol> </html>"
                );
                instructPanel.add(instructText);
                instructFrame.add(instructPanel, BorderLayout.CENTER);
                final JButton returnMenu = new JButton("Return to Menu");
                returnMenu.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        instructFrame.setVisible(false);
                        dialog.setVisible(true);
                    }
                });
                instructFrame.add(returnMenu, BorderLayout.SOUTH);
                instructFrame.pack();
                instructFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                instructFrame.setVisible(true);
            }
        });

        final JButton highScores = new JButton("High Scores");
        highScores.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                final JFrame highScoreFrame = new JFrame("High Scores");
                highScoreFrame.setLocation(500, 100);
                final JPanel highScorePanel = new JPanel();
                String highScoreLabel = "<html><h3>HIGH SCORES <br> UNAVAILABLE</h3></html>";
                try {
                    ScoreScanner scorer = ScoreScanner.make("highScores.sarah");
                    highScoreLabel= scorer.getScores();
                }
                catch (Exception ex) {
                }
                final JLabel highScoreText = new JLabel(highScoreLabel);
                highScorePanel.add(highScoreText);
                highScoreFrame.add(highScorePanel, BorderLayout.CENTER);
                highScoreFrame.setPreferredSize(new Dimension(150, 300));
                final JButton returnMenu = new JButton("Return to Menu");
                returnMenu.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        highScoreFrame.setVisible(false);
                        dialog.setVisible(true);
                    }
                });
                highScoreFrame.add(returnMenu, BorderLayout.SOUTH);
                highScoreFrame.pack();
                highScoreFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                highScoreFrame.setVisible(true);
            }
        });

        pausedScreen.setOptions(new Object[] {unpause, instructions, highScores});
        dialog.setSize(new Dimension(420,175));
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }

    private void resetDialog() {
        JOptionPane timedOrMoves = new JOptionPane("PICK A MODE!");
        JDialog dialog = timedOrMoves.createDialog(null, "Welcome to Sarah's Dots!");

        final JButton timed = new JButton("Timed Mode");
        timed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isTimed = true;
                dialog.setVisible(false);
                gameTimer.start();
            }
        });
        final JButton moves = new JButton("Moves Mode");
        moves.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isTimed = false;
                dialog.setVisible(false);
            }
        });
        timedOrMoves.setOptions(new Object[] {timed, moves});
        dialog.setSize(new Dimension(300,100));
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }

    public void reset() {
        gameTimer.stop();
        resetDialog();
        movesCount = 30;
        time = 60;
        playing = true;
        score = 0;
        shrinkOn = false;
        expandOn = false;
        takeFiveOn = false;
        hasExpanded = false;
        hasTakenFive = false;
        hasPrompted = false;
        shrinkCounter = 5;
        expander.setText("Use Expander");
        takeFive.setText("Use TakeFive");
        shrinker.setText("Shrinkers Left: 5");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                board[i][j] = makeRandomDot(i, j);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (playing) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    board[i][j].draw(g);
                }
            }
            path.draw(g);
            g.setFont(new Font("TimesRoman", Font.BOLD, 20));
            g.setColor(Color.black);
            g.drawString(("Score: " + score), 260, 25);
            if (isTimed) {
                g.drawString(("Time Remaining: " + time), 40, 25);
            } else {
                g.drawString(("Moves Remaining: " + movesCount), 25, 25);
            }
        }
        else {
            g.setFont(new Font("TimesRoman", Font.BOLD, 40));
            g.setColor(Color.black);
            g.drawString(("FINAL SCORE"), 50, 170);
            g.setFont(new Font("TimesRoman", Font.BOLD, 70));
            g.drawString((score + "!!!"), 100, 230);
        }
    }

    void isDonePlaying() {
        if (movesCount == 0 || time == 0) {
            playing = false;
            if (!hasPrompted) {
                writeOnFile();
                hasPrompted = true;
            }
        }
    }

    void writeOnFile() {
        String nickname = JOptionPane.showInputDialog("Save your HIGH SCORE!");
        nickname+= "," + score;
        try {
            ScoreScanner scorer = ScoreScanner.make("highScores.sarah");
            if(scorer.scoresNum() != 0) {
                bw.newLine();
            }
            bw.append(nickname);
            bw.flush();
        }
        catch (Exception e) {

        }
    }

    public void getBestMove() {
        int highestScore = 0;
        ArrayList<Dot> dotSet;
        ArrayList<Dot> oneNeighborSet;
        ArrayList<Dot> removedDotSet = new ArrayList<>();

        for (Color color: colorSet) {
            dotSet = new ArrayList<>();
            oneNeighborSet = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (board[i][j].getColor().equals(color) || board[i][j].getColor().equals(Color.gray)) {
                        dotSet.add(board[i][j]);
                    }
                }
            }
            for (Dot d : dotSet) {
                if (numNeighbors(d, color) == 1) {
                    oneNeighborSet.add(d);
                }
            }
            for (Dot n : oneNeighborSet) {
                for (Dot n2 : oneNeighborSet) {
                    ArrayList<Dot> tempDots = findLongestPath(n, n2, color);
                    if (tempDots != null) {
                        if (tempDots.size() > highestScore) {
                            highestScore = tempDots.size();
                            removedDotSet = tempDots;
                        }
                    }
                }
            }
        }
        for (Dot d: removedDotSet) {
            d.setColor(Color.WHITE);
        }
        fall(Color.WHITE);
        movesCount--;
        isDonePlaying();
    }

    public int numNeighbors(Dot d, Color color) {
        int xIndex = d.getXIndex();
        int yIndex = d.getYIndex();
        int neighborCount = 0;
        if (xIndex > 0) {
            if (board[xIndex - 1][yIndex].getColor().equals(color) ||
                    board[xIndex - 1][yIndex].getColor().equals(Color.gray)) {
                neighborCount++;
            }
        }
        if (xIndex < 5) {
            if (board[xIndex + 1][yIndex].getColor().equals(color) ||
                    board[xIndex + 1][yIndex].getColor().equals(Color.gray)) {
                neighborCount++;
            }
        }
        if (yIndex > 0) {
            if (board[xIndex][yIndex - 1].getColor().equals(color) ||
                    board[xIndex][yIndex - 1].getColor().equals(Color.gray)) {
                neighborCount++;
            }
        }
        if (yIndex < 5) {
            if (board[xIndex][yIndex + 1].getColor().equals(color) ||
                    board[xIndex][yIndex + 1].getColor().equals(Color.gray)) {
                neighborCount++;
            }
        }
        return neighborCount;
    }

    private ArrayList<Dot> findLongestPath(Dot start, Dot end, Color color) {
        boolean[][] truePath = new boolean[6][6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                truePath[i][j] = true;
                if (board[i][j].getColor().equals(color) ||
                        board[i][j].getColor().equals(Color.gray)) {
                    truePath[i][j] = false;

                }
            }
        }
        truePath[start.getXIndex()][start.getYIndex()] = false;
        truePath[end.getXIndex()][end.getYIndex()] = false;
        ArrayList<Dot> dots = pathHelper(truePath, start, end);
        return dots;
    }
    private ArrayList<Dot> pathHelper(boolean[][] grid, Dot start, Dot end) {

        ArrayList<Dot> result = null;
        if (start == null || end == null)
            return null;
        int startX = start.getXIndex();
        int startY = start.getYIndex();
        if (startX < 0 || startY < 0)
            return null;
        if (start.getXIndex() == 6 || start.getYIndex() == 6)
            return null;
        if (grid[startX][startY] == true)
            return null;
        if (start.equals(end))
        {
            ArrayList<Dot> path = new ArrayList<>();
            path.add(start);
            return path;
        }
        grid[start.getXIndex()][start.getYIndex()] = true;
        Dot[] sideDots = new Dot[4];
        if (startX > 0) {
            sideDots[0] = board[startX - 1][startY];
        }
        if (startX < 5) {
            sideDots[2] = board[startX + 1][startY];
        }
        if (startY > 0) {
            sideDots[1] = board[startX][startY - 1];
        }
        if (startY < 5) {
            sideDots[3] = board[startX][startY + 1];
        }
        int maxLength = -1;
        for (Dot d: sideDots)
        {
            ArrayList<Dot> path = pathHelper(grid, d, end);
            if (path != null && path.size() > maxLength)
            {
                maxLength = path.size();
                path.add(0, start);
                result = path;
            }
        }
        grid[startX][startY] = false;
        if (result == null || result.size() == 0)
            return null;
        return result;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }
}