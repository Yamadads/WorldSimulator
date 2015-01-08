/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import java.util.concurrent.Semaphore;

/**
 * Class describing the crossroads at which civilians, superheroes and criminals
 * will walk. It will control traffic.
 *
 * @author Sebastian
 */
public class Crossing extends Place {

    private Semaphore semaphore;
    private Human humanOnSemaphore;

    /**
     * Contructor of crossing
     *
     * @param id
     * @param size This helps determine how much time need a man to walk
     * @param x coordinate x of the crossing
     * @param y coordinate y of the crossing
     */
    public Crossing(int id, int size, int x, int y) {
        super(id, size, x, y);
        semaphore = new Semaphore(1, true);
        this.humanOnSemaphore = null;
    }

    /**
     * @return the humanOnSemaphore
     */
    synchronized public Human getHumanOnSemaphore() {
        return humanOnSemaphore;
    }

    /**
     * @param humanOnSemaphore the humanOnSemaphore to set
     */
    synchronized public void setHumanOnSemaphore(Human humanOnSemaphore) {
        this.humanOnSemaphore = humanOnSemaphore;
    }

    /**
     * @return the semaphore
     */
    synchronized public Semaphore getSemaphore() {        
        return semaphore;
    }
}
