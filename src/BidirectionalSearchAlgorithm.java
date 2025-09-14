import java.util.*;

public class BidirectionalSearchAlgorithm implements PathfindingAlgorithm {
    private Cell[][] grid;
    private Cell start;
    private Cell end;

    private Queue<Cell> frontierStart;
    private Queue<Cell> frontierEnd;

    private Map<Cell, Cell> parentStart;
    private Map<Cell, Cell> parentEnd;

    private Set<Cell> visitedStart;
    private Set<Cell> visitedEnd;

    private boolean finished;
    private List<Cell> path;

    @Override
    public void initialize(Cell[][] grid, Cell start, Cell end) {
        this.grid = grid;
        this.start = start;
        this.end = end;

        this.frontierStart = new ArrayDeque<>();
        this.frontierEnd = new ArrayDeque<>();

        this.parentStart = new HashMap<>();
        this.parentEnd = new HashMap<>();

        this.visitedStart = new HashSet<>();
        this.visitedEnd = new HashSet<>();

        this.finished = false;
        this.path = null;

        frontierStart.add(start);
        frontierEnd.add(end);

        visitedStart.add(start);
        visitedEnd.add(end);

        parentStart.put(start, null);
        parentEnd.put(end, null);
    }

    @Override
    public boolean step() {
        if (finished) return true;

        if (!frontierStart.isEmpty()) {
            if (expandFrontier(frontierStart, visitedStart, visitedEnd, parentStart, parentEnd, true)) {
                return true;
            }
        }

        if (!frontierEnd.isEmpty()) {
            if (expandFrontier(frontierEnd, visitedEnd, visitedStart, parentEnd, parentStart, false)) {
                return true;
            }
        }

        if (frontierStart.isEmpty() && frontierEnd.isEmpty()) {
            finished = true;
            path = null;
            return true;
        }

        return false;
    }

    private boolean expandFrontier(Queue<Cell> frontier, Set<Cell> thisVisited, Set<Cell> otherVisited,
                                   Map<Cell, Cell> thisParent, Map<Cell, Cell> otherParent, boolean fromStart) {
        Cell current = frontier.poll();

        for (Cell neighbor : getNeighbors(grid, current)) {
            if (neighbor.isWall() || thisVisited.contains(neighbor)) continue;

            thisVisited.add(neighbor);
            thisParent.put(neighbor, current);
            frontier.add(neighbor);

            // Meeting point
            if (otherVisited.contains(neighbor)) {
                finished = true;
                path = buildPath(neighbor, thisParent, otherParent);
                return true;
            }
        }
        return false;
    }

    private List<Cell> buildPath(Cell meetingPoint, Map<Cell, Cell> parentStart, Map<Cell, Cell> parentEnd) {
        List<Cell> pathFromStart = new ArrayList<>();
        for (Cell at = meetingPoint; at != null; at = parentStart.get(at)) {
            pathFromStart.add(at);
        }
        Collections.reverse(pathFromStart);

        List<Cell> pathFromEnd = new ArrayList<>();
        for (Cell at = parentEnd.get(meetingPoint); at != null; at = parentEnd.get(at)) {
            pathFromEnd.add(at);
        }

        pathFromStart.addAll(pathFromEnd);
        return pathFromStart;
    }

    @Override
    public List<Cell> getPath() {
        return path;
    }

    @Override
    public Set<Cell> getFrontier() {
        Set<Cell> combined = new HashSet<>();
        combined.addAll(frontierStart);
        combined.addAll(frontierEnd);
        return combined;
    }

    @Override
    public Set<Cell> getVisited() {
        Set<Cell> combined = new HashSet<>();
        combined.addAll(visitedStart);
        combined.addAll(visitedEnd);
        return combined;
    }

    @Override
    public boolean isFinished() {
        return finished;
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
