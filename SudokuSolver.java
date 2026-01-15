import java.util.*;

// Sudoku Solver with Backtracking
class SudokuSolver {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;

    public boolean solve(int[][] grid) {
        return backtrack(grid, 0, 0);
    }

    public boolean isValidSolution(int[][] grid) {
        for (int i = 0; i < SIZE; i++) {
            if (!isValidRow(grid, i) || !isValidColumn(grid, i)) return false;
        }
        for (int r = 0; r < SIZE; r += SUBGRID) {
            for (int c = 0; c < SIZE; c += SUBGRID) {
                if (!isValidSubgrid(grid, r, c)) return false;
            }
        }
        return true;
    }

    private boolean backtrack(int[][] grid, int row, int col) {
        if (row == SIZE) return true;
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SIZE - 1) ? 0 : col + 1;

        if (grid[row][col] != 0) return backtrack(grid, nextRow, nextCol);

        for (int num = 1; num <= 9; num++) {
            if (isValid(grid, row, col, num)) {
                grid[row][col] = num;
                if (backtrack(grid, nextRow, nextCol)) return true;
                grid[row][col] = 0;
            }
        }
        return false;
    }

    private boolean isValid(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (grid[row][i] == num || grid[i][col] == num) return false;
        }
        int sr = (row / SUBGRID) * SUBGRID;
        int sc = (col / SUBGRID) * SUBGRID;
        for (int i = sr; i < sr + SUBGRID; i++) {
            for (int j = sc; j < sc + SUBGRID; j++) {
                if (grid[i][j] == num) return false;
            }
        }
        return true;
    }

    private boolean isValidRow(int[][] grid, int row) {
        return new HashSet<Integer>() {{
            for (int n : grid[row]) add(n);
        }}.size() == 9;
    }

    private boolean isValidColumn(int[][] grid, int col) {
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < SIZE; i++) set.add(grid[i][col]);
        return set.size() == 9;
    }

    private boolean isValidSubgrid(int[][] grid, int r, int c) {
        HashSet<Integer> set = new HashSet<>();
        for (int i = r; i < r + SUBGRID; i++)
            for (int j = c; j < c + SUBGRID; j++)
                set.add(grid[i][j]);
        return set.size() == 9;
    }
}

