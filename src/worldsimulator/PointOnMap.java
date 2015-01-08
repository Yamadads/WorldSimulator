/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

/**
 * Class which hold the coordinates, and id
 *
 * @author Sebastian
 */
public class PointOnMap {

    private volatile int id;
    private volatile int x;
    private volatile int y;

    /**
     * Constructor of PointOnMap set coordinate
     *
     * @param x coordinate x of point
     * @param y coordinate y of point
     * @param id unique identification number of object
     */
    public PointOnMap(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    /**
     * Constructor of PointOnMap set coordinate to 0,0
     *
     * @param id unique identification number of object
     */
    public PointOnMap(int id) {
        this.x = 0;
        this.y = 0;
        this.id = id;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
