/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import com.sun.javafx.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Class defining attributes of cities.
 *
 * @author Sebastian
 */
public class City extends Place {

    private volatile String name;
    private volatile int population;
    private volatile ArrayList<PowerSource> powerSources;
    private volatile Boolean isDamage;
    private volatile Boolean isAttacked;
    private volatile Ellipse2D oval;

    /**
     * Constructor of City
     *
     * @param name name of the city e.g. Poznan
     * @param id unique identification number of city
     * @param size This helps determine how much time need a man to walk
     * @param x coordinate x of the city
     * @param y coordinate y of the city
     */
    public City(String name, int id, int size, int x, int y) {
        super(id, size, x, y);
        this.name = name;
        this.population = 0;
        this.powerSources = new ArrayList<PowerSource>();
        this.isDamage = false;
        this.isAttacked = false;
        this.oval = new Ellipse2D(x, y, 70, 70);
    }

    /**
     * Add PowerSource to the City
     *
     * @param name unique name of PowerSource
     * @param ability PowerSource enhances this ability
     */
    public void addPowerSource(String name, String ability) {
        powerSources.add(new PowerSource(name, ability));
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the population
     */
    synchronized public int getPopulation() {
        return population;
    }

    /**
     * @param population the population to set
     */
    synchronized public void setPopulation(int population) {
        if (population >= 0) {
            this.population = population;
        } else {
            this.population = 0;
        }
    }

    /**
     * @return the powerSources
     */
    public ArrayList<PowerSource> getPowerSources() {
        return powerSources;
    }

    /**
     * @param powerSources the powerSources to set
     */
    public void setPowerSources(ArrayList powerSources) {
        this.powerSources = powerSources;
    }

    /**
     * @return the isDamage
     */
    synchronized public Boolean getIsDamage() {
        return isDamage;
    }

    /**
     * @param isDamage the isDamage to set
     */
    synchronized public void setIsDamage(Boolean isDamage) {
        this.isDamage = isDamage;
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
     * @return the isAttacked
     */
    synchronized public Boolean getIsAttacked() {
        return isAttacked;
    }

    /**
     * @param isAttacked the isAttacked to set
     */
    synchronized public void setIsAttacked(Boolean isAttacked) {
        this.isAttacked = isAttacked;
    }

}
