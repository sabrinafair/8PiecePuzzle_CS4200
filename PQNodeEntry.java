package puzzleSolver;

public class PQNodeEntry implements Comparable<PQNodeEntry> {

	private String puzzle;
	private int cost;
	private int g;
	private int h1;
	private int h2;
	private PQNodeEntry parent;

	public PQNodeEntry(String puzzle, int cost, PQNodeEntry parent, int g, int h1, int h2) {
//		super();
		this.puzzle = puzzle;
		this.cost = cost;
		this.g = g;
		this.h1 = h1;
		this.h2 = h2;
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
	
	public int getH1() {
		return h1;
	}
	
	public int getH2() {
		return h2;
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
