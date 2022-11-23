package chess.ui;

public class UiMoveState {
	private boolean inProgress = false;
	private ChessCellButton sourceCell;
	
	public boolean isInProgress() {
		return inProgress;
	}
	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}
	
	public ChessCellButton getSourceCell() {
		return sourceCell;
	}
	public void setSourceCell(ChessCellButton sourceCell) {
		this.sourceCell = sourceCell;
	}
	
	@Override
	public String toString() {
		return "MoveState [inProgress=" + inProgress + ", sourceCell=" + sourceCell + "]";
	}
}
