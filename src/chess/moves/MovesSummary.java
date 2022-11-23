package chess.moves;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import chess.model.BoardModel;
import chess.model.PieceType;

public class MovesSummary {
	private Color player;
	private BoardModel board;
	private Collection<Move> moves = new ArrayList<>();
	private Map<PieceType, Collection<Move>> byPieceType = new HashMap<>();
	private Map<PieceType, Collection<Move>> byCapturedPiece = new HashMap<>();
	//other ideas: byTargetCell?, byPromotedPieces?
	
	public MovesSummary(Color player, BoardModel board) {
		super();
		if (!Color.WHITE.equals(player) && !Color.BLACK.equals(player)) {
			throw new IllegalArgumentException("Color must be Black or White");
		}
		
		if (board == null) {
			throw new IllegalArgumentException("Board mustn't be null");
		}
		
		this.player = player;
		this.board = board;
	}
	
	public MovesSummary addMoves(Collection<Move> movesToAdd) {
		for (Move move : movesToAdd) {
			moves.add(move);
			
			PieceType pieceType = move.getPiece().getPieceType();
			Collection<Move> movesForThisPieceType = byPieceType.get(pieceType);
			if (movesForThisPieceType == null) {
				movesForThisPieceType = new ArrayList<>();
				byPieceType.put(pieceType, movesForThisPieceType);
			}
			movesForThisPieceType.add(move);
			
			if (move.isCapturePiece()) {
				PieceType capturedPieceType = board.getPiece(move.getTarget()).getPieceType();
				Collection<Move> movesForThisCapturedPieceType = byCapturedPiece.get(capturedPieceType);
				if (movesForThisCapturedPieceType == null) {
					movesForThisCapturedPieceType = new ArrayList<>();
					byCapturedPiece.put(capturedPieceType, movesForThisCapturedPieceType);
				}
				movesForThisCapturedPieceType.add(move);
			}
		}
		
		return this;
	}
	
	public Color getColor() {
		return player;
	}
	
	public Collection<Move> getAllMoves() {
		return moves;
	}
	
	public int getNumberOfMoves() {
		return moves.size();
	}
	
	public Collection<Move> getMovesForPieceType(PieceType pieceType) {
		return byPieceType.get(pieceType);
	}
	
	public Collection<Move> getMovesForCapturedPieceType(PieceType pieceType) {
		return byCapturedPiece.get(pieceType);
	}
}
