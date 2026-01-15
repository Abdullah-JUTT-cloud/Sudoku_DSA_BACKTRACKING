import java.util.*;

// Puzzle Generator
class PuzzleGenerator {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;
    private static Random random = new Random();

    public static int[][] generate() {
        int[][] grid = new int[SIZE][SIZE];
        fillDiagonal(grid);
        solve(grid);
        remove(grid, 40);
        return grid;
    }
    
    private static void fillDiagonal(int[][] grid) {
        for (int i = 0; i < SIZE; i += SUBGRID) fillBox(grid, i, i);
    }

    private static void fillBox(int[][] grid, int r, int c) {
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= 9; i++) nums.add(i);
        Collections.shuffle(nums);
        int k = 0;
        for (int i = 0; i < SUBGRID; i++)
            for (int j = 0; j < SUBGRID; j++)
                grid[r + i][c + j] = nums.get(k++);
    }

    private static boolean solve(int[][] grid) {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == 0)
                    for (int n = 1; n <= 9; n++)
                        if (valid(grid, r, c, n)) {
                            grid[r][c] = n;
                            if (solve(grid)) return true;
                            grid[r][c] = 0;
                        }
        return true;
    }

    private static void remove(int[][] grid, int count) {
        while (count-- > 0) {
            int r = random.nextInt(SIZE);
            int c = random.nextInt(SIZE);
            grid[r][c] = 0;
        }
    }

    private static boolean valid(int[][] g, int r, int c, int n) {
        for (int i = 0; i < SIZE; i++)
            if (g[r][i] == n || g[i][c] == n) return false;
        int sr = (r / SUBGRID) * SUBGRID;
        int sc = (c / SUBGRID) * SUBGRID;
        for (int i = sr; i < sr + SUBGRID; i++)
            for (int j = sc; j < sc + SUBGRID; j++)
                if (g[i][j] == n) return false;
        return true;
    }
}

