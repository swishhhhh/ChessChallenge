package chess.ui;
import javax.swing.JMenuItem;

import chess.model.PieceType;

public class PromotePieceMenuItem extends JMenuItem {
	private final PieceType pieceType;

	public PromotePieceMenuItem(PieceType pieceType) {
		super();
		setText(pieceType.toString());
		this.pieceType = pieceType;
	}
	
	public PieceType getPieceType() {
		return pieceType;
	}
}
