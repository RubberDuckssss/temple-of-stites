package org.templeofstites;

import static org.templeofstites.GameDriver.*;
import java.io.File;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class LoadGame
{
    private static final Pattern VALID_PATTERN = Pattern.compile("[0-9]+|[A-Z]+");
    private static ArrayList<Image> backgroundTiles = new ArrayList<Image>(), sprites = new ArrayList<Image>();

    static void loadAssets(String folderLocation) {
        try {
            Scanner input = new Scanner(new File(folderLocation + "aassets.txt"));
            String category = "", temp;
            while (input.hasNext()) {
                temp = input.next();
                switch (temp)
                {
                    case "Background":
                        category = "Background";
                        break;
                    case "Sprite":
                        category = "Sprite";
                        break;
                    default:
                        switch (category) {
                            case "Background":
                                backgroundTiles.add(new Image("file:" + folderLocation + "worldtiles/" + temp + ".png", PIXEL_PER_TILE, PIXEL_PER_TILE, true, false));
                                break;
                            case "Sprite":
                                sprites.add(new Image("file:" + folderLocation + "NotLink/" + temp + ".png", PIXEL_PER_TILE, PIXEL_PER_TILE, true, false));
                                break;
                        }
                        break;
                }
            }
        }
        catch (Exception e)
        {
            error.setContentText("Something terrible happened:\n\n" + e);
            error.showAndWait();
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void loadBackground(int maxWidth, int maxHeight, ArrayList<ImageView> background, ArrayList<Hitbox> hitboxList, Pane backgroundPane, Scanner input) {
        for (int height = 0; height < maxHeight; height++) {
            for (int width = 0; width < maxWidth; width++) {
                ArrayList<String> temp = parse(input.next().toUpperCase());
                background.add(new ImageView(backgroundTiles.get(Integer.parseInt(temp.get(0)))));
                background.get(background.size() - 1).relocate(width * PIXEL_PER_TILE, height * PIXEL_PER_TILE);
                backgroundPane.getChildren().add(background.get(background.size() - 1));
                if (temp.size() > 1) {
                    switch (Integer.parseInt(temp.get(1))) {
                        case 1:
                            background.add(new ImageView(backgroundTiles.get(Integer.parseInt(temp.get(2)))));
                            background.get(background.size() - 1).relocate(width * PIXEL_PER_TILE, height * PIXEL_PER_TILE);
                            backgroundPane.getChildren().add(background.get(background.size() - 1));
                        case 2:
                            hitboxList.add(new Hitbox(false, "wall", 0));
                            hitboxList.get(hitboxList.size() - 1).createBackgroundHitBox(width, height);
                            break;
                        case 3:
                            hitboxList.add(new Hitbox(true, "wall", 1));
                            hitboxList.get(hitboxList.size() - 1).createBackgroundHitBox(width, height);
                            break;
                        case 4:
                            ImageView one = new ImageView(new Image("file:h_p.png", 64, 64, true, false));
                            one.relocate(width * PIXEL_PER_TILE, height * PIXEL_PER_TILE);
                            backgroundPane.getChildren().add(one);
                            hitboxList.add(new Hitbox(true, "pickup", -2));
                            hitboxList.get(hitboxList.size() - 1).createBackgroundHitBox(width, height);
                            break;
                        case 5:
                            int offset = 2;
                            hitboxList.add(new DoorHitbox(true, "door", 0, Integer.parseInt(temp.get(2)), Integer.parseInt(temp.get(3)), temp.get(4)));
                            switch (temp.get(4)) {
                                case "U":
                                    hitboxList.get(hitboxList.size() - 1).createSpecialRectangle(width * PIXEL_PER_TILE, height * PIXEL_PER_TILE + player.HB_OFFSET_Y, Integer.parseInt(temp.get(5)) * PIXEL_PER_TILE, offset);
                                    break;
                                case "D":
                                    hitboxList.get(hitboxList.size() - 1).createSpecialRectangle(width * PIXEL_PER_TILE, ((height + 1) * PIXEL_PER_TILE) - offset, Integer.parseInt(temp.get(5)) * PIXEL_PER_TILE, offset);
                                    break;
                                case "L":
                                    hitboxList.get(hitboxList.size() - 1).createSpecialRectangle(width * PIXEL_PER_TILE, height * PIXEL_PER_TILE, offset, Integer.parseInt(temp.get(5)) * PIXEL_PER_TILE);
                                    break;
                                case "R":
                                    hitboxList.get(hitboxList.size() - 1).createSpecialRectangle((width + 1) * PIXEL_PER_TILE - offset, height * PIXEL_PER_TILE, offset, Integer.parseInt(temp.get(5)) * PIXEL_PER_TILE);
                                    break;
                                default:
                                    System.err.println("WHOOPS. I CAN'T INTO DOOR");
                                    break;
                            }
                            ((DoorHitbox) hitboxList.get(hitboxList.size() - 1)).updatePosition(width, height);
                            break;
                    }
                }
            }
        }
    }

    private static ArrayList<String> parse(String p)
    {
        ArrayList<String> chunks = new ArrayList<String>();
        Matcher matcher = VALID_PATTERN.matcher(p);
        while (matcher.find())
        {
            chunks.add(matcher.group());
        }
        return chunks;
    }
}
