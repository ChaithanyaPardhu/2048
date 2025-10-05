import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.border.Border;
import java.util.Random;

// All-in-one version of 2048 game in Java (MVC pattern)
public class GameOf2048All {
    
    // ========== MODEL ==========

    public static class Board {
        public static final int SIZE = 4;
        public static final int WINNING_TILE = 2048;
        public static final int EMPTY_TILE = 0;
        public static final Random RANDOM = new Random();
        private int[][] board;

        public Board() {
            board = new int[SIZE][SIZE];
            addRandomDigit(2);
            addRandomDigit(4);
        }

        public void setBoard(int[][] b) {
            for (int i = 0; i < SIZE; i++)
                for (int j = 0; j < SIZE; j++)
                    board[i][j] = b[i][j];
        }

        public int[][] getBoard() {
            int[][] copy = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++)
                for (int j = 0; j < SIZE; j++)
                    copy[i][j] = board[i][j];
            return copy;
        }

        public boolean searchOnBoard(int x) {
            for (int i = 0; i < SIZE; i++)
                for (int j = 0; j < SIZE; j++)
                    if (board[i][j] == x)
                        return true;
            return false;
        }

        public void addRandomDigit() {
            if (RANDOM.nextInt() % 2 == 0) addRandomDigit(2);
            else addRandomDigit(4);
        }

        private void addRandomDigit(int digit) {
            int i = RANDOM.nextInt(SIZE);
            int j = RANDOM.nextInt(SIZE);
            while (board[i][j] != 0) {
                i = RANDOM.nextInt(SIZE);
                j = RANDOM.nextInt(SIZE);
            }
            board[i][j] = digit;
        }
    }


    public static class GameOf2048 {
        public static final char MOVE_LEFT = 'A';
        public static final char MOVE_RIGHT = 'D';
        public static final char MOVE_UP = 'W';
        public static final char MOVE_DOWN = 'S';

        private Board board;
        private int score;

        public GameOf2048() {
            board = new Board();
            score = 0;
        }

        public Board getBoard() { return board; }
        public int getScore() { return score; }

        public boolean gameWon() {
            return board.searchOnBoard(Board.WINNING_TILE);
        }

        public boolean isGameOver() {
            if (gameWon()) return true;
            if (board.searchOnBoard(Board.EMPTY_TILE)) return false;
            return !userCanMakeAMove();
        }

        public boolean userCanMakeAMove() {
            int[][] b = board.getBoard();
            for (int i = 0; i < Board.SIZE - 1; i++)
                for (int j = 0; j < Board.SIZE - 1; j++)
                    if (b[i][j] == b[i][j + 1] || b[i][j] == b[i + 1][j])
                        return true;
            for (int j = 0; j < Board.SIZE - 1; j++)
                if (b[Board.SIZE - 1][j] == b[Board.SIZE - 1][j + 1])
                    return true;
            for (int i = 0; i < Board.SIZE - 1; i++)
                if (b[i][Board.SIZE - 1] == b[i + 1][Board.SIZE - 1])
                    return true;
            return false;
        }

        public int[] processLeftMove(int[] row) {
            int[] newRow = new int[Board.SIZE];
            int j = 0;
            for (int i = 0; i < Board.SIZE; i++)
                if (row[i] != 0) newRow[j++] = row[i];
            for (int i = 0; i < Board.SIZE - 1; i++) {
                if (newRow[i] != 0 && newRow[i] == newRow[i + 1]) {
                    newRow[i] = 2 * newRow[i];
                    score += newRow[i];
                    for (j = i + 1; j < Board.SIZE - 1; j++)
                        newRow[j] = newRow[j + 1];
                    newRow[Board.SIZE - 1] = 0;
                }
            }
            return newRow;
        }

        public int[] reverseArray(int[] arr) {
            int[] rev = new int[arr.length];
            for (int i = arr.length - 1; i >= 0; i--)
                rev[i] = arr[arr.length - i - 1];
            return rev;
        }

        public int[] processRightMove(int[] row) {
            int[] newRow = new int[Board.SIZE];
            int j = 0;
            for (int i = 0; i < Board.SIZE; i++)
                if (row[i] != 0)
                    newRow[j++] = row[i];
            newRow = reverseArray(newRow);
            newRow = processLeftMove(newRow);
            return reverseArray(newRow);
        }

        private boolean checkMoveMade(int[][] oldB, int[][] newB) {
            for (int i = 0; i < Board.SIZE; i++)
                for (int j = 0; j < Board.SIZE; j++)
                    if (oldB[i][j] != newB[i][j]) return true;
            return false;
        }

        public boolean processMove(char move) {
            int[][] boardArr = this.board.getBoard();
            int[][] oldBoard = new int[Board.SIZE][Board.SIZE];
            for (int i = 0; i < Board.SIZE; i++)
                oldBoard[i] = boardArr[i].clone();

            switch (move) {
            case MOVE_LEFT:
                for (int i = 0; i < Board.SIZE; i++) {
                    int[] newRow = processLeftMove(boardArr[i]);
                    System.arraycopy(newRow, 0, boardArr[i], 0, Board.SIZE);
                }
                break;
            case MOVE_RIGHT:
                for (int i = 0; i < Board.SIZE; i++) {
                    int[] newRow = processRightMove(boardArr[i]);
                    System.arraycopy(newRow, 0, boardArr[i], 0, Board.SIZE);
                }
                break;
            case MOVE_UP:
                for (int j = 0; j < Board.SIZE; j++) {
                    int[] col = new int[Board.SIZE];
                    for (int i = 0; i < Board.SIZE; i++) col[i] = boardArr[i][j];
                    int[] newCol = processLeftMove(col);
                    for (int i = 0; i < Board.SIZE; i++) boardArr[i][j] = newCol[i];
                }
                break;
            case MOVE_DOWN:
                for (int j = 0; j < Board.SIZE; j++) {
                    int[] col = new int[Board.SIZE];
                    for (int i = 0; i < Board.SIZE; i++) col[i] = boardArr[i][j];
                    int[] newCol = processRightMove(col);
                    for (int i = 0; i < Board.SIZE; i++) boardArr[i][j] = newCol[i];
                }
                break;
            }

            boolean moveMade = checkMoveMade(oldBoard, boardArr);

            this.board.setBoard(boardArr);
            return moveMade;
        }
    }

    // ========== CONTROLLER ==========

    public static class GameOf2048Controller {
        private GameOf2048 gameOf2048;
        private GameOf2048View gameOf2048View;

        public void initModel(GameOf2048 gameOf2048) {
            this.gameOf2048 = gameOf2048;
        }

        public void initView(GameOf2048View gameOf2048View) {
            this.gameOf2048View = gameOf2048View;
            gameOf2048View.updateBoard(gameOf2048.getBoard(), gameOf2048.getScore());
        }

        public void handleUserInput(char move) {
            boolean moveMade = gameOf2048.processMove(move);

            if (gameOf2048.isGameOver()) {
                gameOf2048View.updateBoard(gameOf2048.getBoard(), gameOf2048.getScore());
                if (gameOf2048.gameWon()) {
                    gameOf2048View.showGameResult("You won!");
                } else {
                    gameOf2048View.showGameResult("You lost!");
                }
            } else {
                if (moveMade) {
                    gameOf2048.getBoard().addRandomDigit();
                    gameOf2048View.updateBoard(gameOf2048.getBoard(), gameOf2048.getScore());
                }
            }
        }
    }

    // ========== VIEW ==========

    public static class GameOf2048View extends JFrame {
        private static final int TILE_WIDTH = 100;
        private static final int TILE_HEIGHT = 100;
        private static final int WINDOW_WIDTH = TILE_WIDTH * Board.SIZE + 50;
        private static final int WINDOW_HEIGHT = TILE_HEIGHT * Board.SIZE + 100;

        private static final Color COLOR_BLANK_TILE = new Color(197, 183, 170);
        private static final Color COLOR_2 = new Color(240, 240, 240);
        private static final Color COLOR_4 = new Color(237, 224, 200);
        private static final Color COLOR_8 = new Color(242, 177, 121);
        private static final Color COLOR_16 = new Color(245, 149, 99);
        private static final Color COLOR_32 = new Color(246, 124, 95);
        private static final Color COLOR_64 = new Color(246, 94, 59);
        private static final Color COLOR_128 = new Color(237, 207, 114);
        private static final Color COLOR_256 = new Color(237, 204, 97);
        private static final Color COLOR_512 = new Color(237, 200, 80);
        private static final Color COLOR_1024 = new Color(237, 197, 63);
        private static final Color COLOR_2048 = new Color(237, 194, 46);

        private class GameOf2048Tile extends JLabel {
            public GameOf2048Tile() {
                super("", SwingConstants.CENTER);
                setOpaque(true);
                setPreferredSize(new Dimension(TILE_WIDTH, TILE_HEIGHT));
                setBorder(BorderFactory.createLineBorder(new Color(147, 133, 120), 3));
                setBackground(COLOR_BLANK_TILE);
                setFont(new Font("Serif", Font.BOLD, 40));
            }
            public void setNumber(int n) {
                setText(n == 0 ? "" : String.valueOf(n));
                switch (n) {
                    case 0: setBackground(COLOR_BLANK_TILE); break;
                    case 2: setBackground(COLOR_2); break;
                    case 4: setBackground(COLOR_4); break;
                    case 8: setBackground(COLOR_8); break;
                    case 16: setBackground(COLOR_16); break;
                    case 32: setBackground(COLOR_32); break;
                    case 64: setBackground(COLOR_64); break;
                    case 128: setBackground(COLOR_128); break;
                    case 256: setBackground(COLOR_256); break;
                    case 512: setBackground(COLOR_512); break;
                    case 1024: setBackground(COLOR_1024); break;
                    case 2048: setBackground(COLOR_2048); break;
                }
            }
        }

        private JLabel labelScore;
        private GameOf2048Tile[][] tiles;
        private GameOf2048Controller gameOf2048Controller;

        public GameOf2048View() {
            setup();
            setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            setTitle("Game of 2048");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            setVisible(true);
        }

        public void initController(GameOf2048Controller controller) {
            this.gameOf2048Controller = controller;
        }

        private void setup() {
            JPanel mainPanel = new JPanel(new BorderLayout());
            JPanel topPanel = new JPanel(new FlowLayout());
            labelScore = new JLabel("Score: 0");
            topPanel.add(labelScore);

            JPanel centerPanel = new JPanel(new GridLayout(Board.SIZE, Board.SIZE));
            tiles = new GameOf2048Tile[Board.SIZE][Board.SIZE];
            for (int i = 0; i < Board.SIZE; i++) {
                for (int j = 0; j < Board.SIZE; j++) {
                    tiles[i][j] = new GameOf2048Tile();
                    centerPanel.add(tiles[i][j]);
                }
            }

            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(centerPanel, BorderLayout.CENTER);

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (gameOf2048Controller == null) return;
                    if (e.getKeyCode() == KeyEvent.VK_UP)
                        gameOf2048Controller.handleUserInput(GameOf2048.MOVE_UP);
                    if (e.getKeyCode() == KeyEvent.VK_DOWN)
                        gameOf2048Controller.handleUserInput(GameOf2048.MOVE_DOWN);
                    if (e.getKeyCode() == KeyEvent.VK_LEFT)
                        gameOf2048Controller.handleUserInput(GameOf2048.MOVE_LEFT);
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                        gameOf2048Controller.handleUserInput(GameOf2048.MOVE_RIGHT);
                }
            });

            setContentPane(mainPanel);
        }

        public void updateBoard(Board boardObject, int score) {
            int[][] board = boardObject.getBoard();
            for (int i = 0; i < Board.SIZE; i++)
                for (int j = 0; j < Board.SIZE; j++)
                    tiles[i][j].setNumber(board[i][j]);
            labelScore.setText("Score: " + score);
        }

        public void showGameResult(String message) {
            JOptionPane.showMessageDialog(this, message, "Game Over!", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    // ========== MAIN ==========
    public static void main(String[] args) {
        // model
        GameOf2048 game = new GameOf2048();
        // view
        GameOf2048View view = new GameOf2048View();
        // controller
        GameOf2048Controller controller = new GameOf2048Controller();
        controller.initModel(game);
        controller.initView(view);
        view.initController(controller);
    }
}

