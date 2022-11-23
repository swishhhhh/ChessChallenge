package chess.model;
import java.awt.Color;

public enum ChessPiece {
	WHITE_PAWN(PieceType.PAWN, Color.WHITE, (byte) 1), 
	WHITE_ROOK(PieceType.ROOK, Color.WHITE, (byte) 2), 
	WHITE_KNIGHT(PieceType.KNIGHT, Color.WHITE, (byte) 3), 
	WHITE_BISHOP(PieceType.BISHOP, Color.WHITE, (byte) 4), 
	WHITE_QUEEN(PieceType.QUEEN, Color.WHITE, (byte) 5), 
	WHITE_KING(PieceType.KING, Color.WHITE, (byte) 6),
	BLACK_PAWN(PieceType.PAWN, Color.BLACK, (byte) 11), 
	BLACK_ROOK(PieceType.ROOK, Color.BLACK, (byte) 12), 
	BLACK_KNIGHT(PieceType.KNIGHT, Color.BLACK, (byte) 13), 
	BLACK_BISHOP(PieceType.BISHOP, Color.BLACK, (byte) 14), 
	BLACK_QUEEN(PieceType.QUEEN, Color.BLACK, (byte) 15), 
	BLACK_KING(PieceType.KING, Color.BLACK, (byte) 16),
	NO_PIECE(PieceType.NO_PIECE, Color.GRAY, (byte) 0); //gray color (rather than null) avoids having to do a null-check when comparing colors of 2 pieces...
	
	private PieceType pieceType;
	private Color color;
	private byte id;

	private ChessPiece(PieceType type, Color color, byte id) {
		this.pieceType = type;
		this.color = color;
		this.id = id;
	}
	
	public Color getColor() {
		return color;
	}
	
	public PieceType getPieceType() {
		return pieceType;
	}
	
	public byte getId() {
		return id;
	}
	
	public static ChessPiece fromId(byte id) {
		switch (id) {
			case 0:
				return NO_PIECE;
			case 1:
				return WHITE_PAWN;
			case 2:
				return WHITE_ROOK;
			case 3:
				return WHITE_KNIGHT;
			case 4:
				return WHITE_BISHOP;
			case 5:
				return WHITE_QUEEN;
			case 6:
				return WHITE_KING;
			case 11:
				return BLACK_PAWN;
			case 12:
				return BLACK_ROOK;
			case 13:
				return BLACK_KNIGHT;
			case 14:
				return BLACK_BISHOP;
			case 15:
				return BLACK_QUEEN;
			case 16:
				return BLACK_KING;	
			default:
				throw new IllegalArgumentException("Invalid id " + id);
		}
	}
	
	public static ChessPiece fromString(String value) {
		if ("NO_PIECE".equalsIgnoreCase(value)) return NO_PIECE;
		else if ("WHITE_PAWN".equalsIgnoreCase(value)) return WHITE_PAWN;
		else if ("WHITE_ROOK".equalsIgnoreCase(value)) return WHITE_ROOK;
		else if ("WHITE_KNIGHT".equalsIgnoreCase(value)) return WHITE_KNIGHT;
		else if ("WHITE_BISHOP".equalsIgnoreCase(value)) return WHITE_BISHOP;
		else if ("WHITE_QUEEN".equalsIgnoreCase(value)) return WHITE_QUEEN;
		else if ("WHITE_KING".equalsIgnoreCase(value)) return WHITE_KING;
		else if ("BLACK_PAWN".equalsIgnoreCase(value)) return BLACK_PAWN;
		else if ("BLACK_ROOK".equalsIgnoreCase(value)) return BLACK_ROOK;
		else if ("BLACK_KNIGHT".equalsIgnoreCase(value)) return BLACK_KNIGHT;
		else if ("BLACK_BISHOP".equalsIgnoreCase(value)) return BLACK_BISHOP;
		else if ("BLACK_QUEEN".equalsIgnoreCase(value)) return BLACK_QUEEN;
		else if ("BLACK_KING".equalsIgnoreCase(value)) return BLACK_KING;

		return null;
	}
}
