package model;
import java.util.Random;

/**
 * Periodically generate passengers.
 * Keep track of the number of passengers for whom
 * a vehicle cannot be found.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A.Morelle
 * @version 2013.12.30
 */
public class PassengerSource implements Actor
{
    private static final double CREATION_PROBABILITY = 0.06;
	
    private City city;
    private TaxiCompany company;
    private Random rand;
    private int missedPickups;

    /**
     * Constructor for objects of class PassengerSource.
     * @param company The company to be used. Must not be null.
     * @throws NullPointerException if company is null.
     */
    public PassengerSource(City city, TaxiCompany company)
    {
        if(city == null) {
            throw new NullPointerException("city");
        }
        if(company == null) {
            throw new NullPointerException("company");
        }
        this.city = city;
        this.company = company;
        // Use a fixed random seed for repeatable effects.
        // Example for test: rand = new Random(12345);
        rand = new Random();
        missedPickups = 0;
    }

    /**
     * Randomly generate a new passenger.
     * Keep a count of missed pickups.
     */
    public void act()
    {
        if(rand.nextDouble() <= CREATION_PROBABILITY) {
            Passenger passenger = createPassenger();
            if(company.requestPickup(passenger)) {
                city.addItem(passenger);
            }
            else {
                missedPickups++;
            }
        }
    }

    /**
     * @return The number of passengers for whom a pickup
     *         could not be found.
     */
    public int getMissedPickups()
    {
        return missedPickups;
    }

    /**
     * Create a new passenger with distinct pickup and
     * destination locations.
     * @return The created passenger.
     */
    private Passenger createPassenger()
    {
        int cityWidth = city.getWidth();
        int cityHeight = city.getHeight();

        Location pickupLocation =
                    new Location(rand.nextInt(cityWidth),
                                 rand.nextInt(cityHeight));
        Location destination;
        do{
            destination =
                    new Location(rand.nextInt(cityWidth),
                                 rand.nextInt(cityHeight));
        } while(pickupLocation.equals(destination));
        return new Passenger(pickupLocation, destination);
    }
}
