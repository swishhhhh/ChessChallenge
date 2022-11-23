package chess.moves;

public enum Direction {
	N(-1, 0), 
	NE(-1, 1), 
	E(0, 1), 
	SE(1, 1), 
	S(1, 0), 
	SW(1, -1), 
	W(0, -1), 
	NW(-1, -1);
	
	private int rowIncrement;
	private int colIncrement;
	
	private Direction(int rowIncrement, int colIncrement) {
		this.rowIncrement = rowIncrement;
		this.colIncrement = colIncrement;
	}

	public int getRowIncrement() {
		return rowIncrement;
	}

	public int getColIncrement() {
		return colIncrement;
	}
}
