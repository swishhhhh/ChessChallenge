package chess.ui;
import javax.swing.JMenuItem;

import chess.model.ChessPiece;

@SuppressWarnings("serial")
public class AddPieceMenuItem extends JMenuItem {
	private ChessPiece piece;

	public AddPieceMenuItem(String label, ChessPiece piece) {
		super();
		setText(label);
		this.piece = piece;
	}
	
	public ChessPiece getPiece() {
		return piece;
	}
}
