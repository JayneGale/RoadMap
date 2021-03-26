import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is a small example class to demonstrate extending the GUI class and
 * implementing the abstract methods. Instead of doing anything maps-related, it
 * draws some squares to the drawing area which are removed when clicked. Some
 * information is given in the text area, and pressing any of the navigation
 * buttons makes a new set of squares.
 * 
 * @author tony
 */
public class SquaresExample extends GUI {
	private static final int NUM_SQUARES = 10;
	private static final int SQUARE_SIZE = 30;

	private final Random random = new Random();
	private final List<Square> squares = new ArrayList<Square>();

	public SquaresExample() {
		makeSquares();
	}

	private void makeSquares() {
		squares.clear();

		for (int i = 0; i < NUM_SQUARES; i++) {
			squares.add(new Square(random, getDrawingAreaDimension()));
		}

		getTextOutputArea().setText("new squares created.");
	}

	@Override
	protected void redraw(Graphics g) {
		for (Square s : squares)
			s.draw(g);
	}

	@Override
	protected void onClick(MouseEvent e) {
		/*
		 * we search from the back to the front of the list (while drawing
		 * happens front-to-back) so that we always remove the top square if
		 * there are any overlapping. this is why we use a list and not a set to
		 * store the squares in the first place.
		 */
		int i = squares.size();
		while (i --> 0) {
			if (squares.get(i).contains(e.getX(), e.getY())) {
				squares.remove(i);
				break;
			}
		}

		getTextOutputArea().append("\nsquares remaining: " + squares.size());
	}

	@Override
	protected void onSearch() {
		getTextOutputArea().setText(getSearchBox().getText());
	}

	@Override
	protected void onMove(Move m) {
		makeSquares();
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		getTextOutputArea().setText("example doesn't load any files.");
	}

	/**
	 * A simple inner class that stores the data for the squares and has some
	 * helper methods.
	 */
	private static class Square {
		public final int x, y;
		public final Color color;

		public Square(Random random, Dimension area) {
			x = random.nextInt(area.width - SQUARE_SIZE);
			y = random.nextInt(area.height - SQUARE_SIZE);
			color = new Color(random.nextInt());
		}

		public void draw(Graphics g) {
			g.setColor(color);
			g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
		}

		public boolean contains(int x, int y) {
			return x > this.x && y > this.y && x < this.x + SQUARE_SIZE
					&& y < this.y + SQUARE_SIZE;
		}
	}

	public static void main(String[] args) {
		new SquaresExample();
	}
}

// code for COMP261 assignments