package chess.moves;

import static chess.ChessConstants.*;
import static chess.Utils.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chess.Utils;
import chess.model.BoardModel;
import chess.model.Cell;
import chess.model.ChessPiece;
import chess.model.PieceType;

public class MovesProcessor {
	public static int[][] knightMoveIncrements = //8 pairs; each pair in the format of {rowIncrement, colIncrement}
			new int[][]{{-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}};
	
	/**
	 * 
	 * @param board
	 * @param color
	 * @param previousMove needed during en-passante calculations
	 * @param filterOutMovesResultingInCheck the reason this is sometimes false (i.e. don't bother checking/filtering-out moves resulting in check) - is
	 *        to short-circuit the recursive loop where e.g. we're evaluating all of white's moves, and for each one we check (pun!) whether applying
	 *        that move will result in (white) being in check - which entails getting all of black's subsequent moves and seeing if any of them capture 
	 *        white's king. Therefore, if in the process of doing this (calculating all of black's potential moves) we would not disable this 
	 *        "filterOutMovesResultingInCheck" for black - then it would potentially never end - because to validate/filter black's moves, we must get 
	 *        all of white's subsequent moves, which further have to validate and get all of black's moves, etc etc). So instead, we only do the filter
	 *        at the first level (i.e. for the first color at the beginning of the chain - in our example white), and then when evaluating all of black's 
	 *        subsequent moves it's OK not to filter out any moves that would leave it (black) in check - because the purpose of getting all of black's 
	 *        moves is purely to see if there's any moves that captures white's king - and if that were true, it's by definition a valid move (since the  
	 *        game would be over as soon as the king is captured) thus allowing us to short-circuit the filter/check.  
	 * @return
	 */
	public static MovesSummary getAllMoves(BoardModel board, Color color, Move previousMove, boolean filterOutMovesResultingInCheck) {
		MovesSummary movesSummary = new MovesSummary(color, board);
		for (byte row = 0; row < 8; row++) {
			for (byte col = 0; col < 8; col++) {
				ChessPiece piece = board.getPiece(row, col);
				if (!piece.getColor().equals(color)) {
					continue;
				}
				
				movesSummary.addMoves(getMovesForPiece(board, new Cell(row, col), piece, previousMove, filterOutMovesResultingInCheck));
			}
		}
		
		return movesSummary;
	}
	
	public static Collection<Move> getMovesForPawn(BoardModel board, Cell origin, ChessPiece pawn, Move prevMove, boolean filterCheck) {
		List<Move> moves = new ArrayList<>();
		Color color = pawn.getColor();
		
		//one square forward 
		int rowIncrement = pawn.getColor().equals(Color.WHITE) ? -1 : 1;
		int row = origin.getRow() + rowIncrement;
		int col = origin.getCol();
		if (!isOutOfBounds(row, col) && isSquareEmpty(board, row, col)) {
			createPawnMoveAndPossiblePromotionPermutations(board, pawn, origin, row, col, moves);
		}
		
		//2 squares forward
		boolean isPawnInStartingPosition = (color.equals(Color.WHITE) && origin.getRow() == 6) 
										|| (color.equals(Color.BLACK) && origin.getRow() == 1);
		if (isPawnInStartingPosition) {
			int row2SquaresForward = origin.getRow() + (2 * rowIncrement);
			int row1SquareForward = origin.getRow() + rowIncrement;
			col = origin.getCol();
			if (!isOutOfBounds(row2SquaresForward, col) && isSquareEmpty(board, row2SquaresForward, col) && isSquareEmpty(board, row1SquareForward, col)) {
				moves.add(new Move(board, pawn, origin, new Cell(row2SquaresForward, col)));
			}
		}
		
		//left capture and right capture (including en-passant)
		for (int leftThenRight : new int[]{-1, 1}) {
			row = origin.getRow() + rowIncrement;
			col = origin.getCol() + leftThenRight;
			
			if (!isOutOfBounds(row, col)) {
				if (isEnPassantCapture(board, pawn, row, col, prevMove)) {
					moves.add(new Move(board, pawn, origin, new Cell(row, col), true));
				} else if (isOccupiedByOpposingPiece(board, row, col, color)) {
					createPawnMoveAndPossiblePromotionPermutations(board, pawn, origin, row, col, moves);
				}
			}
		}
		
		return filterCheck ? filterOutMovesResultingInCheck(board, pawn, moves, prevMove) : moves;
	}
	
	public static Collection<Move> getMovesForKing(BoardModel board, Cell origin, ChessPiece king, 
			Move previousMove, boolean filterCheck) {
		List<Direction> directions = 
				Arrays.asList(Direction.NW, Direction.N, Direction.NE, Direction.E, 
							  Direction.SE, Direction.S, Direction.SW, Direction.W);
		Collection<Move> moves = getGenericMovesInDirection(board, origin, king, directions, 1);
		
		//add valid castling moves
		moves.addAll(getCastlingMoves(board, origin, king, previousMove, filterCheck));
		
		//validate (and remove) moves that will result in the king being in check
		return filterCheck ? filterOutMovesResultingInCheck(board, king, moves, previousMove) : moves;
	}

	public static boolean noPiecesOnRowBetween(BoardModel board, Cell leftCell, Cell rightCell) {
		if (leftCell.getRow() != rightCell.getRow()) {
			throw new IllegalArgumentException(String.format("Left-cell row (%s) differes from right-cell row (%s)", 
					leftCell.getRow(), rightCell.getRow()));
		}
		
		for (int i = leftCell.getCol() + 1; i < rightCell.getCol(); i++) {
			if (board.isCellOccupied(new Cell(leftCell.getRow(), i))) {
				return false;
			}
		}
		
		return true;
	}

	public static Collection<Move> getMovesForQueen(BoardModel board, Cell origin, ChessPiece queen, Move previousMove, boolean filterCheck) {
		List<Direction> directions = 
				Arrays.asList(Direction.NW, Direction.N, Direction.NE, Direction.E, 
							  Direction.SE, Direction.S, Direction.SW, Direction.W);
		
		Collection<Move> moves = getGenericMovesInDirection(board, origin, queen, directions);
		return filterCheck ? filterOutMovesResultingInCheck(board, queen, moves, previousMove) : moves;
	}
	
	public static Collection<Move> getMovesForRook(BoardModel board, Cell origin, ChessPiece rook, Move previousMove, boolean filterCheck) {
		List<Direction> directions = Arrays.asList(Direction.N, Direction.E, Direction.S, Direction.W);
		Collection<Move> moves = getGenericMovesInDirection(board, origin, rook, directions);
		return filterCheck ? filterOutMovesResultingInCheck(board, rook, moves, previousMove) : moves;
	}

	public static Collection<Move> getMovesForKnight(BoardModel board, Cell origin, ChessPiece knight, Move previousMove, boolean filterCheck) {
		List<Move> moves = new ArrayList<>();
		
		for (int i = 0; i < knightMoveIncrements.length; i++) {
			int row = origin.getRow() + knightMoveIncrements[i][0];
			int col = origin.getCol() + knightMoveIncrements[i][1];
			
			if (isOutOfBounds(row, col) || isOccupiedByPeerPiece(board, row, col, knight.getColor())) {
				continue;
			}
			
			Cell newCell = new Cell(row, col);
			moves.add(new Move(board, knight, origin, newCell));
		}
		
		return filterCheck ? filterOutMovesResultingInCheck(board, knight, moves, previousMove) : moves;		
	}
	
	public static Collection<Move> getMovesForBishop(BoardModel board, Cell origin, ChessPiece bishop, Move previousMove, boolean filterCheck) {
		List<Direction> directions = Arrays.asList(Direction.NW, Direction.NE, Direction.SE, Direction.SW);
		
		Collection<Move> moves = getGenericMovesInDirection(board, origin, bishop, directions);
		return filterCheck ? filterOutMovesResultingInCheck(board, bishop, moves, previousMove) : moves;
	}
	
	public static boolean isValidMove(BoardModel board, Move move, Move previousMove) {
		return getMovesForPiece(board, move.getSource(), move.getPiece(), previousMove, true)
				.contains(move);
	}
	
	public static boolean isCheckOnColor(BoardModel board, Color color, Move previousMove) {
		MovesSummary opponentMoves = 
				getAllMoves(board, Utils.getOpponentColor(color), previousMove, false);
		
		Collection<Move> captureKingMoves = opponentMoves.getMovesForCapturedPieceType(PieceType.KING);
		return captureKingMoves != null && !captureKingMoves.isEmpty();
	}
	
	public static boolean isCheckMateOnColor(BoardModel board, Color color, Move previousMove) {
		return isCheckOnColor(board, color, previousMove) &&
				getAllMoves(board, color, previousMove, true).getNumberOfMoves() == 0;
	}
	
	public static boolean isStaleMateOnColor(BoardModel board, Color color, Move previousMove) {
		return !isCheckOnColor(board, color, previousMove) &&
				getAllMoves(board, color, previousMove, true).getNumberOfMoves() == 0;
	}
	
	public static Cell locateKing(BoardModel board, Color color) {
		Collection<Cell> cells = locateKings(board, color);
		return cells.isEmpty() ? null : cells.iterator().next();
	}
	
	public static Collection<Cell> locateKings(BoardModel board, Color color) {
		Collection<Cell> cells = new ArrayList<>();
		ChessPiece king = color.equals(Color.WHITE) ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING;
		for (byte row = 0; row < 8; row++) {
			for (byte col = 0; col < 8; col++) {
				if (board.getPiece(row, col).equals(king)) {
					cells.add(new Cell(row, col));
				}
			}
		}
		
		return cells;
	}
	
	public static BoardModel applyMove(BoardModel board, Move move) {
		//all changes are applied to a clone of the original board (leave original unchanged) 
		BoardModel clone = board.getClone();
		
		//remove piece from source cell
		clone.removePiece(move.getSource(), true);
		
		//add piece to target cell
		clone.placePiece(move.getTarget(), move.getPiece());
		
		//handle promotions
		if (move.isPromotePawn()) {
			clone.placePiece(move.getTarget(), move.getPromotedPiece());
		}
		
		//handle castling (need to move rook)
		if (move.isCastling()) {
			CellPair cellPair = getCellPairForRookOnCastle(move);
			
			clone.placePiece(cellPair.getToCell(), 
					move.getPiece().getColor().equals(Color.WHITE) ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK);
			
			//remove rook from original place
			clone.removePiece(cellPair.getFromCell(), true);
		}
		
		//handle en-passant (need to remove opponent/captured pawn  
		if (move.isEnPassant()) {
			clone.removePiece(move.getEnPassantCaptureCell(), true);
		}
		
		return clone;
	}
	
	public static BoardModel undoMove(BoardModel board, Move move) {
		//all changes are applied to a clone of the original board (leave original unchanged) 
		BoardModel clone = board.getClone();
		
		//remove piece from target cell
		clone.removePiece(move.getTarget(), true);
		
		//add piece back to source cell
		clone.placePiece(move.getSource(), move.getPiece());
		
		//if capture move, replace captured piece on target cell
		if (move.isCapturePiece()) {
			//handle en-passant (need to restore opponent/captured pawn  
			if (move.isEnPassant()) {
				clone.placePiece(move.getEnPassantCaptureCell(), move.getCapturedPiece());
			} else {
				clone.placePiece(move.getTarget(), move.getCapturedPiece());
			}
		}
		
		//handle castling (need to remove rook from castled space and move back to original space)
		if (move.isCastling()) {
			CellPair cellPair = getCellPairForRookOnCastle(move);
			
			clone.placePiece(cellPair.getFromCell(), 
					move.getPiece().getColor().equals(Color.WHITE) ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK);
			
			//move rook from original place
			clone.removePiece(cellPair.getToCell(), true);
		}
		
		return clone;
	}
	
	private static CellPair getCellPairForRookOnCastle(Move move) {
		Cell rookTargetCell = null, rookOriginCell = null;
		
		if (move.getPiece().getColor().equals(Color.WHITE)) {
			//check if king-side or queen side
			if (move.getTarget().equals(CASTLE_CELL_KING_WHITE_KINGSIDE)) {
				rookTargetCell = CASTLE_CELL_ROOK_WHITE_KINGSIDE;
				rookOriginCell = STARTING_CELL_WHITE_KINGSIDE_ROOK;
			} else {
				rookTargetCell = CASTLE_CELL_ROOK_WHITE_QUEENSIDE;
				rookOriginCell = STARTING_CELL_WHITE_QUEENSIDE_ROOK;
			}
		} else {  //black piece
			if (move.getTarget().equals(CASTLE_CELL_KING_BLACK_KINGSIDE)) {
				rookTargetCell = CASTLE_CELL_ROOK_BLACK_KINGSIDE;
				rookOriginCell = STARTING_CELL_BLACK_KINGSIDE_ROOK;
			} else {
				rookTargetCell = CASTLE_CELL_ROOK_BLACK_QUEENSIDE;
				rookOriginCell = STARTING_CELL_BLACK_QUEENSIDE_ROOK;
			}
		}
		
		return new CellPair(rookOriginCell, rookTargetCell);
	}
	
	public static boolean willMoveResultInCheckForColor(BoardModel board, Move move, Color player, Move previousMove) {
		return isCheckOnColor(applyMove(board, move), player, move);
	}
	
	public static boolean willMoveCaptureOpposingKing(BoardModel board, Move move, ChessPiece playerPiece) {
		return move.isCapturePiece()
				&& board.getPiece(move.getTarget()).getPieceType() == PieceType.KING
				&& areOppositeColors(playerPiece, board.getPiece(move.getTarget()));
	}
	
	public static boolean areSameColors(ChessPiece piece1, ChessPiece piece2) {
		return piece1.getColor().equals(piece2.getColor());
	}
	
	public static boolean areOppositeColors(ChessPiece piece1, ChessPiece piece2) {
		return !areSameColors(piece1, piece2);
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<Move> getMovesForPiece(BoardModel board, Cell origin, ChessPiece piece, 
			Move previousMove, boolean filterOutMovesResultingInCheck) {
		
		switch (piece.getPieceType()) {
			case KING:
				return getMovesForKing(board, origin, piece, previousMove, filterOutMovesResultingInCheck);
			case QUEEN:
				return getMovesForQueen(board, origin, piece, previousMove, filterOutMovesResultingInCheck);
			case BISHOP:
				return getMovesForBishop(board, origin, piece, previousMove, filterOutMovesResultingInCheck);
			case KNIGHT:
				return getMovesForKnight(board, origin, piece, previousMove, filterOutMovesResultingInCheck);
			case ROOK:
				return getMovesForRook(board, origin, piece, previousMove, filterOutMovesResultingInCheck);
			case PAWN:
				return getMovesForPawn(board, origin, piece, previousMove, filterOutMovesResultingInCheck);
			case NO_PIECE:
				return Collections.EMPTY_LIST;
		}
		
		throw new IllegalArgumentException();
	}

	private static Collection<Move> getGenericMovesInDirection(BoardModel board, Cell origin, ChessPiece piece, List<Direction> directions) {
		return getGenericMovesInDirection(board, origin, piece, directions, Integer.MAX_VALUE);
	}
	
	private static Collection<Move> getGenericMovesInDirection(BoardModel board, Cell origin, ChessPiece piece, 
			List<Direction> directions, int limit) {
		List<Move> moves = new ArrayList<>();
		
		for (Direction direction : directions) {
			int numMovesInDirection = numberOfAvailableSquaresInDirection(board, origin, piece.getColor(), direction);
			for (int i = 1; i <= numMovesInDirection && i <= limit; i++) {
				Cell newCell = new Cell(origin.getRow() + (i * direction.getRowIncrement()), origin.getCol() + (i * direction.getColIncrement()));
				moves.add(new Move(board, piece, origin, newCell));
			}
		}
		
		return moves;
	}
	
	/**
	 * @return the number of squares that are open to be advanced by a piece of color "color" on board "board" in direction "direction" from
	 * 		cell "origin". The "piece" attempting the move is treated generically - i.e. it is assumed that it is valid to move in that 
	 * 		direction (we don't check the legality of the moves for the piece type, and we certainly don't check whether the move will result 
	 * 		in a checkmate for the moving side).
	 * 		The only validation that is done is: ensure all squares are open between the origin cell and the target destination (i.e. 
	 * 		don't go off the board, don't leapfrog any other piece, and don't collide with your own piece (but can collide with an 
	 * 		opposing piece - i.e. capture it)).  
	 * 		This method is only useful for "straight" or "diagonal" moves - and not for any of the special moves like knight moves, or castling.
	 */
	private static int numberOfAvailableSquaresInDirection(BoardModel board, Cell origin, Color color, Direction direction) {
		int moves = 0;
		int row = origin.getRow();
		int col = origin.getCol();
		
		while (true) {
			//check if target square is occupied by an opposing piece (i.e. a capture move) in which case that was the last square the piece can advance to
			if (moves != 0 && isOccupiedByOpposingPiece(board, row, col, color)) {
				return moves;
			}
			
			row = row + direction.getRowIncrement();
			col = col + direction.getColIncrement();
			if (isOutOfBounds(row, col) || isOccupiedByPeerPiece(board, row, col, color)) {
				return moves;
			}
			moves++; //piece can advance
		}
	}
	
	private static boolean isOccupiedByPeerPiece(BoardModel board, int row, int col, Color color) {
		ChessPiece piece = board.getPiece((byte) row, (byte) col);
		return piece != ChessPiece.NO_PIECE && piece.getColor() == color;
	}
	
	private static boolean isOccupiedByOpposingPiece(BoardModel board, int row, int col, Color color) {
		ChessPiece piece = board.getPiece((byte) row, (byte) col);
		return piece != ChessPiece.NO_PIECE && piece.getColor() != color;
	}
	
	private static boolean isSquareEmpty(BoardModel board, int row, int col) {
		return board.getPiece((byte) row, (byte) col) == ChessPiece.NO_PIECE;
	}
	
	private static Collection<Move> filterOutMovesResultingInCheck(BoardModel board, ChessPiece piece, Collection<Move> moves, Move previousMove) {
		Collection<Move> validMoves = new ArrayList<>();
		for (Move move : moves) {
			if (willMoveCaptureOpposingKing(board, move, piece) ||
				!willMoveResultInCheckForColor(board, move, piece.getColor(), previousMove)) {
				validMoves.add(move);
			}
		}
		return validMoves;
	}
	
	private static Collection<? extends Move> getCastlingMoves(BoardModel board, Cell origin, ChessPiece king, Move previousMove, boolean filterCheck) {
		/*
		 *  Note that we don't fully comply with the entire list of castling rules. Specifically, the following restrictions are ignored. 
		 *  1) The king has been moved earlier in the game.
		 *  2) The rook that castles has been moved earlier in the game.
		 *  3) The king moves through a square that is attacked by a piece of the opponent.
		 */
		
		Collection<Move> moves = new ArrayList<>();
		
		//can't castle if king is in check
		if (filterCheck && isCheckOnColor(board, king.getColor(), previousMove)) {
			return moves;
		}
		
		if (king.getColor().equals(Color.WHITE)) {
			if (origin.equals(STARTING_CELL_WHITE_KING)) { //only if king is in original position
				if (board.getPiece(STARTING_CELL_WHITE_QUEENSIDE_ROOK).equals(ChessPiece.WHITE_ROOK)) {
					//can't castle if there are any pieces between the king and rook					
					if (noPiecesOnRowBetween(board, STARTING_CELL_WHITE_QUEENSIDE_ROOK, STARTING_CELL_WHITE_KING)) {
						moves.add(new Move(board, king, origin, CASTLE_CELL_KING_WHITE_QUEENSIDE));
					}
				} 
				if (board.getPiece(STARTING_CELL_WHITE_KINGSIDE_ROOK).equals(ChessPiece.WHITE_ROOK)) {
					if (noPiecesOnRowBetween(board, STARTING_CELL_WHITE_KING, STARTING_CELL_WHITE_KINGSIDE_ROOK)) {
						moves.add(new Move(board, king, origin, CASTLE_CELL_KING_WHITE_KINGSIDE));
					}
				} 
			}
		} else { //black
			if (origin.equals(STARTING_CELL_BLACK_KING)) { //only if king is in original position
				if (board.getPiece(STARTING_CELL_BLACK_QUEENSIDE_ROOK).equals(ChessPiece.BLACK_ROOK)) {
					//can't castle if there are any pieces between the king and rook
					if (noPiecesOnRowBetween(board, STARTING_CELL_BLACK_QUEENSIDE_ROOK, STARTING_CELL_BLACK_KING)) {
						moves.add(new Move(board, king, origin, CASTLE_CELL_KING_BLACK_QUEENSIDE));
					}
				} 
				if (board.getPiece(STARTING_CELL_BLACK_KINGSIDE_ROOK).equals(ChessPiece.BLACK_ROOK)) {
					if (noPiecesOnRowBetween(board, STARTING_CELL_BLACK_KING, STARTING_CELL_BLACK_KINGSIDE_ROOK)) {
						moves.add(new Move(board, king, origin, CASTLE_CELL_KING_BLACK_KINGSIDE));
					}
				} 
			}
		}
		
		return moves;
	}
	
	public static boolean isEnPassantCapture(BoardModel board, ChessPiece pawn, int attackingPawnTargetRow, int attackingPawnTargetCol, Move prevMove) {
		if (prevMove == null || prevMove.getPiece().getPieceType() != PieceType.PAWN
				|| prevMove.getPiece().getColor().equals(pawn.getColor())) { //not a valid condition during game play (consecutive moves by same color) but is during setup
			return false;
		}
		
		//now that we've determined that the previous move was made by a pawn, check that it moved from its starting rank 2 squares 
		//and that the attacking pawn's target square (e.g. targetRow, targetCol) is in between those 2 squares
		if (prevMove.getPiece().getColor().equals(Color.WHITE)) {
			if (   prevMove.getSource().getRow() == 6 
				&& prevMove.getTarget().getRow() == 4 
				&& attackingPawnTargetRow == 5
				&& prevMove.getSource().getCol() == attackingPawnTargetCol) {
				return true;
			}
		} else { //prevMove made by black
			if (   prevMove.getSource().getRow() == 1 
				&& prevMove.getTarget().getRow() == 3 
				&& attackingPawnTargetRow == 2
				&& prevMove.getSource().getCol() == attackingPawnTargetCol) {
				return true;
			}
		}
		
		return false;
	}
	
	private static void createPawnMoveAndPossiblePromotionPermutations(BoardModel board, ChessPiece pawn, Cell origin, 
			int targetRow, int targetCol, List<Move> moves) {
		
		if (pawn.equals(ChessPiece.WHITE_PAWN) && targetRow == 0) {
			for (ChessPiece promotedPiece : WHITE_PAWN_PROMOTION_PIECES) {
				moves.add(new Move(board, pawn, origin, new Cell(targetRow, targetCol), promotedPiece));
			}
		} else if (pawn.equals(ChessPiece.BLACK_PAWN) && targetRow == 7) {
			for (ChessPiece promotedPiece : BLACK_PAWN_PROMOTION_PIECES) {
				moves.add(new Move(board, pawn, origin, new Cell(targetRow, targetCol), promotedPiece));
			}
		} else { //not a promotion
			moves.add(new Move(board, pawn, origin, new Cell(targetRow, targetCol)));
		}
	}
}
