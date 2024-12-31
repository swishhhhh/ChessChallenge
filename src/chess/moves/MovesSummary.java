package chess.moves;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import chess.model.BoardModel;
import chess.model.PieceType;

public class MovesSummary {
    private final BoardModel board;
	private final Collection<Move> moves = new ArrayList<>();
	private final Map<PieceType, Collection<Move>> byPieceType = new HashMap<>();
	private final Map<PieceType, Collection<Move>> byCapturedPiece = new HashMap<>();
	//other ideas: byTargetCell?, byPromotedPieces?
	
	public MovesSummary(Color player, BoardModel board) {
		super();
		if (!Color.WHITE.equals(player) && !Color.BLACK.equals(player)) {
			throw new IllegalArgumentException("Color must be Black or White");
		}
		
		if (board == null) {
			throw new IllegalArgumentException("Board mustn't be null");
		}

        this.board = board;
	}
	
	public MovesSummary addMoves(Collection<Move> movesToAdd) {
		for (Move move : movesToAdd) {
			moves.add(move);
			
			PieceType pieceType = move.getPiece().getPieceType();
            Collection<Move> movesForThisPieceType = byPieceType.computeIfAbsent(pieceType, k -> new ArrayList<>());
            movesForThisPieceType.add(move);
			
			if (move.isCapturePiece()) {
				PieceType capturedPieceType = board.getPiece(move.getTarget()).getPieceType();
                Collection<Move> movesForThisCapturedPieceType = byCapturedPiece.computeIfAbsent(capturedPieceType, k -> new ArrayList<>());
                movesForThisCapturedPieceType.add(move);
			}
		}
		
		return this;
	}
	
	public Collection<Move> getAllMoves() {
		return moves;
	}
	
	public int getNumberOfMoves() {
		return moves.size();
	}

	public Collection<Move> getMovesForCapturedPieceType(PieceType pieceType) {
		return byCapturedPiece.get(pieceType);
	}
}
