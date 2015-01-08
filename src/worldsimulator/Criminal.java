/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import static java.lang.Math.max;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Class defining attributes and behavior of criminals.
 *
 * @author Sebastian
 */
public class Criminal extends Hero {

    /**
     * Constructor
     *
     * @param id unique identification number of Human
     * @param forename of Criminal
     * @param travel Objet which hold all information about walking betweens
     * cities
     * @param health 1000 is max
     * @param intellect potential of this ability
     * @param force potential of this ability
     * @param speed potential of this ability
     * @param stamina potential of this ability
     * @param energy potential of this ability
     * @param fightingSkill potential of this ability
     * @param world the world in which he lives
     */
    public Criminal(int id, String forename, Travel travel, int health, int intellect, int force, int speed, int stamina, int energy, int fightingSkill, World world) {
        super(id, forename, travel, health, intellect, force, speed, stamina, energy, fightingSkill, world);
    }

    /**
     * Constructor
     *
     * @param id unique identification number of Human
     * @param forename of Criminal
     * @param travel Objet which hold all information about walking betweens
     * cities
     * @param world the world in which he lives
     */
    public Criminal(int id, String forename, Travel travel, World world) {
        super(id, forename, travel, world);
    }

    /**
     * Constructor
     *
     * @param id unique identification number of Human
     * @param forename of Criminal
     * @param world the world in which he lives
     */
    public Criminal(int id, String forename, World world) {
        super(id, forename, new Travel(), world);
    }

    /**
     * Criminal will attack specified city.
     *
     * @param city to attack
     */
    public void attackTheCity(City city) {
        getWorld().setNotification("   " + city.getName() + "\n   was attacked ");
        while (true) {
            if (city.getIsDamage()) {
                break;
            }
            city.setIsAttacked(true);
            //find SuperHero            
            for (Entry<Integer, Human> entryHuman : city.getHumans().entrySet()) {
                if (entryHuman.getValue() instanceof SuperHero) {
                    fight((SuperHero) entryHuman.getValue());
                }
            }
            if (getDead()) {
                city.setIsAttacked(false);
                break;
            }
            //sleep 5 sec
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
            }
            //kill 1 Citizen
            Random generator = new Random();
            if (generator.nextInt(2) == 0) {
                for (Entry<Integer, Human> entryHuman : city.getHumans().entrySet()) {
                    if (entryHuman.getValue() instanceof Citizen) {
                        getWorld().kill(entryHuman.getValue());
                        break;
                    }
                }
            }
            //increase power
            for (PowerSource entry : city.getPowerSources()) {
                if (entry.getPotential() >= 5) {
                    entry.setPotential(entry.getPotential() - 5);
                    switch (entry.getAbility()) {
                        case "intellect":
                            setIntellect(getIntellect() + 5);
                            break;
                        case "force":
                            setForce(getForce() + 5);
                            break;
                        case "speed":
                            setSpeed(getSpeed() + 5);
                            break;
                        case "stamina":
                            setStamina(getStamina() + 5);
                            break;
                        case "energy":
                            setEnergy(getEnergy() + 5);
                            break;
                        case "fightingSkill":
                            setFightingSkill(getFightingSkill() + 5);
                            break;
                    }
                } else {
                    switch (entry.getAbility()) {
                        case "intellect":
                            setIntellect(getIntellect() + entry.getPotential());
                            break;
                        case "force":
                            setForce(getForce() + entry.getPotential());
                            break;
                        case "speed":
                            setSpeed(getSpeed() + entry.getPotential());
                            break;
                        case "stamina":
                            setStamina(getStamina() + entry.getPotential());
                            break;
                        case "energy":
                            setEnergy(getEnergy() + entry.getPotential());
                            break;
                        case "fightingSkill":
                            setFightingSkill(getFightingSkill() + entry.getPotential());
                            break;
                    }
                    entry.setPotential(0);
                }
            }
            Boolean isAllPowerSourcesEmpty = true;
            for (PowerSource entry : city.getPowerSources()) {
                if (entry.getPotential() > 0) {
                    isAllPowerSourcesEmpty = false;
                    break;
                }
            }
            Boolean isNoCitizens = true;
            for (Entry<Integer, Human> humans : city.getHumans().entrySet()) {
                if (humans.getValue() instanceof Citizen) {
                    isNoCitizens = false;
                    break;
                }
            }
            if ((isAllPowerSourcesEmpty) && (isNoCitizens)) {
                destroyCity(city);
                city.setIsAttacked(false);
                getWorld().setNotification(" " + city.getName() + "\n was destroyed");
                break;
            }
        }
    }

    /**
     * Fight between Criminal and SuperHero, criminal always initiates the fight
     *
     * @param superHero enemy
     */
    private void fight(SuperHero superHero) {
        if (superHero.getIsFighting()) {
            return;
        } else {
            superHero.setIsFighting(true);
        }
        if (this.getSpeed() >= superHero.getSpeed()) {
            while (true) {
                if (hit("intellect", true, superHero)) {
                    getWorld().kill(superHero);
                    break;
                }
                if (hit("intellect", false, superHero)) {
                    getWorld().kill(this);
                    break;
                }
                if (hit("force", true, superHero)) {
                    getWorld().kill(superHero);
                    break;
                }
                if (hit("force", false, superHero)) {
                    getWorld().kill(this);
                    break;
                }
                if (hit("energy", true, superHero)) {
                    getWorld().kill(superHero);
                    break;
                }
                if (hit("energy", false, superHero)) {
                    getWorld().kill(this);
                    break;
                }
            }
        } else {
            while (true) {
                if (hit("intellect", false, superHero)) {
                    getWorld().kill(this);
                    break;
                }
                if (hit("intellect", true, superHero)) {
                    getWorld().kill(superHero);
                    break;
                }
                if (hit("force", false, superHero)) {
                    getWorld().kill(this);
                    break;
                }
                if (hit("force", true, superHero)) {
                    getWorld().kill(superHero);
                    break;
                }
                if (hit("energy", false, superHero)) {
                    getWorld().kill(this);
                    break;
                }
                if (hit("energy", true, superHero)) {
                    getWorld().kill(superHero);
                    break;
                }
            }
        }
    }

    /**
     * The hit function, part of fight (between superHero and Criminal)
     *
     * @param Ability which ability use to hit
     * @param criminalHits true criminal hits, false if superHero hits
     * @param superHero superhero from the battle
     * @return true if Hero is dead
     */
    private Boolean hit(String Ability, Boolean criminalHits, SuperHero superHero) {
        if (criminalHits) {
            switch (Ability) {
                case "intellect":
                    superHero.setHealth(superHero.getHealth() - max((getIntellect() * (getFightingSkill() / 10) - superHero.getStamina()), 1));
                    break;
                case "force":
                    superHero.setHealth(superHero.getHealth() - max((getForce() * (getFightingSkill() / 10) - superHero.getStamina()), 1));
                    break;
                case "energy":
                    superHero.setHealth(superHero.getHealth() - max((getEnergy() * (getFightingSkill() / 10) - superHero.getStamina()), 1));
                    break;
            }
            if (superHero.getHealth() > 0) {
                return false;
            } else {
                getWorld().setNotification("  SuperHero " + superHero.getForename() + " \n   lost the battle");
                superHero.setIsFighting(false);
                return true;
            }
        } else {
            switch (Ability) {
                case "intellect":
                    setHealth(getHealth() - max((superHero.getIntellect() * (superHero.getFightingSkill() / 10) - getStamina()), 1));
                    break;
                case "force":
                    setHealth(getHealth() - max((superHero.getForce() * (superHero.getFightingSkill() / 10) - getStamina()), 1));
                    break;
                case "energy":
                    setHealth(getHealth() - max((superHero.getEnergy() * (superHero.getFightingSkill() / 10) - getStamina()), 1));
                    break;
            }
            if (getHealth() > 0) {
                return false;
            } else {
                getWorld().setNotification("  SuperHero " + superHero.getForename() + " \n   win the battle");
                superHero.setIsFighting(false);
                return true;
            }
        }
    }

    /**
     * Set that city is damage, and check if all cities are damaged
     *
     * @param city
     */
    private void destroyCity(City city) {
        if (city.getIsDamage()) {
            return;
        }
        city.setIsDamage(true);
        getWorld().getNonDamagedCities().remove(city);
        Boolean isTheEnd = true;
        for (Entry<Integer, City> entry
                : getWorld()
                .getCities().entrySet()) {
            if (!entry.getValue().getIsDamage()) {
                isTheEnd = false;
                break;
            }
        }
        if (isTheEnd) {
            getWorld().setTheEnd(true);
        }
    }

    /**
     * part of updateTravel(), increase the progress in current place if can
     */
    @Override
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
                        if (((Crossing)getTravel().getNextPlace()).getHumanOnSemaphore() instanceof Citizen) {
                            getWorld().kill(((Crossing)getTravel().getNextPlace()).getHumanOnSemaphore());
                        } else if (((Crossing)getTravel().getNextPlace()).getHumanOnSemaphore() instanceof SuperHero) {
                            fight((SuperHero) ((Crossing)getTravel().getNextPlace()).getHumanOnSemaphore());
                        }
                    }
                }
                for (Entry<Integer, Human> entryHuman : getTravel().getCurrentPlace().getHumans().entrySet()) {
                    if (entryHuman.getValue().getId() != getId()) {
                        //if in the same direction
                        if (entryHuman.getValue().getTravel().getLastPlace() == this.getTravel().getLastPlace()) {
                            //if bigger progress than me
                            if (entryHuman.getValue().getTravel().getProgress() > getTravel().getProgress()) {
                                if (entryHuman.getValue().getTravel().getProgress() < (13000 / getTravel().getCurrentPlace().getSize() + getTravel().getProgress())) {
                                    if (entryHuman.getValue() instanceof Citizen) {
                                        getWorld().kill(entryHuman.getValue());
                                    } else if (entryHuman.getValue() instanceof Criminal) {
                                        delta = 0;
                                    } else {
                                        delta = 0;
                                        fight((SuperHero) entryHuman.getValue());
                                    }
                                    break;
                                }
                            }
                            //if in other direction
                        } else {
                            //if bigger progress than me
                            if ((1000 - entryHuman.getValue().getTravel().getProgress()) > getTravel().getProgress()) {
                                if ((1000 - entryHuman.getValue().getTravel().getProgress()) < (13000 / getTravel().getCurrentPlace().getSize() + getTravel().getProgress())) {
                                    if (entryHuman.getValue() instanceof Citizen) {
                                        getWorld().kill(entryHuman.getValue());
                                    } else if (entryHuman.getValue() instanceof SuperHero) {
                                        delta = 0;
                                        fight((SuperHero) entryHuman.getValue());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (getTravel().getCurrentPlace().getId() < 100) {
                for (Entry<Integer, Human> entryHuman : getTravel().getCurrentPlace().getHumans().entrySet()) {
                    if (entryHuman.getValue() instanceof SuperHero) {
                        fight((SuperHero) entryHuman.getValue());
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
     * part of updateTravel(), go to next place
     */
    @Override
    protected void goToNextPlace() {
        getTravel().setProgress(0);
        getTravel().getCurrentPlace().removeHuman(getId());
        getTravel().getNextPlace().addHuman(getId(), this);
        getTravel().setLastPlace(getTravel().getCurrentPlace());
        getTravel().setCurrentPlace(getTravel().getNextPlace());

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
                newTravel(getTravel().getCurrentPlace());
            }
            if (getTravel().getDestinationCity() != null) {
                //if it is end of the travel
                if (getTravel().getDestinationCity() == getTravel().getCurrentPlace()) {
                    if (!((City)getTravel().getDestinationCity()).getIsDamage()) {
                        attackTheCity((City) getTravel().getDestinationCity());
                    }
                    if (getDead()) {
                        return;
                    }
                    while (((City)getTravel().getDestinationCity()).getIsDamage()) {
                        Collections.shuffle(getWorld().getNonDamagedCities());
                        if (!getWorld().getNonDamagedCities().isEmpty()) {
                            newTravel(getTravel().getCurrentPlace(), getWorld().getNonDamagedCities().get(0));
                        }
                    }
                }
                if (getTravel().getDestinationCity() != null) {
                    //if dont know where to go
                    if (getTravel().getCurrentPlace() == getTravel().getNextPlace()) {
                        //then ask
                        if (getTravel().getLastPlace() != null) {
                            getTravel().setNextPlace(getTravel().getCurrentPlace().whichway(getTravel().getDestinationCity().getId(), getTravel().getLastPlace()));
                        } else {
                            newTravel(getTravel().getCurrentPlace(), getTravel().getDestinationCity());
                        }
                    }
                }
                //if it is the end of current place
                if (getTravel().getProgress() >= 1000) {
                    goToNextPlace();
                } else {
                    increaseProgress();
                }
            }
        }
    }

}
