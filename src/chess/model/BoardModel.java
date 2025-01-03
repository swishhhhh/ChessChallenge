package chess.model;

import java.util.Arrays;

public class BoardModel {
	private final byte[][] grid = new byte[8][8];
	
	public void placePiece(byte row, byte col, ChessPiece piece) {
		grid[row][col] = piece.getId();
	}
	
	public void placePiece(Cell cell, ChessPiece piece) {
		placePiece((byte) cell.getRow(), (byte) cell.getCol(), piece);
	}
	
	public ChessPiece removePiece(byte row, byte col, boolean failIfSquareIsAlreadyEmpty) {
		ChessPiece piece = getPiece(row, col);
		if (failIfSquareIsAlreadyEmpty && piece == ChessPiece.NO_PIECE) {
			throw new IllegalStateException(String.format("Square[%s][%s] is already empty", row, col));
		}
		
		grid[row][col] = ChessPiece.NO_PIECE.getId();
		return piece;
	}
	
	public ChessPiece removePiece(Cell cell, boolean failIfSquareIsAlreadyEmpty) {
		return removePiece((byte) cell.getRow(), (byte) cell.getCol(), failIfSquareIsAlreadyEmpty);
	}
	
	public ChessPiece getPiece(byte row, byte col) {
		return ChessPiece.fromId(grid[row][col]);
	}
	
	public ChessPiece getPiece(Cell cell) {
		return getPiece((byte) cell.getRow(), (byte) cell.getCol());
	}
	
	public BoardModel getClone() {
		BoardModel clone = new BoardModel();
		for (int row = 0; row < grid.length; row++) {
            System.arraycopy(this.grid[row], 0, clone.grid[row], 0, grid[row].length);
		}
		return clone;
	}
	
	public boolean isCellVacant(Cell cell) {
		return grid[cell.getRow()][cell.getCol()] == ChessPiece.NO_PIECE.getId();
	}
	
	public boolean isCellOccupied(Cell cell) {
		return !isCellVacant(cell);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(grid);
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
		BoardModel other = (BoardModel) obj;
        return Arrays.deepEquals(grid, other.grid);
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
        for (byte[] bytes : grid) {
            for (byte aByte : bytes) {
                sb.append(String.format("%-12s", ChessPiece.fromId(aByte))).append("|");
            }
            sb.append("\r\n");
            sb.append("\r\n"); //empty row between each row
        }
		return sb.toString();
	}
}
