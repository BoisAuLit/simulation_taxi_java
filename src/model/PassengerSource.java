package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

/**
 * Randomly put generate Passengers or PassengerGroups on the map. Internally
 * pass requests of Passengers or PassengerGroups to one of the exsisting
 * TaxiCompanies (the choice of TaxiComany is random), if the selected
 * TaxiCompany can provide the service (has available vehicles) for the client
 * (Passenger or PassengerGroup), then requests are registered in the selected
 * TaxiCompany; if the selected comany cannot provide the service, then we won't
 * put the Passenger or PassengerGroup on the map, in this case, a missed pickup
 * is registered.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A.Morelle. Modified
 *         Bohao LI
 * @version 2017.03.21
 */
public class PassengerSource implements Actor {

    @SuppressWarnings("unused")
    private static final class NbPassengersOnMapNegativeException extends Exception {
        private static final long serialVersionUID = 1L;
        private String msg;

        private NbPassengersOnMapNegativeException(String msg) {
            this.msg = msg;
        }
    }

    @SuppressWarnings("unused")
    private static final class NbPassengersInShuttlesNegativeException extends Exception {
        private static final long serialVersionUID = 1L;
        private String msg;

        private NbPassengersInShuttlesNegativeException(String msg) {
            this.msg = msg;
        }
    }

    private static final float CREATION_PROBABILITY = .35f;

    /**
     * The maximu number of persons on the map nb_max_passengers >=
     * passengers_on_map + passengers_in_shuttles
     */
    private int nb_max_passengers;
    /**
     * passengers_on_map = (number of passengers waiting for a pickup) + (number
     * of passengers in Shuttles) Attention: the number of passengers waiting
     * for a pickup can wait for either a Taxi or a Shuttle here we don't count
     * the Passenger in Taxis
     */
    private int passengers_on_map;
    /**
     * The number of Passengers in Shuttles If a Passenger is in a Shuttle, it
     * means that he waits for the shuttle to put him at his destination
     */
    private int passengers_in_shuttles;

    private City city;
    private List<TaxiCompany> companyList;

    private Random rand;
    private int missedPickups;

    /**
     * Create a PassengerSource object with the given city model.
     * 
     * @param city
     *            The city model associated with the PassengerSource object.
     */
    public PassengerSource(City city) {
        if (city == null)
            throw new NullPointerException("city");
        this.city = city;
        companyList = new ArrayList<>();
        nb_max_passengers = 0;
        passengers_on_map = 0;
        passengers_in_shuttles = 0;
        missedPickups = 0;
        rand = new Random();
    }

    /**
     * Create a PassengerSource object with a city model and a TaxiCompany.
     * 
     * @param city
     *            The city model associated with the PassengerSource.
     * @param company
     *            The TaxiCompnay associated with the PassengerSource.
     */
    public PassengerSource(City city, TaxiCompany company) {
        this(city);
        if (company == null)
            throw new NullPointerException("company");
        companyList.add(company);

        int count = 0;
        for (Vehicle v : company.getVehicles())
            count += v.getCapacity();
        nb_max_passengers = count;
        company.setPassengerSource(this);
    }

    /**
     * Add a TaxiCompnay to the current PassengerSource.
     * 
     * @param newCompany
     */
    public void addCompany(TaxiCompany newCompany) {
        if (newCompany == null)
            throw new NullPointerException("company");
        companyList.add(newCompany);
        int count = 0;
        for (Vehicle v : newCompany.getVehicles())
            count += v.getCapacity();
        nb_max_passengers += count;
        newCompany.setPassengerSource(this);
    }

    /**
     * Randomly generate a new passenger. Keep a count of missed pickups.
     */
    public void act() {
        for (TaxiCompany company : companyList) {
            company.incrementWaitingTime();
            company.checkWaitingTime();
        }
        showStatus();
        if (rand.nextDouble() <= CREATION_PROBABILITY) {
            Passenger passenger = createPassenger();
            if (passenger == null)
                return;
            TaxiCompany company = companyList.get(new Random().nextInt(companyList.size()));
            if (company.requestPickup(passenger)) {
                System.out.println(passenger + " appears");
                incrementPassengersOnMap(passenger.getNb_persons());
                city.addItem(passenger);
            } else {
                System.out.println("Pickup missed for " + passenger);
                missedPickups += passenger.getNb_persons();
            }
        }
    }

    /**
     * Show all the information of the PassengerSource
     */
    public void showStatus() {
        System.out.format("On map: %2d" + ", In shuttles: %2d" + ", Max: %2d" + ", missed pickups: %2d\n",
                passengers_on_map, passengers_in_shuttles, nb_max_passengers, missedPickups);
        for (TaxiCompany company : companyList)
            company.showStatus();
    }

    /**
     * When the simulation is running, handle the dial of the user For
     * simplicity, we consider the ID of a Taxi as the telephone number of the
     * Taxi
     */
    public void handleDial(String telephone, int x, int y) {
        Passenger passenger = createPassenger(new Location(x, y));
        if (passenger == null) {
            JOptionPane.showMessageDialog(null, "City is too crowded!", "Inane warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (TaxiCompany company : companyList) {
            String result = company.handleDial(telephone, passenger);
            if (result.equals("isBusy")) {
                System.out.println(passenger + " dialing taxi failed because taxi is busy");
                JOptionPane.showMessageDialog(null, "The taxi you've dialed is busy, " + "please dial again later",
                        "Inane warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (result.equals("success")) {
                System.out.println(passenger + " dialing taxi successfully");
                JOptionPane.showMessageDialog(null, "Success!", "Inane warning", JOptionPane.WARNING_MESSAGE);
                incrementPassengersOnMap(passenger.getNb_persons());
                city.addItem(passenger);
                return;
            }
        }
        System.out.println(passenger + " dialing the wrong number");
        missedPickups += passenger.getNb_persons();
        JOptionPane.showMessageDialog(null, "You've dialed the wrong number", "Inane warning",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Set the PassengerSource for the give compnay.
     * 
     * @param company
     *            The TaxiCompany for whom the PassengerSource is to be set.
     */
    public void setCompnayPassengerSource(TaxiCompany company) {
        company.setPassengerSource(this);
    }

    public boolean cannotAddPassengersAnymore(int nb) {
        return passengers_on_map + passengers_in_shuttles + nb > nb_max_passengers;
    }

    /**
     * Increment the number of people on the map. This method is called when
     * ever a Passenger or a PassengerGroup is generated by PassengerSource and
     * is put on the map.
     * 
     * @param nb
     *            The number used to increment the number of persons on the map.
     */
    public synchronized void incrementPassengersOnMap(int nb) {
        passengers_on_map += nb;
    }

    /**
     * Decrement the number of people on the map. This method is called when: 1.
     * A Passenger or PassengerGroup waits too long for a pickup that he/they
     * disappear on the map 2. A Shuttle picks up a Passenger or a
     * PassengerGroup on the map 3. A Taxi picks up a Passenger.
     * 
     * @param nb
     *            The number used to decrement the number of persons on the map.
     */
    public synchronized void decrementPassengersOnMap(int nb) {
        try {
            if (passengers_on_map - nb < 0)
                throw new NbPassengersOnMapNegativeException("negative number of passengers on the map");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        passengers_on_map -= nb;
    }

    /**
     * Increment the number of people on the map. This method is called when a
     * Shuttle arrives at a pickup Location to pick up a Passenger or
     * PassengerGroup
     * 
     * @param nb
     *            The number of people getting on a Shuttle.
     */
    public void incrementPassengersInShuttle(int nb) {
        passengers_in_shuttles += nb;
    }

    /**
     * Decrement the number of people in the shuttles.
     * 
     * @param nb
     *            The number of persons that disappear in a Shuttle, in the
     *            first case, he/they disappear(s) because of waiting too long
     *            in the Shuttle without arriving at his/their destination(s);
     *            in the second case, he/they arrive(s) at his/their destination
     *            after staying in a Shuttle. (we don't count persons in taxis,
     *            because in our implementation, a Passenger is considered to
     *            disappear at the moment of the pickup by a Taxi)
     */
    public void decrementPassengersInShuttle(int nb) {
        try {
            if (passengers_in_shuttles - nb < 0)
                throw new NbPassengersInShuttlesNegativeException("negative number of passengers on the map");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        passengers_in_shuttles -= nb;
    }

    public int getMissedPickups() {
        return missedPickups;
    }

    /**
     * If the limit number of persons on the map is not achieved yet, create a
     * Passenger choosing to take a Taxi at the given pickup position.
     * 
     * @param pickupLocation
     *            The pickup location of the Passenger to be created
     * @return The Passenger created.
     */
    private Passenger createPassenger(Location pickupLocation) {
        int cityWidth = city.getWidth();
        int cityHeight = city.getHeight();
        Location destination;
        do {
            destination = new Location(rand.nextInt(cityWidth), rand.nextInt(cityHeight));
        } while (pickupLocation.isEqualTo(destination));
        Passenger passenger = new Passenger(pickupLocation, destination, Passenger.Choice.TAXI);
        return cannotAddPassengersAnymore(passenger.getNb_persons()) ? null : passenger;
    }

    /**
     * If the limit number of persons on the map is not achieved yet, randomly
     * create a Passenger or a PassengerGroup.
     * 
     * @return The Passenger or PassengerGroup created.
     */
    private Passenger createPassenger() {
        int cityWidth = city.getWidth();
        int cityHeight = city.getHeight();
        Location pickupLocation = new Location(rand.nextInt(cityWidth), rand.nextInt(cityHeight));
        Location destination;
        do {
            destination = new Location(rand.nextInt(cityWidth), rand.nextInt(cityHeight));
        } while (pickupLocation.isEqualTo(destination));
        Passenger passenger = Math.random() < .5 ? new Passenger(pickupLocation, destination)
                : new PassengerGroup(pickupLocation, destination);
        return cannotAddPassengersAnymore(passenger.getNb_persons()) ? null : passenger;
    }
}