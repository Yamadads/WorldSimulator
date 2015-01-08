/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import java.util.Random;

/**
 * It holds information about power sources.
 *
 * @author Sebastian
 */
public class PowerSource {

    static volatile int nextId = 0;
    private volatile int id;
    private volatile String name;
    private volatile int potential;
    private volatile String ability;

    /**
     * Constructor
     *
     * @param name unique name of PowerSource
     * @param ability PowerSource enhances this ability
     */
    public PowerSource(String name, String ability) {
        this.id = PowerSource.nextId;
        PowerSource.nextId++;
        this.name = name;
        this.ability = ability;
        Random generator = new Random();
        this.potential = generator.nextInt(6);
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
     * @return the potential
     */
    public int getPotential() {
        return potential;
    }

    /**
     * @param potential the potential to set
     */
    public void setPotential(int potential) {
        if (potential <= 100) {
            if (potential < 0) {
                this.potential = 0;
            } else {
                this.potential = potential;
            }
        } else {
            this.potential = 100;
        }
    }

    /**
     * @return the ability
     */
    public String getAbility() {
        return ability;
    }

    /**
     * @param ability the ability to set
     */
    public void setAbility(String ability) {
        this.ability = ability;
    }

}
