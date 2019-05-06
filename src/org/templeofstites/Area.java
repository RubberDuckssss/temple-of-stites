package org.templeofstites;

import static org.templeofstites.GameDriver.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Area {

    private String name;
    private int tileWidth, tileHeight;
    double maxWidth, maxHeight;
    private ArrayList<ImageView> background;
    Pane backgroundPane, entityPane;
    ArrayList<Hitbox> hitBoxList;

    public Area(String name) {
        this.name = name;
        try {
            // Here's a secret. These .txt files are just renamed .csv files because Java apparently can't read those.
            // Or, it can, but not properly... either way, make the maps in some csv editor then rename them to txt
            Scanner input = new Scanner(new File(this.name + ".txt"));
            tileWidth = input.nextInt();
            maxWidth = tileWidth * PIXEL_PER_TILE;
            tileHeight = input.nextInt();
            maxHeight = tileHeight * PIXEL_PER_TILE;
            background = new ArrayList<>();
            backgroundPane = new Pane();
            entityPane = new Pane();
            hitBoxList = new ArrayList<>();
            LoadGame.loadBackground(tileWidth, tileHeight, background, hitBoxList, backgroundPane, input);
        } catch (Exception e) {
            error.setContentText("Something terrible happened:\n\n" + e);
            error.showAndWait();
            e.printStackTrace();
            System.exit(1);
        }
    }
}
