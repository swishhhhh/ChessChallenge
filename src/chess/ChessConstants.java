package chess;
import java.util.Arrays;
import java.util.List;

import chess.model.Cell;
import chess.model.ChessPiece;
import chess.model.PieceType;

public interface ChessConstants {
	public static final ChessPiece[] STARTING_ROW_WHITE_PIECES = {
    	ChessPiece.WHITE_ROOK, ChessPiece.WHITE_KNIGHT, ChessPiece.WHITE_BISHOP, ChessPiece.WHITE_QUEEN, 
    	ChessPiece.WHITE_KING, ChessPiece.WHITE_BISHOP, ChessPiece.WHITE_KNIGHT, ChessPiece.WHITE_ROOK
	};
	public static final ChessPiece[] STARTING_ROW_BLACK_PIECES = {
    	ChessPiece.BLACK_ROOK, ChessPiece.BLACK_KNIGHT, ChessPiece.BLACK_BISHOP, ChessPiece.BLACK_QUEEN, 
    	ChessPiece.BLACK_KING, ChessPiece.BLACK_BISHOP, ChessPiece.BLACK_KNIGHT, ChessPiece.BLACK_ROOK
    };
	public static final Cell STARTING_CELL_WHITE_KING = new Cell(7, 4);
	public static final Cell STARTING_CELL_BLACK_KING = new Cell(0, 4);
	public static final Cell STARTING_CELL_WHITE_KINGSIDE_ROOK = new Cell(7, 7);
	public static final Cell STARTING_CELL_WHITE_QUEENSIDE_ROOK = new Cell(7, 0);
	public static final Cell STARTING_CELL_BLACK_KINGSIDE_ROOK = new Cell(0, 7);
	public static final Cell STARTING_CELL_BLACK_QUEENSIDE_ROOK = new Cell(0, 0);
	public static final Cell CASTLE_CELL_KING_WHITE_KINGSIDE = new Cell(7, 6);
	public static final Cell CASTLE_CELL_ROOK_WHITE_KINGSIDE = new Cell(7, 5);
	public static final Cell CASTLE_CELL_KING_WHITE_QUEENSIDE = new Cell(7, 2);
	public static final Cell CASTLE_CELL_ROOK_WHITE_QUEENSIDE = new Cell(7, 3);
	public static final Cell CASTLE_CELL_KING_BLACK_KINGSIDE = new Cell(0, 6);
	public static final Cell CASTLE_CELL_ROOK_BLACK_KINGSIDE = new Cell(0, 5);
	public static final Cell CASTLE_CELL_KING_BLACK_QUEENSIDE = new Cell(0, 2);
	public static final Cell CASTLE_CELL_ROOK_BLACK_QUEENSIDE = new Cell(0, 3);
	public static final List<ChessPiece> WHITE_PAWN_PROMOTION_PIECES = 
			Arrays.asList(new ChessPiece[]{ChessPiece.WHITE_QUEEN, ChessPiece.WHITE_ROOK, ChessPiece.WHITE_KNIGHT, ChessPiece.WHITE_BISHOP});
	public static final List<ChessPiece> BLACK_PAWN_PROMOTION_PIECES =
			Arrays.asList(new ChessPiece[]{ChessPiece.BLACK_QUEEN, ChessPiece.BLACK_ROOK, ChessPiece.BLACK_KNIGHT, ChessPiece.BLACK_BISHOP});
	public static final List<PieceType> PROMOTION_PIECE_TYPES = Arrays.asList(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT);
}
