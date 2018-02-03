package model;

/**
 * Model the common elements of taxis and shuttles.
 * 
 * @author David J. Barnes and Michael Kolling. Modified Bohao LI.
 * @version 2017.03.23
 */
public abstract class Vehicle implements Actor {

    private String id;
    private TaxiCompany company;

    // Where the vehicle is.
    private Location location;
    // Where the vehicle is headed.
    private Location targetLocation;
    // Record how often the vehicle has nothing to do.
    private int idleCount;
    // Number of successful transport achieved by the Vehicle
    private int nb_success;

    /**
     * Constructor of class Vehicle
     * 
     * @param company
     *            The taxi company. Must not be null.
     * @param location
     *            The vehicle's starting point. Must not be null.
     * @throws NullPointerException
     *             If company or location is null.
     */
    public Vehicle(TaxiCompany company, Location location, String id) {
        if (company == null)
            throw new NullPointerException("company");
        if (location == null)
            throw new NullPointerException("location");

        this.company = company;
        this.location = location;
        this.id = id;
        targetLocation = null;
        idleCount = 0;
        nb_success = 0;
    }

    /**
     * @return The number of successful transport.
     */
    public int getNbSuccess() {
        return nb_success;
    }

    /**
     * Increment the number of successful transport
     */
    public void incrementNbSuccess() {
        nb_success++;
    }

    /**
     * @return The TaxiCompany in charge of the current Vehicle.
     */
    public synchronized TaxiCompany getCompany() {
        return company;
    }

    /**
     * @return Id number.
     */
    public String getID() {
        return id;
    }

    /**
     * Notify the company of our arrival at a pickup location.
     */
    public void notifyPickupArrival() {
        company.arrivedAtPickup(this);
    }

    /**
     * Notify the company of our arrival at a passenger's destination.
     */
    public void notifyPassengerArrival(Passenger passenger) {
        company.arrivedAtDestination(this, passenger);
    }

    /**
     * Show all the information relevant to the current Vehicle.
     */
    public abstract void showStatus();

    /**
     * @return The maximum number of Passengers that the current Vehicle can
     *         take.
     */
    public abstract int getCapacity();

    /**
     * Receive a pickup location. How this is handled depends on the type of
     * vehicle.
     * 
     * @param location
     *            The pickup location.
     */
    public abstract void setPickupLocation(Location location);

    /**
     * Receive a passenger. How this is handled depends on the type of vehicle.
     * 
     * @param passenger
     *            The passenger.
     */
    public abstract void pickup(Passenger passenger);

    /**
     * @return Whether or not this vehicle is free.
     */
    public abstract boolean isFree();

    /**
     * Offload any passengers whose destination is the current location.
     */
    public abstract void offloadPassenger();

    /**
     * @return Where this vehicle is currently located.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Set the current location.
     * 
     * @param location
     *            Where it is. Must not be null.
     * @throws NullPointerException
     *             If location is null.
     */
    public void setLocation(Location location) {
        if (location != null)
            this.location = location;
        else
            throw new NullPointerException();
    }

    /**
     * @return Where this vehicle is currently headed, or null if it is idle.
     */
    public Location getTargetLocation() {
        return targetLocation;
    }

    /**
     * Set the required target location.
     * 
     * @param location
     *            Where to go. Must not be null.
     * @throws NullPointerException
     *             If location is null.
     */
    public synchronized void setTargetLocation(Location location) {
        if (location != null)
            targetLocation = location;
        else
            throw new NullPointerException();
    }

    /**
     * Clear the target location.
     */
    public void clearTargetLocation() {
        targetLocation = null;
    }

    /**
     * @return On how many steps this vehicle has been idle.
     */
    public int getIdleCount() {
        return idleCount;
    }

    /**
     * Increment the number of steps on which this vehicle has been idle.
     */
    public void incrementIdleCount() {
        idleCount++;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((company == null) ? 0 : company.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Vehicle))
            return false;
        Vehicle other = (Vehicle) obj;
        if (company == null) {
            if (other.company != null)
                return false;
        } else if (!company.equals(other.company))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
