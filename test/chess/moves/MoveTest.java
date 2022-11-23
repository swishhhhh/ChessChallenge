package chess.moves;

import static org.junit.Assert.*;

import org.junit.Test;

import chess.model.BoardModel;
import chess.model.Cell;
import chess.model.ChessPiece;

public class MoveTest {

	@Test
	public void testBasicMove() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(0, 0);
		ChessPiece king = ChessPiece.BLACK_KING;
		board.placePiece(source, king);
	
		Cell target = new Cell(1, 1);
		Move move = new Move(board, king, source, target);
		assertEquals(king, move.getPiece());
		assertEquals(source, move.getSource());
		assertEquals(target, move.getTarget());
		assertNull(move.getPromotedPiece());
		assertFalse(move.isCapturePiece());
		assertFalse(move.isCastling());
		assertFalse(move.isPromotePawn());
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testPieceNotOnSourceCell() {
		BoardModel board = new BoardModel();
		Cell someCell = new Cell(0, 0);
		board.placePiece(someCell, ChessPiece.BLACK_KING);
	
		Cell source = new Cell(1, 1);
		Cell target = new Cell(1, 0);
		new Move(board, ChessPiece.BLACK_KING, source, target);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testSourceAndTargetSame() {
		BoardModel board = new BoardModel();
		Cell cell = new Cell(0, 0);
		board.placePiece(cell, ChessPiece.BLACK_KING);
	
		new Move(board, ChessPiece.BLACK_KING, cell, cell);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testTargetCellAlreadyOccupiedByPeer() {
		BoardModel board = new BoardModel();
		Cell target = new Cell(0, 0);
		board.placePiece(target, ChessPiece.BLACK_KING);
	
		Cell source = new Cell(1, 1);
		new Move(board, ChessPiece.BLACK_QUEEN, source, target);
	}
	
	@Test
	public void testCaptureMove() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(0, 0);
		ChessPiece blackRook = ChessPiece.BLACK_ROOK;
		board.placePiece(source, blackRook);
	
		Cell target = new Cell(0, 7);
		ChessPiece whiteBishop = ChessPiece.WHITE_BISHOP;
		board.placePiece(target, whiteBishop);
		
		Move move = new Move(board, blackRook, source, target);
		assertEquals(blackRook, move.getPiece());
		assertEquals(source, move.getSource());
		assertEquals(target, move.getTarget());
		assertNull(move.getPromotedPiece());
		assertTrue(move.isCapturePiece());
		assertFalse(move.isCastling());
		assertFalse(move.isPromotePawn());
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testPawnPromtionWithoutPromotedPiece() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(1, 1);
		ChessPiece pawn = ChessPiece.WHITE_PAWN;
		board.placePiece(source, pawn);
		
		Cell target = new Cell(0, 1);
		new Move(board, pawn, source, target);
	}
	
	@Test
	public void testPawnPromotionWhite() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(1, 1);
		ChessPiece pawn = ChessPiece.WHITE_PAWN;
		board.placePiece(source, pawn);
	
		Cell target = new Cell(0, 1);
		Move move = new Move(board, pawn, source, target, ChessPiece.WHITE_QUEEN);
		assertEquals(pawn, move.getPiece());
		assertTrue(move.isPromotePawn());
		assertEquals(ChessPiece.WHITE_QUEEN, move.getPromotedPiece());
		assertFalse(move.isCapturePiece());
		assertFalse(move.isCastling());
	}
	
	@Test
	public void testPawnPromotionBlack() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(6, 1);
		ChessPiece pawn = ChessPiece.BLACK_PAWN;
		board.placePiece(source, pawn);
	
		Cell target = new Cell(7, 1);
		Move move = new Move(board, pawn, source, target, ChessPiece.BLACK_QUEEN);
		assertEquals(pawn, move.getPiece());
		assertTrue(move.isPromotePawn());
		assertEquals(ChessPiece.BLACK_QUEEN, move.getPromotedPiece());
		assertFalse(move.isCapturePiece());
		assertFalse(move.isCastling());
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testPawnPromtionInvalidSourceCellWhite() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(2, 1);
		ChessPiece pawn = ChessPiece.WHITE_PAWN;
		board.placePiece(source, pawn);
		
		Cell target = new Cell(1, 1);
		new Move(board, pawn, source, target, ChessPiece.WHITE_QUEEN);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testPawnPromtionInvalidSourceCellBlack() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(5, 1);
		ChessPiece pawn = ChessPiece.BLACK_PAWN;
		board.placePiece(source, pawn);
		
		Cell target = new Cell(6, 1);
		new Move(board, pawn, source, target, ChessPiece.BLACK_QUEEN);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testPawnPromtionWrongDirection() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(1, 1);
		ChessPiece pawn = ChessPiece.BLACK_PAWN;
		board.placePiece(source, pawn);
		
		Cell target = new Cell(0, 1);
		new Move(board, pawn, source, target, ChessPiece.BLACK_QUEEN);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testPawnPromtionInvalidPieceWrongColor() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(1, 1);
		ChessPiece pawn = ChessPiece.WHITE_PAWN;
		board.placePiece(source, pawn);
		
		Cell target = new Cell(0, 1);
		new Move(board, pawn, source, target, ChessPiece.BLACK_QUEEN);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testPawnPromtionInvalidPieceKing() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(1, 1);
		ChessPiece pawn = ChessPiece.WHITE_PAWN;
		board.placePiece(source, pawn);
		
		Cell target = new Cell(0, 1);
		new Move(board, pawn, source, target, ChessPiece.WHITE_KING);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testPawnPromtionInvalidPiecePawn() {
		BoardModel board = new BoardModel();
		Cell source = new Cell(1, 1);
		ChessPiece pawn = ChessPiece.WHITE_PAWN;
		board.placePiece(source, pawn);
		
		Cell target = new Cell(0, 1);
		new Move(board, pawn, source, target, ChessPiece.WHITE_PAWN);
	}
}
