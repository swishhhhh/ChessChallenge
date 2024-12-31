package chess.ui;

import static chess.Utils.isOutOfBounds;
import static chess.ChessConstants.PROMOTION_PIECE_TYPES;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import chess.model.Cell;
import chess.model.ChessPiece;
import chess.model.PieceType;

public class ChessCellButton extends JButton {
	private final Cell cell;
	private ChessPiece piece;
	private final CellClickListener eventHandler;
	private final JPopupMenu popUpMenu;
	private final JMenuItem deleteMenuItem;
	private final JMenu addPieceSubMenu;
	private final JPopupMenu promotePiecePopUpMenu;
	private final Color selectedCellColor = new Color(100, 150, 200);
	private final Color validTargetCellColor = new Color(200, 220, 240);
	private final Color inCheckColor = new Color(250, 190, 190);
	private final Color inCheckMateColor = Color.RED;
	private final Color inStaleMateColor = Color.ORANGE;
	private final Color prevCellColor = Color.LIGHT_GRAY;
	private final Color currentCellColor = Color.GRAY;
	
	public ChessCellButton(int rowNum, int colNum, ChessGUI gui) {
		super();
		
		if (isOutOfBounds(rowNum, colNum)) {
			throw new IllegalArgumentException("row and col must be 0-7");
		}
		
		this.cell = new Cell(rowNum, colNum);
		setToolTipText(cell.getLabel());
		
		eventHandler = new CellClickListener(this, gui); 
		this.addMouseListener(eventHandler);
		
		popUpMenu = new JPopupMenu("Chess");
		deleteMenuItem = new JMenuItem("Delete");
		deleteMenuItem.setEnabled(false); 
		deleteMenuItem.addMouseListener(eventHandler);
		popUpMenu.add(deleteMenuItem);
		
		addPieceSubMenu = new JMenu("Add...");
		populateAddPiecesSubMenu();
		
//		popUpMenu.add(new JMenuItem("Cancel"));
		
		//---
		promotePiecePopUpMenu = new JPopupMenu("Promote...");
		for (PieceType pieceType : PROMOTION_PIECE_TYPES) {
			PromotePieceMenuItem menuItem = new PromotePieceMenuItem(pieceType);
			menuItem.addMouseListener(eventHandler);
			promotePiecePopUpMenu.add(menuItem);
		}
		promotePiecePopUpMenu.add(new JMenuItem("Cancel"));
	}
	
	public Cell getCell() {
		return cell;
	}

	public ChessPiece getPiece() {
		return piece;
	}

	public void setPiece(ChessPiece piece) {
		if (piece == null) {
			return;
		}
		
		this.piece = piece;
		deleteMenuItem.setEnabled(true);
		addPieceSubMenu.setEnabled(false);
		setToolTipText(cell.getLabel() + ", " + piece);
	}
	
	public void removePiece() {
        deleteMenuItem.setEnabled(false);
		addPieceSubMenu.setEnabled(true);
		setToolTipText(cell.getLabel());
		this.piece = null;
	}
	
	public boolean isOccupied() {
		return piece != null;
	}

	public JPopupMenu getPopUpMenu() {
		return popUpMenu;
	}

	public JPopupMenu getPromotePiecePopUpMenu() {
		return promotePiecePopUpMenu;
	}

	public JMenuItem getDeleteMenuItem() {
		return deleteMenuItem;
	}
	
	public void highlightAsSelected() {
		setBackground(selectedCellColor);
	}
	
	public void highlightAsValidTarget() {
		setBackground(validTargetCellColor);
	}
	
	public void highlightAsInCheck() {
		setBackground(inCheckColor);
	}
	
	public void highlightAsInCheckMate() {
		setBackground(inCheckMateColor);
	}
	
	public void highlightAsInStaleMate() {
		setBackground(inStaleMateColor);
	}

	public void highlightAsPrevCell() {
		setBackground(prevCellColor);
	}

	public void highlightAsCurrentCell() {
		setBackground(currentCellColor);
	}

	private void populateAddPiecesSubMenu() {
		popUpMenu.add(addPieceSubMenu);
		JMenu whitePieces = new JMenu("White...");
		addPiecesAsSubMenuItems(whitePieces, true);
		addPieceSubMenu.add(whitePieces);
		JMenu blackPieces = new JMenu("Black...");
		addPiecesAsSubMenuItems(blackPieces, false);
		addPieceSubMenu.add(blackPieces);
	}
	
	private void addPiecesAsSubMenuItems(JMenu piecesSubMenu, boolean white) {
		AddPieceMenuItem apmi = new AddPieceMenuItem("Pawn", white ? ChessPiece.WHITE_PAWN : ChessPiece.BLACK_PAWN);
		apmi.addMouseListener(eventHandler);
		piecesSubMenu.add(apmi);
		
		apmi = new AddPieceMenuItem("King", white ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING);
		apmi.addMouseListener(eventHandler);
		piecesSubMenu.add(apmi);
		
		apmi = new AddPieceMenuItem("Queen", white ? ChessPiece.WHITE_QUEEN : ChessPiece.BLACK_QUEEN);
		apmi.addMouseListener(eventHandler);
		piecesSubMenu.add(apmi);
		
		apmi = new AddPieceMenuItem("Rook", white ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK);
		apmi.addMouseListener(eventHandler);
		piecesSubMenu.add(apmi);
		
		apmi = new AddPieceMenuItem("Knight", white ? ChessPiece.WHITE_KNIGHT : ChessPiece.BLACK_KNIGHT);
		apmi.addMouseListener(eventHandler);
		piecesSubMenu.add(apmi);
		
		apmi = new AddPieceMenuItem("Bishop", white ? ChessPiece.WHITE_BISHOP : ChessPiece.BLACK_BISHOP);
		apmi.addMouseListener(eventHandler);
		piecesSubMenu.add(apmi);
	}
	
	void enablePopupMenu(boolean enable) {
		this.popUpMenu.setEnabled(enable);
		deleteMenuItem.setVisible(enable);
		addPieceSubMenu.setVisible(enable);
	}
}
