package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Random;
import java.util.Set;

/**
 * Model the operation of a taxi company, operating different types of vehicle.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A. Morelle. Modified
 *         Bohao LI
 * @version 2017.03.23
 */
public class TaxiCompany {

    private static final class Pair<L, R> {
        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }

        @Override
        public int hashCode() {
            return left.hashCode() ^ right.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair))
                return false;
            @SuppressWarnings("unchecked")
            Pair<L, R> pairo = (Pair<L, R>) o;
            return this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight());
        }
    }

    private final int NUMBER_OF_TAXIS;
    private final int NUMBER_OF_SHUTTLES;

    private String companyName;
    // The vehicles operated by the company.
    private List<Vehicle> vehicles;
    private City city;

    /**
     * The associations between Taxis and the Passengers they are to pick up.
     * Use Hashtable because it is a synchronized structure. We need a
     * synchronized structure because when we simulate a dial (with a IHM
     * keybord), Two threas may want toaccess the HashTable at the same moment,
     * no collision will appear because Hashtable is a synchronized structure.
     * (Collision are potential if we use a HashMap).
     * 
     * Well, this kind of collision will never happen for the associations
     * between Shuttles and the Passengers, because we don't do dialing shuttle
     * simulation in our program.
     */
    private Hashtable<Vehicle, Passenger> assignments_taxis;
    /**
     * Attention: now I explain why I did not override the equals() method in
     * Location class: because we want the default behavior of equals() in
     * Object class, that is --> two objects are equal if and only if they are
     * exactly the same object in the memory.
     * 
     * What if we provide our own version of equals (that considers two
     * Locations to be equal if they have the same abscisses and ordinates) in
     * Location class? What will happen?
     * 
     * Well, something terrible will happen, here's the story: every time before
     * the HashMap put a pair(key, value) into it, it will first transform the
     * key into an integer value (using the internal hash function inside the
     * HashMap class (this method will also internally call the hashCode()
     * fucntion of the key object(here Location object)), if we override the
     * equals() method inside Location (that consider two Locations to be equal
     * if they have the same abscisses and ordinates), before we put this pair,
     * if there is already a key that have the same hash code as the key of the
     * pair that we are going to put in the HashMap, then HashMap will
     * internally call equals() method of Location, at this time, it finds that
     * the two Locations are equals, then the HashMap will replace the old value
     * with the new value, but this is not what we want! And you can imagin that
     * the default version of equals() provided by the Object class is exactly
     * what we want. We should know that two passengers at the same Location are
     * two distinct Passengers! (even if we can't see it on the map)
     * 
     * If you are still confused after this explanation, you can contact the
     * author at: bohao.li.20160103@efrei.net for furthur details.
     */
    private Map<Location, Pair<Passenger, Shuttle>> assignments_shuttles;

    private PassengerSource passengerSource;

    /**
     * @param city
     *            The city.
     * @param nb_taxis
     *            The number of taxis that the TaxiCompnay operates.
     * @param nb_shuttles
     *            The number of shuttles the TaxiCompany operates.
     */
    public TaxiCompany(String companyName, City city, int nb_taxis, int nb_shuttles) {
        this.companyName = companyName;
        this.city = city;
        NUMBER_OF_TAXIS = nb_taxis;
        NUMBER_OF_SHUTTLES = nb_shuttles;
        vehicles = new LinkedList<>();

        assignments_taxis = new Hashtable<>();
        // Here we did not use Hashtable because synchronization is not needed
        // for Shuttles.
        assignments_shuttles = new HashMap<>();
        setupVehicles();
    }

    /**
     * show all the information of the current TaxiCompany
     */
    public void showStatus() {
        System.out.println("\t\t" + this);
        for (Vehicle v : vehicles)
            if (v instanceof Shuttle)
                v.showStatus();
        for (Vehicle v : vehicles)
            if (v instanceof Taxi)
                v.showStatus();
    }

    /**
     * @param passengerSource
     *            The source of passenger to be set as the current source.
     */
    public void setPassengerSource(PassengerSource passengerSource) {
        this.passengerSource = passengerSource;
    }

    /**
     * @return The passenger source of the current TaxiCompany.
     */
    public PassengerSource getPassengerSource() {
        return passengerSource;
    }

    /**
     * @return The city model associated with the current TaxiCompany.
     */
    public City getCity() {
        return city;
    }

    /**
     * Request a pickup for the given passenger.
     * 
     * @param passenger
     *            The passenger requesting a pickup.
     * @return Whether a free vehicle is available.
     */
    public boolean requestPickup(Passenger passenger) {
        Vehicle vehicle = scheduleVehicle(passenger);
        if (vehicle != null) {
            if (vehicle instanceof Taxi) {
                assignments_taxis.put(vehicle, passenger);
                vehicle.setPickupLocation(passenger.getPickupLocation());
            } else {
                ((Shuttle) vehicle).receiveRequest(passenger);
                assignments_shuttles.put(passenger.getLocation(), new Pair<>(passenger, (Shuttle) vehicle));
            }
            return true;
        } else
            return false;
    }

    /**
     * A vehicle has arrived at a pickup point (where a passenger is supposed to
     * be waiting).
     * 
     * @param The
     *            vehicle at the pickup point.
     */
    public void arrivedAtPickup(Vehicle vehicle) {
        if (vehicle instanceof Taxi) {
            Passenger passenger = (Passenger) assignments_taxis.remove(vehicle);
            city.removeItem(passenger);
            vehicle.pickup(passenger);
        } else {
            Location key;
            Pair<Passenger, Shuttle> value;
            Shuttle shuttle;
            Passenger passenger;

            for (Iterator<Map.Entry<Location, Pair<Passenger, Shuttle>>> it = assignments_shuttles.entrySet()
                    .iterator(); it.hasNext();) {
                Map.Entry<Location, Pair<Passenger, Shuttle>> entry = it.next();
                key = entry.getKey();
                value = entry.getValue();
                passenger = value.getLeft();
                shuttle = value.getRight();

                if (shuttle.getLocation().isEqualTo(key)) {
                    shuttle.removeFromRequestList(key);
                    it.remove();
                    city.removeItem(passenger);
                    vehicle.pickup(passenger);
                }
            }

        }
    }

    public String handleDial(String telephone, Passenger passenger) {
        for (Vehicle v : vehicles)
            if (v instanceof Taxi) {
                if (v.getID().equals(telephone))
                    if (!v.isFree())
                        return "isBusy";
                    else {
                        assignments_taxis.put(v, passenger);
                        v.setPickupLocation(passenger.getPickupLocation());
                        return "success";
                    }
            }
        return "failed";
    }

    public void checkWaitingTime() {
        Location key;
        Pair<Passenger, Shuttle> value;
        Shuttle shuttle;
        Passenger passenger;

        for (Iterator<Map.Entry<Location, Pair<Passenger, Shuttle>>> it = assignments_shuttles.entrySet().iterator(); it
                .hasNext();) {
            Map.Entry<Location, Pair<Passenger, Shuttle>> entry = it.next();
            (shuttle = (value = entry.getValue()).getRight()).checkWaitingTime();

            if ((passenger = value.getLeft()).waitingTooLong())
                if (!shuttle.getTargetLocation().isEqualTo(key = entry.getKey())) {
                    System.out
                            .println(passenger + " waiting too long for a pickup that " + "he disappeared on the map");
                    shuttle.removeFromRequestList(key);
                    it.remove();
                    city.removeItem(passenger);
                    passengerSource.decrementPassengersOnMap(passenger.getNb_persons());
                }
        }
    }

    public void incrementWaitingTime() {
        for (Entry<Location, Pair<Passenger, Shuttle>> entry : assignments_shuttles.entrySet())
            entry.getValue().getLeft().incrementWaitingTime();
    }

    /**
     * A vehicle has arrived at a passenger's destination.
     * 
     * @param The
     *            vehicle at the destination.
     * @param The
     *            passenger being dropped off.
     */
    public void arrivedAtDestination(Vehicle vehicle, Passenger passenger) {
    }

    /**
     * @return The list of vehicles.
     */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * Find a free vehicle, if any.
     * 
     * @return A free vehicle, or null if there is none.
     */
    private Vehicle scheduleVehicle(Passenger p) {

        if (p.getChoice() == Passenger.Choice.SHUTTLE) {

            if (p instanceof PassengerGroup) {
                double restCapacityMax = -.1;
                double temp = .0;
                Vehicle result = null;
                for (Vehicle v : vehicles)
                    if (v instanceof Shuttle)
                        if (((Shuttle) v).canReceiveGroupRequest((PassengerGroup) p)) {
                            if ((temp = ((Shuttle) v).restCapacity()) > restCapacityMax) {
                                restCapacityMax = temp;
                                result = v;
                            }
                        }
                return result;
            } else {
                for (Vehicle v : vehicles)
                    if (v instanceof Shuttle)
                        if (v.isFree())
                            return v;
            }

            return null;

        } else {
            Set<Vehicle> freeVehicles = new HashSet<>();

            for (Vehicle v : vehicles)
                if (v instanceof Taxi)
                    if (v.isFree())
                        freeVehicles.add(v);

            if (freeVehicles.isEmpty())
                return null;
            if (freeVehicles.size() == 1)
                return freeVehicles.toArray(new Vehicle[1])[0];

            Vehicle result = null;
            int min = new Location(0, 0).distance(new Location(city.getWidth(), city.getHeight())) + 1;
            int distanceTemp = 0;

            for (Vehicle v : freeVehicles) {
                if ((distanceTemp = p.getLocation().distance(v.getLocation())) < min) {
                    min = distanceTemp;
                    result = v;
                }
            }
            return result;
        }
    }

    /**
     * Set up this company's vehicles. The optimum number of vehicles should be
     * determined by analysis of the data gathered from the simulation.
     *
     * Vehicles start at random locations.
     */
    private void setupVehicles() {
        int cityWidth = city.getWidth();
        int cityHeight = city.getHeight();
        Random rand = new Random(12345);

        for (int i = 0; i < NUMBER_OF_TAXIS; i++) {
            Taxi taxi = null;
            boolean flag = false;
            do {
                flag = false;
                taxi = new Taxi(this, new Location(rand.nextInt(cityWidth), rand.nextInt(cityHeight)),
                        "T-" + Math.abs((i + 1 + hashCode())));

                for (Item item : city.getItems())
                    if (item.getLocation().isEqualTo(taxi.getLocation()))
                        flag = true;
            } while (flag);
            vehicles.add(taxi);
            city.addItem(taxi);
        }

        for (int i = 0; i < NUMBER_OF_SHUTTLES; i++) {
            Shuttle shuttle = null;
            boolean flag = false;
            do {
                flag = false;
                shuttle = new Shuttle(this, new Location(rand.nextInt(cityWidth), rand.nextInt(cityHeight)),
                        "S-" + Math.abs((i + 1 + hashCode())));

                for (Item item : city.getItems())
                    if (item.getLocation().isEqualTo(shuttle.getLocation()))
                        flag = true;
            } while (flag);
            vehicles.add(shuttle);
            city.addItem(shuttle);
        }
    }

    @Override
    public String toString() {
        return "TaxiCompany [NUMBER_OF_TAXIS=" + NUMBER_OF_TAXIS + ", NUMBER_OF_SHUTTLES=" + NUMBER_OF_SHUTTLES
                + ", companyName=" + companyName + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TaxiCompany))
            return false;
        TaxiCompany other = (TaxiCompany) obj;
        if (companyName == null) {
            if (other.companyName != null)
                return false;
        } else if (!companyName.equals(other.companyName))
            return false;
        return true;
    }

}
