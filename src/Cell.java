public class Cell {
	private final int row;
	private final int col;
	private boolean isStart;
	private boolean isEnd;
	private boolean isWall;
	private boolean isVisited;
	private Cell parent;
	private int gCost;
	private int hCost;

	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		this.isStart = false;
		this.isEnd = false;
		this.isWall = false;
		this.isVisited = false;
		this.parent = null;
		this.gCost = 0;
		this.hCost = 0;
	}

	public int getRow() { return row; }
	public int getCol() { return col; }
	public boolean isStart() { return isStart; }
	public void setStart(boolean isStart) { this.isStart = isStart; }
	public boolean isEnd() { return isEnd; }
	public void setEnd(boolean isEnd) { this.isEnd = isEnd; }
	public boolean isWall() { return isWall; }
	public void setWall(boolean isWall) { this.isWall = isWall; }
	public boolean isVisited() { return isVisited; }
	public void setVisited(boolean isVisited) { this.isVisited = isVisited; }
	public Cell getParent() { return parent; }
	public void setParent(Cell parent) { this.parent = parent; }
	public int getGCost() { return gCost; }
	public void setGCost(int gCost) { this.gCost = gCost; }
	public int getHCost() { return hCost; }
	public void setHCost(int hCost) { this.hCost = hCost; }
	public int getFCost() { return gCost + hCost; }
}
