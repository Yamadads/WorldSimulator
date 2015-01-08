/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import java.io.Serializable;

/**
 * Class which return time of the game .Object of this class are serialized to
 * the file as 5 the best results of the game
 *
 * @author Sebastian
 */
public class Result implements Serializable, Comparable<Result> {

    private transient volatile long startTime;
    private volatile String name;
    private volatile long gameTime;

    /**
     * Constructor, set the initial values 
     */
    public Result() {
        name = "";
        gameTime = 0;
        start();
    }

    /**
     * calculates the total time of the game
     *
     * @param pauseTime the time when game was stopped is not included
     */
    public void endGame(long pauseTime) {
        gameTime = System.currentTimeMillis() - startTime - pauseTime;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Set startTime at actual time
     */
    public void start() {
        startTime = System.currentTimeMillis();
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
     * @return the gameTime
     */
    public long getGameTime() {
        return gameTime;
    }

    /**
     * @param gameTime the gameTime to set
     */
    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    /**
     * Comparing two Results, needed to sorting in Collections
     *
     * @param other other Result object
     * @return
     */
    @Override
    public int compareTo(Result other) {
        if (getGameTime() < other.getGameTime()) {
            return 1;
        } else if (getGameTime() > other.getGameTime()) {
            return -1;
        }
        return 0;
    }

}
