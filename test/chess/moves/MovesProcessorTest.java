package chess.moves;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

import chess.model.BoardModel;
import chess.model.Cell;
import chess.model.ChessPiece;

public class MovesProcessorTest {

	@Test
	public void testLocateKing() {
		BoardModel board = new BoardModel();
		Cell location = new Cell(5, 5);
		ChessPiece king = ChessPiece.WHITE_KING;
		board.placePiece(location, king);
	
		assertEquals(location, MovesProcessor.locateKing(board, Color.WHITE));
	}
	
	public void testLocateKingWhenAbsent() {
		BoardModel board = new BoardModel();
		Cell location = new Cell(5, 5);
		ChessPiece king = ChessPiece.WHITE_KING;
		board.placePiece(location, king);
	
		assertNull(MovesProcessor.locateKing(board, Color.BLACK)); //we placed a white king on the board, black is missing
	}

}
