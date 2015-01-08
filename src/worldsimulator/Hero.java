/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

/**
 * Abstract class which contains the common fields of SuperHero and Criminal.
 *
 * @author Sebastian
 */
public abstract class Hero extends Human {

    private volatile int health;
    private volatile int intellect;
    private volatile int force;
    private volatile int speed;
    private volatile int stamina;
    private volatile int energy;
    private volatile int fightingSkill;

    /**
     * General constructor with all fields
     *
     * @param id unique identification number of Human
     * @param forename of Hero
     * @param travel set to 'null' best
     * @param health 1000 is max
     * @param intellect potential of this ability
     * @param force potential of this ability
     * @param speed potential of this ability
     * @param stamina potential of this ability
     * @param energy potential of this ability
     * @param fightingSkill potential of this ability
     * @param world the world in which he lives
     */
    public Hero(int id, String forename, Travel travel, int health, int intellect, int force, int speed, int stamina, int energy, int fightingSkill, World world) {
        super(id, forename, world);
        this.health = health;
        this.intellect = intellect;
        this.force = force;
        this.speed = speed;
        this.stamina = stamina;
        this.energy = energy;
        this.fightingSkill = fightingSkill;
    }

    /**
     * Constructor. Generate random values ​​<10; 100> and enter them to the
     * fields with abilities. Health is set to 1000 by default.
     *
     * @param id unique identification number of Human
     * @param forename of Hero
     * @param travel set to 'null' best
     * @param world the world in which he lives
     */
    public Hero(int id, String forename, Travel travel, World world) {
        super(id, forename, world);
        int[] tab = new int[6];
        for (int i = 0; i < 6; i++) {
            tab[i] = (int) Math.floor(Math.random() * 91 + 10);
        }
        this.health = 1000;
        this.intellect = tab[0];
        this.force = tab[1];
        this.speed = tab[2];
        this.stamina = tab[3];
        this.energy = tab[4];
        this.fightingSkill = tab[5];
    }

    /**
     * @return the helth
     */
    public int getHealth() {
        return health;
    }

    /**
     * @param health the health to set, truncates the value to the range
     * <0;1000>
     */
    public void setHealth(int health) {
        if (health <= 1000) {
            if (health < 0) {
                this.health = 0;
            } else {
                this.health = health;
            }
        } else {
            this.health = 1000;
        }
    }

    /**
     * @return the intellect
     */
    public int getIntellect() {
        return intellect;
    }

    /**
     * @param intellect the intellect to set
     */
    public void setIntellect(int intellect) {
        this.intellect = setAbility(intellect);
    }

    /**
     * @return the force
     */
    public int getForce() {
        return force;
    }

    /**
     * @param force the force to set
     */
    public void setForce(int force) {
        this.force = setAbility(force);
    }

    /**
     * @return the speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(int speed) {
        this.speed = setAbility(speed);
    }

    /**
     * @return the stamina
     */
    public int getStamina() {
        return stamina;
    }

    /**
     * @param stamina the stamina to set
     */
    public void setStamina(int stamina) {
        this.stamina = setAbility(stamina);
    }

    /**
     * @return the energy
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * @param energy the energy to set
     */
    public void setEnergy(int energy) {
        this.energy = setAbility(energy);
    }

    /**
     * @return the fightingSkill
     */
    public int getFightingSkill() {
        return fightingSkill;
    }

    /**
     * @param fightingSkill the fightingSkill to set
     */
    public void setFightingSkill(int fightingSkill) {
        this.fightingSkill = setAbility(fightingSkill);
    }

    /**
     * truncates the value to the range <0;100>
     *
     * @param potential
     * @return the same value if is between 0 and 100, 0 if less than 0, 100 if
     * more than 100
     */
    private int setAbility(int potential) {
        if (potential <= 100) {
            if (potential < 0) {
                return 0;
            } else {
                return potential;
            }
        } else {
            return 100;
        }
    }

}
