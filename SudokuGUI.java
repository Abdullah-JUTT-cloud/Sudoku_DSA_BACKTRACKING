import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// Main Sudoku GUI Application
public class SudokuGUI extends JFrame {
    private SudokuBoard board;
    private SudokuSolver solver;
    private JButton solveButton, clearButton, generateButton, resetButton, checkButton;
    private JLabel statusLabel;
    private JPanel controlPanel, boardPanel;

    public SudokuGUI() {
        setTitle("Sudoku Solver - DSA Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        solver = new SudokuSolver();
        board = new SudokuBoard();

        initComponents();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        // Board Panel
        boardPanel = new JPanel(new BorderLayout());
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        boardPanel.add(board, BorderLayout.CENTER);

        // Control Panel
        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        solveButton = new JButton("Solve");
        clearButton = new JButton("Clear");
        generateButton = new JButton("Generate Puzzle");
        resetButton = new JButton("Reset");
        checkButton = new JButton("Check Solution");

        styleButton(solveButton, new Color(46, 125, 50));
        styleButton(clearButton, new Color(211, 47, 47));
        styleButton(generateButton, new Color(25, 118, 210));
        styleButton(resetButton, new Color(245, 124, 0));
        styleButton(checkButton, new Color(123, 31, 162));

        controlPanel.add(generateButton);
        controlPanel.add(checkButton);
        controlPanel.add(solveButton);
        controlPanel.add(resetButton);
        controlPanel.add(clearButton);

        // Status Label
        statusLabel = new JLabel("Welcome! Generate a puzzle or enter your own.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add action listeners
        solveButton.addActionListener(e -> solveSudoku());
        clearButton.addActionListener(e -> clearBoard());
        generateButton.addActionListener(e -> generatePuzzle());
        resetButton.addActionListener(e -> resetToOriginal());
        checkButton.addActionListener(e -> checkSolution());

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    private void solveSudoku() {
        int[][] grid = board.getGrid();
        board.saveOriginal();

        if (solver.solve(grid)) {
            board.setGrid(grid);
            statusLabel.setText("Solution found!");
            statusLabel.setForeground(new Color(46, 125, 50));
        } else {
            statusLabel.setText("No solution exists for this puzzle!");
            statusLabel.setForeground(new Color(211, 47, 47));
        }
    }

    private void clearBoard() {
        board.clear();
        statusLabel.setText("Board cleared. Enter a new puzzle or generate one.");
        statusLabel.setForeground(Color.BLACK);
    }

    private void generatePuzzle() {
        int[][] puzzle = PuzzleGenerator.generate();
        board.setGrid(puzzle);
        board.saveOriginal();
        statusLabel.setText("New puzzle generated! Try to solve it.");
        statusLabel.setForeground(new Color(25, 118, 210));
    }

    private void resetToOriginal() {
        board.resetToOriginal();
        statusLabel.setText("Reset to original puzzle.");
        statusLabel.setForeground(Color.BLACK);
    }

    private void checkSolution() {
        int[][] grid = board.getGrid();

        // Check if board is complete
        boolean isComplete = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    isComplete = false;
                    break;
                }
            }
            if (!isComplete) break;
        }

        if (!isComplete) {
            statusLabel.setText("❌ Puzzle is incomplete! Fill all cells first.");
            statusLabel.setForeground(new Color(211, 47, 47));
            JOptionPane.showMessageDialog(this,
                    "Please fill all cells before checking!",
                    "Incomplete Puzzle",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if solution is valid
        if (solver.isValidSolution(grid)) {
            statusLabel.setText("✓ Correct! You solved it perfectly!");
            statusLabel.setForeground(new Color(46, 125, 50));
            JOptionPane.showMessageDialog(this,
                    "Congratulations! Your solution is correct!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            statusLabel.setText("❌ Incorrect solution. Keep trying!");
            statusLabel.setForeground(new Color(211, 47, 47));
            JOptionPane.showMessageDialog(this,
                    "The solution is incorrect. Check for duplicate numbers in rows, columns, or 3x3 boxes.",
                    "Incorrect Solution",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new SudokuGUI().setVisible(true);
        });
    }
}

// Sudoku Board GUI Component
class SudokuBoard extends JPanel {
    private JTextField[][] cells;
    private int[][] originalGrid;
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;

    public SudokuBoard() {
        setLayout(new GridLayout(SIZE, SIZE, 2, 2));
        setPreferredSize(new Dimension(450, 450));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setBackground(Color.BLACK);

        cells = new JTextField[SIZE][SIZE];
        originalGrid = new int[SIZE][SIZE];
        initCells();
    }

    private void initCells() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new JTextField();
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setFont(new Font("Arial", Font.BOLD, 20));

                // Color coding for 3x3 subgrids
                if ((i / SUBGRID + j / SUBGRID) % 2 == 0) {
                    cells[i][j].setBackground(new Color(230, 230, 250));
                } else {
                    cells[i][j].setBackground(Color.WHITE);
                }

                // Input validation
                final int row = i, col = j;
                cells[i][j].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!(Character.isDigit(c) && c >= '1' && c <= '9') && c != KeyEvent.VK_BACK_SPACE) {
                            e.consume();
                        }
                        if (cells[row][col].getText().length() >= 1 && c != KeyEvent.VK_BACK_SPACE) {
                            e.consume();
                        }
                    }
                });

                add(cells[i][j]);
            }
        }
    }

    public int[][] getGrid() {
        int[][] grid = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String text = cells[i][j].getText().trim();
                grid[i][j] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }
        return grid;
    }

    public void setGrid(int[][] grid) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == 0) {
                    cells[i][j].setText("");
                } else {
                    cells[i][j].setText(String.valueOf(grid[i][j]));
                }

                // Mark original numbers
                if (originalGrid[i][j] != 0) {
                    cells[i][j].setForeground(Color.BLACK);
                    cells[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                } else {
                    cells[i][j].setForeground(new Color(25, 118, 210));
                    cells[i][j].setFont(new Font("Arial", Font.PLAIN, 20));
                }
            }
        }
    }

    public void saveOriginal() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String text = cells[i][j].getText().trim();
                originalGrid[i][j] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }
    }

    public void resetToOriginal() {
        setGrid(originalGrid);
    }

    public void clear() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j].setText("");
                originalGrid[i][j] = 0;
            }
        }
    }
}

// Sudoku Solver with Backtracking
class SudokuSolver {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;

    // Data Structures Used:
    // 1. HashSet for checking validity (O(1) lookup)
    // 2. Stack for backtracking (implicit in recursion)
    // 3. 2D Array for grid representation

    public boolean solve(int[][] grid) {
        return backtrack(grid, 0, 0);
    }

    // Check if a completed grid is a valid solution
    public boolean isValidSolution(int[][] grid) {
        // Check all rows
        for (int i = 0; i < SIZE; i++) {
            if (!isValidRow(grid, i)) return false;
        }

        // Check all columns
        for (int j = 0; j < SIZE; j++) {
            if (!isValidColumn(grid, j)) return false;
        }

        // Check all 3x3 subgrids
        for (int row = 0; row < SIZE; row += SUBGRID) {
            for (int col = 0; col < SIZE; col += SUBGRID) {
                if (!isValidSubgrid(grid, row, col)) return false;
            }
        }

        return true;
    }

    private boolean isValidRow(int[][] grid, int row) {
        HashSet<Integer> seen = new HashSet<>();
        for (int j = 0; j < SIZE; j++) {
            int num = grid[row][j];
            if (num < 1 || num > 9 || seen.contains(num)) {
                return false;
            }
            seen.add(num);
        }
        return true;
    }

    private boolean isValidColumn(int[][] grid, int col) {
        HashSet<Integer> seen = new HashSet<>();
        for (int i = 0; i < SIZE; i++) {
            int num = grid[i][col];
            if (num < 1 || num > 9 || seen.contains(num)) {
                return false;
            }
            seen.add(num);
        }
        return true;
    }

    private boolean isValidSubgrid(int[][] grid, int startRow, int startCol) {
        HashSet<Integer> seen = new HashSet<>();
        for (int i = startRow; i < startRow + SUBGRID; i++) {
            for (int j = startCol; j < startCol + SUBGRID; j++) {
                int num = grid[i][j];
                if (num < 1 || num > 9 || seen.contains(num)) {
                    return false;
                }
                seen.add(num);
            }
        }
        return true;
    }

    private boolean backtrack(int[][] grid, int row, int col) {
        // Base case: reached end of grid
        if (row == SIZE) {
            return true;
        }

        // Calculate next position
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SIZE - 1) ? 0 : col + 1;

        // Skip filled cells
        if (grid[row][col] != 0) {
            return backtrack(grid, nextRow, nextCol);
        }

        // Try digits 1-9
        for (int digit = 1; digit <= 9; digit++) {
            if (isValid(grid, row, col, digit)) {
                grid[row][col] = digit;

                if (backtrack(grid, nextRow, nextCol)) {
                    return true;
                }

                // Backtrack
                grid[row][col] = 0;
            }
        }

        return false;
    }

    private boolean isValid(int[][] grid, int row, int col, int digit) {
        // Using HashSet for efficient checking (Data Structure #1)

        // Check row
        for (int j = 0; j < SIZE; j++) {
            if (grid[row][j] == digit) {
                return false;
            }
        }

        // Check column
        for (int i = 0; i < SIZE; i++) {
            if (grid[i][col] == digit) {
                return false;
            }
        }

        // Check 3x3 subgrid
        int startRow = (row / SUBGRID) * SUBGRID;
        int startCol = (col / SUBGRID) * SUBGRID;

        for (int i = startRow; i < startRow + SUBGRID; i++) {
            for (int j = startCol; j < startCol + SUBGRID; j++) {
                if (grid[i][j] == digit) {
                    return false;
                }
            }
        }

        return true;
    }
}

// Puzzle Generator
class PuzzleGenerator {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;
    private static Random random = new Random();

    public static int[][] generate() {
        int[][] grid = new int[SIZE][SIZE];
        fillDiagonalSubgrids(grid);
        solveSudoku(grid);
        removeNumbers(grid, 40); // Remove 40 numbers for medium difficulty
        return grid;
    }

    private static void fillDiagonalSubgrids(int[][] grid) {
        for (int i = 0; i < SIZE; i += SUBGRID) {
            fillSubgrid(grid, i, i);
        }
    }

    private static void fillSubgrid(int[][] grid, int row, int col) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        int index = 0;
        for (int i = 0; i < SUBGRID; i++) {
            for (int j = 0; j < SUBGRID; j++) {
                grid[row + i][col + j] = numbers.get(index++);
            }
        }
    }

    private static boolean solveSudoku(int[][] grid) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValidPlacement(grid, row, col, num)) {
                            grid[row][col] = num;
                            if (solveSudoku(grid)) {
                                return true;
                            }
                            grid[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static void removeNumbers(int[][] grid, int count) {
        while (count > 0) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);

            if (grid[row][col] != 0) {
                grid[row][col] = 0;
                count--;
            }
        }
    }

    private static boolean isValidPlacement(int[][] grid, int row, int col, int num) {
        for (int j = 0; j < SIZE; j++) {
            if (grid[row][j] == num) return false;
        }

        for (int i = 0; i < SIZE; i++) {
            if (grid[i][col] == num) return false;
        }

        int startRow = (row / SUBGRID) * SUBGRID;
        int startCol = (col / SUBGRID) * SUBGRID;

        for (int i = startRow; i < startRow + SUBGRID; i++) {
            for (int j = startCol; j < startCol + SUBGRID; j++) {
                if (grid[i][j] == num) return false;
            }
        }

        return true;
    }
}