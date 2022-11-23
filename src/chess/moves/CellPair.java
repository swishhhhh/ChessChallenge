package chess.moves;

import chess.model.Cell;

public class CellPair {
	private Cell fromCell;
	private Cell toCell;
	
	public CellPair(Cell fromCell, Cell toCell) {
		super();
		this.fromCell = fromCell;
		this.toCell = toCell;
	}

	public Cell getFromCell() {
		return fromCell;
	}

	public Cell getToCell() {
		return toCell;
	}
}
