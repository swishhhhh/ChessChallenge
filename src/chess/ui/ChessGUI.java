package chess.ui;
import static chess.ChessConstants.STARTING_ROW_BLACK_PIECES;
import static chess.ChessConstants.STARTING_ROW_WHITE_PIECES;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import chess.Utils;
import chess.model.BoardModel;
import chess.model.Cell;
import chess.model.ChessPiece;
import chess.moves.Move;
import chess.moves.MovesProcessor;
import chess.solver.Solver;

public class ChessGUI {

	private static final JLabel messageLabel = new JLabel("Chess Challegne is ready to play!");
    private static final String COLS = "ABCDEFGH";
    private static final int KING = 0, QUEEN = 1, ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
    
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private ChessCellButton[][] chessBoardSquares = new ChessCellButton[8][8];
    private Image[][] chessPieceImages = new Image[2][6];
    private JPanel chessBoard;
    private UiMoveState moveState = new UiMoveState();
    private GameController gameController = new GameController(new BoardModel(), this); //start with empty board
    private boolean setupMode = true;
    private JRadioButton setupModeButton = new JRadioButton("Setup Mode", true);
    private JRadioButton gameModeButton = new JRadioButton("Game Mode", false);
    private JSlider maxMovesSlider;
    private int maxMovesToSolve = 3;
    private int maxSecondsToSolve = 5;
    private JFileChooser fileChooser = new JFileChooser();
    
    public static final ImageIcon BLANK_SQUARE = new ImageIcon(
            new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
    
    ChessGUI() {
        initializeGui();
    }

    public final void initializeGui() {
        // create the images for the chess pieces
        createImages();

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar topTools = new JToolBar();
        topTools.setFloatable(false);
        gui.add(topTools, BorderLayout.PAGE_START);
        
        Action resetBoardAction = getResetAction();
        topTools.add(resetBoardAction);
        
        Action clearBoardAction = getClearBoardAction();
        topTools.add(clearBoardAction);
        
        Action undoMoveAction = getUndoMoveAction();
        topTools.add(undoMoveAction);
        
        topTools.addSeparator();
        Action saveAction = getSaveAction();
        topTools.add(saveAction);
        
        Action loadAction = getLoadAction();
        topTools.add(loadAction);
        
        topTools.addSeparator();
        setupModeButton.addActionListener(event -> toggleGameMode(false));
        gameModeButton.addActionListener(e -> {
			if (!gameController.canEnterGameMode()) {
				gameModeButton.setSelected(false);
				return;
			}
			
			toggleGameMode(true);
		});
        topTools.add(setupModeButton);
        topTools.add(gameModeButton);
      
        topTools.addSeparator();
        topTools.addSeparator();
        topTools.add(messageLabel);
        
        //2nd tool bar
        JToolBar bottomTools = new JToolBar();
        bottomTools.setFloatable(false);
        gui.add(bottomTools, BorderLayout.AFTER_LAST_LINE);
        
        Action solveForWhite = getSolveForAction(Color.WHITE, "White");
        bottomTools.add(solveForWhite);
        
        Action solveForBlack = getSolveForAction(Color.BLACK, "Black");
        bottomTools.add(solveForBlack);
        
        bottomTools.addSeparator();
        
        bottomTools.add(new JLabel("max moves:"));
        maxMovesSlider = createSlider(1, 5, maxMovesToSolve, 1);
        maxMovesSlider.addChangeListener(event -> maxMovesToSolve = maxMovesSlider.getValue());
        bottomTools.add(maxMovesSlider);
        
        bottomTools.addSeparator();
        bottomTools.add(new JLabel("max seconds:"));        
        JSlider maxSecondsSlider = createSlider(0, 30_000, maxSecondsToSolve, 5000);
        maxSecondsSlider.setToolTipText(String.valueOf(maxSecondsToSolve));
        maxSecondsSlider.addChangeListener(event -> {
			maxSecondsToSolve = maxSecondsSlider.getValue();
			maxSecondsSlider.setToolTipText(String.valueOf(maxSecondsToSolve));
		});
        bottomTools.add(maxSecondsSlider);
        
        bottomTools.addSeparator();

//        gui.add(new JLabel("?"), BorderLayout.LINE_START);

        chessBoard = new JPanel() {
            /**
             * Override the preferred size to return the largest it can, in
             * a square shape.  Must (must, must) be added to a GridBagLayout
             * as the only component (it uses the parent as a guide to size)
             * with no GridBagConstaint (so it is centered).
             */
            @Override
            public final Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Dimension prefSize = null;
                Component c = getParent();
                if (c == null) {
                    prefSize = new Dimension(
                            (int)d.getWidth(),(int)d.getHeight());
                } else if (c!=null &&
                        c.getWidth()>d.getWidth() &&
                        c.getHeight()>d.getHeight()) {
                    prefSize = c.getSize();
                } else {
                    prefSize = d;
                }
                int w = (int) prefSize.getWidth();
                int h = (int) prefSize.getHeight();
                // the smaller of the two sizes
                int s = (w>h ? h : w);
                return new Dimension(s,s);
            }
        };

        RelativeLayout rl = new RelativeLayout(RelativeLayout.X_AXIS);
        rl.setRoundingPolicy( RelativeLayout.FIRST );
        rl.setFill(true);
        chessBoard.setLayout( rl );

        chessBoard.setBorder(new CompoundBorder(
                new EmptyBorder(8,8,8,8),
                new LineBorder(Color.BLACK)
                ));
        // Set the BG to be ochre
        Color ochre = new Color(204,119,34);
        chessBoard.setBackground(ochre);
        JPanel boardConstrain = new JPanel(new GridBagLayout());
        boardConstrain.setBackground(ochre);
        boardConstrain.add(chessBoard);
        gui.add(boardConstrain);


        // our chess pieces are 64x64 px in size, so we'll
        // 'fill this in' using a transparent icon..
//        ImageIcon icon = new ImageIcon(
//                //new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
//                new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB));

        // create the chess board squares
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int row = 0; row < chessBoardSquares.length; row++) {
            for (int col = 0; col < chessBoardSquares[row].length; col++) {
            	ChessCellButton b = new ChessCellButton(row, col, this);
                b.setMargin(buttonMargin);
                b.setIcon(BLANK_SQUARE);
                chessBoardSquares[row][col] = b;
            }
        }
        
        resetBoardBackgroundColors();

        /*
         * fill the chess board
         */

        RelativeLayout topRL = new RelativeLayout(RelativeLayout.Y_AXIS);
        topRL.setRoundingPolicy( RelativeLayout.FIRST );
        topRL.setFill(true);
        JPanel top = new JPanel( topRL );
        top.setOpaque(false);
        chessBoard.add(top, new Float(1));

        top.add(new JLabel(""), new Float(1));

        // fill the top row
        for (int ii = 0; ii < 8; ii++) {
        	JLabel label = new JLabel(" " + (9-(ii + 1)), SwingConstants.CENTER);
            top.add(label, new Float(1));
        }
        // fill the black non-pawn piece row
        for (int ii = 0; ii < 8; ii++) {

            RelativeLayout rowRL = new RelativeLayout(RelativeLayout.Y_AXIS);
            rowRL.setRoundingPolicy( RelativeLayout.FIRST );
            rowRL.setFill(true);
            JPanel row = new JPanel( rowRL );
            row.setOpaque(false);
            chessBoard.add(row, new Float(1));

            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                    	row.add(new JLabel(COLS.substring(ii, ii + 1), SwingConstants.CENTER), new Float(1));
                    default:
                        row.add(chessBoardSquares[jj][ii], new Float(1));
                }
            }
        }           
    }

	private AbstractAction getResetAction() {
		return new AbstractAction("New Board") {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            setupNewGame();
	        }
	    };
	}

	private AbstractAction getClearBoardAction() {
		return new AbstractAction("Clear Board") {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBoard();
            }
        };
	}

	private AbstractAction getUndoMoveAction() {
		return new AbstractAction("Undo Move") {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            undoMove();
	        }			
	    };
	}

	private AbstractAction getSaveAction() {
		return new AbstractAction("Save...") {
	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	int returnVal = fileChooser.showSaveDialog(gui);

	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fileChooser.getSelectedFile();
	                
	                if (file.exists()) {
	                	//for safety, don't allow overwriting existing file (kids and all. . .)
	                	updateMessage("File " + file + " already exists...", true);
	                	return;
	                }
	                
	                try {
	                	FileWriter fw = new FileWriter(file);
		                fw.write(gameController.getBoard().toString());
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
						updateMessage("Unable to save file...", true);
					}
	                
	                updateMessage("Saved board to: " + file.getAbsolutePath(), false);
	            } 
	        }			
	    };
	}

	private AbstractAction getLoadAction() {
		return new AbstractAction("Load...") {
	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	if (!setupMode) {
	        		updateMessage("Must be in setup mode to load board...", true);
                	return;
	        	}
	        	
	        	int returnVal = fileChooser.showOpenDialog(gui);
	            if (returnVal != JFileChooser.APPROVE_OPTION) {
	            	return;
	            }
	            
                File file = fileChooser.getSelectedFile();
                
                if (!file.exists()) {
                	updateMessage("File " + file + " doesn't exist...", true);
                	return;
                }
                
                BoardModel board = new BoardModel();
                try {
                	FileReader fr = new FileReader(file);
                	BufferedReader br = new BufferedReader(fr);
                	int row = 0;
                	String line;
                	while ((line = br.readLine()) != null && row < 8) {
	                	if (line.trim().isEmpty()) {
	                		continue;
	                	}
	                	
	                	String[] ary = line.split("\\|");
	                	if (ary.length < 8) {
	                		updateMessage("File " + file + " has invalid content...", true);
		                	return;
	                	}
	                	
	                	for (int col = 0; col < 8; col++) {
							ChessPiece piece = ChessPiece.fromString(ary[col].trim());
							if (piece == null) {
								updateMessage("File " + file + " has invalid content...", true);
			                	return;
							}
							
							board.placePiece(new Cell(row, col), piece);
						}
	                	
	                	row++;
	                }
	                
					br.close();
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
					updateMessage("Unable to load file...", true);
				}
				
				clearBoard();
				gameController.setBoard(board);
				syncViewWithModel();
                updateMessage("Loaded board from: " + file.getAbsolutePath(), false);
	        }			
	    };
	}

	private AbstractAction getSolveForAction(Color color, String colorText) {
		return new AbstractAction("Solve for " + colorText) {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            solveFor(color);
	        }
	    };
	}

	private JSlider createSlider(int min, int max, int selected, int majorTicks) {
    	JSlider slider = new JSlider();
        slider.setMinimum(min);
        slider.setMaximum(max);
        slider.setValue(selected);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(majorTicks);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }
    
    public void resetBoardBackgroundColors() {
    	for (int row = 0; row < chessBoardSquares.length; row++) {
            for (int col = 0; col < chessBoardSquares[row].length; col++) {
            	ChessCellButton b = chessBoardSquares[row][col];
            	if ((col % 2 == 1 && row % 2 == 1)
                        || (col % 2 == 0 && row % 2 == 0)) {
                    b.setBackground(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                }
            }
        }
        
        gameController.highlightCheckCmSm();
    }

    public final JComponent getGui() {
        return gui;
    }
    
    public UiMoveState getMoveState() {
		return moveState;
	}
    
    public GameController getGameController() {
		return gameController;
	}

    private final void createImages() {
        try {
        	URL url = getClass().getClassLoader().getResource("resources/chess_pieces.png");
            BufferedImage bi = ImageIO.read(url);
            for (int ii = 0; ii < 2; ii++) {
                for (int jj = 0; jj < 6; jj++) {
                    chessPieceImages[ii][jj] = bi.getSubimage(
//*                            jj * 64, ii * 64, 48, 48);
                    		  jj * 64, ii * 64, 60, 60);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initializes the icons of the initial chess board piece places
     */
    private final void setupNewGame() {
        messageLabel.setText("Make your move");
        
        BoardModel board = new BoardModel();
        gameController = new GameController(board, this);
		gameController.setPlayerToMoveNext(Color.WHITE);
		gameController.getMoves().clear();
		resetBoardBackgroundColors();
		
		//black pieces
	    for (int col = 0; col < 8; col++) {
	    	board.placePiece((byte) 0, (byte) col, STARTING_ROW_BLACK_PIECES[col]);
	    	board.placePiece((byte) 1, (byte) col, ChessPiece.BLACK_PAWN);
    	}
    	
    	//white pieces
	    for (int col = 0; col < 8; col++) {
	    	board.placePiece((byte) 6, (byte) col, ChessPiece.WHITE_PAWN);
	    	board.placePiece((byte) 7, (byte) col, STARTING_ROW_WHITE_PIECES[col]);	    	
    	}
    	
    	syncViewWithModel();
    	updateMessage("", false);
    	
    	//toggle into game mode by default
    	toggleGameMode(true);
    }

	private void toggleGameMode(boolean toggle) {
		setupMode = !toggle;
		setupModeButton.setSelected(!toggle);
		gameModeButton.setSelected(toggle);
		enableOrDisableAllPopups(!toggle);
	}
    
	private final void clearBoard() {
		BoardModel board = new BoardModel();
        gameController = new GameController(board, this);
		gameController.setPlayerToMoveNext(Color.WHITE);
		gameController.getMoves().clear();
		resetBoardBackgroundColors();
		syncViewWithModel();
		updateMessage("", false);
		
		//toggle into setup-mode 
		setupMode = true;
		setupModeButton.setSelected(true);
		gameModeButton.setSelected(false);
    	toggleGameMode(false);
    }
	
	private final void undoMove() {
		if (moveState.isInProgress()) {
			updateMessage("complete your move first...", true);
			return; //don't do anything if in middle of a move
		}
		
		if (gameController.getLastMove() == null) {
			updateMessage("No moves to undo...", true);
			return;
		}
		
		gameController.undoLastMove();
		syncViewWithModel();
		resetBoardBackgroundColors();
	}
    
    public Image getImageForPiece(ChessPiece piece) {
     	switch (piece) {
			case BLACK_PAWN:
				return chessPieceImages[0][PAWN];
			case BLACK_KING:
				return chessPieceImages[0][KING];
			case BLACK_QUEEN:
				return chessPieceImages[0][QUEEN];
			case BLACK_ROOK:
				return chessPieceImages[0][ROOK];
			case BLACK_KNIGHT:
				return chessPieceImages[0][KNIGHT];
			case BLACK_BISHOP:
				return chessPieceImages[0][BISHOP];
			case WHITE_PAWN:
				return chessPieceImages[1][PAWN];
			case WHITE_KING:
				return chessPieceImages[1][KING];
			case WHITE_QUEEN:
				return chessPieceImages[1][QUEEN];
			case WHITE_ROOK:
				return chessPieceImages[1][ROOK];
			case WHITE_KNIGHT:
				return chessPieceImages[1][KNIGHT];
			case WHITE_BISHOP:
				return chessPieceImages[1][BISHOP];
			default:
				return null;
		}
    }
    
    public void updateMessage(String message, boolean isError) {
    	updateMessage(message, isError, false);
    }
    
    public void updateMessage(String message, boolean isError, boolean appendMode) {
    	if (appendMode) {
    		message = messageLabel.getText() + ", " + message; 
    	}
    	
    	messageLabel.setText(message);
    	messageLabel.setForeground(isError ? Color.RED : Color.BLACK);
    	if (isError) {
    		System.err.println(message);
    	} else {
    		System.out.println(message);
    	}
    }

    public static void main(String[] args) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                ChessGUI cg = new ChessGUI();

                JFrame f = new JFrame("Chess Challenge");
                f.add(cg.getGui());
                // Ensures JVM closes after frame(s) closed and
                // all non-daemon threads are finished
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                // See http://stackoverflow.com/a/7143398/418556 for demo.
                f.setLocationByPlatform(true);

                // ensures the frame is the minimum size it needs to be
                // in order display the components within it
                f.pack();
                // ensures the minimum size is enforced.
//                f.setMinimumSize(f.getSize());
                Dimension d = cg.getGui().getSize();
                d.setSize(1.1 * d.getWidth(), d.getHeight()); //widen the board a bit (message label gets chopped sometimes)
                f.setMinimumSize(d);
                f.setVisible(true);
            }
        };
        // Swing GUIs should be created and updated on the EDT
        // http://docs.oracle.com/javase/tutorial/uiswing/concurrency
        SwingUtilities.invokeLater(r);
    }
    
    public ImageIcon getMalky() {
    	return new ImageIcon(getClass().getClassLoader().getResource("resources/queen.jpg"));
	}
    
    public ChessCellButton getCellButton(int row, int col) {
    	return chessBoardSquares[row][col]; 
    }
    
    public void syncViewWithModel() {
    	BoardModel board = this.gameController.getBoard();
    	for (int row = 0; row < 8; row++) {
    		for (int col = 0; col < 8; col++) {
    			ChessCellButton b = chessBoardSquares[row][col];
    			ChessPiece piece = board.getPiece((byte) row, (byte) col);
    			if (piece.equals(ChessPiece.NO_PIECE)) {
    				b.setIcon(BLANK_SQUARE);
    				b.removePiece();
    			} else {
    				b.setIcon(new ImageIcon(getImageForPiece(piece)));
    				b.setPiece(piece);
    				
    				if (piece == ChessPiece.WHITE_QUEEN) {
    					b.setIcon(getMalky());
    				}
    			}
    		}
		}
    }
    
    private void enableOrDisableAllPopups(boolean enable) {
    	for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				chessBoardSquares[row][col].enablePopupMenu(enable);
			}
		}
    }
    
    public boolean isInSetupMode() {
    	return this.setupMode;
    }
    
    public boolean isInGameMode() {
    	return !this.setupMode;
    }
    
    private void solveFor(Color color) {
    	if (setupMode) {
    		updateMessage("Must be in game mode to solve...", true);
    		return;
    	}
    	
    	gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	boolean solved = false;
    	
    	//optimization: try to solve in least amount of moves first (if there's a shorter solution find it first)... 
    	for (int maxMoves = 1; maxMoves <= maxMovesToSolve; maxMoves++) {
			if (solveForInNmoves(color, maxMoves)) {
				solved = true;
				break;
			}
		}
    	
    	if (!solved) {
    		updateMessage(String.format("Unable to solve in %s move(s) in %s second(s)", maxMovesToSolve, maxSecondsToSolve), true);
    	}
    	
    	gui.setCursor(Cursor.getDefaultCursor());
    }
    
    private boolean solveForInNmoves(Color color, int maxMoves) {
    	System.out.println(String.format("==== Trying to solve in max %s moves =====", maxMovesToSolve));
    	
    	Solver solver = new Solver(gameController.getBoard(), color, gameController.getLastMove());
		if (solver.tryToSolveIn(maxMoves, maxSecondsToSolve)) {
			Move move = solver.getNextMoveIfSolved();
			gameController.applyMove(move);
			
			if (maxMovesToSolve > 1 && !MovesProcessor.isCheckMateOnColor(gameController.getBoard(), Utils.getOpponentColor(color), move)) {
				//subtract 1 from slider
				maxMovesSlider.setValue(maxMovesToSolve - 1);
			}
			
			syncViewWithModel();
			resetBoardBackgroundColors();
			solver.printCounters(maxMoves);
			return true;
		}
		
		solver.printCounters(maxMoves);
		return false;
    }
}