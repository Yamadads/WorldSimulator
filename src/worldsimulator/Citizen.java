/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class defining attributes and behavior of Citizens.
 *
 * @author Sebastian
 */
public class Citizen extends Human {

    private volatile String lastname;
    private volatile City city;

    /**
     * Constructor of citizen.
     *
     * @param id unique identification number of Human
     * @param lastname of citizen
     * @param city the city in which he lives
     * @param forename of citizen
     * @param world the world in which he lives
     */
    public Citizen(int id, String lastname, City city, String forename, World world) {
        super(id, forename, world);
        this.lastname = lastname;
        this.city = city;
    }

    /**
     * Constructor of citizen. It sets random city from collection
     *
     * @param id unique identification number of Human
     * @param lastname of citizen
     * @param forename of citizen
     * @param world the world in which he lives
     */
    public Citizen(int id, String forename, String lastname, World world) {
        super(id, forename, world);
        this.lastname = lastname;
        Collections.shuffle(getWorld().getNonDamagedCities());
        if (!getWorld().getNonDamagedCities().isEmpty()) {
            this.city = getWorld().getNonDamagedCities().get(0);
        }
    }

    /**
     * part of updateTravel(), Citizen go sleep randomly
     */
    private void randomSleeping() {
        Random generator = new Random();
        if (generator.nextInt(10000) == 0) {
            getTravel().setTime(0); //must be because "increaseProgress()" will do too much 
            setPause(true);
        }
    }

    /**
     * Main method in Human thread, is executed in infinite loop in run()
     */
    @Override
    public void updateTravel() {
        // if travel exist 
        if (getTravel() != null) {
            //if some of element null
            if ((getTravel().getDestinationCity() == null) || (getTravel().getNextPlace() == null) || (getTravel().getCurrentPlace() == null) || (getTravel().getLastPlace() == null)) {
                newTravel(city);
            }
            //if it is end of the travel
            if (getTravel().getDestinationCity() == getTravel().getCurrentPlace()) {
                if (getTravel().getCurrentPlace() == getCity()) {
                    if (getCity().getIsDamage()) {
                        if ((getTravel().getRecentlyVisited() != null) && (!((City)getTravel().getRecentlyVisited()).getIsDamage())) {
                            city.setPopulation(city.getPopulation() - 1);
                            this.city = getWorld().getCities().get(getTravel().getRecentlyVisited().getId());
                        } else {
                            city.setPopulation(city.getPopulation() - 1);
                            Random generator = new Random();
                            do {
                                this.city = getWorld().getCities().get(generator.nextInt(getWorld().getCities().size()) + 1);
                            } while ((city.getIsDamage()) || (getTravel().getCurrentPlace().getId() == getCity().getId()));
                        }
                        city.setPopulation(city.getPopulation() + 1);
                        newTravel(getTravel().getCurrentPlace(), getCity());
                    } else {
                        try {
                            Random generator = new Random();
                            Thread.sleep(generator.nextInt(100000) + 1000);//min 1 sec, max 101 sec 
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Citizen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        newTravel(getTravel().getCurrentPlace());
                    }
                } else {
                    getTravel().setRecentlyVisited(getTravel().getCurrentPlace());
                    newTravel(getTravel().getCurrentPlace(), getCity());
                }
            }
            //if dont know where to go
            if (getTravel().getCurrentPlace() == getTravel().getNextPlace()) {
                //then ask
                if (getTravel().getLastPlace() != null) {
                    getTravel().setNextPlace(getTravel().getCurrentPlace().whichway(getTravel().getDestinationCity().getId(), getTravel().getLastPlace()));
                } else {
                    newTravel(city);
                }
            }
            //if it is the end of current place
            if (getTravel().getProgress() >= 1000) {
                goToNextPlace();
            } else {

                increaseProgress();
            }
        } else {
            //set new Travel
            setTravel(new Travel());
        }
        //slepp randomly
        if ((getTravel().getProgress() > 300) && (getTravel().getProgress() < 700) && (getTravel().getCurrentPlace().getId() > 200)) {
            randomSleeping();
        }
    }

    /**
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return the city inhabited by him
     */
    public City getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @return the name of city inhabited by him
     */
    public String getCityName() {
        return this.city.getName();
    }

}
