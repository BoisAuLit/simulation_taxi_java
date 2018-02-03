package model;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

/**
 * Model the operation of a taxi company, operating different
 * types of vehicle. This version operates a only taxis.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A. Morelle
 * @version 2013.12.30
 */
public class TaxiCompany  
{
    private static final int NUMBER_OF_TAXIS = 3;

	// The vehicles operated by the company.
    private List<Vehicle> vehicles;
    
    private City city;
    
    // The associations between vehicles and the passengers
    // they are to pick up.
    private Map<Vehicle, Passenger> assignments;
    
    /**
     * @param city The city.
     */
    public TaxiCompany(City city)
    {
        this.city = city;
        vehicles = new LinkedList<>();
        assignments = new HashMap<>();
        setupVehicles();
    }

    /**
     * Request a pickup for the given passenger.
     * @param passenger The passenger requesting a pickup.
     * @return Whether a free vehicle is available.
     */
    public boolean requestPickup(Passenger passenger)
    {
        Vehicle vehicle = scheduleVehicle();
        if(vehicle != null) {
            assignments.put(vehicle, passenger);
            vehicle.setPickupLocation(passenger.getPickupLocation());
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * A vehicle has arrived at a pickup point
     * (where a passenger is supposed to be waiting).
     * @param The vehicle at the pickup point.
     */
    public void arrivedAtPickup(Vehicle vehicle) 
    {
        Passenger passenger = (Passenger) assignments.remove(vehicle);
        city.removeItem(passenger);
        vehicle.pickup(passenger);
    }
    
    /**
     * A vehicle has arrived at a passenger's destination.
     * @param The vehicle at the destination.
     * @param The passenger being dropped off.
     */
    public void arrivedAtDestination(Vehicle vehicle,
                                     Passenger passenger)
    {
    }
    
    /**
     * @return The list of vehicles.
     */
    public List<Vehicle> getVehicles()
    {
        return vehicles;
    }
    
    /**
     * Find a free vehicle, if any.
     * @return A free vehicle, or null if there is none.
     */
    private Vehicle scheduleVehicle()
    {
        for(Vehicle vehicle : vehicles) {
        	if(vehicle.isFree()) {
                return vehicle;
            }
        }
        
        return null;
    }

    /**
     * Set up this company's vehicles. The optimum number of
     * vehicles should be determined by analysis of the
     * data gathered from the simulation.
     *
     * Vehicles start at random locations.
     */
    private void setupVehicles()
    {
        int cityWidth = city.getWidth();
        int cityHeight = city.getHeight();
        
        // Use a fixed random seed for predictable behavior.
        // Or use different seeds for less predictable behavior.
        Random rand = new Random(12345);

        // Create the taxis.
        for(int i = 0; i < NUMBER_OF_TAXIS; i++){
            Taxi taxi =
                new Taxi(this,
                         new Location(rand.nextInt(cityWidth),
                                      rand.nextInt(cityHeight)));
            vehicles.add(taxi);
            city.addItem(taxi);
        }
   }
}
