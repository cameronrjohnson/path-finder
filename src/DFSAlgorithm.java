import java.util.*;

public class DFSAlgorithm implements PathfindingAlgorithm {
    private Cell[][] grid;
    private Cell end;
    private Deque<Cell> stack; 
    private boolean[][] closedSet;
    private boolean finished;
    private Set<Cell> frontier;
    private Set<Cell> visitedSet;
    private List<Cell> path;

    @Override
    public void initialize(Cell[][] grid, Cell start, Cell end) {
        this.grid = grid;
        this.end = end;
        int rows = grid.length;
        int cols = grid[0].length;

        this.stack = new ArrayDeque<>();
        this.closedSet = new boolean[rows][cols];
        this.frontier = new HashSet<>();
        this.visitedSet = new HashSet<>();
        this.finished = false;
        this.path = null;

        stack.push(start);
        frontier.clear();
        frontier.add(start);
        visitedSet.clear();
    }

    @Override
    public boolean step() {
        if (finished) return true;

        Set<Cell> newFrontier = new HashSet<>();
        if (stack.isEmpty()) {
            finished = true;
            path = null;
            frontier.clear();
            return true;
        }

        Cell current = stack.pop();
        visitedSet.add(current);

        if (current == end) {
            finished = true;
            path = reconstructPath(end);
            frontier.clear();
            return true;
        }

        closedSet[current.getRow()][current.getCol()] = true;

        for (Cell neighbor : getNeighbors(grid, current)) {
            if (neighbor.isWall() || closedSet[neighbor.getRow()][neighbor.getCol()]) continue;

            if (!visitedSet.contains(neighbor)) {
                neighbor.setParent(current);
                stack.push(neighbor);
                newFrontier.add(neighbor);
            }
        }

        frontier = newFrontier;
        return finished;
    }

    @Override
    public List<Cell> getPath() {
        return path;
    }

    @Override
    public Set<Cell> getFrontier() {
        return new HashSet<>(frontier);
    }

    @Override
    public Set<Cell> getVisited() {
        return new HashSet<>(visitedSet);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    private List<Cell> reconstructPath(Cell end) {
        List<Cell> result = new ArrayList<>();
        for (Cell at = end; at != null; at = at.getParent()) {
            result.add(at);
        }
        Collections.reverse(result);
        return result;
    }

    private List<Cell> getNeighbors(Cell[][] grid, Cell cell) {
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};
        List<Cell> neighbors = new ArrayList<>();
        int row = cell.getRow();
        int col = cell.getCol();
        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];
            if (newRow >= 0 && newRow < grid.length && newCol >= 0 && newCol < grid[0].length) {
                neighbors.add(grid[newRow][newCol]);
            }
        }
        return neighbors;
    }
}
