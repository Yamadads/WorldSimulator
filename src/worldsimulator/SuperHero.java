/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class defining attributes and behavior of criminals.
 *
 * @author Sebastian
 */
public class SuperHero extends Hero {

    private volatile City goTo;
    private volatile Boolean isFighting;

    /**
     * Constructor
     *
     * @param id unique identification number of Human
     * @param forename of SuperHero
     * @param travel set to 'null' best
     * @param health 1000 is max
     * @param intellect potential of this ability
     * @param force potential of this ability
     * @param speed potential of this ability
     * @param stamina potential of this ability
     * @param energy potential of this ability
     * @param fightingSkill potential of this ability
     * @param world the world in which he lives
     */
    public SuperHero(int id, String forename, Travel travel, int health, int intellect, int force, int speed, int stamina, int energy, int fightingSkill, World world) {
        super(id, forename, travel, health, intellect, force, speed, stamina, energy, fightingSkill, world);
        goTo = null;
        isFighting = false;
    }

    /**
     * Constructor
     *
     * @param id unique identification number of Human
     * @param forename of SuperHero
     * @param travel set to 'null' best
     * @param world the world in which he lives
     */
    public SuperHero(int id, String forename, Travel travel, World world) {
        super(id, forename, travel, world);
        goTo = null;
        isFighting = false;
    }

    /**
     * Constructor, set empty Travel
     *
     * @param id unique identification number of Human
     * @param forename of Hero
     * @param world the world in which he lives
     */
    public SuperHero(int id, String forename, World world) {
        super(id, forename, new Travel(), world);
        goTo = null;
        isFighting = false;
    }

    /**
     * Main method in Human thread, is executed in infinite loop in run()
     * supports traveling
     */
    @Override
    public void updateTravel() {
        // if travel exist 
        if (getTravel() != null) {
            //if some of element null
            if ((getTravel().getDestinationCity() == null) || (getTravel().getNextPlace() == null) || (getTravel().getCurrentPlace() == null) || (getTravel().getLastPlace() == null)) {
                if (getGoTo() != null) {
                    newTravel(getWorld().getCities().get(12), getGoTo());
                }
            }
            if (getTravel().getDestinationCity() != null) {
                //if it is end of the travel
                if (getTravel().getDestinationCity() == getTravel().getCurrentPlace()) {
                    try {
                        Thread.sleep(7500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SuperHero.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    getTravel().setTime(0);
                    if (getTravel().getCurrentPlace() == getWorld().getCities().get(12)) {
                        if (getGoTo() != null) {
                            newTravel(getWorld().getCities().get(12), getGoTo());
                        } else {
                            getTravel().setDestinationCity(null);
                        }
                    } else {
                        newTravel(getTravel().getCurrentPlace(), getWorld().getCities().get(12));
                        setGoTo(null);
                    }
                }
                if (getTravel().getDestinationCity() != null) {
                    //if dont know where to go
                    if (getTravel().getCurrentPlace() == getTravel().getNextPlace()) {
                        //then ask
                        if (getTravel().getLastPlace() != null) {
                            getTravel().setNextPlace(getTravel().getCurrentPlace().whichway(getTravel().getDestinationCity().getId(), getTravel().getLastPlace()));
                        } else {
                            newTravel(getWorld().getCities().get(12), goTo);
                        }
                    }
                }
                //if it is the end of current place
                if (getTravel().getProgress() >= 1000) {
                    //if this city is attacked then wait for fight
                    if (getTravel().getCurrentPlace() instanceof City){
                        if (((City)getTravel().getCurrentPlace()).getIsAttacked()){
                        try {
                            Thread.sleep(7500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SuperHero.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        getTravel().setTime(0);
                    }}
                    goToNextPlace();
                } else {
                    increaseProgress();
                }
            }
        }
    }

    /**
     * @return the goTo  - the destination city of SuperHero travel
     */
    public City getGoTo() {
        return goTo;
    }

    /**
     * @param goTo the goTo to set - the destination city of SuperHero travel
     */
    public void setGoTo(City goTo) {
        this.goTo = goTo;
    }

    /**
     * @return the isFighting
     */
    public Boolean getIsFighting() {
        return isFighting;
    }

    /**
     * @param isFighting the isFighting to set
     */
    public void setIsFighting(Boolean isFighting) {
        this.isFighting = isFighting;
    }

}
