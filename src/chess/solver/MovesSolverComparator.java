package chess.solver;

import java.util.Comparator;

import chess.model.PieceType;
import chess.moves.Move;

public class MovesSolverComparator implements Comparator<Move> {
	private static Integer getPieceMovePriority(PieceType pieceType) {
		switch (pieceType) {
			case QUEEN:
				return 1;
			case ROOK:
				return 2;
			case KNIGHT:
				return 3;
			case BISHOP:
				return 4;
			case PAWN:
				return 5;
			case KING:
				return 6;
			default:
				return 10;
		}
	}
			
	@Override
	public int compare(Move move1, Move move2) {
		return getPieceMovePriority(move1.getPiece().getPieceType())
				.compareTo(getPieceMovePriority(move2.getPiece().getPieceType()));
	}
}
