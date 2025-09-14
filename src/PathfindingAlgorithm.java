import java.util.List;
import java.util.Set;

public interface PathfindingAlgorithm {
    void initialize(Cell[][] grid, Cell start, Cell end);
    boolean step();
    List<Cell> getPath();
    Set<Cell> getFrontier();
    Set<Cell> getVisited();
    boolean isFinished();
}
