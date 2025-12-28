package puzzleSolver;

public class PQNodeEntry implements Comparable<PQNodeEntry> {

	private String puzzle;
	private int cost;
	private int g;
	private int h;
	private PQNodeEntry parent;

	public PQNodeEntry(String puzzle, int cost, PQNodeEntry parent, int g, int h) {
//		super();
		this.puzzle = puzzle;
		this.cost = cost;
		this.g = g;
		this.h = h;
		this.parent = parent;
	}

	public String getPuzzle() {
		return puzzle;
	}

	public void setKey(String puzzle) {
		this.puzzle = puzzle;
	}
	
	public int getG() {
		return g;
	}
	
	public int getH() {
		return h;
	}
	
	public int getCost() {
		return cost;
	}
	
	public PQNodeEntry getParentNode() {
		return parent;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public int compareTo(PQNodeEntry o) {
		// TODO Auto-generated method stub
		return Integer.compare(this.cost, o.cost);
	}
	
}
