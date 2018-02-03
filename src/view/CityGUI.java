package view;

import java.util.List;
import java.awt.*;

import javax.swing.*;

import model.Actor;
import model.City;
import model.DrawableItem;
import model.Item;
import model.Location;

/**
 * Provide a view of the vehicles and passengers in the city.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A. Morelle
 * @version 2013.12.30
 */
public class CityGUI extends JFrame implements Actor 
{	
	private static final long serialVersionUID = 20131230;

	private City city;
	private CityView cityView;

	/**
	 * Constructor for objects of class CityGUI
	 * 
	 * @param city : the city whose state is to be displayed.
	 */
	public CityGUI(City city) {
		
		// Create and set up the window
		super("Simulation of taxis operating on a city grid");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Init attributes
		this.city = city;
		cityView = new CityView(city.getWidth(), city.getHeight());
		
		// Create and set up the content pane
		createContentPane(); 
		
		// Size and display this frame
		displayGUI();
	}
	
	/**
	 * Create and set up the content pane 
	 */
	private void createContentPane() {
		getContentPane().add(cityView, BorderLayout.CENTER);
	}
	
	/**
	 * Size and display this frame
	 */
	private void displayGUI() {
		// Set up an initial size (90% of the screen height)
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int frameHeight = (int) (screenSize.height * 0.90); 
		int frameWidth = frameHeight;
		setPreferredSize(new Dimension(frameWidth, frameHeight));
		// Size this window to fit the preferred size and layouts 
		// of its components
		pack();
		// Center on the screen
		setLocationRelativeTo(null);
		// Display
		setVisible(true);
	}

	/**
	 * Display the current state of the city.
	 */
	public void act() {
		
		cityView.preparePaint();

		List<Item> items = city.getItems();
		for (Item item : items) {
			if (item instanceof DrawableItem) {
				DrawableItem it = (DrawableItem) item;
				Location location = it.getLocation();
				cityView.drawImage(location.getX(), location.getY(),
						it.getImage());
			}
		}

		cityView.repaint();
	}
	

	/*************************************************************************/

	/**
	 * Provide a graphical view of a rectangular city. This is a nested class (a
	 * class defined inside a class) which defines a custom component for the
	 * user interface. This component displays the city. This is rather advanced
	 * GUI stuff - you can ignore this for your project if you like.
	 */
	private class CityView extends JPanel 
	{
		static final long serialVersionUID = 20131230;

		private final int VIEW_SCALING_FACTOR = 10;

		private int cityWidth, cityHeight;
		private int xScale, yScale; // panel size / city size
		private Dimension size; 	// size of this panel
		private Graphics g;
		private Image cityImage;

		/**
		 * Create a new CityView component.
		 */
		public CityView(int cityWidth, int cityHeight) {
			this.cityWidth = cityWidth;
			this.cityHeight = cityHeight;
			setBackground(Color.white);
			size = new Dimension(0, 0);
		}

		public void preparePaint() {
			// If the size has changed...
			if (!size.equals(getSize())) {
				size = getSize();
				cityImage = cityView.createImage(size.width, size.height);
				g = cityImage.getGraphics();

				xScale = size.width / cityWidth;
				if (xScale < 1) {
					xScale = VIEW_SCALING_FACTOR;
				}
				yScale = size.height / cityHeight;
				if (yScale < 1) {
					yScale = VIEW_SCALING_FACTOR;
				}
			}

			// Draw the grid
			g.setColor(Color.white);
			g.fillRect(0, 0, size.width, size.height);
			g.setColor(Color.gray);
			for (int i = 0, x = 0; x < size.width; i++, x = i * xScale) {
				g.drawLine(x, 0, x, size.height - 1);
			}
			for (int i = 0, y = 0; y < size.height; i++, y = i * yScale) {
				g.drawLine(0, y, size.width - 1, y);
			}
		}

		/**
		 * Draw the image for a particular item.
		 */
		public void drawImage(int x, int y, Image image) {
			g.drawImage(image, x * xScale + 1, y * yScale + 1, 
					xScale - 1,	yScale - 1, this);
		}

		/**
		 * The city view component needs to be redisplayed. Copy the internal
		 * image to screen.
		 */
		@Override
		public void paintComponent(Graphics g) {
			if (cityImage != null) {
				g.drawImage(cityImage, 0, 0, null);
			}
		}

	} // End internal class CityView

	/*************************************************************************/

} // End class CityGUI
