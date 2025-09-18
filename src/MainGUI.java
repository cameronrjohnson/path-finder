import java.awt.*;
import java.util.*;
import javax.swing.*;

public class MainGUI extends JFrame {
    private static final int ROWS = 10;
    private static final int COLS = 10;

    private final JPanel gridPanel;
    private final MazeGenerator mazeGenerator;

    private Cell[][] cells;
    private JButton[][] cellButtons;
    private final Set<Cell> lastAffectedCells = new HashSet<>();

    private String selectedAlgorithm = "A*";
    private PathfindingAlgorithm algo = null;
    private boolean algoInitialized = false;

    private Runnable updateCellColors;
    private javax.swing.Timer autoIterateTimer;

    public MainGUI() {
        setTitle("Path Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(128, 128, 128));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        gridPanel = createGridPanel();
        add(gridPanel, BorderLayout.CENTER);

        mazeGenerator = new MazeGenerator(ROWS, COLS);
        generateInitialMaze();

        setupUpdateCellColors();
        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel controlsPanel = createControlsPanel();
        JPanel algoPanel = createAlgorithmPanel();

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(128, 128, 128));
        headerPanel.add(controlsPanel, BorderLayout.WEST);
        headerPanel.add(algoPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createControlsPanel() {
        JPanel bgPanel = new JPanel();
        bgPanel.setBackground(new Color(128, 128, 128));
        bgPanel.setLayout(new BoxLayout(bgPanel, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(128, 128, 128));
        JButton startButton = createButton("Start", 220, 60, e -> onStart());
        JButton regenerateButton = createButton("R", 80, 60, e -> onRegenerate());
        topPanel.add(startButton);
        topPanel.add(regenerateButton);

        JPanel iteratePanel = new JPanel();
        iteratePanel.setBackground(new Color(128, 128, 128));
        JButton iterateButton = createButton("Iterate", 220, 60, e -> iterateStep());
        JToggleButton autoToggle = createToggle("Auto", 90, 60, e -> toggleAutoIterate((JToggleButton) e.getSource()));
        iteratePanel.add(iterateButton);
        iteratePanel.add(autoToggle);

        bgPanel.add(topPanel);
        bgPanel.add(iteratePanel);

        return bgPanel;
    }

    private JPanel createAlgorithmPanel() {
        JPanel algoPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        algoPanel.setBackground(new Color(128, 128, 128));

        ButtonGroup group = new ButtonGroup();
        algoPanel.add(createAlgoToggle("A*", "A*", group, true));
        algoPanel.add(createAlgoToggle("Dijkstra", "Dijkstra", group, false));
        algoPanel.add(createAlgoToggle("BFS", "BFS", group, false));
        algoPanel.add(createAlgoToggle("DFS", "DFS", group, false));
        algoPanel.add(createAlgoToggle("Greedy Best-First", "Greedy", group, false));
        algoPanel.add(createAlgoToggle("Bidirectional", "Bidirectional", group, false));

        return algoPanel;
    }

    private JPanel createGridPanel() {
        JPanel panel = new JPanel(new GridLayout(ROWS, COLS));
        panel.setBackground(new Color(128, 128, 128));

        cells = new Cell[ROWS][COLS];
        cellButtons = new JButton[ROWS][COLS];

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c] = new Cell(r, c);
                JButton cellBtn = new JButton();
                cellBtn.setPreferredSize(new Dimension(40, 40));
                cellBtn.setEnabled(false);
                cellBtn.setOpaque(true);
                cellBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                cellButtons[r][c] = cellBtn;
                panel.add(cellBtn);
            }
        }
        return panel;
    }

    private JButton createButton(String text, int w, int h, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 28));
        button.setPreferredSize(new Dimension(w, h));
        button.addActionListener(listener);
        return button;
    }

    private JToggleButton createToggle(String text, int w, int h, java.awt.event.ActionListener listener) {
        JToggleButton toggle = new JToggleButton(text);
        toggle.setFont(new Font("Arial", Font.PLAIN, 22));
        toggle.setPreferredSize(new Dimension(w, h));
        toggle.addActionListener(listener);
        return toggle;
    }

    private JToggleButton createAlgoToggle(String label, String algoName, ButtonGroup group, boolean selected) {
        JToggleButton button = new JToggleButton(label);
        button.addActionListener(e -> selectedAlgorithm = algoName);
        group.add(button);
        button.setSelected(selected);
        return button;
    }

    private void generateInitialMaze() {
        mazeGenerator.generateMaze(cells);
        cells[ROWS / 2][1].setStart(true);
        cells[ROWS / 2][COLS - 2].setEnd(true);
    }

    private void setupUpdateCellColors() {
        updateCellColors = () -> {
            Set<Cell> pathSet = new HashSet<>();
            if (algo != null && algo.isFinished()) {
                java.util.List<Cell> path = algo.getPath();
                if (path != null) {
                    for (Cell cell : path) {
                        if (!cell.isStart() && !cell.isEnd()) pathSet.add(cell);
                    }
                }
            }
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    JButton btn = cellButtons[r][c];
                    Cell cell = cells[r][c];
                    if (cell.isStart()) btn.setBackground(Color.GREEN);
                    else if (cell.isEnd()) btn.setBackground(Color.RED);
                    else if (cell.isWall()) btn.setBackground(Color.DARK_GRAY);
                    else if (pathSet.contains(cell)) btn.setBackground(Color.BLUE);
                    else if (lastAffectedCells.contains(cell)) btn.setBackground(Color.ORANGE);
                    else btn.setBackground(null);
                }
            }
        };
        SwingUtilities.invokeLater(updateCellColors);
    }

    private void onStart() {
        lastAffectedCells.clear();
        Cell start = null, end = null;

        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.isStart()) start = cell;
                if (cell.isEnd()) end = cell;
                cell.setVisited(false);
                cell.setParent(null);
                cell.setGCost(0);
                cell.setHCost(0);
            }
        }
        if (start == null || end == null) return;

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
        updateCellColors.run();
    }

    private void onRegenerate() {
        lastAffectedCells.clear();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.setStart(false);
                cell.setEnd(false);
                cell.setVisited(false);
                cell.setParent(null);
                cell.setWall(false);
            }
        }
        generateInitialMaze();
        updateCellColors.run();
    }

    private void iterateStep() {
        if (!algoInitialized || algo == null || algo.isFinished()) return;
        lastAffectedCells.clear();

        algo.step();
        lastAffectedCells.addAll(algo.getFrontier());
        lastAffectedCells.addAll(algo.getVisited());

        if (algo.isFinished()) {
            if (algo.getPath() != null) {
                for (Cell cell : algo.getPath()) {
                    if (!cell.isStart() && !cell.isEnd()) lastAffectedCells.add(cell);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No path found.");
            }
            stopAutoIterate();
        }
        updateCellColors.run();
    }

    private void toggleAutoIterate(JToggleButton toggle) {
        if (toggle.isSelected()) {
            if (!algoInitialized || algo == null || algo.isFinished()) {
                toggle.setSelected(false);
                return;
            }
            autoIterateTimer = new javax.swing.Timer(100, e -> iterateStep());
            autoIterateTimer.start();
        } else {
            stopAutoIterate();
        }
    }

    private void stopAutoIterate() {
        if (autoIterateTimer != null && autoIterateTimer.isRunning()) {
            autoIterateTimer.stop();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
