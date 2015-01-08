/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import com.sun.javafx.geom.Ellipse2D;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class which contains the common fields and behaviors of Hero and
 * Citizen.
 *
 * @author Sebastian
 */
public abstract class Human extends PointOnMap implements Runnable {

    private volatile String forename;
    private volatile Travel travel;    
    private volatile Ellipse2D oval;
    private volatile Boolean pause;
    private volatile Boolean dead;
    private volatile Boolean isOnCrossing;
    private volatile Place onSemaphore;    
    private volatile World world;

    /**
     * Constructor
     *
     * @param id unique identification number of Human
     * @param forename of Human
     * @param world the world in which he lives
     */
    public Human(int id, String forename, World world) {
        super(id);
        this.world=world;
        this.forename = forename;
        this.travel = new Travel();
        this.oval = new Ellipse2D(0, 0, 0, 0);
        this.pause = false;
        this.dead = false;
        onSemaphore = null;
        isOnCrossing = false;
    }


    /**
     * Create new Travel. With ramdom destinationCity
     *
     * @param currentPlace
     */
    synchronized public void newTravel(Place currentPlace) {
        if (getTravel() == null) {
            Random generator = new Random();
            City city = world.getCities().get(generator.nextInt(world.getCities().size()) + 1);
            while (city == currentPlace) {
                city = world.getCities().get(generator.nextInt(world.getCities().size()) + 1);
            }
            travel = new Travel(city, currentPlace, currentPlace);
        } else {
            Random generator = new Random();
            City city = world.getCities().get(generator.nextInt(world.getCities().size()) + 1);
            while (city == currentPlace) {
                city = world.getCities().get(generator.nextInt(world.getCities().size()) + 1);
            }
            travel.setDestinationCity(city);
            travel.setCurrentPlace(currentPlace);
            travel.setNextPlace(currentPlace);
            travel.setLastPlace(currentPlace);
            travel.setProgress(0);
        }
    }

    /**
     * Create new Travel. With specified destinationCity
     *
     * @param currentPlace
     * @param destinationCity
     */
    synchronized public void newTravel(Place currentPlace, Place destinationCity) {
        if (getTravel() == null) {
            travel = new Travel(destinationCity, currentPlace, currentPlace);
        } else {
            travel.setDestinationCity(destinationCity);
            travel.setCurrentPlace(currentPlace);
            travel.setNextPlace(currentPlace);
            travel.setLastPlace(currentPlace);
            travel.setProgress(0);
        }
    }

    /**
     * part of updateTravel(), increase the progress in current place if can
     */
    protected void increaseProgress() {
        //System.out.println(getTravel().getProgress());
        if (getTravel().getTime() == 0) {
            getTravel().setTime(System.currentTimeMillis());
        } else {
            long newTime = System.currentTimeMillis();
            double delta = newTime - getTravel().getTime();
            delta /= 10;
            //set new time                    
            getTravel().setTime(newTime);
            //checking collision
            if (getTravel().getCurrentPlace().getId() > 200) {
                if (getTravel().getNextPlace().getId() > 100) {
                    if (getTravel().getProgress() > (1000 - (40000 / getTravel().getCurrentPlace().getSize()))) {
                        if (!getIsOnCrossing()) {
                            setIsOnCrossing(true);
                            if (getDead()) {
                                return;
                            }
                            try {
                                ((Crossing)getTravel().getNextPlace()).getSemaphore().acquire();//semaphoreAcquire();
                                setOnSemaphore(getTravel().getNextPlace());
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Human.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            ((Crossing)getTravel().getNextPlace()).setHumanOnSemaphore(this);
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Citizen.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            getTravel().setTime(0);
                        }
                    }
                } /*else if (getTravel().getLastPlace().getId() > 100) {
                 if (getIsOnCrossing()) {
                 if (getTravel().getProgress() > (40000 / getTravel().getCurrentPlace().getSize())) {
                 System.out.print("tak  ");
                 setIsOnCrossing(false);
                 getTravel().getLastPlace().setHumanOnSemaphore(null);
                 setOnSemaphore(null);
                 getTravel().getLastPlace().getSemaphore().release();
                 System.out.print("jak  ");
                 }
                 }
                 }*/

                for (Entry<Integer, Human> entryHuman : getTravel().getCurrentPlace().getHumans().entrySet()) {
                    if (entryHuman.getValue().getId() != getId()) {
                        if (entryHuman.getValue().getTravel().getLastPlace() == this.getTravel().getLastPlace()) {
                            if (entryHuman.getValue().getTravel().getProgress() > getTravel().getProgress()) {
                                if (entryHuman.getValue().getTravel().getProgress() < (13000 / getTravel().getCurrentPlace().getSize() + getTravel().getProgress())) {
                                    delta = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            getTravel().setProgress(getTravel().getProgress() + (delta / getTravel().getCurrentPlace().getSize()) * 1000);
            if (getTravel().getProgress() > 1000) {
                getTravel().setProgress(1000);
            }
        }
    }

    /**
     * part of updateTravel(), go to next place if it is possible
     */
    protected void goToNextPlace() {
        //System.out.print(getId()+" "+getTravel().getCurrentPlace().getId()+" \n");
        if (getDead()) {
            return;
        }
        boolean isFree = true;
        if (getTravel().getNextPlace().getId() > 200) {//if it is road
            for (Entry<Integer, Human> entryHuman : getTravel().getNextPlace().getHumans().entrySet()) {
                if (entryHuman.getValue().getTravel().getLastPlace() == this.getTravel().getCurrentPlace()) {
                    if (entryHuman.getValue().getTravel().getProgress() < (40000 / getTravel().getNextPlace().getSize())) {
                        getTravel().setTime(0);
                        isFree = false;
                        break;
                    }
                }
            }
        }
        if (isFree) {
            getTravel().setProgress(0);
            getTravel().getCurrentPlace().removeHuman(getId());
            getTravel().getNextPlace().addHuman(getId(), this);
            getTravel().setLastPlace(getTravel().getCurrentPlace());
            if ((getTravel().getCurrentPlace().getId() > 100) && (getTravel().getCurrentPlace().getId() < 200)) {
                setIsOnCrossing(false);
                ((Crossing)getTravel().getCurrentPlace()).setHumanOnSemaphore(null);
                setOnSemaphore(null);
                ((Crossing)getTravel().getCurrentPlace()).getSemaphore().release();//semaphoreRelease();
                // System.out.print("tak  ");
                // System.out.print("poza crossing\n");
            }
            getTravel().setCurrentPlace(getTravel().getNextPlace());

        }
    }

    /**
     * abstract method have to be different for Citizen, SuperHero and Criminal
     * Main method in Human thread, is executed in infinite loop in run()
     */
    public abstract void updateTravel();
    
    /**
     * Function which is executes by thread
     */
    @Override
    public void run() {
        while (true) {
            if (world.getTheEnd()) {
                break;
            }
            if (getDead()) {

                if (getOnSemaphore() != null) {
                    ((Crossing)getOnSemaphore()).getSemaphore().release();//semaphoreRelease();
                   ((Crossing)getOnSemaphore()).setHumanOnSemaphore(null);
                }
                break;
            }
            try {
                if ((getPause())||(world.getPause())) {                    
                    Thread.sleep(20);  
                } else {
                    updateTravel();
                    Thread.sleep(20);  
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Human.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @return the forename
     */
    public String getForename() {
        return forename;
    }

    /**
     * @param forename the forename to set
     */
    public void setForename(String forename) {
        this.forename = forename;
    }

    /**
     * @return the travel
     */
    synchronized public Travel getTravel() {
        return travel;
    }

    /**
     * @param travel the travel to set
     */
    synchronized public void setTravel(Travel travel) {
        this.travel = travel;
    }

    /**
     * @return the oval
     */
    public Ellipse2D getOval() {
        return oval;
    }

    /**
     * @param oval the oval to set
     */
    public void setOval(Ellipse2D oval) {
        this.oval = oval;
    }

    /**
     * @return the pause
     */
    synchronized public Boolean getPause() {
        return pause;
    }

    /**
     * @param pause the pause to set
     */
    synchronized public void setPause(Boolean pause) {
        this.pause = pause;
    }

    /**
     * @return the dead
     */
    public Boolean getDead() {
        return dead;
    }

    /**
     * @param dead the dead to set
     */
    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    /**
     * @return the isOnCrossing
     */
    public Boolean getIsOnCrossing() {
        return isOnCrossing;
    }

    /**
     * @param isOnCrossing the isOnCrossing to set
     */
    public void setIsOnCrossing(Boolean isOnCrossing) {
        this.isOnCrossing = isOnCrossing;
    }

    /**
     * @return the onSemaphore
     */
    public Place getOnSemaphore() {
        return onSemaphore;
    }

    /**
     * @param onSemaphore the onSemaphore to set
     */
    public void setOnSemaphore(Place onSemaphore) {
        this.onSemaphore = onSemaphore;
    }

    /**
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * @param world the world to set
     */
    public void setWorld(World world) {
        this.world = world;
    }
}
