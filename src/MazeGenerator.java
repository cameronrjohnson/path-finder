import java.util.*;

public class MazeGenerator {
    private final int rows;
    private final int cols;
    private final Random random = new Random();

    public MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public void generateMaze(Cell[][] grid) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c].setWall(false);
            }
        }

        boolean[][] visited = new boolean[rows][cols];
        Stack<Cell> stack = new Stack<>();

        Cell start = grid[random.nextInt(rows)][random.nextInt(cols)];
        visited[start.getRow()][start.getCol()] = true;
        stack.push(start);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Cell> neighbors = getUnvisitedNeighbors(current, grid, visited);

            if (!neighbors.isEmpty()) {
                Cell next = neighbors.get(random.nextInt(neighbors.size()));
                visited[next.getRow()][next.getCol()] = true;

                if (random.nextDouble() < 0.3 && !next.isStart() && !next.isEnd()) {
                    next.setWall(true);
                }

                stack.push(next);
            } else {
                stack.pop();
            }
        }
    }

    private List<Cell> getUnvisitedNeighbors(Cell cell, Cell[][] grid, boolean[][] visited) {
        int r = cell.getRow();
        int c = cell.getCol();
        List<Cell> neighbors = new ArrayList<>();

        if (r > 0 && !visited[r - 1][c]) neighbors.add(grid[r - 1][c]);
        if (r < rows - 1 && !visited[r + 1][c]) neighbors.add(grid[r + 1][c]);
        if (c > 0 && !visited[r][c - 1]) neighbors.add(grid[r][c - 1]);
        if (c < cols - 1 && !visited[r][c + 1]) neighbors.add(grid[r][c + 1]);

        return neighbors;
    }
}
