package chess.model;

import static chess.Utils.*;
import static java.lang.String.format;

public class Cell {
	private int row;
	private int col;
	private String label;
	
	public Cell(int rowNum, int colNum) {
		super();
		if (isOutOfBounds(rowNum, colNum)) {
			throw new IllegalArgumentException(format("Invalid row (%s) or col (%s)", rowNum, colNum));
		}
		
		this.row = rowNum;
		this.col = colNum;
		this.label = Character.valueOf((char) ('A' + colNum)).toString() + (8 - rowNum);
	}
	
	public int getRow() {
		return row;
	}
	public int getCol() {
		return col;
	}
	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return String.format("Cell [%s (%s, %s)]", label, row, col);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
}
