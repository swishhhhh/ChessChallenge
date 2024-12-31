package chess.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import chess.model.BoardModel;
import chess.model.Cell;
import chess.moves.Move;
import chess.moves.MovesProcessor;

public class GameController {
	private BoardModel board;
	private final ChessGUI gui;
	private final List<Move> moves = new ArrayList<>();

	public GameController(BoardModel board, ChessGUI gui) {
		super();
		this.board = board;
		this.gui = gui;
	}
	
	public BoardModel getBoard() {
		return board;
	}
	public void setBoard(BoardModel board) {
		this.board = board;
	}
	
	public boolean isValidMove(Move move) {
		return gui.isInSetupMode() || MovesProcessor.isValidMove(board, move, getLastMove());
	}
	
	public void applyMove(Move move) {
		board = MovesProcessor.applyMove(board, move);
		moves.add(move);
		gui.updateMessage(String.format("%s from %s to %s", move.getPiece(), move.getSource().getLabel(), move.getTarget().getLabel()), false);
	}
	
	public void undoLastMove() {
		if (moves.isEmpty()) {
			throw new IllegalArgumentException("No moves to undo...");
		}
		Move move = moves.get(moves.size() - 1);
		
		board = MovesProcessor.undoMove(board, move);
		moves.remove(moves.size() - 1);
		gui.updateMessage(String.format("Undid %s from %s to %s", move.getPiece(), move.getSource().getLabel(), move.getTarget().getLabel()), false);
	}

	public List<Move> getMoves() {
		return moves;
	}
	
	public Move getLastMove() {
		return moves.isEmpty() ? null : moves.get(moves.size() - 1);
	}

	/**
	 * scans board and highlights cells in check, check-mate, and stale-mate
	 */
	public void highlightCheckCmSm() {
		BoardModel board = getBoard();
		Move lastMove = getLastMove();
		
		for (Color color : new Color[]{Color.WHITE, Color.BLACK}) {
			Cell kingsCell = MovesProcessor.locateKing(board, color);
			if (kingsCell == null) { //can happen if in set-up mode and king not on board yet
				continue;
			}
			
			ChessCellButton kingsCellButton = gui.getCellButton(kingsCell.getRow(), kingsCell.getCol());
			if (MovesProcessor.isCheckMateOnColor(board, color, lastMove)) {
				kingsCellButton.highlightAsInCheckMate();
				gui.updateMessage("CheckMate!", true, true);
			} else if (MovesProcessor.isStaleMateOnColor(board, color, lastMove)) {
				kingsCellButton.highlightAsInStaleMate();
				gui.updateMessage("StaleMate", true, true);
			} else if (MovesProcessor.isCheckOnColor(board, color, lastMove)) {
				kingsCellButton.highlightAsInCheck();
				gui.updateMessage("Check...", true, true);
			} 
		}
	}
	
	public boolean canEnterGameMode() {
		for (Color color : new Color[]{Color.WHITE, Color.BLACK}) {
			Collection<Cell> kingsCells = MovesProcessor.locateKings(board, color);
			if (kingsCells.isEmpty()) {
				gui.updateMessage("Missing king...", true);
				return false;
			} else if (kingsCells.size() >= 2) {
				gui.updateMessage("Only 1 king please...", true);
				return false;
			}
		}
		
		highlightCheckCmSm(); 
		Move lastMove = getLastMove();
        return !MovesProcessor.isCheckMateOnColor(board, Color.WHITE, lastMove)
				&& !MovesProcessor.isCheckMateOnColor(board, Color.BLACK, lastMove);
    }

}
