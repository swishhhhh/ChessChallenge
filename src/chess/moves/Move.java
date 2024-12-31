package chess.moves;

import static chess.ChessConstants.*;

import chess.model.BoardModel;
import chess.model.Cell;
import chess.model.ChessPiece;

public class Move {
	private final ChessPiece piece;
	private final Cell source;
	private final Cell target;
	private boolean capturePiece;
	private ChessPiece capturedPiece;
	private boolean castling;
	private ChessPiece promotedPiece;
	private boolean enPassant;
	private Cell enPassantCaptureCell;
	
	/**
	 * constructor with validation
	 */
	public Move(BoardModel board, ChessPiece piece, Cell source, Cell target, ChessPiece promotedPiece) {
		super();
		
		this.piece = piece;
		this.source = source;
		this.target = target;
		this.promotedPiece = promotedPiece;
		
		//validate piece is actually on "source" cell
		if (board.getPiece(source) != piece) {
			throw new IllegalArgumentException(String.format("Source cell %s contains a %s which is not as expected (%s)", 
					source, board.getPiece(source), piece));
		}
		
		//validate source != target
		if (source.equals(target)) {
			throw new IllegalArgumentException(String.format("Source and target cells (%s) are the same", source));
		}
		
		//check/set if capture move
//		if (board.isCellOccupied(target) && piece.getColor() != board.getPiece(target).getColor()) {
		if (board.isCellOccupied(target)) { //capturing own pieces is allowed in setup mode...
			this.setCapturePiece(true);
			this.setCapturedPiece(board.getPiece(target));
		}
		
		//validate that pawn promotions includes promoted piece (and null otherwise)
		validatePromotionPiece();
		
		//check/set castling
		setCastling();
	}

	public Move(BoardModel board, ChessPiece piece, Cell source, Cell target) {
		this(board, piece, source, target, null);
	}
	
	public Move(BoardModel board, ChessPiece piece, Cell source, Cell target, boolean isEnPassantMove) {
		this(board, piece, source, target, null);
		this.enPassant = isEnPassantMove;
		if (isEnPassantMove) {
			//set captured piece
			this.setCapturePiece(true);
			
			Cell captureCell = null;
			if (target.getRow() == 2) {
				captureCell = new Cell(3, target.getCol());
			} else if (target.getRow() == 5) {
				captureCell = new Cell(4, target.getCol());
			}
            assert captureCell != null;
            this.setCapturedPiece(board.getPiece(captureCell));
			this.enPassantCaptureCell = captureCell;
		}
	}
	
	private void validatePromotionPiece() {
		if (piece.equals(ChessPiece.WHITE_PAWN) && target.getRow() == 0) {
			if (promotedPiece == null || !WHITE_PAWN_PROMOTION_PIECES.contains(promotedPiece)) {
				throw new IllegalArgumentException(
						String.format("White pawn promotion must include one of the following promoted pieces %s", WHITE_PAWN_PROMOTION_PIECES)); 
			}
		} else if (piece.equals(ChessPiece.BLACK_PAWN) && target.getRow() == 7) {
			if (promotedPiece == null || !BLACK_PAWN_PROMOTION_PIECES.contains(promotedPiece)) {
				throw new IllegalArgumentException(
						String.format("Black pawn promotion must include one of the following promoted pieces %s", BLACK_PAWN_PROMOTION_PIECES)); 
			}
		} else if (promotedPiece != null) { 
			//no business promoting...
			throw new IllegalArgumentException("Not a valid promotion move");
		}
		
		this.setPromotedPiece(promotedPiece);
	}
	
	private void setCastling() {
		//white 
		if (piece.equals(ChessPiece.WHITE_KING) && source.equals(STARTING_CELL_WHITE_KING)) {
			if (target.equals(CASTLE_CELL_KING_WHITE_QUEENSIDE) || target.equals(CASTLE_CELL_KING_WHITE_KINGSIDE)) {
				castling = true;
				return;
			}
		}
		
		//black
		if (piece.equals(ChessPiece.BLACK_KING) && source.equals(STARTING_CELL_BLACK_KING)) {
			if (target.equals(CASTLE_CELL_KING_BLACK_QUEENSIDE) || target.equals(CASTLE_CELL_KING_BLACK_KINGSIDE)) {
				castling = true;
			}
		}
	}
	
	public ChessPiece getPiece() {
		return piece;
	}

	public Cell getSource() {
		return source;
	}

	public Cell getTarget() {
		return target;
	}

	public boolean isCapturePiece() {
		return capturePiece;
	}

	public boolean isCastling() {
		return castling;
	}

	public boolean isPromotePawn() {
		return promotedPiece != null;
	}

	public ChessPiece getPromotedPiece() {
		return promotedPiece;
	}

	public void setCapturePiece(boolean capturePiece) {
		this.capturePiece = capturePiece;
	}

	public void setPromotedPiece(ChessPiece promotedPiece) {
		this.promotedPiece = promotedPiece;
	}
	
	public ChessPiece getCapturedPiece() {
		return capturedPiece;
	}

	public void setCapturedPiece(ChessPiece capturedPiece) {
		this.capturedPiece = capturedPiece;
	}
	
	public boolean isEnPassant() {
		return enPassant;
	}

	public Cell getEnPassantCaptureCell() {
		return enPassantCaptureCell;
	}

	@Override
	public String toString() {
		return "Move [piece=" + piece + ", source=" + source + ", target=" + target + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((piece == null) ? 0 : piece.hashCode());
		result = prime * result + ((promotedPiece == null) ? 0 : promotedPiece.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		if (piece != other.piece)
			return false;
		if (promotedPiece != other.promotedPiece)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
            return other.target == null;
		} else return target.equals(other.target);
    }
}
