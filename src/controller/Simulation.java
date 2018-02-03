package controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import model.Actor;
import model.City;
import model.PassengerSource;
import model.TaxiCompany;

import view.CityGUI;

/**
 * Run the simulation by asking a collection of actors to act.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A. Morelle. Modified
 *         Bohao LI.
 * @version 2017.03.23
 */
public class Simulation {

    private List<Actor> actors;

    /**
     * Create the initial set of actors for the simulation.
     */
    public Simulation() {

        // Output the content on the console to an external file (auto
        // generated)
        // If file does not exist, create it; otherwise overwrite the file
        try {
            PrintStream out;
            out = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Use CountDownLatch to forbid the lauch of the simulation until
        // configuration is done
        CountDownLatch latch = new CountDownLatch(1);
        ConfigReader config = new ConfigReader(latch);
        config.start();
        try {
            // Waiting for the latch to be released in the configuratoin thread
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // At this moment, the CountDownLatch is released, and the following
        // statements will be executed

        actors = new LinkedList<>();

        City city = new City(config.getSize_width(), config.getSize_height());
        // Create the first taxi company
        TaxiCompany companyOfBohao = new TaxiCompany("Bohao's vehicle company", city, config.getNb_taxis(),
                config.getNb_navettes());
        PassengerSource source = new PassengerSource(city, companyOfBohao);

        // Create the second taxi company
        TaxiCompany companyOfVictor = new TaxiCompany("Victor's vehicle company", city, config.getNb_taxis(),
                config.getNb_navettes());
        // Add the second compnay to the passenger source
        source.addCompany(companyOfVictor);

        actors.addAll(companyOfBohao.getVehicles());
        actors.addAll(companyOfVictor.getVehicles());
        actors.add(source);
        actors.add(new CityGUI(city, source));
    }

    /**
     * Run the simulation for a fixed number of steps. Pause after each step to
     * allow the GUI to keep up.
     */
    public void run() {
        System.out.println("Begin simulation");
        for (int i = 0; i < 300; i++) {
            step();
            wait(400);
        }
        System.out.println("End simulation");
    }

    /**
     * Take a single step of the simulation.
     */
    public void step() {
        for (Actor actor : actors)
            actor.act();
    }

    /**
     * Wait for a specified number of milliseconds before finishing. This
     * provides an easy way to cause a small delay.
     * 
     * @param milliseconds
     *            The number of milliseconds to wait.
     */
    private void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // ignore the exception
        }
    }
}
