package model;

/**
 * Model a location in a city.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A. Morelle. Modified
 *         Bohao LI.
 * @version 2017.03.23
 */
public class Location {

    // (0,0)-----> x (width)
    // |
    // |
    // y (height)
    private int x;
    private int y;

    /**
     * Model a location in the city.
     * 
     * @param x
     *            The x coordinate. Must be positive.
     * @param y
     *            The y coordinate. Must be positive.
     * @throws IllegalArgumentException
     *             If a coordinate is negative.
     */
    public Location(int x, int y) {
        if (x < 0)
            throw new IllegalArgumentException("Negative x-coordinate: " + x);
        if (y < 0)
            throw new IllegalArgumentException("Negative y-coordinate: " + y);

        this.x = x;
        this.y = y;
    }

    /**
     * Decide where to go next.
     * 
     * @param destination
     *            The destination according to which we can calculate the next
     *            Location.
     * @return The next Location
     */
    public Location nextLocation(Location destination) {
        int destX = destination.getX();
        int destY = destination.getY();
        int offsetX = x > destX ? -1 : x < destX ? 1 : 0;
        int offsetY = y > destY ? -1 : y < destY ? 1 : 0;

        if ((offsetX != 0) || (offsetY != 0))
            return new Location(x + offsetX, y + offsetY);
        return destination;
    }

    /**
     * Determine the number of movements required to get from here to the
     * destination.
     * 
     * @param destination
     *            The required destination.
     * @return The number of movement steps.
     */
    public int distance(Location destination) {
        int xDist = Math.abs(destination.getX() - x);
        int yDist = Math.abs(destination.getY() - y);

        return Math.max(xDist, yDist);
    }

    /**
     * Used by a hashed structure (HashMap, HashSet, Hashtable, etc) to turn an
     * object of this class to a key (an integer number)
     * 
     * @return The hash code associated with the current object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (x ^ (x >>> 8));
        result = prime * result + (int) (y ^ (y >>> 16));

        return result;
    }

    // Here we did not override the method equals(), we did it deliberately,
    // because: when Location is
    // used as a key of a hashed structure (like HashMap, Hashset, Hashtable,
    // etc), we need the behavior
    // of the default version of equals() inherited from Object class. (see the
    // implementation of PassengerSource
    // for furthur details)
    // We provide another methond called isEqualTo() in this class to do the job
    // of the overriden equals()

    /**
     * @return A representation of the location.
     */
    @Override
    public String toString() {
        return "location " + String.format("%3d, %3d", x, y);
    }

    /**
     * Implement content equality for locations.
     * 
     * @return true if this location matches the other, false otherwise.
     */
    public boolean isEqualTo(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Location))
            return false;
        Location other = (Location) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

    /**
     * @return The x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * @return The y coordinate.
     */
    public int getY() {
        return y;
    }
}
