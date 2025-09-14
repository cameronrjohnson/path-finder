import java.awt.*;
import java.util.*;
import javax.swing.*;

public class MainGUI extends JFrame {
    private final JPanel gridPanel;
    private final int ROWS = 10;
    private final int COLS = 10;
    private Cell[][] cells;
    private JButton[][] cellButtons;
    private Set<Cell> lastAffectedCells = new HashSet<>();
    private String selectedAlgorithm = "A*";
    private PathfindingAlgorithm algo = null;
    private boolean algoInitialized = false;
    private final MazeGenerator mazeGenerator;

    public MainGUI() {
        setTitle("Path Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        Color bgGrey = new Color(128, 128, 128);
        getContentPane().setBackground(bgGrey);

    JPanel topPanel = new JPanel();
    topPanel.setBackground(bgGrey);
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 28));
        startButton.setPreferredSize(new Dimension(220, 60));
        topPanel.add(startButton);

        JButton regenerateButton = new JButton("R");
        regenerateButton.setFont(new Font("Arial", Font.BOLD, 28));
        regenerateButton.setPreferredSize(new Dimension(80, 60));
        topPanel.add(regenerateButton);

    JPanel iteratePanel = new JPanel();
    iteratePanel.setBackground(bgGrey);
        JButton iterateForwardButton = new JButton("Iterate");
        iterateForwardButton.setFont(new Font("Arial", Font.BOLD, 28));
        iterateForwardButton.setPreferredSize(new Dimension(220, 60));
        iteratePanel.add(iterateForwardButton);
        JToggleButton autoIterateToggle = new JToggleButton("Auto");
        autoIterateToggle.setFont(new Font("Arial", Font.PLAIN, 22));
        autoIterateToggle.setPreferredSize(new Dimension(90, 60));
        iteratePanel.add(autoIterateToggle);

    JPanel algoPanel = new JPanel(new GridLayout(2, 3, 10, 5));
    algoPanel.setBackground(bgGrey);
        JToggleButton aStarButton = new JToggleButton("A*");
        JToggleButton dijkstraButton = new JToggleButton("Dijkstra");
        JToggleButton bfsButton = new JToggleButton("BFS");
        JToggleButton dfsButton = new JToggleButton("DFS");
        JToggleButton greedyButton = new JToggleButton("Greedy Best-First");
        JToggleButton bidirectionalButton = new JToggleButton("Bidirectional");

        aStarButton.addActionListener(e -> selectedAlgorithm = "A*");
        bfsButton.addActionListener(e -> selectedAlgorithm = "BFS");
        dijkstraButton.addActionListener(e -> selectedAlgorithm = "Dijkstra");
        dfsButton.addActionListener(e -> selectedAlgorithm = "DFS");
        greedyButton.addActionListener(e -> selectedAlgorithm = "Greedy");
        bidirectionalButton.addActionListener(e -> selectedAlgorithm = "Bidirectional");
        ButtonGroup algoGroup = new ButtonGroup();
        algoGroup.add(aStarButton);
        algoGroup.add(dijkstraButton);
        algoGroup.add(bfsButton);
        algoGroup.add(dfsButton);
        algoGroup.add(greedyButton);
        algoGroup.add(bidirectionalButton);
        algoPanel.add(aStarButton);
        algoPanel.add(dijkstraButton);
        algoPanel.add(bfsButton);
        algoPanel.add(dfsButton);
        algoPanel.add(greedyButton);
        algoPanel.add(bidirectionalButton);
        aStarButton.setSelected(true);

    JPanel controlsPanel = new JPanel();
    controlsPanel.setBackground(bgGrey);
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.add(topPanel);
        controlsPanel.add(iteratePanel);

    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(bgGrey);
        headerPanel.add(controlsPanel, BorderLayout.WEST);
        headerPanel.add(algoPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

    gridPanel = new JPanel(new GridLayout(ROWS, COLS));
    gridPanel.setBackground(bgGrey);
        cells = new Cell[ROWS][COLS];
        cellButtons = new JButton[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cells[row][col] = new Cell(row, col);
                JButton cellBtn = new JButton();
                cellBtn.setPreferredSize(new Dimension(40, 40));
                cellBtn.setEnabled(false);
                cellBtn.setOpaque(true);
                cellBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                cellButtons[row][col] = cellBtn;
                gridPanel.add(cellBtn);
            }
        }

        mazeGenerator = new MazeGenerator(ROWS, COLS);

        Runnable updateCellColors = () -> {
            Set<Cell> pathSet = new HashSet<>();
            if (algo != null && algo.isFinished()) {
                java.util.List<Cell> path = algo.getPath();
                if (path != null) {
                    for (Cell cell : path) {
                        if (!cell.isStart() && !cell.isEnd()) pathSet.add(cell);
                    }
                }
            }
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    JButton btn = cellButtons[row][col];
                    Cell cell = cells[row][col];
                    if (cell.isStart()) {
                        btn.setBackground(Color.GREEN);
                    } else if (cell.isEnd()) {
                        btn.setBackground(Color.RED);
                    } else if (cell.isWall()) {
                        btn.setBackground(Color.DARK_GRAY);
                    } else if (pathSet.contains(cell)) {
                        btn.setBackground(Color.BLUE);
                    } else if (lastAffectedCells.contains(cell)) {
                        btn.setBackground(Color.ORANGE);
                    } else {
                        btn.setBackground(null);
                    }
                }
            }
        };

        mazeGenerator.generateMaze(cells);
        cells[ROWS / 2][1].setStart(true);
        cells[ROWS / 2][COLS - 2].setEnd(true);
        SwingUtilities.invokeLater(updateCellColors);
        add(gridPanel, BorderLayout.CENTER);

        startButton.addActionListener(e -> {
            lastAffectedCells.clear();
            Cell start = null, end = null;
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (cells[r][c].isStart()) start = cells[r][c];
                    if (cells[r][c].isEnd()) end = cells[r][c];
                }
            }
            if (start == null || end == null) return;
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    cells[r][c].setVisited(false);
                    cells[r][c].setParent(null);
                    cells[r][c].setGCost(0);
                    cells[r][c].setHCost(0);
                }
            }

            algo = switch (selectedAlgorithm) {
                case "A*" -> new AStarAlgorithm();
                case "Dijkstra" -> new DijkstraAlgorithm();
                case "DFS" -> new DFSAlgorithm();
                case "Greedy" -> new GreedyBestFirstAlgorithm();
                case "Bidirectional" -> new BidirectionalSearchAlgorithm();
                default -> new BFSAlgorithm();
            };
            algo.initialize(cells, start, end);
            algoInitialized = true;
            lastAffectedCells.clear();
            updateCellColors.run();
        });

        regenerateButton.addActionListener(e -> {
            lastAffectedCells.clear();
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    cells[r][c].setStart(false);
                    cells[r][c].setEnd(false);
                    cells[r][c].setVisited(false);
                    cells[r][c].setParent(null);
                    cells[r][c].setWall(false);
                }
            }
            mazeGenerator.generateMaze(cells);
            cells[ROWS / 2][1].setStart(true);
            cells[ROWS / 2][COLS - 2].setEnd(true);
            updateCellColors.run();
        });

        final javax.swing.Timer[] autoIterateTimer = new javax.swing.Timer[1];

        Runnable iterateStep = () -> {
            if (!algoInitialized || algo == null || algo.isFinished()) return;
            lastAffectedCells.clear();
            algo.step();
            Set<Cell> visited = algo.getVisited();
            Set<Cell> frontier = algo.getFrontier();
            lastAffectedCells.addAll(frontier);
            lastAffectedCells.addAll(visited);
            if (algo.isFinished()) {
                java.util.List<Cell> path = algo.getPath();
                if (path != null) {
                    for (Cell cell : path) {
                        if (!cell.isStart() && !cell.isEnd()) lastAffectedCells.add(cell);
                    }
                    updateCellColors.run();
                } else {
                    updateCellColors.run();
                    JOptionPane.showMessageDialog(this, "No path found.");
                }
                if (autoIterateTimer[0] != null && autoIterateTimer[0].isRunning()) {
                    autoIterateTimer[0].stop();
                    autoIterateToggle.setSelected(false);
                }
            } else {
                updateCellColors.run();
            }
        };

        iterateForwardButton.addActionListener(e -> iterateStep.run());

        autoIterateToggle.addActionListener(e -> {
            if (autoIterateToggle.isSelected()) {
                if (!algoInitialized || algo == null || algo.isFinished()) {
                    autoIterateToggle.setSelected(false);
                    return;
                }
                autoIterateTimer[0] = new javax.swing.Timer(100, evt -> {
                    iterateStep.run();
                    if (algo == null || algo.isFinished()) {
                        autoIterateTimer[0].stop();
                        autoIterateToggle.setSelected(false);
                    }
                });
                autoIterateTimer[0].start();
            } else {
                if (autoIterateTimer[0] != null && autoIterateTimer[0].isRunning()) {
                    autoIterateTimer[0].stop();
                }
            }
        });

        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
