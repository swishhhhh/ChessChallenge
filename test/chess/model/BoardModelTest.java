package chess.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class BoardModelTest {

	@Test
	public void testGetPlaceAndRemovePiece() {
		BoardModel board = new BoardModel();
		Cell cell = new Cell(0, 0);
		
		//before placing any pieces should be NO_PIECE in this cell
		assertEquals(ChessPiece.NO_PIECE, board.getPiece(cell));
		assertFalse(board.isCellOccupied(cell));
		
		//place a piece and confirm there
		board.placePiece((byte) cell.getRow(), (byte) cell.getCol(), ChessPiece.BLACK_BISHOP);
		assertEquals(ChessPiece.BLACK_BISHOP, board.getPiece(cell));
		assertTrue(board.isCellOccupied(cell));
	
		//replace piece with new one and confirm new one is there
		board.placePiece((byte) cell.getRow(), (byte) cell.getCol(), ChessPiece.WHITE_QUEEN);
		assertEquals(ChessPiece.WHITE_QUEEN, board.getPiece(cell));
		assertTrue(board.isCellOccupied(cell));
		
		//remove piece and confirm gone
		assertEquals(ChessPiece.WHITE_QUEEN, board.removePiece((byte) cell.getRow(), (byte) cell.getCol(), true));
		assertEquals(ChessPiece.NO_PIECE, board.getPiece(cell));
		assertFalse(board.isCellOccupied(cell));
		
		//remove piece again from empty square but with failIfSquareIsAlreadyEmpty = false
		assertEquals(ChessPiece.NO_PIECE, board.removePiece(cell, false));
		
		//this time remove with failIfSquareIsAlreadyEmpty = true, should throw an IllegalStateException
		try {
			board.removePiece(cell, true);
			fail("Expecting an IllegalStateException");
		} catch (IllegalStateException expected) {}
	}

	@Test
	public void testGetClone() {
		BoardModel original = new BoardModel();
		Cell cell = new Cell(1, 1);
		
		original.placePiece((byte) cell.getRow(), (byte) cell.getCol(), ChessPiece.BLACK_BISHOP);
		assertTrue(original.isCellOccupied(cell));
				
		//clone board and confirm corresponding cell is still occupied
		BoardModel clone = original.getClone();
		assertTrue(clone.isCellOccupied(cell));
		assertEquals(original, clone);
		
		//remove piece from first board, confirm that it hasn't been removed from clone
		original.removePiece((byte) cell.getRow(), (byte) cell.getCol(), true);
		assertFalse(original.isCellOccupied(cell));
		assertTrue(clone.isCellOccupied(cell));
		assertNotEquals(original, clone);
	}
}
