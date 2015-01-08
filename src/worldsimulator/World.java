/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class keeps all the basic objects.
 *
 * @author Sebastian
 */
public class World {

    private volatile int worldPopulation;
    private volatile int numberOfPeople;
    private volatile int power;
    private volatile int freePower;
    private volatile String notification;
    private volatile ConcurrentHashMap<Integer, Citizen> citizens;
    private volatile ConcurrentHashMap<Integer, Thread> citizensThreads;
    private volatile ConcurrentHashMap<Integer, Criminal> criminals;
    private volatile ConcurrentHashMap<Integer, Thread> criminalsThreads;
    private volatile ConcurrentHashMap<Integer, SuperHero> superHeroes;
    private volatile ConcurrentHashMap<Integer, Thread> superHeroesThreads;
    private volatile LinkedHashMap<Integer, City> cities;
    private volatile LinkedHashMap<Integer, Crossing> crossings;
    private volatile LinkedHashMap<Integer, Road> roads;
    private volatile FileSupport filesupport;
    private volatile Result result;
    private volatile Boolean theEnd;
    private volatile ArrayList<City> nonDamagedCities;
    private volatile Boolean pause;

    /**
     * Constructor create the map, initialize humans and threads
     */
    public World() {
        worldPopulation = 0;
        numberOfPeople = 0;
        citizens = new ConcurrentHashMap<Integer, Citizen>();
        citizensThreads = new ConcurrentHashMap<Integer, Thread>();
        criminals = new ConcurrentHashMap<Integer, Criminal>();
        criminalsThreads = new ConcurrentHashMap<Integer, Thread>();
        superHeroes = new ConcurrentHashMap<Integer, SuperHero>();
        superHeroesThreads = new ConcurrentHashMap<Integer, Thread>();
        cities = new LinkedHashMap<Integer, City>();
        crossings = new LinkedHashMap<Integer, Crossing>();
        roads = new LinkedHashMap<Integer, Road>();
        getMapFromFile();
        setSignposts();
        notification = "";
        filesupport = new FileSupport();
        result = new Result();
        theEnd = false;
        nonDamagedCities = new ArrayList<City>();
        createNonDamagedCities();
        pause=false;
    }

    /**
     * Add all cities to nonDamagedCities
     */
    private void createNonDamagedCities() {
        for (Entry<Integer, City> entry : getCities().entrySet()) {
            getNonDamagedCities().add(entry.getValue());
        }
    }

    /**
     * Create one new citizen and new Thread
     */
    synchronized public void addCitizen() {
        if (getFreePower() >= 5) {
            if (!getTheEnd()) {
                setWorldPopulation(getWorldPopulation() + 1);
                citizens.put(getWorldPopulation(), new Citizen(getWorldPopulation(), getFilesupport().readLineFromFile("/resources/forename.txt"), getFilesupport().readLineFromFile("/resources/lastname.txt"), this));
                citizens.get(getWorldPopulation()).getCity().setPopulation(citizens.get(getWorldPopulation()).getCity().getPopulation() + 1);
                citizensThreads.put(getWorldPopulation(), new Thread(citizens.get(getWorldPopulation())));
                citizensThreads.get(getWorldPopulation()).setDaemon(true);
                citizensThreads.get(getWorldPopulation()).start();
            }
        }
    }

    /**
     * Create one new SuperHero and new Thread
     */
    synchronized public void addSuperHero() {
        if (getFreePower() >= 100) {
            if (!getCities().get(12).getIsDamage()) {
                setWorldPopulation(getWorldPopulation() + 1);
                superHeroes.put(getWorldPopulation(), new SuperHero(getWorldPopulation(), getFilesupport().readLineFromFile("/resources/superheroname.txt"), this));
                superHeroesThreads.put(getWorldPopulation(), new Thread(superHeroes.get(getWorldPopulation())));
                superHeroesThreads.get(getWorldPopulation()).setDaemon(true);
                superHeroes.get(getWorldPopulation()).getTravel().setCurrentPlace(getCities().get(12));
                getCities().get(12).addHuman(getWorldPopulation(), superHeroes.get(getWorldPopulation()));
                superHeroesThreads.get(getWorldPopulation()).start();
            }
        }
    }

    /**
     * Create one new Criminal and new Thread
     */
    synchronized public void addCriminal() {
        if (!getTheEnd()) {
            setWorldPopulation(getWorldPopulation() + 1);
            criminals.put(getWorldPopulation(), new Criminal(getWorldPopulation(), getFilesupport().readLineFromFile("/resources/criminalname.txt"), this));
            criminalsThreads.put(getWorldPopulation(), new Thread(criminals.get(getWorldPopulation())));
            criminalsThreads.get(getWorldPopulation()).setDaemon(true);
            Random generator = new Random();
            criminals.get(getWorldPopulation()).getTravel().setCurrentPlace(cities.get(generator.nextInt(cities.size()) + 1));
            criminalsThreads.get(getWorldPopulation()).start();
        }
    }

    /**
     * Kill Human, remove from all list
     *
     * @param toKill Human object to kill
     */
    synchronized public void kill(Human toKill) {
        if (toKill.getDead()) {
            return;
        }
        toKill.setDead(true);
        try {
            Thread.sleep(55);
        } catch (InterruptedException ex) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((toKill instanceof Citizen)) {
            getCitizens().remove(toKill.getId());
            getCitizensThreads().remove(toKill.getId());
            ((Citizen)toKill).getCity().setPopulation(((Citizen)toKill).getCity().getPopulation() - 1);
            toKill.getTravel().getCurrentPlace().removeHuman(toKill.getId());
        } else if (toKill instanceof SuperHero) {
            getSuperHeroes().remove(toKill.getId());
            getSuperHeroesThreads().remove(toKill.getId());
            toKill.getTravel().getCurrentPlace().removeHuman(toKill.getId());
        } else if (toKill instanceof Criminal) {
            getCriminals().remove(toKill.getId());
            getCriminalsThreads().remove(toKill.getId());
            toKill.getTravel().getCurrentPlace().removeHuman(toKill.getId());
        }
    }

    /**
     * Increase Potential of Power Sources in all Cities
     */
    public void increasePowerSourcesPotential() {
        for (Entry<Integer, City> entryCity : cities.entrySet()) {
            if (!entryCity.getValue().getIsDamage()) {
                for (PowerSource powerSource : entryCity.getValue().getPowerSources()) {
                    if (entryCity.getValue().getPopulation() <= 2) {
                        powerSource.setPotential(powerSource.getPotential() + 1);
                    } else if (entryCity.getValue().getPopulation() <= 5) {
                        powerSource.setPotential(powerSource.getPotential() + 2);
                    } else if (entryCity.getValue().getPopulation() <= 8) {
                        powerSource.setPotential(powerSource.getPotential() + 3);
                    } else if (entryCity.getValue().getPopulation() <= 13) {
                        powerSource.setPotential(powerSource.getPotential() + 4);
                    } else if (entryCity.getValue().getPopulation() <= 18) {
                        powerSource.setPotential(powerSource.getPotential() + 5);
                    } else if (entryCity.getValue().getPopulation() <= 25) {
                        powerSource.setPotential(powerSource.getPotential() + 6);
                    } else if (entryCity.getValue().getPopulation() <= 35) {
                        powerSource.setPotential(powerSource.getPotential() + 7);
                    } else if (entryCity.getValue().getPopulation() <= 45) {
                        powerSource.setPotential(powerSource.getPotential() + 8);
                    } else if (entryCity.getValue().getPopulation() > 45) {
                        powerSource.setPotential(powerSource.getPotential() + 10);
                    }
                }
            }
        }
    }

    /**
     * Calculate Potential of all PowerSources in all Cities also Free Power and
     * set NumberOfPeople
     */
    public void calculatePowerAndPeople() {
        int tempPower = 0;
        for (Entry<Integer, City> entry : getCities().entrySet()) {
            if (!entry.getValue().getIsDamage()) {
                for (PowerSource powerSource : entry.getValue().getPowerSources()) {
                    tempPower += powerSource.getPotential();
                }
            }
        }
        setPower(tempPower);
        setFreePower(tempPower - ((getCitizens().size() * 5) + getSuperHeroes().size() * 100));
        setNumberOfPeople(getCitizens().size());
    }

    //used to create the world
    //<editor-fold>
    /**
     * Finds the shortest path between cities and sets the signposts.
     */
    private void setSignposts() {
        //System.out.println("setsignposts");
        HashMap<Integer, Integer> distance = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> previous = new HashMap<Integer, Integer>();
        ArrayList<Place> toVisit = new ArrayList<Place>();
        for (Entry<Integer, City> entry : cities.entrySet()) {
            if (entry.getValue().getNeighbors().size() > 1) {

                dijkstra(toVisit, distance, previous, entry.getValue());
                for (Entry<Integer, City> city : cities.entrySet()) {
                    if (city != entry) {
                        int cityid = city.getValue().getId();
                        while (cityid != entry.getValue().getId()) {
                            if (previous.get(cityid) == entry.getValue().getId()) {
                                int i;
                                for (i = 0; i < entry.getValue().getNeighbors().size(); i++) {
                                    if (entry.getValue().getNeighbors().get(i).getId() == cityid) {
                                        break;
                                    }
                                }
                                entry.getValue().getSignpost().put(city.getValue().getId(), i);
                                //entry.getValue().getSignpost().put(city.getValue().getId(), entry.getValue().getNeighbors().indexOf(city.getValue()));
                                //what the fuck is going on
                                //System.out.println(entry.getValue().getSignpost().get(city.getValue().getId()));
                            }

                            cityid = previous.get(cityid);

                        }

                    }
                }
            } else {
                for (Entry<Integer, City> city : cities.entrySet()) {
                    if (city != entry) {
                        entry.getValue().getSignpost().put(city.getValue().getId(), 0);
                    }
                }
            }
        }
        for (Entry<Integer, Crossing> entry : crossings.entrySet()) {
            if (entry.getValue().getNeighbors().size() > 1) {
                dijkstra(toVisit, distance, previous, entry.getValue());
                for (Entry<Integer, City> city : cities.entrySet()) {

                    int cityid = city.getValue().getId();
                    while (cityid != entry.getValue().getId()) {
                        if (previous.get(cityid) == entry.getValue().getId()) {
                            int i;
                            for (i = 0; i < entry.getValue().getNeighbors().size(); i++) {
                                if (entry.getValue().getNeighbors().get(i).getId() == cityid) {
                                    break;
                                }
                            }
                            entry.getValue().getSignpost().put(city.getValue().getId(), i);
                            //System.out.println(entry.getValue().getSignpost().get(city.getValue().getId()));
//entry.getValue().getSignpost().put(city.getValue().getId(), entry.getValue().getNeighbors().indexOf(city.getValue()));
                        }
                        cityid = previous.get(cityid);
                    }

                }
            } else {
                for (Entry<Integer, Crossing> city : crossings.entrySet()) {
                    if (city != entry) {
                        entry.getValue().getSignpost().put(city.getValue().getId(), 0);
                    }
                }
            }
        }
        distance.clear();
        previous.clear();
        toVisit.clear();
    }

    /**
     * Part of setSignpost();
     */
    private void dijkstra(ArrayList<Place> toVisit, HashMap<Integer, Integer> distance, HashMap<Integer, Integer> previous, Place entry) {
        HashMap<Integer, Place> notVisit = new HashMap<Integer, Place>();
        notVisit.clear();
        toVisit.clear();
        distance.clear();
        previous.clear();
        toVisit.add(entry);
        distance.put(entry.getId(), 0);
        previous.put(entry.getId(), entry.getId());
        while (!toVisit.isEmpty()) {
            for (Place place : toVisit.get(0).getNeighbors()) {
                visitPlace(place, toVisit.get(0), distance, previous);
                int i;
                for (i = 0; i < toVisit.size(); i++) {
                    if (distance.get(toVisit.get(i).getId()) > distance.get(place.getId())) {
                        break;
                    }
                }
                if (!notVisit.containsKey(place.getId())) {
                    toVisit.add(i, place);
                }
            }
            notVisit.put(toVisit.get(0).getId(), toVisit.get(0));
            toVisit.remove(0);
        }
    }

    /**
     * Part of dijkstra();
     */
    private void visitPlace(Place currentPlace, Place previousPlace, HashMap<Integer, Integer> distance, HashMap<Integer, Integer> previous) {
        if (previousPlace == currentPlace) {
            distance.put(previousPlace.getId(), 0);
            previous.put(previousPlace.getId(), previousPlace.getId());
        } else {
            if (distance.containsKey(currentPlace.getId())) {
                if (distance.get(currentPlace.getId()) > (distance.get(previousPlace.getId()) + previousPlace.getSize())) {
                    distance.remove(currentPlace.getId());
                    distance.put(currentPlace.getId(), distance.get(previousPlace.getId()) + previousPlace.getSize());
                    previous.remove(currentPlace.getId());
                    previous.put(currentPlace.getId(), previousPlace.getId());
                }
            } else {
                distance.put(currentPlace.getId(), distance.get(previousPlace.getId()) + previousPlace.getSize());
                previous.put(currentPlace.getId(), previousPlace.getId());
            }
        }
    }

    /**
     * Get information about map from file and create Cities, Roads and
     * crossings.(temporarly without file support) number of cities - max 100
     * number of crossings - max 100
     */
    private void getMapFromFile() {
        //cities 
        getCities().put(1, new City("Poznań", 1, 100, 400, 610));
        getCities().put(2, new City("Wrocław", 2, 100, 400, 900));
        getCities().put(3, new City("Szczecin", 3, 100, 90, 370));
        getCities().put(4, new City("Gorzów Wielkopolski", 4, 100, 185, 550));
        getCities().put(5, new City("Gdańsk", 5, 100, 650, 180));
        getCities().put(6, new City("Opole", 6, 100, 570, 980));
        getCities().put(7, new City("Katowice", 7, 100, 720, 1080));
        getCities().put(8, new City("Kraków", 8, 100, 870, 1160));
        getCities().put(9, new City("Kielce", 9, 100, 960, 970));
        getCities().put(10, new City("Rzeszów", 10, 100, 1140, 1150));
        getCities().put(11, new City("Lublin", 11, 100, 1200, 800));
        getCities().put(12, new Capital("Warszawa", 12, 100, 960, 590));
        getCities().put(13, new City("Białystok", 13, 100, 1300, 440));
        getCities().put(14, new City("Olsztyn", 14, 100, 930, 300));
        getCities().put(15, new City("Łódź", 15, 100, 770, 780));
        getCities().put(16, new City("Bydgoszcz", 16, 100, 550, 420));
        getCities().put(17, new City("Toruń", 17, 100, 690, 470));
        //crossings
        crossings.put(101, new Crossing(101, 0, 280, 470));
        crossings.put(102, new Crossing(102, 0, 420, 310));
        crossings.put(103, new Crossing(103, 0, 500, 530));
        crossings.put(104, new Crossing(104, 0, 770, 390));
        crossings.put(105, new Crossing(105, 0, 600, 670));
        crossings.put(106, new Crossing(106, 0, 1090, 450));
        crossings.put(107, new Crossing(107, 0, 1260, 660));
        crossings.put(108, new Crossing(108, 0, 1020, 820));
        crossings.put(109, new Crossing(109, 0, 1210, 1030));
        crossings.put(110, new Crossing(110, 0, 1000, 1108));
        crossings.put(111, new Crossing(111, 0, 780, 990));
        crossings.put(112, new Crossing(112, 0, 550, 830));
        crossings.put(113, new Crossing(113, 0, 250, 770));
        crossings.put(114, new Crossing(114, 0, 950, 200));
        crossings.put(115, new Crossing(115, 0, 630, 1130));
        crossings.put(116, new Crossing(116, 0, 401, 1000));
        crossings.put(117, new Crossing(117, 0, 800, 650));
        //roads
        roads.put(201, new Road(201, cities.get(3), crossings.get(102)));
        roads.put(202, new Road(202, cities.get(3), crossings.get(101)));
        roads.put(203, new Road(203, crossings.get(101), crossings.get(102)));
        roads.put(204, new Road(204, crossings.get(101), crossings.get(103)));
        roads.put(205, new Road(205, crossings.get(102), crossings.get(103)));
        roads.put(206, new Road(206, crossings.get(102), cities.get(5)));
        roads.put(207, new Road(207, crossings.get(103), cities.get(16)));
        roads.put(208, new Road(208, crossings.get(102), crossings.get(104)));
        roads.put(209, new Road(209, cities.get(5), crossings.get(104)));
        roads.put(210, new Road(210, cities.get(4), crossings.get(101)));
        roads.put(211, new Road(211, cities.get(4), crossings.get(113)));
        roads.put(212, new Road(212, cities.get(17), crossings.get(104)));
        roads.put(213, new Road(213, cities.get(1), crossings.get(103)));
        roads.put(214, new Road(214, cities.get(1), crossings.get(113)));
        roads.put(215, new Road(215, cities.get(1), crossings.get(112)));
        roads.put(216, new Road(216, cities.get(1), crossings.get(105)));
        roads.put(217, new Road(217, cities.get(17), crossings.get(105)));
        roads.put(218, new Road(218, cities.get(17), crossings.get(103)));
        roads.put(219, new Road(219, cities.get(14), crossings.get(104)));
        roads.put(220, new Road(220, crossings.get(105), crossings.get(112)));
        roads.put(221, new Road(221, cities.get(13), crossings.get(106)));
        roads.put(222, new Road(222, cities.get(12), crossings.get(106)));
        roads.put(223, new Road(223, cities.get(12), crossings.get(108)));
        roads.put(224, new Road(224, cities.get(12), crossings.get(107)));
        roads.put(225, new Road(225, cities.get(12), crossings.get(117)));
        roads.put(226, new Road(226, cities.get(12), crossings.get(104)));
        roads.put(227, new Road(227, cities.get(14), crossings.get(106)));
        roads.put(228, new Road(228, cities.get(15), crossings.get(105)));
        roads.put(229, new Road(229, cities.get(15), crossings.get(108)));
        roads.put(230, new Road(230, cities.get(15), crossings.get(111)));
        roads.put(231, new Road(231, cities.get(15), crossings.get(112)));
        roads.put(232, new Road(232, cities.get(13), crossings.get(107)));
        roads.put(233, new Road(233, cities.get(11), crossings.get(107)));
        roads.put(234, new Road(234, cities.get(11), crossings.get(108)));
        roads.put(235, new Road(235, cities.get(11), crossings.get(109)));
        roads.put(236, new Road(236, cities.get(9), crossings.get(108)));
        roads.put(237, new Road(237, cities.get(9), crossings.get(109)));
        roads.put(238, new Road(238, cities.get(9), crossings.get(110)));
        roads.put(239, new Road(239, cities.get(9), crossings.get(111)));
        roads.put(240, new Road(240, cities.get(8), crossings.get(110)));
        roads.put(241, new Road(241, cities.get(10), crossings.get(110)));
        roads.put(242, new Road(242, cities.get(6), crossings.get(111)));
        roads.put(243, new Road(243, cities.get(7), crossings.get(111)));
        roads.put(244, new Road(244, cities.get(8), crossings.get(111)));
        roads.put(245, new Road(245, cities.get(10), crossings.get(109)));
        roads.put(246, new Road(246, cities.get(2), crossings.get(112)));
        roads.put(247, new Road(247, cities.get(6), crossings.get(112)));
        roads.put(248, new Road(248, cities.get(2), crossings.get(113)));
        roads.put(249, new Road(249, crossings.get(106), crossings.get(107)));
        roads.put(250, new Road(250, crossings.get(114), cities.get(14)));
        roads.put(251, new Road(251, crossings.get(114), cities.get(5)));
        roads.put(252, new Road(252, crossings.get(114), cities.get(13)));
        roads.put(253, new Road(253, crossings.get(115), cities.get(8)));
        roads.put(254, new Road(254, crossings.get(115), cities.get(6)));
        roads.put(255, new Road(255, crossings.get(115), cities.get(7)));
        roads.put(256, new Road(256, crossings.get(116), cities.get(2)));
        roads.put(257, new Road(257, crossings.get(116), cities.get(6)));
        roads.put(258, new Road(258, crossings.get(116), crossings.get(115)));
        roads.put(259, new Road(259, crossings.get(117), cities.get(15)));
        roads.put(260, new Road(260, crossings.get(117), cities.get(17)));
        roads.put(261, new Road(261, crossings.get(105), crossings.get(117)));
        roads.put(262, new Road(262, crossings.get(101), cities.get(1)));

        //set powersources
        getCities().get(1).addPowerSource("Rogal Świętomarciński", "energy");
        getCities().get(1).addPowerSource("Pyra Wzmocnienia", "force");
        getCities().get(1).addPowerSource("Kozły Czasu", "speed");
        getCities().get(2).addPowerSource("Most Nieskończoności", "speed");
        getCities().get(2).addPowerSource("Pręgierz Skazańców", "stamina");
        getCities().get(3).addPowerSource("Paprykarz Szczeciński", "stamina");
        getCities().get(4).addPowerSource("Bułka z Pieczarkami", "energy");
        getCities().get(4).addPowerSource("Tarcza Antyrakietowa", "force");
        getCities().get(5).addPowerSource("Płot solidarności", "stamina");
        getCities().get(6).addPowerSource("Mikrofon Ogłuszania", "force");
        getCities().get(7).addPowerSource("Spodek Rozrywki", "speed");
        getCities().get(8).addPowerSource("Jaja Smoka", "fightingSkill");
        getCities().get(8).addPowerSource("Smog Wawelski", "stamina");
        getCities().get(9).addPowerSource("Kołczan Prawilności", "intellect");
        getCities().get(10).addPowerSource("Moherowy Beret", "speed");
        getCities().get(11).addPowerSource("Koziołek Zwinności", "stamina");
        getCities().get(11).addPowerSource("Perła Niepasteryzowana", "force");
        getCities().get(12).addPowerSource("Tęcza Sporu", "fightingSkill");
        getCities().get(12).addPowerSource("Słoik Przybysza", "stamina");
        getCities().get(12).addPowerSource("Syrenka Zwycięstwa", "force");
        getCities().get(13).addPowerSource("Szalik Jagielloni", "fightingSkill");
        getCities().get(13).addPowerSource("Żubr Przeznaczenia", "force");
        getCities().get(14).addPowerSource("Jezioro Mocy", "energy");
        getCities().get(15).addPowerSource("Wiosło Intelektu", "intellect");
        getCities().get(16).addPowerSource("Kask Golloba", "speed");
        getCities().get(17).addPowerSource("Piernik Toruński", "energy");
        getCities().get(17).addPowerSource("Radio Dyrektora", "intellect");
    }
    //</editor-fold>
    //setters and getters
    //<editor-fold>

    /**
     * @return the citizens
     */
    synchronized public ConcurrentHashMap<Integer, Citizen> getCitizens() {
        return citizens;
    }

    /**
     * @param citizens the citizens to set
     */
    synchronized public void setCitizens(ConcurrentHashMap<Integer, Citizen> citizens) {
        this.citizens = citizens;
    }

    /**
     * @return the citizensThreads
     */
    synchronized public ConcurrentHashMap<Integer, Thread> getCitizensThreads() {
        return citizensThreads;
    }

    /**
     * @param citizensThreads the citizensThreads to set
     */
    synchronized public void setCitizensThreads(ConcurrentHashMap<Integer, Thread> citizensThreads) {
        this.citizensThreads = citizensThreads;
    }

    /**
     * @return the criminals
     */
    synchronized public ConcurrentHashMap<Integer, Criminal> getCriminals() {
        return criminals;
    }

    /**
     * @param criminals the criminals to set
     */
    synchronized public void setCriminals(ConcurrentHashMap<Integer, Criminal> criminals) {
        this.criminals = criminals;
    }

    /**
     * @return the criminalsThreads
     */
    synchronized public ConcurrentHashMap<Integer, Thread> getCriminalsThreads() {
        return criminalsThreads;
    }

    /**
     * @param criminalsThreads the criminalsThreads to set
     */
    synchronized public void setCriminalsThreads(ConcurrentHashMap<Integer, Thread> criminalsThreads) {
        this.criminalsThreads = criminalsThreads;
    }

    /**
     * @return the superHeroes
     */
    synchronized public ConcurrentHashMap<Integer, SuperHero> getSuperHeroes() {
        return superHeroes;
    }

    /**
     * @param superHeroes the superHeroes to set
     */
    synchronized public void setSuperHeroes(ConcurrentHashMap<Integer, SuperHero> superHeroes) {
        this.superHeroes = superHeroes;
    }

    /**
     * @return the superHeroesThreads
     */
    synchronized public ConcurrentHashMap<Integer, Thread> getSuperHeroesThreads() {
        return superHeroesThreads;
    }

    /**
     * @param superHeroesThreads the superHeroesThreads to set
     */
    synchronized public void setSuperHeroesThreads(ConcurrentHashMap<Integer, Thread> superHeroesThreads) {
        this.superHeroesThreads = superHeroesThreads;
    }

    /**
     * @return the cities
     */
    public LinkedHashMap<Integer, City> getCities() {
        return cities;
    }

    /**
     * @param cities the cities to set
     */
    public void setCities(LinkedHashMap<Integer, City> cities) {
        this.cities = cities;
    }

    /**
     * @return the crossings
     */
    public LinkedHashMap<Integer, Crossing> getCrossings() {
        return crossings;
    }

    /**
     * @param crossings the crossings to set
     */
    public void setCrossings(LinkedHashMap<Integer, Crossing> crossings) {
        this.crossings = crossings;
    }

    /**
     * @return the roads
     */
    public LinkedHashMap<Integer, Road> getRoads() {
        return roads;
    }

    /**
     * @param roads the roads to set
     */
    public void setRoads(LinkedHashMap<Integer, Road> roads) {
        this.roads = roads;
    }

    /**
     * @return the numberOfPeople
     */
    synchronized public int getNumberOfPeople() {
        return numberOfPeople;
    }

    /**
     * @param numberOfPeople the numberOfPeople to set
     */
    synchronized public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    /**
     * @return the worldPopulation
     */
    synchronized public int getWorldPopulation() {
        return worldPopulation;
    }

    /**
     * @param worldPopulation the worldPopulation to set
     */
    synchronized public void setWorldPopulation(int worldPopulation) {
        this.worldPopulation = worldPopulation;
    }

    /**
     * @return the power
     */
    public int getPower() {
        return power;
    }

    /**
     * @param power the power to set
     */
    public void setPower(int power) {
        this.power = power;
    }

    /**
     * @return the freePower
     */
    public int getFreePower() {
        return freePower;
    }

    /**
     * @param freePower the freePower to set
     */
    public void setFreePower(int freePower) {
        this.freePower = freePower;
    }

    /**
     * @return the notification
     */
    public String getNotification() {
        return notification;
    }

    /**
     * @param notification the notification to set
     */
    public void setNotification(String notification) {
        this.notification = notification;
    }

    /**
     * @return the result
     */
    public Result getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * @return the theEnd
     */
    synchronized public Boolean getTheEnd() {
        return theEnd;
    }

    /**
     * @param theEnd the theEnd to set
     */
    synchronized public void setTheEnd(Boolean theEnd) {
        this.theEnd = theEnd;
    }

    /**
     * @return the filesupport
     */
    public FileSupport getFilesupport() {
        return filesupport;
    }

    /**
     * @param filesupport the filesupport to set
     */
    public void setFilesupport(FileSupport filesupport) {
        this.filesupport = filesupport;
    }
    //</editor-fold>

    /**
     * @return the nonDamagedCities
     */
    synchronized public ArrayList<City> getNonDamagedCities() {
        return nonDamagedCities;
    }

    /**
     * @param nonDamagedCities the nonDamagedCities to set
     */
    synchronized public void setNonDamagedCities(ArrayList<City> nonDamagedCities) {
        this.nonDamagedCities = nonDamagedCities;
    }

    /**
     * @return the pause
     */
    public Boolean getPause() {
        return pause;
    }

    /**
     * @param pause the pause to set
     */
    public void setPause(Boolean pause) {
        this.pause = pause;
    }
}
