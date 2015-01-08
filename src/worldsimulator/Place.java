/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract class .
 *
 * @author Sebastian
 */
public abstract class Place extends PointOnMap {

    private volatile int size;
    private volatile ArrayList<Place> neighbors;
    private volatile HashMap<Integer, Integer> signpost;
    private volatile ConcurrentHashMap<Integer, Human> humans;

    /**
     * Contructor of place
     *
     * @param id unique identification number
     * @param size This helps determine how much time need a man to walk
     * @param x coordinate x of the object
     * @param y coordinate y of the object
     */
    public Place(int id, int size, int x, int y) {
        super(x, y, id);
        this.size = size;
        this.signpost = new HashMap<Integer, Integer>();
        this.neighbors = new ArrayList<Place>();
        this.humans = new ConcurrentHashMap<Integer, Human>();
    }

    /**
     * Human asks how to go to town. It return the way.
     *
     * @param idOfDestinationCity
     * @param lastPlace
     * @return object which should be next in Human travel
     */
    public Place whichway(int idOfDestinationCity, Place lastPlace) {
        return neighbors.get(signpost.get(idOfDestinationCity));

    }

    /**
     * Add Human to local place collection
     *
     * @param id of Human
     * @param human Object of Human to add
     */
    synchronized public void addHuman(Integer id, Human human) {
        if (human != null) {
            humans.put(id, human);
        }
    }

    /**
     * Remove Human from local place collection
     *
     * @param id of Human
     */
    synchronized public void removeHuman(Integer id) {
        humans.remove(id);
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the signpost
     */
    public HashMap<Integer, Integer> getSignpost() {
        return signpost;
    }

    /**
     * @param signpost the signpost to set
     */
    public void setSignpost(HashMap<Integer, Integer> signpost) {
        this.signpost = signpost;
    }

    /**
     * @return the neighbors
     */
    public ArrayList<Place> getNeighbors() {
        return neighbors;
    }

    /**
     * @param neighbors the neighbors to set
     */
    public void setNeighbors(ArrayList<Place> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Adds place connected with that place to arraylist of neighbors
     *
     * @param place
     */
    public void addNeighbor(Place place) {
        neighbors.add(place);
    }

    /**
     * @return the humans
     */
    public ConcurrentHashMap<Integer, Human> getHumans() {
        return humans;
    }

    /**
     * @param humans the humans to set
     */
    synchronized public void setHumans(ConcurrentHashMap<Integer, Human> humans) {
        this.humans = humans;
    }
}
