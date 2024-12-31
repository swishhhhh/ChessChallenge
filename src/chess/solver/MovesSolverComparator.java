package chess.solver;

import java.util.Comparator;

import chess.model.PieceType;
import chess.moves.Move;

public class MovesSolverComparator implements Comparator<Move> {
	private static Integer getPieceMovePriority(PieceType pieceType) {
        return switch (pieceType) {
            case QUEEN -> 1;
            case ROOK -> 2;
            case KNIGHT -> 3;
            case BISHOP -> 4;
            case PAWN -> 5;
            case KING -> 6;
            default -> 10;
        };
	}
			
	@Override
	public int compare(Move move1, Move move2) {
		return getPieceMovePriority(move1.getPiece().getPieceType())
				.compareTo(getPieceMovePriority(move2.getPiece().getPieceType()));
	}
}
