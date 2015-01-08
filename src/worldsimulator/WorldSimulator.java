/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main class
 *
 * @author Sebastian
 */
public class WorldSimulator extends Application {

    private Image poland;
    private Image endPoland;
    private Image cityImage;
    private Image cityDamageImage;
    private Image cityAttackedImage;
    private Image crossingImage;
    private Image capitalImage;
    private Image capitalDamageImage;
    private Image capitalAttackedImage;
    private Group root;
    private BorderPane borderPane;
    private HBox menuPane;
    private Button ranking;
    private Button pause;
    private volatile long pauseTime;
    private volatile long pauseStartTime;
    private Button play;
    private Label powerInfoL;
    private Label powerInfoR;
    private Button addCitizen;
    private Label addCitizenLabel;
    private Button addSuperHero;
    private Label addSuperHeroLabel;
    private Label infoBoxLabel;
    private Label infoBoxTextArea;
    private Button stopCitizen;
    private Button runCitizen;
    private Button killCitizen;
    private Button changeCity;
    private Button sendSuperHero;
    private VBox citizenVBox;
    private VBox capitalVBox;
    private Canvas layer1;
    private Canvas layer2;
    private Canvas layer3;
    private Canvas layer4;
    private Canvas layer5;
    private GraphicsContext graphicsContext1;
    private GraphicsContext graphicsContext2;
    private GraphicsContext graphicsContext3;
    private GraphicsContext graphicsContext4;
    private GraphicsContext graphicsContext5;
    private double mousePositionX;
    private double mousePositionY;
    private double mapPositionXTemp;
    private double mapPositionYTemp;
    private double mapPositionX;
    private double mapPositionY;
    private World world;
    private Object infoBoxObject;
    private ListView<String> listOfCities;
    private ListView<String> listOfCities2;
    private long time;
    private long lapTime;
    private long criminalTime;
    private long powerSourceTime;
    private Stage dialogStageGetName;
    private Stage dialogStageRanking;
    private Button button;
    private TextField textField;

    /**
     * It supports resizing the form
     *
     * @param stage
     */
    private void windowResizeHandling(Stage stage) {
        //check if windows resize (width)
        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                //resize layers                
                layer1.setWidth((double) newSceneWidth - 175);
                layer2.setWidth((double) newSceneWidth - 175);
                layer3.setWidth((double) newSceneWidth - 175);
                layer4.setWidth((double) newSceneWidth - 175);
                layer5.setWidth((double) newSceneWidth - 175);
                //draw image again
                graphicsContext5.fillRect(0, 0, (double) newSceneWidth - 175, layer1.getHeight());
                if (world.getTheEnd()) {
                    graphicsContext5.drawImage(endPoland, mapPositionX, mapPositionY, 1500, 1458);
                } else {
                    graphicsContext5.drawImage(poland, mapPositionX, mapPositionY, 1500, 1458);
                }
                drawCities(world.getCities(), mapPositionX, mapPositionY);
                drawCrossings(world.getCrossings(), mapPositionX, mapPositionY);
                drawRoads(world.getRoads(), mapPositionX, mapPositionY);
                drawHuman(world.getRoads(), world.getCrossings(), mapPositionX, mapPositionY);
            }
        });
        //check if windows resize (height)
        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                //resize layers
                layer1.setHeight((double) newSceneHeight - 38);
                layer2.setHeight((double) newSceneHeight - 38);
                layer3.setHeight((double) newSceneHeight - 38);
                layer4.setHeight((double) newSceneHeight - 38);
                layer5.setHeight((double) newSceneHeight - 38);
                //draw image again
                graphicsContext5.fillRect(0, 0, layer1.getWidth(), (double) newSceneHeight - 38);
                if (world.getTheEnd()) {
                    graphicsContext5.drawImage(endPoland, mapPositionX, mapPositionY, 1500, 1458);
                } else {
                    graphicsContext5.drawImage(poland, mapPositionX, mapPositionY, 1500, 1458);
                }
                drawCities(world.getCities(), mapPositionX, mapPositionY);
                drawCrossings(world.getCrossings(), mapPositionX, mapPositionY);
                drawRoads(world.getRoads(), mapPositionX, mapPositionY);
                drawHuman(world.getRoads(), world.getCrossings(), mapPositionX, mapPositionY);
            }
        });
    }

    /**
     * It supports "Drag and Drop" map scrolling
     */
    private void mapScrollHandling() {

        layer1.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //System.out.println("X: " + mouseEvent.getX() + " Y: " + mouseEvent.getY());
                mousePositionX = mouseEvent.getX();
                mousePositionY = mouseEvent.getY();
                //double newMapPositionX = mapPositionX - (mousePositionX - mouseEvent.getX());
                //double newMapPositionY = mapPositionY - (mousePositionY - mouseEvent.getY());
                mapPositionXTemp = mapPositionX - (mousePositionX - mouseEvent.getX());
                mapPositionYTemp = mapPositionY - (mousePositionY - mouseEvent.getY());
                if (mapPositionXTemp > -1) {
                    mapPositionXTemp = -1;
                }
                if (mapPositionYTemp > -2) {
                    mapPositionYTemp = -2;
                }
                drawHuman(world.getRoads(), world.getCrossings(), mapPositionXTemp, mapPositionYTemp);
            }
        });

        layer1.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double newMapPositionX = mapPositionXTemp - (mousePositionX - mouseEvent.getX());
                double newMapPositionY = mapPositionYTemp - (mousePositionY - mouseEvent.getY());
                if (newMapPositionX > -1) {
                    newMapPositionX = -1;
                }
                if (newMapPositionY > -2) {
                    newMapPositionY = -2;
                }
                if (newMapPositionX < layer1.getWidth() - 1501) {
                    newMapPositionX = layer1.getWidth() - 1501;
                }
                if (newMapPositionY < layer1.getHeight() - 1460) {
                    newMapPositionY = layer1.getHeight() - 1460;
                }
                mapPositionX = newMapPositionX;
                mapPositionY = newMapPositionY;
                if (world.getTheEnd()) {
                    graphicsContext5.drawImage(endPoland, newMapPositionX, newMapPositionY, 1500, 1458);
                } else {
                    graphicsContext5.drawImage(poland, newMapPositionX, newMapPositionY, 1500, 1458);
                }
                drawCities(world.getCities(), newMapPositionX, newMapPositionY);
                drawCrossings(world.getCrossings(), newMapPositionX, newMapPositionY);
                drawRoads(world.getRoads(), newMapPositionX, newMapPositionY);
                drawHuman(world.getRoads(), world.getCrossings(), newMapPositionX, newMapPositionY);
            }
        });
    }

    /**
     * Supports reaction at addCitizen button click
     */
    private void addCitizenButton() {
        addCitizen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if ((!world.getTheEnd()) && (!world.getPause())) {
                    world.addCitizen();
                }
            }
        });
    }

    /**
     * Supports reaction at addSuperHero button click
     */
    private void addSuperHeroButton() {
        addSuperHero.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if ((!world.getTheEnd()) && (!world.getPause())) {
                    world.addSuperHero();
                }
            }
        });
    }

    /**
     * Supports reaction at runCitizen button click
     */
    private void runCitizenButton() {
        runCitizen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            synchronized public void handle(ActionEvent t) {
                if ((!world.getTheEnd()) && (!world.getPause())) {
                    Citizen tempCitizen = (Citizen) infoBoxObject;
                    tempCitizen.setPause(false);
                }
            }
        });
    }

    /**
     * Supports reaction at stopCitizen button click
     */
    private void stopCitizenButton() {
        stopCitizen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if ((!world.getTheEnd()) && (!world.getPause())) {
                    Citizen tempCitizen = (Citizen) infoBoxObject;
                    tempCitizen.setPause(true);
                    tempCitizen.getTravel().setTime(0);
                }
            }
        });
    }

    /**
     * Supports reaction at killCitizen button click
     */
    private void killCitizenButton() {
        killCitizen.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                if ((!world.getTheEnd()) && (!world.getPause())) {
                    Citizen tempCitizen = (Citizen) infoBoxObject;
                    world.kill(tempCitizen);
                }
            }
        });
    }

    /**
     * Supports reaction at change next City button click
     */
    private void changeNextCityButton() {
        changeCity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if ((!world.getTheEnd()) && (!world.getPause())) {
                    Citizen tempCitizen = (Citizen) infoBoxObject;
                    if ((listOfCities.getSelectionModel().getSelectedIndex() > -1) && (listOfCities.getSelectionModel().getSelectedIndex() < 17)) {
                        tempCitizen.getTravel().setDestinationCity(world.getCities().get(listOfCities.getSelectionModel().getSelectedIndex() + 1));
                    }
                }
            }
        });
    }

    /**
     * Supports reaction at change next City button click
     */
    private void sendSuperHeroButton() {
        sendSuperHero.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if ((!world.getTheEnd()) && (!world.getPause())) {
                    Capital capital = (Capital) world.getCities().get(12);
                    capital.sendSuperhero(listOfCities2, world.getCities());
                }
            }
        });
    }

    /**
     * Write information about clicked object, (Human)
     */
    private void fillInfoBoxTextArea(Human object) {
        world.calculatePowerAndPeople();
        powerInfoR.setText(world.getPower() + "\n" + world.getFreePower() + "\n" + world.getNumberOfPeople() + "\n" + world.getSuperHeroes().size());
        infoBoxLabel.setText("                 Info Box\n" + world.getNotification());
        //notificationLabel.setText(world.getNotification());
        if (object.getDead()) {
            infoBoxTextArea.setText("");
            citizenVBox.setVisible(false);
            capitalVBox.setVisible(false);
        } else {
            setInfoBoxObject(object);
            if (object instanceof Citizen) {
                citizenVBox.setVisible(true);
                capitalVBox.setVisible(false);
                infoBoxTextArea.setText(
                        "               Citizen\n\n"
                        + "   Name\n      " + object.getForename()
                        + " " + ((Citizen) object).getLastname() + "\n"
                        + "   From: \n      " + ((Citizen) object).getCityName() + "\n"
                        + "   Travel destination:\n      " + ((City) object.getTravel().getDestinationCity()).getName() + "\n\n\n"
                );
            } else if (object instanceof SuperHero) {
                citizenVBox.setVisible(false);
                capitalVBox.setVisible(false);
                infoBoxTextArea.setText(
                        "               SuperHero\n\n"
                        + "   Name:\n      " + object.getForename() + "\n"
                        + "   health:         " + ((Hero) object).getHealth() + "\n"
                        + "   intellect:      " + ((Hero) object).getIntellect() + "\n"
                        + "   force:          " + ((Hero) object).getForce() + "\n"
                        + "   speed:          " + ((Hero) object).getSpeed() + "\n"
                        + "   stamina:        " + ((Hero) object).getStamina() + "\n"
                        + "   energy:         " + ((Hero) object).getEnergy() + "\n"
                        + "   fightingSkill:  " + ((Hero) object).getFightingSkill() + "\n"
                );
                if (object.getTravel().getDestinationCity() != null) {
                    infoBoxTextArea.setText(
                            infoBoxTextArea.getText() + "   Travel destination:\n      "
                            + ((City) object.getTravel().getDestinationCity()).getName() + "\n\n\n"
                    );
                }
            } else if (object instanceof Criminal) {
                citizenVBox.setVisible(false);
                capitalVBox.setVisible(false);
                infoBoxTextArea.setText(
                        "               Criminal\n\n"
                        + "   Name\n      " + object.getForename() + "\n"
                        + "   health:         " + ((Hero) object).getHealth() + "\n"
                        + "   intellect:      " + ((Hero) object).getIntellect() + "\n"
                        + "   force:          " + ((Hero) object).getForce() + "\n"
                        + "   speed:          " + ((Hero) object).getSpeed() + "\n"
                        + "   stamina:        " + ((Hero) object).getStamina() + "\n"
                        + "   energy:         " + ((Hero) object).getEnergy() + "\n"
                        + "   fightingSkill:  " + ((Hero) object).getFightingSkill()
                );
            }
        }
    }

    /**
     * Write information about clicked object, (City)
     */
    private void fillInfoBoxTextArea(City object) {
        world.calculatePowerAndPeople();
        powerInfoR.setText(world.getPower() + "\n" + world.getFreePower() + "\n" + world.getNumberOfPeople() + "\n" + world.getSuperHeroes().size());
        infoBoxLabel.setText("                 Info Box\n" + world.getNotification());
        //notificationLabel.setText(world.getNotification());
        setInfoBoxObject(object);
        infoBoxTextArea.setText(
                "    City "
                + object.getName() + "\n\n"
                + "   Population:   " + object.getPopulation() + "\n"
                + "   Power Sources:\n\n"
        );
        for (int i = 0; i < object.getPowerSources().size(); i++) {
            infoBoxTextArea.setText(
                    infoBoxTextArea.getText()
                    + "   " + object.getPowerSources().get(i).getName() + "\n"
                    + "   Ability:      " + object.getPowerSources().get(i).getAbility() + "\n"
                    + "   Potential:      " + object.getPowerSources().get(i).getPotential() + "\n\n"
            );
        }
        if (object instanceof Capital) {
            capitalVBox.setVisible(true);
            citizenVBox.setVisible(false);
        } else {
            capitalVBox.setVisible(false);
            citizenVBox.setVisible(false);
        }
    }

    /**
     * It supports clicking on map;
     */
    private void onMapClicked() {
        layer1.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if (world.getTheEnd()) {
                    if (world.getResult().getGameTime() == 0) {
                        endOfGame();
                    }
                } else {
                    Boolean found = false;
                    for (Entry<Integer, City> entry : world.getCities().entrySet()) {
                        if (entry.getValue().getOval().contains((float) t.getX(), (float) t.getY())) {
                            fillInfoBoxTextArea(entry.getValue());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        for (Entry<Integer, Road> roads : world.getRoads().entrySet()) {
                            if (roads.getValue().getPolygon().contains((float) t.getX(), (float) t.getY())) {
                                //infoBoxTextArea.setText(roads.getValue().getId() + " ");
                                for (Entry<Integer, Human> entry : roads.getValue().getHumans().entrySet()) {
                                    if (entry.getValue().getOval().contains((float) t.getX(), (float) t.getY())) {
                                        fillInfoBoxTextArea(entry.getValue());
                                        found = true;
                                        break;
                                    }
                                }
                                if (found) {
                                    break;
                                }
                            }
                        }
                    }
                    if (!found) {
                        for (Entry<Integer, Crossing> crossings : world.getCrossings().entrySet()) {
                            for (Entry<Integer, Human> entry : crossings.getValue().getHumans().entrySet()) {
                                if (entry.getValue().getOval().contains((float) t.getX(), (float) t.getY())) {
                                    fillInfoBoxTextArea(entry.getValue());
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                break;
                            }
                        }
                    }

                }
            }
        });
    }

    /**
     * Create all parts of GUI
     */
    private void createGUI(Stage stage) {
        //create elements of GUI
        borderPane = new BorderPane();
        AnchorPane anchorPane = new AnchorPane();
        Pane paneMap = new Pane();
        VBox paneControlPanel = new VBox();
        anchorPane.getChildren().add(paneControlPanel);
        anchorPane.getChildren().add(paneMap);
        //set ControlPanel anchors
        AnchorPane.setBottomAnchor(paneMap, 0.0);
        AnchorPane.setTopAnchor(paneMap, 0.0);
        AnchorPane.setLeftAnchor(paneMap, 0.0);
        anchorPane.setStyle("-fx-background-color:rgba(177, 237, 177,1);");
        //set ContorlPanel width
        anchorPane.prefWidth(160);
        paneControlPanel.setPrefWidth(160);
        //Menu
        menuPane = new HBox();
        //buttons in menuPane
        ranking = new Button("ranking");
        ranking.setPrefSize(70, 25);
        pause = new Button("⫴");
        pause.setPrefSize(45, 25);
        play = new Button("⫸");
        play.setPrefSize(45, 25);
        menuPane.getChildren().addAll(ranking, pause, play);
        //Power Info
        HBox hBoxPowerInfo = new HBox();
        hBoxPowerInfo.setPrefSize(160, 80);
        powerInfoL = new Label("   Total Power:\n   Free Power:\n   Population:\n SuperHeroes:");
        powerInfoL.setPrefSize(80, 80);
        powerInfoL.setTextAlignment(TextAlignment.RIGHT);
        powerInfoR = new Label("    100\n25");
        powerInfoR.setTextAlignment(TextAlignment.RIGHT);
        powerInfoR.setPrefSize(80, 80);
        powerInfoL.setStyle("-fx-background-color:rgba(0, 179, 0,0.7);");
        powerInfoR.setStyle("-fx-background-color:rgba(0, 179, 0,0.7);");
        hBoxPowerInfo.getChildren().addAll(powerInfoL, powerInfoR);
        //addHuman buttons
        HBox hBoxAddCitizen = new HBox();
        addCitizen = new Button("Add Citizen");
        addCitizen.setPrefWidth(110);
        addCitizenLabel = new Label("cost 5");
        hBoxAddCitizen.getChildren().addAll(addCitizen, addCitizenLabel);
        HBox hBoxAddSuperHero = new HBox();
        addSuperHero = new Button("Add SuperHero");
        addSuperHero.setPrefWidth(110);
        addSuperHeroLabel = new Label("cost 100");
        hBoxAddSuperHero.getChildren().addAll(addSuperHero, addSuperHeroLabel);
        //infobox
        infoBoxLabel = new Label("                 Info Box");
        infoBoxLabel.setPrefHeight(60);
        infoBoxLabel.setPrefWidth(160);
        infoBoxLabel.setStyle("-fx-background-color:rgba(0, 179, 0,0.7);");
        infoBoxTextArea = new Label("");
        //buttons to manage humans and send superheroes
        Pane manageButtons = new Pane();
        citizenVBox = new VBox();
        citizenVBox.setVisible(false);
        stopCitizen = new Button("Stop Citizen");
        stopCitizen.setPrefWidth(160);
        runCitizen = new Button("Run Citizen");
        runCitizen.setPrefWidth(160);
        killCitizen = new Button("Kill Citizen");
        killCitizen.setPrefWidth(160);
        changeCity = new Button("Change Next City");
        changeCity.setPrefWidth(160);
        listOfCities = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList(
                "Poznań", "Wrocław", "Szczecin", "Gorzów Wielkopolski", "Gdańsk", "Opole", "Katowice", "Kraków", "Kielce",
                "Rzeszów", "Lublin", "Warszawa", "Białystok", "Olsztyn", "Łódź", "Bydgoszcz", "Toruń"
        );
        listOfCities.setItems(items);
        listOfCities.setPrefSize(160, 100);
        citizenVBox.getChildren().addAll(stopCitizen, runCitizen, killCitizen, changeCity, listOfCities);
        capitalVBox = new VBox();
        sendSuperHero = new Button("Send SuperHero");
        sendSuperHero.setPrefWidth(160);
        listOfCities2 = new ListView<String>();
        listOfCities2.setItems(items);
        listOfCities2.setPrefSize(160, 100);
        capitalVBox.getChildren().addAll(sendSuperHero, listOfCities2);
        capitalVBox.setVisible(false);
        manageButtons.getChildren().addAll(citizenVBox, capitalVBox);
        //notificationLabel=new Label("");
        paneControlPanel.getChildren().addAll(menuPane, hBoxPowerInfo, hBoxAddCitizen, hBoxAddSuperHero, infoBoxLabel, infoBoxTextArea, manageButtons);//,notificationLabel);
        //set Map anchors
        AnchorPane.setBottomAnchor(paneMap, 0.0);
        AnchorPane.setTopAnchor(paneMap, 0.0);
        AnchorPane.setRightAnchor(paneMap, 0.0);
        AnchorPane.setLeftAnchor(paneMap, 160.0);
        // create layers
        layer1 = new Canvas(800, 600);
        layer2 = new Canvas(800, 600);
        layer3 = new Canvas(800, 600);
        layer4 = new Canvas(800, 600);
        layer5 = new Canvas(800, 600);
        // Obtain Graphics Contexts
        graphicsContext1 = layer1.getGraphicsContext2D();
        graphicsContext2 = layer2.getGraphicsContext2D();
        graphicsContext3 = layer3.getGraphicsContext2D();
        graphicsContext4 = layer4.getGraphicsContext2D();
        graphicsContext5 = layer5.getGraphicsContext2D();
        //add layers to paneMap
        paneMap.getChildren().addAll(layer1, layer2, layer3, layer4, layer5);
        //set layers order
        layer5.toFront();
        layer4.toFront();
        layer3.toFront();
        layer2.toFront();
        layer1.toFront();

        root = new Group();
        borderPane.setCenter(anchorPane);
        root.getChildren().add(borderPane);
        stage.setTitle("World Simulator");
        stage.setScene(new Scene(root));
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.getIcons().add(new Image("/images/icon.png"));
        stage.show();
    }

    /**
     * Draw a cities on map, layer4
     *
     * @param cities set of cities
     * @param actualMapPositionX just put here mapPositionX
     * @param actialMapPositionY just put here mapPositionY
     */
    private void drawCities(LinkedHashMap<Integer, City> cities, double actualMapPositionX, double actualMapPositionY) {
        //clear the layer contenxt
        graphicsContext1.clearRect(0, 0, layer1.getWidth(), layer1.getHeight());
        //walks through all the cities and draws them
        for (Entry<Integer, City> entry : cities.entrySet()) {
            Capital n;
            //set oval
            entry.getValue().getOval().x = (float) (entry.getValue().getX() + actualMapPositionX);
            entry.getValue().getOval().y = (float) (entry.getValue().getY() + actualMapPositionY);
            //entry.getValue().getOval().height=70;
            //entry.getValue().getOval().width=70;
            //draw city
            if (entry.getValue() instanceof Capital) {
                if (entry.getValue().getIsDamage()) {
                    graphicsContext1.drawImage(capitalDamageImage, actualMapPositionX + entry.getValue().getX(), actualMapPositionY + entry.getValue().getY(), 70, 70);
                } else if (entry.getValue().getIsAttacked()) {
                    graphicsContext1.drawImage(capitalAttackedImage, actualMapPositionX + entry.getValue().getX(), actualMapPositionY + entry.getValue().getY(), 70, 70);
                } else {
                    graphicsContext1.drawImage(capitalImage, actualMapPositionX + entry.getValue().getX(), actualMapPositionY + entry.getValue().getY(), 70, 70);
                }
            } else {
                if (entry.getValue().getIsDamage()) {
                    graphicsContext1.drawImage(cityDamageImage, actualMapPositionX + entry.getValue().getX(), actualMapPositionY + entry.getValue().getY(), 70, 70);
                } else if (entry.getValue().getIsAttacked()) {
                    graphicsContext1.drawImage(cityAttackedImage, actualMapPositionX + entry.getValue().getX(), actualMapPositionY + entry.getValue().getY(), 70, 70);
                } else {
                    graphicsContext1.drawImage(cityImage, actualMapPositionX + entry.getValue().getX(), actualMapPositionY + entry.getValue().getY(), 70, 70);
                }

            }
        }
    }

    /**
     * Draw a crossings on map, layer3
     *
     * @param crossings set of crossings
     * @param actualMapPositionX just put here mapPositionX
     * @param actialMapPositionY just put here mapPositionY
     */
    private void drawCrossings(LinkedHashMap<Integer, Crossing> crossings, double actualMapPositionX, double actualMapPositionY) {
        //clear the layer contenxt
        graphicsContext3.clearRect(0, 0, layer1.getWidth(), layer1.getHeight());
        //walks through all the crossings and draws them
        for (Entry<Integer, Crossing> entry : crossings.entrySet()) {
            graphicsContext3.drawImage(crossingImage, actualMapPositionX + entry.getValue().getX(), actualMapPositionY + entry.getValue().getY(), 70, 70);
        }
    }

    /**
     * Draw roads on map, layer 4
     *
     * @param roads Collection with all Road objects
     * @param actualMapPositionX just put here mapPositionX
     * @param actialMapPositionY just put here mapPositionY
     */
    private void drawRoads(LinkedHashMap<Integer, Road> roads, double actualMapPositionX, double actualMapPositionY) {
        //clear the layer contenxt
        graphicsContext4.clearRect(0, 0, layer1.getWidth(), layer1.getHeight());
        //walks through all the roads and draws them
        for (Entry<Integer, Road> entry : roads.entrySet()) {
            double l1 = 12; // width of the road
            //coordinates of the road +to center of place + actualMapPosiotion
            double x1 = entry.getValue().getX() + 35 + actualMapPositionX;
            double y1 = entry.getValue().getY() + 35 + actualMapPositionY;
            double x2 = entry.getValue().getX2() + 35 + actualMapPositionX;
            double y2 = entry.getValue().getY2() + 35 + actualMapPositionY;
            //equation of a line            
            double a = Calculations.linearEquationPerpendicularA(x1, y1, x2, y2);
            double b = Calculations.linearEquationPerpendicularB(x1, y1, x2, y2);
            // points needed            
            double[] xpos = new double[4];
            double[] ypos = new double[4];
            //very long formula from here http://www.wolframalpha.com/input/?i=l%3Dsqrt%28%28x-p%29^2+%2B+%28a*x%2Bb-q%29^2%29
            double[] temporary = Calculations.pointFromVector(x1, y1, a, b, l1, false);
            xpos[0] = temporary[0];
            ypos[0] = temporary[1];
            temporary = Calculations.pointFromVector(x1, y1, a, b, l1, true);
            xpos[1] = temporary[0];
            ypos[1] = temporary[1];
            //update b for next point
            b = Calculations.linearEquationPerpendicularB(x2, y2, x1, y1);
            //again formula
            temporary = Calculations.pointFromVector(x2, y2, a, b, l1, false);
            xpos[3] = temporary[0];
            ypos[3] = temporary[1];
            temporary = Calculations.pointFromVector(x2, y2, a, b, l1, true);
            xpos[2] = temporary[0];
            ypos[2] = temporary[1];

            graphicsContext4.setFill(Color.BLACK);
            graphicsContext4.fillPolygon(xpos, ypos, 4);
            entry.getValue().getPolygon().getPoints().setAll(xpos[0], ypos[0], xpos[1], ypos[1], xpos[2], ypos[2], xpos[3], ypos[3]);
            //draw white line in the middle of road
            graphicsContext4.setLineWidth(0.5);
            graphicsContext4.setStroke(Color.WHITE);
            graphicsContext4.strokeLine(x1, y1, x2, y2);
        }
    }

    /**
     * Part of draw Human. draws oval in accordance with the specified
     * parameters
     *
     * @param human object of Human which is drawn
     * @param x1 x from point 1
     * @param y1 y from point 1
     * @param x2 x from point 2
     * @param y2 y from point 2
     * @param length between points
     * @param sizeOfOval size of owal to draw
     * @param halfWidthOfRoad
     */
    private void drawOval(Human human, double x1, double y1, double x2, double y2, double length, double sizeOfOval, double halfWidthOfRoad) {
        //have to be to ride on right side of road
        boolean startXLessThanEndX;
        startXLessThanEndX = x1 < x2;
        boolean startYGreaterThanEndY;
        startYGreaterThanEndY = y1 > y2;
        //equation of a line
        double a = Calculations.linearEquationPerpendicularA(x1, y1, x2, y2);
        double b = Calculations.linearEquationPerpendicularB(x1, y1, x2, y2);
        double pos1[] = Calculations.pointFromVector(x1, y1, a, b, halfWidthOfRoad, startYGreaterThanEndY);
        //update b for next point                    
        b = Calculations.linearEquationPerpendicularB(x2, y2, x1, y1);//y2 - a * x2;
        double pos2[] = Calculations.pointFromVector(x2, y2, a, b, halfWidthOfRoad, startYGreaterThanEndY);
        //next equation of a line
        a = Calculations.linearEquationA(pos1[0], pos1[1], pos2[0], pos2[1]);
        b = Calculations.linearEquationB(pos1[0], pos1[1], pos2[0], pos2[1]);
        //get position of human                    
        double pointOfCitizen[] = Calculations.pointFromVector(pos1[0], pos1[1], a, b, length, startXLessThanEndX);
        if (human instanceof Citizen) {
            graphicsContext2.setFill(Color.YELLOW);
        } else if (human instanceof SuperHero) {
            graphicsContext2.setFill(Color.BLUE);
        } else {
            graphicsContext2.setFill(Color.RED);
        }

        graphicsContext2.fillOval(pointOfCitizen[0] - 5, pointOfCitizen[1] - 5, sizeOfOval, sizeOfOval);
        human.getOval().x = (float) (pointOfCitizen[0] - 5);
        human.getOval().y = (float) (pointOfCitizen[1] - 5);
        human.getOval().height = (float) sizeOfOval;
        human.getOval().width = (float) sizeOfOval;
    }

    /**
     * Draw humans on them map layer2
     *
     * @param roads Collection with all Road objects
     * @param crossings Collection with all Crossing objects
     * @param actualMapPositionX just put here mapPositionX
     * @param actialMapPositionY just put here mapPositionY
     */
    private void drawHuman(LinkedHashMap<Integer, Road> roads, LinkedHashMap<Integer, Crossing> crossings, double actualMapPositionX, double actualMapPositionY) {
        //if some human on crossing
        graphicsContext2.clearRect(0, 0, layer1.getWidth(), layer1.getHeight());
        for (Entry<Integer, Crossing> entryCrossing : crossings.entrySet()) {
            ConcurrentHashMap<Integer, Human> humans = new ConcurrentHashMap<>(entryCrossing.getValue().getHumans()); //make copy because of nullpointerexception            
            if (humans.isEmpty() == false) {
                Human human = humans.entrySet().iterator().next().getValue();
                Place LastPlaceOfHumanInThisCrossing = human.getTravel().getLastPlace();
                double x1, x2, y1, y2;
                //coordinates of the road +to center of place + actualMapPosiotion                    
                if (LastPlaceOfHumanInThisCrossing.getNeighbors().get(0) != entryCrossing.getValue()) {//lastplace.get(i)) {
                    x2 = (double) LastPlaceOfHumanInThisCrossing.getNeighbors().get(1).getX() + 35 + actualMapPositionX;
                    y2 = (double) LastPlaceOfHumanInThisCrossing.getNeighbors().get(1).getY() + 35 + actualMapPositionY;
                    x1 = (double) LastPlaceOfHumanInThisCrossing.getNeighbors().get(0).getX() + 35 + actualMapPositionX;
                    y1 = (double) LastPlaceOfHumanInThisCrossing.getNeighbors().get(0).getY() + 35 + actualMapPositionY;
                } else {
                    x1 = (double) LastPlaceOfHumanInThisCrossing.getNeighbors().get(1).getX() + 35 + actualMapPositionX;
                    y1 = (double) LastPlaceOfHumanInThisCrossing.getNeighbors().get(1).getY() + 35 + actualMapPositionY;
                    x2 = (double) LastPlaceOfHumanInThisCrossing.getNeighbors().get(0).getX() + 35 + actualMapPositionX;
                    y2 = (double) LastPlaceOfHumanInThisCrossing.getNeighbors().get(0).getY() + 35 + actualMapPositionY;
                }
                double l = LastPlaceOfHumanInThisCrossing.getSize();
                drawOval(human, x1, y1, x2, y2, l, 10, 6);
            }
        }
        //if some human on roads
        for (Entry<Integer, Road> entryRoad : roads.entrySet()) {
            if (entryRoad.getValue().getHumans().isEmpty() == false) {
                for (Entry<Integer, Human> entryHuman : entryRoad.getValue().getHumans().entrySet()) {
                    double l1 = 6; // width of the road
                    double x1, x2, y1, y2;
                    //coordinates of the road +to center of place + actualMapPosiotion                    
                    if (entryRoad.getValue().getNeighbors().get(0) == entryHuman.getValue().getTravel().getLastPlace()) {//lastplace.get(i)) {
                        x2 = (double) entryRoad.getValue().getNeighbors().get(1).getX() + 35 + actualMapPositionX;
                        y2 = (double) entryRoad.getValue().getNeighbors().get(1).getY() + 35 + actualMapPositionY;
                        x1 = (double) entryRoad.getValue().getNeighbors().get(0).getX() + 35 + actualMapPositionX;
                        y1 = (double) entryRoad.getValue().getNeighbors().get(0).getY() + 35 + actualMapPositionY;
                    } else {
                        x1 = (double) entryRoad.getValue().getNeighbors().get(1).getX() + 35 + actualMapPositionX;
                        y1 = (double) entryRoad.getValue().getNeighbors().get(1).getY() + 35 + actualMapPositionY;
                        x2 = (double) entryRoad.getValue().getNeighbors().get(0).getX() + 35 + actualMapPositionX;
                        y2 = (double) entryRoad.getValue().getNeighbors().get(0).getY() + 35 + actualMapPositionY;
                    }
                    double l = ((entryHuman.getValue().getTravel().getProgress() * entryRoad.getValue().getSize()) / 1000);
                    drawOval(entryHuman.getValue(), x1, y1, x2, y2, l, 10, l1);
                }
            }
        }
    }

    /**
     * Draw Map
     */
    private void drawMap() {
        graphicsContext5.setFill(Color.web("b1edb1"));
        graphicsContext5.fillRect(0, 0, 800, 600);
        if (world.getTheEnd()) {
            graphicsContext5.drawImage(endPoland, mapPositionX, mapPositionY, 1500, 1458);
        } else {
            graphicsContext5.drawImage(poland, mapPositionX, mapPositionY, 1500, 1458);
        }
    }

    /**
     * Draw all needed elements of Map, Object on map and Information Panel.
     */
    private void draw() {
        drawMap();
        infoBoxObject = world.getCities().get(12);
        fillInfoBoxTextArea((City) infoBoxObject);
        drawCities(world.getCities(), mapPositionX, mapPositionY);
        drawCrossings(world.getCrossings(), mapPositionX, mapPositionY);
        drawRoads(world.getRoads(), mapPositionX, mapPositionY);
        drawHuman(world.getRoads(), world.getCrossings(), mapPositionX, mapPositionY);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * counts time to the birth of criminal and increase the potential of power
     * sources
     *
     * @param generator
     */
    private void superTimer(Random generator) {
        if (lapTime == 0) {
            lapTime = System.currentTimeMillis();
        } else {
            if (criminalTime <= 0) {
                world.addCriminal();
                long baseTime = 180000;
                if ((System.currentTimeMillis() - world.getResult().getStartTime()) > 900000) {
                    baseTime = 0;
                } else if ((System.currentTimeMillis() - world.getResult().getStartTime()) > 600000) {
                    baseTime = 30000;
                } else if ((System.currentTimeMillis() - world.getResult().getStartTime()) > 300000) {
                    baseTime = 60000;
                }
                criminalTime = generator.nextInt(30000) + baseTime;
            } else {
                criminalTime -= System.currentTimeMillis() - lapTime;
            }
            if (powerSourceTime <= 0) {
                world.increasePowerSourcesPotential();
                powerSourceTime = 20000;
            } else {
                powerSourceTime -= System.currentTimeMillis() - lapTime;
            }
            time = time + System.currentTimeMillis() - lapTime;
            lapTime = System.currentTimeMillis();
        }
    }

    /**
     * elements of interface that must be constantly refreshed
     */
    private void rendering() {
        drawCities(world.getCities(), mapPositionX, mapPositionY);
        drawHuman(world.getRoads(), world.getCrossings(), mapPositionX, mapPositionY);
        if (infoBoxObject instanceof City) {
            fillInfoBoxTextArea((City) infoBoxObject);
        } else if (infoBoxObject instanceof Human) {
            fillInfoBoxTextArea((Human) infoBoxObject);
        }
    }

    /**
     * ask about user name, save the result, end program
     */
    private void endOfGame() {
        world.setTheEnd(true);
        if (world.getResult().getGameTime() == 0) {
            world.getResult().endGame(pauseTime);
        }
        dialogBox();
    }

    /**
     * Create Dialog Box, ask about name
     */
    private void dialogBox() {
        VBox window;
        dialogStageGetName = new Stage();
        dialogStageGetName.initModality(Modality.WINDOW_MODAL);
        dialogStageGetName.setTitle("End Of The Game");
        dialogStageGetName.setResizable(false);
        window = new VBox();
        window.setAlignment(Pos.CENTER);
        window.setPrefSize(250, 100);
        button = new Button("Confirm");
        textField = new TextField();
        textField.setPrefWidth(20);
        textField.setAlignment(Pos.CENTER);
        Label text = new Label("Enter your name");
        text.setPrefSize(120, 30);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font(null, 16));
        window.getChildren().addAll(text, textField, button);
        dialogStageGetName.setScene(new Scene(window));
        dialogStageGetName.getScene().setFill(Color.web("b1edb1"));        
        dialogStageGetName.getIcons().add(new Image("/images/icon.png"));
        dialogStageGetName.show();
        confirmName();
    }

    /**
     * Reaction at button clik, confirm name, set Name in Result
     */
    private void confirmName() {
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                world.getResult().setName(textField.getText());
                world.getFilesupport().saveRanking("ranking.xml", world.getResult());
                dialogStageGetName.close();
                ranking();
            }
        });
    }

    /**
     * Reaction at button clik, confirm name, set Name in Result
     */
    private void rankingButton() {
        ranking.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ranking();
            }
        });
    }

    /**
     * Turn on Pause, all Threads stop working
     */
    private void pauseButton() {
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                pause();
            }
        });
    }

    /**
     * Turn on Pause, all Threads stop working, set TravelTime of Humans at 0
     */
    private void pause() {
        if (!world.getPause()) {
            world.setPause(Boolean.TRUE);
            for (Entry<Integer, Citizen> entry : world.getCitizens().entrySet()) {
                entry.getValue().getTravel().setTime(0);
            }
            for (Entry<Integer, Criminal> entry : world.getCriminals().entrySet()) {
                entry.getValue().getTravel().setTime(0);
            }
            for (Entry<Integer, SuperHero> entry : world.getSuperHeroes().entrySet()) {
                entry.getValue().getTravel().setTime(0);
            }
            lapTime = 0;
            pauseStartTime = System.currentTimeMillis();
        }
    }

    /**
     * Turn off Pause, all Threads start working
     */
    private void playButton() {
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (world.getPause()) {
                    world.setPause(false);
                    pauseTime = pauseTime + (System.currentTimeMillis() - pauseStartTime);
                }
            }
        });
    }

    /**
     * Show actual ranking
     */
    private void ranking() {
        GridPane window;
        dialogStageRanking = new Stage();
        dialogStageRanking.initModality(Modality.WINDOW_MODAL);
        dialogStageRanking.setTitle("Ranking");
        dialogStageRanking.setResizable(false);
        window = new GridPane();
        window.setAlignment(Pos.CENTER);
        window.setPrefSize(360, 250);
        Label text = new Label("RANKING\n   -------------------------------------------   \n");
        text.setPrefSize(360, 250);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font(null, 19));
        window.getChildren().addAll(text);
        dialogStageRanking.setScene(new Scene(window));
        dialogStageRanking.getScene().setFill(Color.web("b1edb1"));
        ArrayList<Result> resultList = world.getFilesupport().getRanking("ranking.xml");
        if (resultList != null) {
            for (Result result : resultList) {
                text.setText(
                        text.getText() + "\n"
                        + result.getName() + " - " + (result.getGameTime() / 1000) + " s");
            }
        }
        
        dialogStageRanking.getIcons().add(new Image("/images/icon.png"));
        dialogStageRanking.show();
    }

    /**
     * Initiate all images
     */
    private void initiateImages() {
        poland = new Image("/images/map.png");
        endPoland = new Image("/images/endmap.png");
        cityImage = new Image("/images/city.png");
        cityDamageImage = new Image("/images/cityDamage.png");
        cityAttackedImage = new Image("/images/cityAttacked.png");
        crossingImage = new Image("/images/crossing.png");
        capitalImage = new Image("/images/capital.png");
        capitalDamageImage = new Image("/images/capitalDamage.png");
        capitalAttackedImage = new Image("/images/capitalAttacked.png");
    }

    /**
     * Initiate some values needed at the beginning
     */
    private void initiateValues() {
        mapPositionX = -1;
        mapPositionY = -2;
        time = 0;
        lapTime = 0;
        criminalTime = 180000;
        powerSourceTime = 20000;
        world = new World();
        pauseTime = 0;
        pauseStartTime = 0;
    }

    /**
     * Turn on all event listeners ( buttons, clicking on map, resizning ...)
     *
     * @param stage
     */
    private void eventListeners(Stage stage) {
        playButton();
        pauseButton();
        onMapClicked();
        windowResizeHandling(stage);
        mapScrollHandling();
        addCitizenButton();
        changeNextCityButton();
        stopCitizenButton();
        addSuperHeroButton();
        runCitizenButton();
        killCitizenButton();
        sendSuperHeroButton();
        rankingButton();
        closeWindowAction(stage);
    }

    /**
     * Reaction at closing the main window close opened files befor exit
     *
     * @param stage
     */
    private void closeWindowAction(Stage stage) {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (!world.getTheEnd()) {
                    endOfGame();
                    event.consume();
                } else {
                    world.getFilesupport().closeAllFiles();
                }
            }
        });
    }

    /**
     * Start the application
     *
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        final Random generator = new Random();
        initiateImages();
        initiateValues();
        createGUI(stage);
        eventListeners(stage);
        draw();
        Thread renderer = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                    }
                    if (world.getTheEnd()) {
                        drawMap();
                        break;
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (!world.getPause()) {
                                superTimer(generator);
                                rendering();
                            }
                        }
                    });
                }
            }
        };
        renderer.setDaemon(true);
        renderer.start();
    }

    /**
     * @return the infoBoxObject
     */
    synchronized public Object getInfoBoxObject() {
        return infoBoxObject;
    }

    /**
     * @param infoBoxObject the infoBoxObject to set
     */
    synchronized public void setInfoBoxObject(Object infoBoxObject) {
        this.infoBoxObject = infoBoxObject;
    }
}
