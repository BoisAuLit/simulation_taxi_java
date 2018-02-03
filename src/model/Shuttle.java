package model;

import java.awt.Image;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.swing.ImageIcon;

/**
 * A shuttle is able to carry multiple passengers.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A. Morelle. Modified
 *         Bohao LI
 * @version 2017.03.23
 */
public class Shuttle extends Vehicle implements DrawableItem {

    private static final class NbRequestsNegativeException extends Exception {
        private static final long serialVersionUID = 1L;
        @SuppressWarnings("unused")
        private String msg;

        private NbRequestsNegativeException(String msg) {
            this.msg = msg;
        }
    }

    private static final class NbPassengersNegativeException extends Exception {
        private static final long serialVersionUID = 1L;
        @SuppressWarnings("unused")
        private String msg;

        private NbPassengersNegativeException(String msg) {
            this.msg = msg;
        }
    }

    private static final int CAPACITY_MIN = 10;
    private static final int CAPACITY_MAX = 20;

    private final int capacity;

    private Map<Location, Passenger> passengers;
    private Map<Location, Passenger> requests;

    private Image emptyImage;
    private Image passengerImage;

    private int nb_requests;
    private int nb_passengers;

    /**
     * Create a Shuttle.
     * 
     * @param company
     *            The TaxiCompnay that the current Shuttle belongs to.
     * @param location
     *            The birth Location of the Shuttle on the map.
     * @param id
     *            The ID of the Shuttle.
     */
    public Shuttle(TaxiCompany company, Location location, String id) {
        super(company, location, id);
        capacity = new Random().nextInt(CAPACITY_MAX + 1 - CAPACITY_MIN) + CAPACITY_MIN;
        passengers = new HashMap<>();
        requests = new HashMap<>();
        emptyImage = new ImageIcon(getClass().getResource("/images/bus.jpg")).getImage();
        passengerImage = new ImageIcon(getClass().getResource("/images/bus+persons.jpg")).getImage();
    }

    public void act() {
        // showStatus();
        Location target = getTargetLocation();

        if (target == null) {
            if (passengers.isEmpty() && requests.isEmpty()) {
                incrementIdleCount();
                return;
            }
            setTargetLocation(target = nearestDestination());
        }

        Location next = getLocation().nextLocation(target);
        setLocation(next);

        // If the current Shuttle arrived at its target location (can be a
        // pickup location
        // or one of the destinations of its Passengers or PassengerGroups).
        if (next.isEqualTo(target)) {
            PassengerSource ps = getCompany().getPassengerSource();

            // Handle offloads
            for (Iterator<Map.Entry<Location, Passenger>> it = passengers.entrySet().iterator(); it.hasNext();) {
                Map.Entry<Location, Passenger> entry = it.next();
                if (target.isEqualTo(entry.getKey())) {
                    System.out.println(entry.getValue() + " arrived at destination");
                    incrementNbSuccess();
                    int nb_persons = entry.getValue().getNb_persons();
                    it.remove();
                    ps.decrementPassengersInShuttle(nb_persons);
                    decrementNb_passengers(nb_persons);
                }
            }

            // Handle pickup
            for (Iterator<Map.Entry<Location, Passenger>> it = requests.entrySet().iterator(); it.hasNext();) {
                Map.Entry<Location, Passenger> entry = it.next();
                Passenger passenger = entry.getValue();
                if (target.isEqualTo(entry.getKey())) {
                    System.out.println(this + "pick up " + passenger);
                    int nb_persons = entry.getValue().getNb_persons();
                    it.remove();
                    ps.decrementPassengersOnMap(nb_persons);
                    ps.incrementPassengersInShuttle(nb_persons);
                    passenger.resetWaitingTime();
                    passengers.put(passenger.getDestination(), passenger);
                    decrementNb_requests(nb_persons);
                    incremetNb_passengers(nb_persons);
                    notifyPickupArrival();
                }
            }

            // Find the next target Location
            Location nearest = nearestDestination();
            if (nearest == null) {
                clearTargetLocation();
                return;
            }
            setTargetLocation(nearest);
        }
    }

    /**
     * Show all the information relevant to the current Shuttle.
     */
    @Override
    public void showStatus() {
        System.out.format("\t%s" + ", Requests: %2d" + ", Passengers: %2d" + ", Capacity: %2d\n", this, nb_requests,
                nb_passengers, capacity);
    }

    /**
     * @return A percentage showing the availability of the current Shuttle
     */
    public double restCapacity() {
        return 1 - (nb_passengers + nb_requests) / ((double) capacity);
    }

    /**
     * Increment the number of requests of the current Shuttle. This method is
     * called when a request of pickup is made by a PassengerSource and the
     * Shuttle is available for the given pickup.
     * 
     * @param nb
     *            The number used to increment.
     */
    public void incrementNb_requests(int nb) {
        nb_requests += nb;
    }

    /**
     * Decrement the number of requests of the current Shuttle. This method is
     * called in either of the two situations below: 1. When a Passenger or a
     * PassengerGroup waits too long for a pickup on the map and disappears 2.
     * When a Passenger or a PassengerGroup is picked up by the current Shuttle.
     * 
     * @param nb
     *            The number used to decrement.
     */
    public void decrementNb_requests(int nb) {
        try {
            if (nb_requests - nb < 0)
                throw new NbRequestsNegativeException("negative number of requests");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        nb_requests -= nb;
    }

    /**
     * Increment the number of passengers of the current Shuttle. This method is
     * called when a Passenger or a PassengerGroup is picked up by the current
     * Shuttle.
     * 
     * @param nb
     *            The number of persons used to increment.
     */
    public void incremetNb_passengers(int nb) {
        nb_passengers += nb;
    }

    /**
     * Decrement the number of passengers of the current Shuttle. This method is
     * called when a Passenger or a PassengerGroup is offloaded, or he/they
     * waits so long that he/they disappear.
     * 
     * @param nb
     *            The number of persons used to decrement.
     */
    public void decrementNb_passengers(int nb) {
        try {
            if (nb_passengers - nb < 0)
                throw new NbPassengersNegativeException("negative of passengers negatif");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        nb_passengers -= nb;
    }

    /**
     * @return The maximum number of persons that the current Shuttle can take.
     */
    @Override
    public int getCapacity() {
        return capacity;
    }

    /**
     * Recieve a request from a Passenger or a PassengerGroup.
     * 
     * @param passenger
     *            The Passenger or PassengerGroup that makes the request
     */
    public void receiveRequest(Passenger passenger) {
        requests.put(passenger.getLocation(), passenger);
        incrementNb_requests(passenger.getNb_persons());
    }

    /**
     * @param passengerGroup
     *            The PassengerGroup to determine wether a pickup request be
     *            accepted by the current Shuttle
     * @return True if the current Shuttle can receive a request or pickup from
     *         a PassengerGroup; otherwise false
     */
    public boolean canReceiveGroupRequest(PassengerGroup passengerGroup) {
        return nb_passengers + nb_requests + passengerGroup.getNb_persons() <= capacity;
    }

    /**
     * Remove all the requests of the Passenger or PassengerGroup at the give
     * Location. This method is called when the current Shuttle arrives at a
     * pickup Location, the Passenger or the PassengerGroup at that pickup
     * Location is removed from the request list of the current Shuttle and is
     * added to the list of passengers of the current Shuttle.
     * 
     * @param location
     *            The Location where a pickup happens.
     */
    public void removeFromRequestList(Location location) {
        for (Iterator<Map.Entry<Location, Passenger>> it = requests.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Location, Passenger> entry = it.next();

            if (location.isEqualTo(entry.getKey())) {
                it.remove();
                decrementNb_requests(entry.getValue().getNb_persons());
            }
        }
    }

    /**
     * Check the waiting time of every Passenger or PassengerGroup in the
     * Shuttle. If a Passenger or a PassengerGroup in the Shuttle has/have
     * waited too long for the current Shuttle to take him/them to his/their
     * destination, two situations are possible: 1. If he/they find(s) out that
     * at the moment the current Shuttle is on the way to his/their destination,
     * then he/they continue(s) to wait util the current Shuttle arrives at
     * his/their destination. 2. If he/they find(s) out that at the moment the
     * current Shuttle is not on the way to his/their destination, then he just
     * disappears. (you can think of that he/they is/are so angry that they get
     * off the Shuttle without arriving at his/their final destination.)
     */
    public void checkWaitingTime() {
        PassengerSource source = getCompany().getPassengerSource();
        incrementWaitingTime();
        Passenger passenger;
        for (Iterator<Map.Entry<Location, Passenger>> it = passengers.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Location, Passenger> entry = it.next();
            if ((passenger = entry.getValue()).waitingTooLong())
                if (!getTargetLocation().isEqualTo(passenger.getDestination())) {
                    System.out.println(passenger + " waiting too long in shuttle " + "and gets off (disappeared)");
                    it.remove();
                    int nb_persons = entry.getValue().getNb_persons();
                    source.decrementPassengersInShuttle(nb_persons);
                    decrementNb_passengers(nb_persons);
                }
        }
    }

    /**
     * Increment the waiting time of every passengers in the Shuttle. The job of
     * incrementing the waiting time for Passengers or PassengerGroups who want
     * a pickup is defined in the TaxiCompany class, because TaxiCompnay class
     * keeps a list of all the Passengers or PassengerGroups requesting a
     * pickup.
     */
    public void incrementWaitingTime() {
        for (Passenger passenger : passengers.values())
            passenger.incrementWaitingTime();
    }

    /**
     * @return True if the current Shuttle is not full; otherwise false.
     */
    public boolean isFree() {
        return nb_requests + nb_passengers < capacity;
    }

    /**
     * @return If the Shuttle is empty.
     */
    public boolean isEmpty() {
        return nb_passengers + nb_requests == 0;
    }

    /**
     * This method is not implemented for the same reason below
     */
    public void setPickupLocation(Location location) {
    }

    /**
     * This method is not implemented for the same reason below
     */
    public void pickup(Passenger passenger) {
    }

    /**
     * Offload a Passenger or a PassengerGroup
     * 
     * This method is not implemented because when a Shuttle offloads a
     * Passenger, perhaps at the same Location some Passenger or PassengerGroup
     * requests a pickup. So every time a Shuttle arrives at its target
     * Location, we will check if pickups or offloads are possible.
     * 
     * The implementation of offloads is done in the act() method of Shuttle
     * class.
     * 
     * In the act() method, we assembled all the implementation of offloads and
     * pickups.
     */
    public void offloadPassenger() {
    }

    /**
     * A Shuttle make the decision where to go next when it picks up a Passenger
     * or a PassengerGroup, or when one of his Passengers or PassengerGroups
     * arrives at his/their destination; when making this decision, the current
     * Shuttle choose from the list of Locations of people requesting a pickup
     * of the current Shuttle and the list of Locations of people already in the
     * Shuttle, and it finds the nearest Location in these two lists.
     * 
     * @return The nearest Location found.
     */
    public Location nearestDestination() {
        if (passengers.isEmpty() && requests.isEmpty())
            return null;

        City city = getCompany().getCity();
        int min = new Location(0, 0).distance(new Location(city.getWidth(), city.getHeight())) + 1;
        int distanceTemp = 0;
        Location result = null;

        for (Location l : passengers.keySet())
            if ((distanceTemp = getLocation().distance(l)) < min) {
                min = distanceTemp;
                result = l;
            }

        for (Location l : requests.keySet())
            if ((distanceTemp = getLocation().distance(l)) < min) {
                min = distanceTemp;
                result = l;
            }
        return result;
    }

    /**
     * @return The image associated with the Shuttle on the map.
     */
    @Override
    public Image getImage() {
        return passengers.isEmpty() ? emptyImage : passengerImage;
    }

    /**
     * @return A representation of the current Shuttle.
     */
    @Override
    public String toString() {
        return "Shuttle at (" + getLocation() + "), Id = " + getID();
    }
}
