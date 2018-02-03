package model;
/**
 * Model a location in a city.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A. Morelle
 * @version 2013.12.30
 */
public class Location
{
	// (0,0)-----> x (width)
	//   |
	//   |
	//	 y (height)
    private int x;	 
    private int y;	

    /**
     * Model a location in the city.
     * @param x The x coordinate. Must be positive.
     * @param y The y coordinate. Must be positive.
     * @throws IllegalArgumentException If a coordinate is negative.
     */
    public Location(int x, int y)
    {
        if(x < 0) {
            throw new IllegalArgumentException(
                        "Negative x-coordinate: " + x);
        }
        if(y < 0) {
            throw new IllegalArgumentException(
                        "Negative y-coordinate: " + y);
        }
        this.x = x;
        this.y = y;
    }
    
    /**
     * Generate the next location to visit in order to
     * reach the destination.
     * @param destination Where we want to get to.
     * @return The next location in a direct line from this to
     *         destination; or destination if already arrived.
     */
    public Location nextLocation(Location destination)
    {
        // TO DO - To implement
        throw new UnsupportedOperationException("nextLocation() not implemented");
    }

    /**
     * Determine the number of movements required to get
     * from here to the destination.
     * @param destination The required destination.
     * @return The number of movement steps.
     */
    public int distance(Location destination)
    {
        int xDist = Math.abs(destination.getX() - x);
        int yDist = Math.abs(destination.getY() - y);
        return Math.max(xDist, yDist);
    }
    
    /**
     * Implement content equality for locations.
     * @return true if this location matches the other,
     *         false otherwise.
     */
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof Location) {
            Location otherLocation = (Location) other;
            return x == otherLocation.getX() &&
                   y == otherLocation.getY();
        }
        else {
            return false;
        }
    }
    
    /**
     * Implement a hashcode coherent with the equals method
     * @return A hashcode of this location
     */
    @Override
    public int hashCode() {
    	final int prime = 31;
        int result = 1;
        result = prime * result + (int)(x ^ (x >>> 8));
        result = prime * result + (int)(y ^ (y >>> 16));
    	return result;
    }
    
    /**
     * @return A representation of the location.
     */
    @Override
    public String toString()
    {
        return "location " + x + "," + y;
    }

    /**
     * @return The x coordinate.
     */
    public int getX()
    {
        return x;
    }

    /**
     * @return The y coordinate.
     */
    public int getY()
    {
        return y;
    }
}
