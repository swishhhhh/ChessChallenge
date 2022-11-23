package chess.ui;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import chess.model.BoardModel;
import chess.model.Cell;
import chess.model.ChessPiece;
import chess.model.PieceType;
import chess.moves.Move;
import chess.moves.MovesProcessor;

public class CellClickListener extends MouseAdapter {
	private ChessCellButton cellButton;
	private ChessGUI gui;
	
	public CellClickListener(ChessCellButton cell, ChessGUI gui) {
		super();
		this.cellButton = cell;
		this.gui = gui;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (event.getSource() == cellButton) {
			handleCellClickEvent(event);
		} else if (event.getSource() == cellButton.getDeleteMenuItem()) {
			handleDeletePieceEvent(event);
		} else if (event.getSource() instanceof AddPieceMenuItem) {
			handleAddPiece(event);
		} else if (event.getSource() instanceof PromotePieceMenuItem) {
			handlePromotePiece(event);
		}
	}

	private void handleAddPiece(MouseEvent event) {
		AddPieceMenuItem addPieceMenuItem = (AddPieceMenuItem) event.getSource();
		ChessPiece piece = addPieceMenuItem.getPiece();
		gui.getGameController().getBoard().placePiece(cellButton.getCell(), piece);
        gui.updateMessage("Added " + piece, false);
        gui.syncViewWithModel();
        gui.resetBoardBackgroundColors();
	}
	
	private void handleDeletePieceEvent(MouseEvent event) {
		if (!cellButton.isOccupied()) {
			return;
		}
		
		UiMoveState moveState = gui.getMoveState();
		if (moveState.isInProgress()) {
			gui.updateMessage("complete your move first...", true);
			return; //don't do anything if in middle of a move
		}
		
		ChessPiece removedPiece = gui.getGameController().getBoard().removePiece(cellButton.getCell(), true);
		
		gui.syncViewWithModel();
		gui.updateMessage("Removed " + removedPiece, false);
		gui.resetBoardBackgroundColors();
	}

	private void handleCellClickEvent(MouseEvent event) {
		boolean isRightClick = event.getButton() == 3;
		UiMoveState moveState = gui.getMoveState();
		ChessPiece piece = cellButton.getPiece();
		
		if (isRightClick) {
			cellButton.getPopUpMenu().show(event.getComponent(), event.getX(), event.getY());
			return;
		}
		
		gui.updateMessage(cellButton.getToolTipText(), false);
		
		GameController gameController = gui.getGameController();
		if (moveState.isInProgress()) { 
			ChessCellButton sourceCell = moveState.getSourceCell();
			
			if (sourceCell == cellButton   //clicked on same cell, cancel move
				 ) { 
				cancelMove(moveState, sourceCell);
				return;
			} else { //complete move
				if (isPawnPromotionMove(sourceCell.getPiece(), this.cellButton.getCell())) {
					cellButton.getPromotePiecePopUpMenu().show(event.getComponent(), event.getX(), event.getY());
				} else {
					completeMove(moveState, gameController, sourceCell, null);
				}
			}
		} else if (piece != null){ //start a new move
			startMove(moveState, piece, gameController);
		}		
	}
	
	private void handlePromotePiece(MouseEvent event) {
		UiMoveState moveState = gui.getMoveState();
		ChessCellButton sourceCell = moveState.getSourceCell();
		boolean white = sourceCell.getPiece().getColor().equals(Color.WHITE);
		ChessPiece promotedPiece = null;
		
		PromotePieceMenuItem menuItem = (PromotePieceMenuItem) event.getSource();
		switch (menuItem.getPieceType()) {
			case QUEEN:
				promotedPiece = white ? ChessPiece.WHITE_QUEEN : ChessPiece.BLACK_QUEEN;
				break;
	
			case ROOK:
				promotedPiece = white ? ChessPiece.WHITE_ROOK: ChessPiece.BLACK_ROOK;
				break;
				
			case BISHOP:
				promotedPiece = white ? ChessPiece.WHITE_BISHOP : ChessPiece.BLACK_BISHOP;
				break;
				
			case KNIGHT:
				promotedPiece = white ? ChessPiece.WHITE_KNIGHT : ChessPiece.BLACK_KNIGHT;
				break;
				
			default:
				break;
		}
		
		completeMove(moveState, gui.getGameController(), moveState.getSourceCell(), promotedPiece);
	}

	private void cancelMove(UiMoveState moveState, ChessCellButton sourceCell) {
		moveState.setInProgress(false);
		moveState.setSourceCell(null);
		if (sourceCell == cellButton) {
			gui.updateMessage("move cancelled", false);
		} else {
			gui.updateMessage("space already occupied", true);
		}
		gui.resetBoardBackgroundColors();
	}

	private void completeMove(UiMoveState moveState, GameController gameController, ChessCellButton sourceCellButton, ChessPiece promotedPiece) {
		ChessPiece piece = sourceCellButton.getPiece();
		
		BoardModel board = gameController.getBoard();
		Cell targetCell = cellButton.getCell();
		Move move = new Move(board, piece, sourceCellButton.getCell(), targetCell, promotedPiece);
		
		//if en-passant move, create move via the en-passant constructor...
		if (piece.getPieceType().equals(PieceType.PAWN)) {
			if (MovesProcessor.isEnPassantCapture(board, piece, targetCell.getRow(), targetCell.getCol(), gameController.getLastMove())) {
				move = new Move(board, piece, sourceCellButton.getCell(), targetCell, true);
			}
		}
		
		if (!gameController.isValidMove(move)) {
			gui.updateMessage("Invalid move", true);
			return;
		}
		
		gameController.applyMove(move);
		
		gui.syncViewWithModel();
		gui.resetBoardBackgroundColors();
		moveState.setInProgress(false);
		moveState.setSourceCell(null);
	}

	private boolean isPawnPromotionMove(ChessPiece piece, Cell targetCell) {
		return piece.equals(ChessPiece.WHITE_PAWN) && targetCell.getRow() == 0
				|| piece.equals(ChessPiece.BLACK_PAWN) && targetCell.getRow() == 7;
	}
	
	private void startMove(UiMoveState moveState, ChessPiece piece, GameController gameController) {
		if (gui.isInGameMode()) {
			//make sure it's this piece/color's turn
			Move lastMove = gameController.getLastMove();
			Color color = piece.getColor();
			if (lastMove == null && color.equals(Color.BLACK)) {
				gui.updateMessage("White's turn to move...", true);
				return; //white should move first
			}
			if (lastMove != null && lastMove.getPiece().getColor().equals(color)) {
				gui.updateMessage((color.equals(Color.WHITE) ? "Black" : "White") + "'s turn to move...", true);
				return; //same color attempting 2 moves in succession
			}
		}
		
		cellButton.highlightAsSelected();
		moveState.setInProgress(true);
		moveState.setSourceCell(cellButton);
		
		BoardModel board = gameController.getBoard();
		Collection<Move> validMoves = 
				MovesProcessor.getMovesForPiece(board, cellButton.getCell(), piece, gameController.getLastMove(), true);
		
		for (Move move : validMoves) {
			Cell target = move.getTarget();
			gui.getCellButton(target.getRow(), target.getCol()).highlightAsValidTarget();
		}
	}
}
