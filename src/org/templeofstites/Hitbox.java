package org.templeofstites;

import static org.templeofstites.GameDriver.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
/*
 * HUGE thanks to Shannon White for helping me out with this garbage
 */
public class Hitbox
{

    private static boolean isVisible = false;
    private boolean canPass;
    private static int attempts = 0;
    private static int lastDoor;
    private String name;
    private int damage;
    Rectangle shape;

    public Hitbox(boolean passable, String n, int d) {
        canPass = passable;
        name = n;
        damage = d;
    }

    public static void setVisible(boolean change) {
        if (change)
        {
            isVisible = !isVisible;
        }
        for (int i = 0; i < areas.get(currentArea).hitBoxList.size(); i++)
        {
            areas.get(currentArea).hitBoxList.get(i).shape.setVisible(isVisible);
        }
        player.hitBox.setVisible(isVisible);
    }

    public void createBackgroundHitBox(int X, int Y) {
        this.shape = new Rectangle(X * PIXEL_PER_TILE + 1, Y * PIXEL_PER_TILE + 1, PIXEL_PER_TILE - 2, PIXEL_PER_TILE - 2);
        this.shape.setFill(Color.TRANSPARENT);
        this.shape.setStroke(Color.RED);
        this.shape.strokeWidthProperty().set(2);
        this.shape.setVisible(isVisible);
    }

    public void createSpecialRectangle(double X, double Y, double width, double height) {
        this.shape = new Rectangle(X + 1, Y + 1, width - 2, height - 2);
        this.shape.setFill(Color.TRANSPARENT);
        this.shape.setStroke(Color.CYAN);
        this.shape.strokeWidthProperty().set(2);
        this.shape.setVisible(isVisible);
    }

    public static Rectangle createEntityHitBoxRectangle(double X, double Y, double width, double height) {
        Rectangle temp = new Rectangle(X + 1, Y + 1, width - 2, height - 2);
        temp.setFill(Color.TRANSPARENT);
        temp.setStroke(Color.CYAN);
        temp.strokeWidthProperty().set(2);
        temp.setVisible(isVisible);
        return temp;
    }

    public static boolean collisionResolver(Shape currentShape){
        for (Hitbox currentHitbox : areas.get(currentArea).hitBoxList) {
            if (currentHitbox.shape != currentShape) {
                Shape intersection = Shape.intersect(currentShape, currentHitbox.shape);
                if (intersection.getBoundsInLocal().getWidth() > 0 || intersection.getBoundsInLocal().getHeight() > 0) {
                    boolean isHorizontal = intersection.getBoundsInLocal().getWidth() > intersection.getBoundsInLocal().getHeight();
                    player.updateHealth(currentHitbox.damage);
                    switch (currentHitbox.name) {
                        case "door":
                            if (attempts - 1 != lastDoor) {
                                paneHandler.doorFade.play();
                                paneHandler.doorFade.jumpTo(Duration.millis(250));
                                double temp;
                                int doorValue = ((DoorHitbox) currentHitbox).correspondingDoor;
                                if (isHorizontal) {
                                    temp = player.hitBox.getTranslateX() - ((DoorHitbox) currentHitbox).width * PIXEL_PER_TILE;
                                } else {
                                    temp = player.hitBox.getTranslateY() - ((DoorHitbox) currentHitbox).height * PIXEL_PER_TILE;
                                }
                                currentArea = ((DoorHitbox) currentHitbox).areaNumber;
                                paneHandler.changePane(areas.get(currentArea));
                                for (int i = 0; i < areas.get(currentArea).hitBoxList.size(); i++) {
                                    if (areas.get(currentArea).hitBoxList.get(i) instanceof DoorHitbox && ((DoorHitbox) areas.get(currentArea).hitBoxList.get(i)).correspondingDoor == doorValue) {
                                        if (isHorizontal) {
                                            player.playerPlacer(((DoorHitbox) areas.get(currentArea).hitBoxList.get(i)).width * PIXEL_PER_TILE + temp, ((DoorHitbox) areas.get(currentArea).hitBoxList.get(i)).height * PIXEL_PER_TILE);
                                            // I HATE THIS SO MUCH. WHY DOES THIS SUCK.
                                        } else {
                                            player.playerPlacer(((DoorHitbox) areas.get(currentArea).hitBoxList.get(i)).width * PIXEL_PER_TILE, ((DoorHitbox) areas.get(currentArea).hitBoxList.get(i)).height * PIXEL_PER_TILE + temp);
                                        }
                                        lastDoor = attempts;
                                        attempts++;
                                        return false;
                                    }
                                }
                            }
                            lastDoor = attempts;
                            attempts++;
                            return false;
                        case "wall":
                            if (!currentHitbox.canPass) {
                                return true;
                            }
                        case "pickup": // not. a truck
                            break;
                    }
                }
            }
        }
        attempts++;
        return false;
    }
}
