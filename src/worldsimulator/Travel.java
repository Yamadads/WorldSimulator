/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

/**
 * Objects of this class hold all informations about Human's travels.
 *
 * @author Sebastian
 */
public class Travel {

    private volatile Place destinationCity;
    private volatile Place currentPlace;
    private volatile Place nextPlace;
    private volatile Place lastPlace;
    private volatile Double progress;
    private volatile long time;
    private volatile Place recentlyVisited;

    /**
     * Constructor. all null
     */
    public Travel() {
        this.destinationCity = null;
        this.currentPlace = null;
        this.nextPlace = null;
        this.lastPlace = null;
        this.progress = 0.0;
        this.time = 0;
        this.recentlyVisited = null;
    }

    /**
     * Constructor. It selects some variable
     *
     * @param destinationCity
     * @param currentPlace
     * @param nextPlace
     */
    public Travel(Place destinationCity, Place currentPlace, Place nextPlace) {
        this.destinationCity = destinationCity;
        this.currentPlace = currentPlace;
        this.nextPlace = nextPlace;
        this.lastPlace = currentPlace;
        this.progress = 0.0;
        this.time = 0;
        this.recentlyVisited = currentPlace;
    }

    /**
     * @return the destinationCity
     */
    synchronized public Place getDestinationCity() {
        return destinationCity;
    }

    /**
     * @param destinationCity the destinationCity to set
     */
    synchronized public void setDestinationCity(Place destinationCity) {
        this.destinationCity = destinationCity;
    }

    /**
     * @return the currentPlace
     */
    synchronized public Place getCurrentPlace() {
        return currentPlace;
    }

    /**
     * @param currentPlace the currentPlace to set
     */
    synchronized public void setCurrentPlace(Place currentPlace) {
        this.currentPlace = currentPlace;
    }

    /**
     * @return the nextPlace
     */
    synchronized public Place getNextPlace() {
        return nextPlace;
    }

    /**
     * @param nextPlace the nextPlace to set
     */
    synchronized public void setNextPlace(Place nextPlace) {
        this.nextPlace = nextPlace;
    }

    /**
     * @return the progress
     */
    synchronized public double getProgress() {
        return progress;
    }

    /**
     * @param progress the progress to set
     */
    synchronized public void setProgress(double progress) {
        this.progress = progress;
    }

    /**
     * @return the time
     */
    synchronized public long getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    synchronized public void setTime(long time) {
        this.time = time;
    }

    /**
     * @return the lastPlace
     */
    synchronized public Place getLastPlace() {
        return lastPlace;
    }

    /**
     * @param lastPlace the lastPlace to set
     */
    synchronized public void setLastPlace(Place lastPlace) {
        this.lastPlace = lastPlace;
    }

    /**
     * @return the recentlyVisited
     */
    public Place getRecentlyVisited() {
        return recentlyVisited;
    }

    /**
     * @param recentlyVisited the recentlyVisited to set
     */
    public void setRecentlyVisited(Place recentlyVisited) {
        this.recentlyVisited = recentlyVisited;
    }
}
