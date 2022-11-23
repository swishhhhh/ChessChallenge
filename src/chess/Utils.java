package chess;

import java.awt.Color;

public class Utils {
	public static boolean isOutOfBounds(int row, int col) {
		return row < 0 || row > 7 || col < 0 || col > 7;
	}
	
	public static Color getOpponentColor(Color playerColor) {
		if (playerColor.equals(Color.WHITE)) return Color.BLACK;
		
		if (playerColor.equals(Color.BLACK)) return Color.WHITE;
		
		throw new IllegalArgumentException("invalid color " + playerColor);
	}
}
