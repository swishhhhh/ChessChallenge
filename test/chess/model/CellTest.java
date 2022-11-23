package chess.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CellTest {

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidRowLow() { new Cell(-1, 0); }
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidRowHigh() { new Cell(8, 0); }
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidColLow() { new Cell(0, -1); }
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidColHigh() { new Cell(0, 8); }
	
	@Test
	public void testGetLabel() {
		assertEquals("A8", new Cell(0, 0).getLabel());
		assertEquals("H8", new Cell(0, 7).getLabel());
		assertEquals("A1", new Cell(7, 0).getLabel());
		assertEquals("H1", new Cell(7, 7).getLabel());
	}
}
