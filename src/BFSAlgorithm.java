import java.util.*;


public class BFSAlgorithm implements PathfindingAlgorithm {
    private Cell[][] grid;
    private Cell end;
    private Queue<Cell> queue;
    private boolean[][] visited;
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
        this.queue = new LinkedList<>();
        this.visited = new boolean[rows][cols];
        this.frontier = new HashSet<>();
        this.visitedSet = new HashSet<>();
    this.finished = false;
    this.path = null;
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;
        start.setParent(null);
        frontier.add(start);
        visitedSet.clear();
    }

    @Override
    public boolean step() {
        if (finished) return true;
        Set<Cell> newFrontier = new HashSet<>();
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            Cell current = queue.poll();
            if (current == null) continue;
            visitedSet.add(current);
            if (current == end) {
                finished = true;
                path = reconstructPath(end);
                frontier.clear();
                return true;
            }
            for (Cell neighbor : getNeighbors(grid, current)) {
                int r = neighbor.getRow(), c = neighbor.getCol();
                if (!visited[r][c] && !neighbor.isWall()) {
                    visited[r][c] = true;
                    neighbor.setParent(current);
                    queue.add(neighbor);
                    newFrontier.add(neighbor);
                }
            }
        }
        frontier = newFrontier;
        if (queue.isEmpty() && !finished) {
            finished = true;
            path = null;
        }
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
