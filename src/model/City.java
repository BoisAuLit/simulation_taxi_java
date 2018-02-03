package model;
import java.util.LinkedList;
import java.util.List;
    
/**
 * A collection of items in the city.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A.Morelle
 * @version 2013.12.30
 */
public class City
{
    private List<Item> items;
    private int width;
    private int height;
    
    private static final int DEFAULT_WIDTH = 35;
    private static final int DEFAULT_HEIGHT = 35;

    /**
     * Constructor for objects of class City
     * @param width The city's width.
     * @param height The city's height.
     */
    public City(int width, int height)
    {
        if(width < 1) {
            throw new IllegalArgumentException(
                        "Width must be positive: " +
                        width);
        }
        if(height < 1) {
            throw new IllegalArgumentException(
                        "Height must be positive: " +
                        height);
        }
        this.width = width;
        this.height = height;
        items = new LinkedList<Item>();
    }
    
    /**
     * Create a city of default size.
     */
    public City()
    {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Add the given item to the city's collection.
     * @param item The item to be added.
     */
    public void addItem(Item item)
    {
        if(items.contains(item)) {
            throw new IllegalArgumentException(
                item + " already recorded in the city.");
        }
        items.add(item);
    }

    /**
	 * @return the items
	 */
	public List<Item> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<Item> items) {
		this.items = items;
	}

	/**
     * Remove the given item from the city's collection.
     * @param item The item to be removed.
     */
    public void removeItem(Item item)
    {
        if(!items.remove(item)) {
            throw new IllegalArgumentException(
                        item + " is not in the city.");
        }
    }
        
    /**
     * @return A string representation of the city.
     */
    @Override
    public String toString()
    {
        return "City size " + width + " by " + height;
    }
    
    /**
     * @return The width.
     */
    public int getWidth()
    {
        return width;
    }
    
    /**
     * @return The height.
     */
    public int getHeight()
    {
        return height;
    }
}
