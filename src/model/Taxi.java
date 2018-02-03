package model;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * A Taxi is able to carry a single passenger. A Taxi cannot receive requests
 * when it is headed for a pickup Location or when it is already carrying a
 * Passenger.
 * 
 * @author David J. Barnes and Michael Kolling. Modified Bohao LI.
 * @version 2017.03.23
 */
public class Taxi extends Vehicle implements DrawableItem {

    private Passenger passenger;

    // Maintain separate images for when the taxi is empty
    // and full.
    private Image emptyImage;
    private Image passengerImage;

    /**
     * Constructor for objects of class Taxi
     * 
     * @param company
     *            The taxi company. Must not be null.
     * @param location
     *            The vehicle's starting point. Must not be null.
     * @throws NullPointerException
     *             If company or location is null.
     */
    public Taxi(TaxiCompany company, Location location, String id) {
        super(company, location, id);
        // Load the two images.
        emptyImage = new ImageIcon(getClass().getResource("/images/taxi.jpg")).getImage();
        passengerImage = new ImageIcon(getClass().getResource("/images/taxi+person.jpg")).getImage();
    }

    /**
     * Move towards the target location if we have one. Otherwise record that we
     * are idle.
     */
    public void act() {
        Location target = getTargetLocation();
        if (target != null) {
            // Find where to move to next.
            Location next = getLocation().nextLocation(target);
            setLocation(next);
            if (next.isEqualTo(target)) {
                if (passenger != null) {
                    notifyPassengerArrival(passenger);
                    incrementNbSuccess();
                    offloadPassenger();
                } else {
                    getCompany().getPassengerSource().decrementPassengersOnMap(1);
                    notifyPickupArrival();
                }
            }
        } else
            incrementIdleCount();
    }

    /**
     * Show all the information relevant to the current Taxi.
     */
    @Override
    public void showStatus() {
        System.out.print("\t" + this);
        if (passenger == null) {
            if (getTargetLocation() != null)
                System.out.println(", On the way to pickup passenger");
            else
                System.out.println(", Idle");
        } else
            System.out.println(", Carrying passenger");
    }

    @Override
    public int getCapacity() {
        return 1;
    }

    /**
     * @return Whether or not this taxi is free.
     */
    public boolean isFree() {
        return getTargetLocation() == null && passenger == null;
    }

    /**
     * Receive a pickup location. This becomes the target location.
     * 
     * @location The pickup location.
     */
    public synchronized void setPickupLocation(Location location) {
        setTargetLocation(location);
    }

    /**
     * Receive a passenger. Set their destination as the target location.
     * 
     * @param passenger
     *            The passenger.
     */
    public void pickup(Passenger passenger) {
        this.passenger = passenger;
        setTargetLocation(passenger.getDestination());
    }

    /**
     * Offload the passenger.
     */
    public void offloadPassenger() {
        passenger = null;
        clearTargetLocation();
    }

    /**
     * Return an image that describes our state: either empty or carrying a
     * passenger.
     */
    @Override
    public Image getImage() {
        return (passenger != null) ? passengerImage : emptyImage;
    }

    /**
     * @return A string representation of the taxi.
     */
    @Override
    public String toString() {
        return "Taxi at (" + getLocation() + "), Id = " + getID();
    }
}
