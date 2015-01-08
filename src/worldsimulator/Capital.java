/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import javafx.scene.control.ListView;

/**
 * Special class for one of the cities. Capital can sand superheroes for help.
 *
 * @author Sebastian
 */
public class Capital extends City {

    /**
     * Constructor of Capital 
     *
     * @param name name of the city e.g. Poznan
     * @param id unique identification number of city
     * @param size This helps determine how much time need a man to walk
     * @param x coordinate x of the city
     * @param y coordinate y of the city
     */
    public Capital(String name, int id, int size, int x, int y) {
        super(name, id, size, x, y);
    }

    /**
     * Kill criminal by sending a superhero to help the city.
     *
     * @param listOfCities2 ListView object from GUI
     * @param cities List of cities in the world object
     */
    public void sendSuperhero(ListView<String> listOfCities2, LinkedHashMap<Integer, City> cities) {
        for (Entry<Integer, Human> entry : getHumans().entrySet()) {
            if (entry.getValue() instanceof SuperHero) {
                if (!entry.getValue().getDead()) {
                    if (((SuperHero) entry.getValue()).getGoTo() == null) {
                        if ((listOfCities2.getSelectionModel().getSelectedIndex() > -1) && (listOfCities2.getSelectionModel().getSelectedIndex() < 17) && (listOfCities2.getSelectionModel().getSelectedIndex() != 11)) {
                            ((SuperHero) entry.getValue()).setGoTo(cities.get(listOfCities2.getSelectionModel().getSelectedIndex() + 1));
                            break;
                        }
                    }
                }
            }
        }
    }
}
