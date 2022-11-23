package chess.solver;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import chess.Utils;
import chess.model.BoardModel;
import chess.moves.Move;
import chess.moves.MovesProcessor;
import chess.moves.MovesSummary;

public class Solver {
	private BoardModel board;
	private Color colorToSolveFor;
	private Move previousMove;
	private Move nextMove;
	private long startMillis;
	private long movesCounter;
	private long recurseCounter;
	
	public Solver(BoardModel board, Color colorToSolveFor, Move previousMove) {
		super();
		this.board = board;
		this.colorToSolveFor = colorToSolveFor;
		this.previousMove = previousMove;
	}
	
	public boolean tryToSolveIn(int maxNumberOfMoves, int maxSeconds) {
		startMillis = System.currentTimeMillis(); //start timer
		return isCMinNmoves(maxNumberOfMoves, maxNumberOfMoves, maxSeconds, board, previousMove);
	}
	
	private boolean isCMinNmoves(int initialMaxNumberOfMoves, int remainingNumberOfMoves, int maxSeconds, BoardModel workingBoard, Move prevMove) {
		recurseCounter++;
		
		MovesSummary movesSummary = MovesProcessor.getAllMoves(workingBoard, colorToSolveFor, prevMove, true);
		List<Move> moves = new ArrayList<>(movesSummary.getAllMoves());
		movesCounter+= moves.size();
		
		//sort moves based on piece-type priority (queen, then rook, then knight, etc) so that we try moves for the stronger pieces first
		moves.sort(new MovesSolverComparator());
		
		Color opponentColor = Utils.getOpponentColor(colorToSolveFor);
		
		//terminating condition 1: if check-mate or stale-mate on next move
		for (Move move : moves) {
			BoardModel tempBoard = MovesProcessor.applyMove(workingBoard, move);
			if (MovesProcessor.isCheckMateOnColor(tempBoard, opponentColor, move)) {
				if (remainingNumberOfMoves == initialMaxNumberOfMoves) { //check if at the top-most level
					this.nextMove = move;
				}
				return true; 
			}  
		}
		
		//terminating condition 2: if remainingNumberOfMoves is already at 1 (and we couldn't solve it in the previous terminating condition)
		if (remainingNumberOfMoves == 1) {
			return false;
		}
		
		//terminating condition 3: if time elapsed
		if (maxTimeExceeded(maxSeconds, initialMaxNumberOfMoves)) {
			return false;
		}
		
		//for each move, filter out stale-mates, then get opposing color's responding move permutations, check for opponent wins, and (barring that) recurse...
		for (Move move : moves) {
			BoardModel tempBoard = MovesProcessor.applyMove(workingBoard, move);
			if (MovesProcessor.isStaleMateOnColor(tempBoard, opponentColor, move)) {
				continue;
			}
			
			boolean skipToNextMove = false;
			MovesSummary opposingMovesSummary = MovesProcessor.getAllMoves(tempBoard, opponentColor, move, true);
			Collection<Move> opponentMoves = opposingMovesSummary.getAllMoves();
			movesCounter+= opponentMoves.size();
			
			for (Move opponentMove : opponentMoves) {
				BoardModel boardAfterApplyingOpponentMove = MovesProcessor.applyMove(tempBoard, opponentMove);
				if (MovesProcessor.isCheckMateOnColor(boardAfterApplyingOpponentMove, colorToSolveFor, opponentMove)
						|| MovesProcessor.isStaleMateOnColor(boardAfterApplyingOpponentMove, colorToSolveFor, opponentMove)) {
					
					skipToNextMove = true;
					break;
				}
			}
			if (skipToNextMove) {
				continue;
			}
			
			//recurse on each opponentMove
			for (Move opponentMove : opponentMoves) {
				BoardModel boardAfterApplyingOpponentMove = MovesProcessor.applyMove(tempBoard, opponentMove);
				if (!isCMinNmoves(initialMaxNumberOfMoves, remainingNumberOfMoves - 1, maxSeconds, boardAfterApplyingOpponentMove, opponentMove)) {
					skipToNextMove = true;
					break;
				}
			}
			if (skipToNextMove) {
				continue;
			}
			
			//at this point we found a good move (where all subsequent opponent moves have solutions in <= remainingNumberOfMoves)
			if (remainingNumberOfMoves == initialMaxNumberOfMoves) { //check if at the top-most level
				this.nextMove = move;
			}
			return true; //terminating condition 4
		}
		
		//exhausted all possible moves without a solution...
		return false; //terminating condition 5
	}

	private boolean maxTimeExceeded(int maxSeconds, int maxMoves) {
		printCounters(maxMoves);
		return System.currentTimeMillis() - (maxSeconds * 1000) > startMillis;
	}
	
	public void printCounters(int maxMoves) {
		double seconds = (System.currentTimeMillis() - startMillis) / 1000d;
		System.out.println(String.format("max # moves: %s, elapsed seconds: %s, recursion count: %s, moves count: %s", maxMoves, seconds, recurseCounter, movesCounter));
	}

	public Move getNextMoveIfSolved() {
		return nextMove;
	}
}
