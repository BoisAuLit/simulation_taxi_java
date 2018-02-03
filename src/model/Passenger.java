package model;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * A Passenger always has a pickup location and a destination, these two
 * positions are never the same.
 * 
 * A Passenger can choose to take a Taxi or a Shuttle, if he decided to take a
 * Taxi, his Image is black on the map (the City model); otherwise, his Image is
 * red.
 * 
 * The choice of a Passenger is made randomly when a Passenger object is
 * created. (50% for the choice of Taxi, 50% for Shuttle)
 * 
 * A Passsenger gets angry when he waits too long, and disappears not long after
 * he gets angry. When a passenger gets angry, his Image is yellow on the map.
 * 
 * Attention, a Passenger doesn't disappear even he waits too long in the
 * following two situations:
 * 
 * 1. The passenger chose to take a Shuttle, he waits too long time for his
 * Shuttle to pick him up, when he is about to disappear (on the map), if he
 * knows that the Shuttle he requested is on the way to pick him up, he will
 * wait until the Shuttle arrives at his position to pick him up even if his
 * waiting limit exceeded.
 * 
 * 2. The passenger chose to take a Shuttle, he is already in the Shuttle, when
 * he has waited a long time and is about to disappear(in other words, get off),
 * if at this moment he knows that the Shuttle he takes is on the way to his
 * destination, he will still wait till the Shuttle arrives at his destination
 * and gets off.
 * 
 * (for more details, see the implementation of PassengerSource class and
 * Shuttle class.)
 * 
 * @author David J. Barnes and Michael Kolling. Modified Bohao LI.
 * @version 2017.03.23
 */
public class Passenger implements DrawableItem {
    // Dispears if this limit is attained.
    public static final int WAINTING_LIMIT = 40;
    // Image becomes yellow on the map is this limit is achieved.
    public static final int ANGRY_LIMIT = 30;

    private Location pickup;
    private Location destination;

    // When Passenger chooses to take a Taxi, his Image is black on the map.
    private final Image choose_taxi;
    // When Passenger chooses to take a Shuttle, his Image is red on the map.
    private final Image choose_shuttle;
    // When Passenger gets angry, his Image is red on the map.
    private final Image angry_person;

    private int waiting_time;

    // Passengers can choos to take a Taxi or a Shuttle.
    public enum Choice {
        TAXI, SHUTTLE;
    }

    private Choice choice;

    /**
     * Create a passenger, randomly make the decesion to take a Taxi or a
     * Shuttle.
     * 
     * @param pickup
     *            The pickup Location of the passenger.
     * @param destination
     *            The destination of the passenger.
     */
    public Passenger(Location pickup, Location destination) {
        // Randomly choose to take a Taxi or a Shuttle
        this(pickup, destination, Math.random() < .5 ? Choice.TAXI : Choice.SHUTTLE);
    }

    /**
     * Create a passenger with a choice.
     * 
     * @param pickup
     *            The pickup location of the passenger.
     * @param destination
     *            The destination of the passenger.
     * @param choice
     *            The choice of the passenger
     */
    protected Passenger(Location pickup, Location destination, Choice choice) {
        if (pickup == null)
            throw new NullPointerException("Pickup location");
        if (destination == null)
            throw new NullPointerException("Destination location");

        this.pickup = pickup;
        this.destination = destination;
        choose_taxi = new ImageIcon(getClass().getResource("/images/person.jpg")).getImage();
        choose_shuttle = new ImageIcon(getClass().getResource("/images/red_person.jpg")).getImage();
        angry_person = new ImageIcon(getClass().getResource("/images/angry_person.jpg")).getImage();
        this.choice = choice;
    }

    /**
     * @return The choice of the passenger.
     */
    public final Choice getChoice() {
        return choice;
    }

    /**
     * @return The number of people associated with one passenger, here 1. in
     *         subclass of Passenger --> PassengerSource, this number is bigger
     *         than 1.
     */
    public int getNb_persons() {
        return 1;
    }

    /**
     * @return A string representation of this person.
     */
    @Override
    public String toString() {
        return "Passenger travelling from " + pickup + " to " + destination;
    }

    /**
     * @return The image to be displayed on a GUI.
     */
    public Image getImage() {
        if (isAngry())
            return angry_person;
        return choice == Choice.TAXI ? choose_taxi : choose_shuttle;
    }

    /**
     * @return If the waiting time is exceeded, the passenger becomes angry, and
     *         in the city GUI, the image of the passenger becomes yellow to
     *         show that he/she is angry.
     */
    protected boolean isAngry() {
        return waiting_time >= ANGRY_LIMIT;
    }

    /**
     * @return The passenger's pickup location.
     */
    public Location getLocation() {
        return pickup;
    }

    /**
     * @return The pickup location.
     */
    public Location getPickupLocation() {
        return pickup;
    }

    /**
     * @return The destination location.
     */
    public Location getDestination() {
        return destination;
    }

    /**
     * @return True if the passenger waits too long; otherwise false.
     */
    public boolean waitingTooLong() {
        return waiting_time >= WAINTING_LIMIT;
    }

    /**
     * Increment the waiting time of the passenger.
     */
    public void incrementWaitingTime() {
        waiting_time++;
    }

    /**
     * Reset the waiting time of the passenger to zero. This method is used when
     * a passenger gets on a Shuttle(but not a Taxi).
     */
    public void resetWaitingTime() {
        waiting_time = 0;
    }
}
