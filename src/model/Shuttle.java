package model;
import java.util.List;
import java.util.LinkedList;
    
/**
 * A shuttle is able to carry multiple passengers.
 * This implementation is non-functional.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A. Morelle
 * @version 2013.12.30
 */
public class Shuttle extends Vehicle
{
    // The maximum number of passengers for this shuttle.
    private final int MAXPASSENGERS;
    // The list of destinations for the shuttle.
    private List<Location> destinations;
    // The list of passengers on the shuttle.
    private List<Passenger> passengers;

    /**
     * Constructor for objects of class Shuttle
     * @param maxPassengers The max number of passengers. Must be positive.
     * @param company The taxi company. Must not be null.
     * @param location The vehicle's starting point. Must not be null.
     * @throws NullPointerException If company or location is null.
     */
    public Shuttle(int maxPassengers, TaxiCompany company, Location location)
    {
        super(company, location);
        MAXPASSENGERS = maxPassengers;
        destinations = new LinkedList<>();
        passengers = new LinkedList<>();
    }

    /**
     * Carry out a shuttle's actions.
     */
    public void act()
    {
    }

    /**
     * @return Whether or not this vehicle is free.
     */
    public boolean isFree()
    {
        return true;
    }
    
    /**
     * Receive a pickup location.
     * @location The pickup location.
     */
    public void setPickupLocation(Location location)
    {
        destinations.add(location);
        chooseTargetLocation();
    }
    
    /**
     * Receive a passenger.
     * Add the destination to the list.
     * @param passenger The passenger.
     */
    public void pickup(Passenger passenger)
    {
        passengers.add(passenger);
        destinations.add(passenger.getDestination());
        chooseTargetLocation();
    }

    /**
     * Decide where to go next, based on the list of
     * possible destinations.
     */
    private void chooseTargetLocation()
    {
    }

    /**
     * Offload all passengers whose destination is the
     * current location.
     */
    public void offloadPassenger()
    {
    }

	/**
	 * @return The maximum number of passengers for this shuttle
	 */
    public int getMaxPassengers() {
		return MAXPASSENGERS;
	}
    
    
}
