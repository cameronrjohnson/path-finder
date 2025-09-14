import java.util.*;

public class AStarAlgorithm implements PathfindingAlgorithm {
    private Cell[][] grid;
    private Cell end;
    private PriorityQueue<Cell> openSet;
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
        this.openSet = new PriorityQueue<>(Comparator.comparingInt(Cell::getFCost));
        this.closedSet = new boolean[rows][cols];
        this.frontier = new HashSet<>();
        this.visitedSet = new HashSet<>();
    this.finished = false;
    this.path = null;
        start.setGCost(0);
        start.setHCost(heuristic(start, end));
        openSet.add(start);
        frontier.clear();
        frontier.add(start);
        visitedSet.clear();
    }

    @Override
    public boolean step() {
        if (finished) return true;
        Set<Cell> newFrontier = new HashSet<>();
        if (openSet.isEmpty()) {
            finished = true;
            path = null;
            frontier.clear();
            return true;
        }
        Cell current = openSet.poll();
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
            int tentativeG = current.getGCost() + 1;
            boolean inOpen = openSet.contains(neighbor);
            if (!inOpen || tentativeG < neighbor.getGCost()) {
                neighbor.setParent(current);
                neighbor.setGCost(tentativeG);
                neighbor.setHCost(heuristic(neighbor, end));
                if (!inOpen) {
                    openSet.add(neighbor);
                    newFrontier.add(neighbor);
                }
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

    private int heuristic(Cell a, Cell b) {
        // Manhattan distance
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
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
