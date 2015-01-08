/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import javafx.scene.shape.Polygon;

/**
 * class holding information about the road and its behavior.
 * @author Sebastian
 */
public class Road extends Place {

    private volatile int x2;
    private volatile int y2;
    private volatile Polygon polygon; //used to support mouse clicking on road

    /**
     * Contructor of road
     *
     * @param id unique identification number of Place
     * @param size This helps determine how much time need a man to walk
     * @param place1 place where road begins
     * @param place2 place where road ends
     *
     */
    public Road(int id, int size, Place place1, Place place2) {
        super(id, size, place1.getX(), place1.getY());
        this.x2 = place2.getX();
        this.y2 = place2.getY();
        this.addNeighbor(place1);
        this.addNeighbor(place2);
        place1.addNeighbor(this);
        place2.addNeighbor(this);
        this.polygon = new Polygon(0, 0, 0, 0);// fill later 
    }

    /**
     * Contructor of road, calculate the size
     * @param id unique identification number of Place
     * @param place1 place where road begins
     * @param place2 place where road ends
     */
    public Road(int id, Place place1, Place place2) {
        this(id, (int) Math.sqrt(Math.pow((place2.getX() - place1.getX()), 2) + Math.pow((place2.getY() - place1.getY()), 2)), place1, place2);
    }

    /**
     * Human asks how to go to town. It return the way.
     *
     * @param id city which you are going to
     * @param lastPlace
     * @return Place the other end of the road
     */
    @Override
    public Place whichway(int id, Place lastPlace) {
        if (lastPlace == getNeighbors().get(0)) {
            return getNeighbors().get(1);
        } else {
            return getNeighbors().get(0);
        }
    }

    /**
     * @return the x2
     */
    synchronized public int getX2() {
        return x2;
    }

    /**
     * @param x2 the x2 to set
     */
    synchronized public void setX2(int x2) {
        this.x2 = x2;
    }

    /**
     * @return the y2
     */
    synchronized public int getY2() {
        return y2;
    }

    /**
     * @param y2 the y2 to set
     */
    synchronized public void setY2(int y2) {
        this.y2 = y2;
    }

    /**
     * @return the polygon
     */
    public Polygon getPolygon() {
        return polygon;
    }

    /**
     * @param polygon the polygon to set
     */
    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
