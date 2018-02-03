package model;

import java.awt.Image;
import java.util.Random;

import javax.swing.ImageIcon;

/**
 * A PassengerGroup is a group of passengers. The number of persons in the group
 * will be generated at creation time of the object of the class, it's a random
 * number between 2 and 10.
 * 
 * Every passenger in the group has exactly the same pickup location and
 * destination.
 * 
 * To a certain degree, you can consider a PassengerGroup as a Passenger,
 * because in our implementatio, PassengerGroup inherits all the behaviors of
 * Passenger and adds some attributs and overrides a few methods.
 * 
 * (Another tentative plan is, PassengerGroup contains a list of passengers
 * having the same pickup location but different destinations, this concept is
 * not implemented, but this concept is more logical)
 * 
 * Likewise (like in Passenger), an angery group has a yellow Image.
 * 
 * PassengerGroup inherited all the almost all the behaviors of Passenger so for
 * furthur detail, see the implementation of Passenger
 * 
 * @author Bohao LI
 * @version 2017.03.23
 */
public class PassengerGroup extends Passenger implements DrawableItem {

    private static final int NB_MIN = 2;
    private static final int NB_MAX = 10;

    private final int nbPassengers;

    private Location pickup;
    private Location destination;

    private final Image group;
    private final Image angry_group;

    public PassengerGroup(Location pickup, Location destination) {
        super(pickup, destination, Choice.SHUTTLE);
        group = new ImageIcon(getClass().getResource("/images/persons.jpg")).getImage();
        angry_group = new ImageIcon(getClass().getResource("/images/angry_persons.jpg")).getImage();
        Random r = new Random(23456);
        nbPassengers = r.nextInt(NB_MAX + 1 - NB_MIN) + NB_MIN;
    }

    @Override
    public String toString() {
        return "Passenger group of " + nbPassengers + " from " + pickup + " to " + destination;
    }

    @Override
    public Image getImage() {
        return isAngry() ? angry_group : group;
    }

    @Override
    public int getNb_persons() {
        return nbPassengers;
    }
}
