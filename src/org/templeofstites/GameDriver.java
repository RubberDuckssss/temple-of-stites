package org.templeofstites;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GameDriver extends Application {

    private String header = "Temple of Stites - Version " + getVersion();
    static String asset_folder = "assets/";
    static Alert error = new Alert(Alert.AlertType.ERROR);

    private static final int WINDOW_WIDTH_TILE = 15, WINDOW_HEIGHT_TILE = 10;
    static final int PIXEL_PER_TILE = 64;
    static final double WINDOW_WIDTH = WINDOW_WIDTH_TILE * PIXEL_PER_TILE, WINDOW_HEIGHT = WINDOW_HEIGHT_TILE * PIXEL_PER_TILE;
    private final AnimationTimer timer;

    static ArrayList<Area> areas = new ArrayList<>();
    static int currentArea = 0;

    static PaneController paneHandler;
    static Player player;
    static long timestamp = 0;

    public GameDriver()
    {
        LoadGame.loadAssets(asset_folder);
        paneHandler = new PaneController();
        player = new Player();
        areas.add(new Area(asset_folder + "rooms/testingroom_1"));
        areas.add(new Area(asset_folder + "rooms/testingroom_2"));
        this.timer = new AnimationTimer()
        {
            @Override
            public void handle(long time)
            {
                timestamp = time;
                player.playerUpdater();
            }
        };
    }

    static String getVersion() {
        try
        {
            Scanner input = new Scanner(new File(asset_folder + "version.txt"));
            return input.next();
        }
        catch (Exception e)
        {
            error.setContentText("Something terrible happened:\n\n" + e);
            error.showAndWait();
            e.printStackTrace();
            System.exit(1);
        }
        return "This should never happen";
    }

    @Override
    public void start(Stage mainStage)
    {
        try
        {
            paneHandler.changePane(areas.get(currentArea));
            player.playerPlacer(PIXEL_PER_TILE, PIXEL_PER_TILE);

            paneHandler.loadHud(player);
            mainStage.setTitle(header);
            mainStage.setResizable(false);
            mainStage.getIcons().add(new Image("file:Assets/icon.png"));
            mainStage.setScene(paneHandler.scene);
            mainStage.show();
            timer.start();
            player.playerEvents(paneHandler.scene);
        }
        catch (Exception e) {
            error.setContentText("Something terrible happened:\n\n" + e);
            error.showAndWait();
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static void main(String[] args)
    {
        System.out.println("Let there be light!");
        launch(args);
    }
}