package org.templeofstites;

import static org.templeofstites.GameDriver.*;

import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

public class Player {

    int currentSprite = 0;
    double spriteTimer = 0;
    final double SPRITE_SPEED = .15;
    final double SPEED = 200;
    final double HB_OFFSET_X = 10, HB_OFFSET_Y = 22;

    private long lastHurt;
    public int num = 0;
    ImageView[] spriteArray = new ImageView[8];
    ArrayList<ImageView> hearts = new ArrayList<ImageView>();
    ArrayList<ImageView> maxHearts = new ArrayList<ImageView>();
    private String[] sprites = {"f1.png", "f2.png", "b1.png", "b2.png", "r1.png", "r2.png", "l1.png", "l2.png"};
    private String primaryDirection, lastDirection;
    private boolean up, down, right, left;
    private double vertVelocity, horizVelocity, lastUpdate;
    Rectangle hitBox;
    public int health = 10, maxHealth = 10;

    public Player()
    {
        loadSprites();
        hitBox = Hitbox.createEntityHitBoxRectangle(HB_OFFSET_X, HB_OFFSET_Y, PIXEL_PER_TILE - HB_OFFSET_X * 2, PIXEL_PER_TILE - HB_OFFSET_Y);
        primaryDirection = "down";
        up = false;
        down = false;
        right = false;
        left = false;
        horizVelocity = 0;
        vertVelocity = 0;
        lastUpdate = 0;
        setAllInvisible();
        spriteArray[0].setVisible(true);
        lastHurt = 0;
    }

    void playerUpdater() {
        setVelocity();
        lastDirection = primaryDirection;
        primaryDirection = setPrimaryDirection(primaryDirection);
        if (lastUpdate > 0) {
            movement();
        }
        lastUpdate = timestamp;
    }

    void playerEvents(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                up = true;
            } else if (event.getCode() == KeyCode.DOWN) {
                down = true;
            } else if (event.getCode() == KeyCode.LEFT) {
                left = true;
            } else if (event.getCode() == KeyCode.RIGHT) {
                right = true;
            } else if (event.getCode() == KeyCode.I) {
                Hitbox.setVisible(true);
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.UP) {
                up = false;
            } else if (event.getCode() == KeyCode.DOWN) {
                down = false;
            } else if (event.getCode() == KeyCode.LEFT) {
                left = false;
            } else if (event.getCode() == KeyCode.RIGHT) {
                right = false;
            }
        });
    }

    private void loadSprites() {
        for (int i = 0; i < spriteArray.length; i++) {
            spriteArray[i] = new ImageView(new Image("file:" + GameDriver.asset_folder + "NotLink/" + sprites[i], PIXEL_PER_TILE, PIXEL_PER_TILE, true, false));
        }
        for (int i = 0; i < health; i++) {
            if (i % 2 == 0) {
                hearts.add(new ImageView(new Image("file:" + GameDriver.asset_folder + "NotLink/h_h.png", 32, 32, true, false)));
            } else {
                hearts.add(new ImageView(new Image("file:" + GameDriver.asset_folder + "NotLink/h_f.png", 32, 32, true, false)));
                maxHearts.add(new ImageView(new Image("file:" + GameDriver.asset_folder + "NotLink/h_e.png", 32, 32, true, false)));
            }
            hearts.get(i).relocate(WINDOW_WIDTH + (i / 2 * 34) - (maxHealth / 2 * 34), 5);
        }
        for (int i = 0; i < maxHearts.size(); i++) {
            maxHearts.get(i).relocate(WINDOW_WIDTH + (i * 34) - (maxHealth / 2 * 34), 5);
        }
    }

    public void setVelocity() {
        if (right && left) {
            horizVelocity = 0;
        } else if (right) {
            horizVelocity = SPEED;
        } else if (left) {
            horizVelocity = -SPEED;
        } else {
            horizVelocity = 0;
        }

        if (up && down) {
            vertVelocity = 0;
        } else if (up) {
            vertVelocity = -SPEED;
        } else if (down) {
            vertVelocity = SPEED;
        } else {
            vertVelocity = 0;
        }
    }

    public String setPrimaryDirection(String currentDirection) {
        if (up && !down && ((!left && !right) || (left && right))) {
            return "up";
        }
        if (down && !up && ((!left && !right) || (left && right))) {
            return "down";
        }
        if (left && !right && ((!up && !down) || (up && down))) {
            return "left";
        }
        if (right && !left && ((!up && !down) || (up && down))) {
            return "right";
        }
        if ((up && !down && left && !right) && (!currentDirection.equals("up") && !currentDirection.equals("left"))) {
            return "up";
        }
        if ((down && !up && left && !right) && (!currentDirection.equals("down") && !currentDirection.equals("left"))) {
            return "left";
        }
        if ((up && !down && !left && right) && (!currentDirection.equals("up") && !currentDirection.equals("right"))) {
            return "right";
        }
        if ((down && !up && !left && right) && (!currentDirection.equals("down") && !currentDirection.equals("right"))) {
            return "down";
        }
        return currentDirection;
    }

    public void movement() {
        animation();
        final double elapsedSeconds = (timestamp - lastUpdate) / 1_000_000_000.0;
        final double deltaX = elapsedSeconds * horizVelocity;
        final double deltaY = elapsedSeconds * vertVelocity;
        final double oldPaneX = paneHandler.hitboxPane.getTranslateX();
        double newPaneX = oldPaneX - deltaX;
        final double oldPaneY = paneHandler.hitboxPane.getTranslateY();
        double newPaneY = oldPaneY - deltaY;
        final double oldHitBoxX = hitBox.getTranslateX();
        double newHitBoxX = oldHitBoxX + deltaX;
        final double oldHitBoxY = hitBox.getTranslateY();
        double newHitBoxY = oldHitBoxY + deltaY;
        final double oldSpriteX = spriteArray[0].getTranslateX();
        double newSpriteX;
        final double oldSpriteY = spriteArray[0].getTranslateY();
        double newSpriteY;

        if (newHitBoxX <= 0) {
            newHitBoxX = 0;
        } else if (newHitBoxX >= areas.get(currentArea).maxWidth) {
            newHitBoxX = areas.get(currentArea).maxWidth;
        }
        if (newHitBoxX <= (WINDOW_WIDTH - PIXEL_PER_TILE) / 2) {
            newPaneX = 0;
            newSpriteX = newHitBoxX;
        } else if (newHitBoxX >= areas.get(currentArea).maxWidth - (WINDOW_WIDTH + PIXEL_PER_TILE) / 2) {
            newPaneX = -(areas.get(currentArea).maxWidth - WINDOW_WIDTH);
            newSpriteX = newHitBoxX - (areas.get(currentArea).maxWidth - WINDOW_WIDTH);
        } else {
            newSpriteX = oldSpriteX;
        }
        hitBox.setTranslateX(newHitBoxX);
        if (Hitbox.collisionResolver(hitBox)) {
            if (newPaneX != oldPaneX) {
                paneHandler.hitboxPane.setTranslateX(oldPaneX);
                areas.get(currentArea).backgroundPane.setTranslateX(oldPaneX);
            }
            hitBox.setTranslateX(oldHitBoxX);
        } else {
            if (newPaneX != oldPaneX) {
                paneHandler.hitboxPane.setTranslateX(newPaneX);
                areas.get(currentArea).backgroundPane.setTranslateX(newPaneX);
            }
            moveAll("x", newSpriteX);
        }

        if (newHitBoxY <= 0) {
            newHitBoxY = 0;
        } else if (newHitBoxY >= areas.get(currentArea).maxHeight) {
            newHitBoxY = areas.get(currentArea).maxHeight;
        }
        if (newHitBoxY <= (WINDOW_HEIGHT - PIXEL_PER_TILE) / 2) {
            newPaneY = 0;
            newSpriteY = newHitBoxY;
        } else if (newHitBoxY >= areas.get(currentArea).maxHeight - (WINDOW_HEIGHT + PIXEL_PER_TILE) / 2) {
            newPaneY = -(areas.get(currentArea).maxHeight - WINDOW_HEIGHT);
            newSpriteY = newHitBoxY - (areas.get(currentArea).maxHeight - WINDOW_HEIGHT);
        } else {
            newSpriteY = oldSpriteY;
        }
        hitBox.setTranslateY(newHitBoxY);
        if (Hitbox.collisionResolver(hitBox)) {
            if (newPaneY != oldPaneY) {
                paneHandler.hitboxPane.setTranslateY(oldPaneY);
                areas.get(currentArea).backgroundPane.setTranslateY(oldPaneY);
            }
            hitBox.setTranslateY(oldHitBoxY);
        } else {
            if (newPaneY != oldPaneY) {
                paneHandler.hitboxPane.setTranslateY(newPaneY);
                areas.get(currentArea).backgroundPane.setTranslateY(newPaneY);
            }

            moveAll("y", newSpriteY);
        }
        
    }

    private void animation()
    {
        if (primaryDirection.equals("up")) {
            if (!lastDirection.equals("up")) {
                setAllInvisible();
                spriteArray[2].setVisible(true);
                spriteTimer = SPRITE_SPEED;
            }
            if (up && !down) {
                spriteTimer = spriteTimer + ((timestamp - lastUpdate) / 1_000_000_000.0);
                if (spriteTimer > SPRITE_SPEED) {
                    if (currentSprite == 2) {
                        setAllInvisible();
                        currentSprite = 3;
                        spriteArray[3].setVisible(true);
                    } else {
                        setAllInvisible();
                        currentSprite = 2;
                        spriteArray[2].setVisible(true);
                    }
                    spriteTimer = 0;
                }
            }
        }
        if (primaryDirection.equals("down")) {
            if (!lastDirection.equals("down")) {
                setAllInvisible();
                spriteArray[0].setVisible(true);
                spriteTimer = SPRITE_SPEED;
            }
            if (down && !up) {
                spriteTimer = spriteTimer + ((timestamp - lastUpdate) / 1_000_000_000.0);
                if (spriteTimer > SPRITE_SPEED) {
                    if (currentSprite == 0) {
                        setAllInvisible();
                        currentSprite = 1;
                        spriteArray[1].setVisible(true);
                    } else {
                        setAllInvisible();
                        currentSprite = 0;
                        spriteArray[0].setVisible(true);
                    }
                    spriteTimer = 0;
                }
            }
        }
        if (primaryDirection.equals("right")) {
            if (!lastDirection.equals("right")) {
                setAllInvisible();
                spriteArray[4].setVisible(true);
                spriteTimer = SPRITE_SPEED;
            }
            if (right && !left) {
                spriteTimer = spriteTimer + ((timestamp - lastUpdate) / 1_000_000_000.0);
                if (spriteTimer > SPRITE_SPEED) {
                    if (currentSprite == 4) {
                        setAllInvisible();
                        currentSprite = 5;
                        spriteArray[5].setVisible(true);
                    } else {
                        setAllInvisible();
                        currentSprite = 4;
                        spriteArray[4].setVisible(true);
                    }
                    spriteTimer = 0;
                }
            } else {
                setAllInvisible();
                currentSprite = 4;
                spriteArray[4].setVisible(true);
            }
        }
        if (primaryDirection.equals("left")) {
            if (!lastDirection.equals("left")) {
                setAllInvisible();
                spriteArray[6].setVisible(true);
                spriteTimer = SPRITE_SPEED;
            }
            if (left && !right) {
                spriteTimer = spriteTimer + ((timestamp - lastUpdate) / 1_000_000_000.0);
                if (spriteTimer > SPRITE_SPEED) {
                    if (currentSprite == 6) {
                        setAllInvisible();
                        currentSprite = 7;
                        spriteArray[7].setVisible(true);
                    } else {
                        setAllInvisible();
                        currentSprite = 6;
                        spriteArray[6].setVisible(true);
                    }
                    spriteTimer = 0;
                }
            } else {
                setAllInvisible();
                currentSprite = 6;
                spriteArray[6].setVisible(true);
            }
        }
    }

    void updateHealth(int damage) {
        if (health == 0)
        {
            error.setHeaderText("You are dead.");
            error.setGraphic(new ImageView(new Image("file:assets/dead.png")));
            error.setContentText("Oh no! You've run out of health and lost.\n\nBetter luck next time.");
            error.setOnHidden(evt -> System.exit(0));

            error.show();
        }
        if (damage == 0)
        {

        }
        else
        {
            if (((timestamp - lastHurt) / 250_000_000.0) > 1 && damage > 0)
            {
                lastHurt = timestamp;
                health = Math.max(health - damage, 0);
                updateHearts(health);
            }
            else if (damage < 0)
            {
                health = Math.min(health - damage, maxHealth);
                updateHearts(health);
            }
        }
    }

    private void moveAll(String direction, double distance)
    {
        for (int i = 0; i < spriteArray.length; i++)
        {
            if (direction.equals("x"))
            {
                spriteArray[i].setTranslateX(distance);
            }
            if (direction.equals("y"))
            {
                spriteArray[i].setTranslateY(distance);
            }
        }
    }

    private void updateHearts(int health)
    {
        for (int i = 0; i < hearts.size(); i++)
        {
            if (i >= health)
            {
                hearts.get(i).setVisible(false);
            }
            else
            {
                hearts.get(i).setVisible(true);
            }
        }
    }

    private void setAllInvisible()
    {
        for (ImageView iv : spriteArray)
        {
            iv.setVisible(false);
        }
    }

    void playerPlacer(double X, double Y)
    {
        if (X <= 0)
        {
            X = 0;
        }
        else if (X >= areas.get(currentArea).maxWidth - PIXEL_PER_TILE)
        {
            X = areas.get(currentArea).maxWidth - PIXEL_PER_TILE;
        }
        if (Y <=0)
        {
            Y = 0;
        }
        else if (Y >= areas.get(currentArea).maxHeight - PIXEL_PER_TILE)
        {
            Y = areas.get(currentArea).maxHeight - PIXEL_PER_TILE;
        }
        hitBox.setTranslateX(X);
        hitBox.setTranslateY(Y);
        if (X <= (WINDOW_WIDTH - PIXEL_PER_TILE) / 2) {
            paneHandler.hitboxPane.setTranslateX(0);
            areas.get(currentArea).backgroundPane.setTranslateX(0);
            player.moveAll("x", X);
        } else if (X >= areas.get(currentArea).maxWidth - (WINDOW_WIDTH + PIXEL_PER_TILE) / 2) {
            paneHandler.hitboxPane.setTranslateX(-(areas.get(currentArea).maxWidth - WINDOW_WIDTH));
            areas.get(currentArea).backgroundPane.setTranslateX(-(areas.get(currentArea).maxWidth - WINDOW_WIDTH));
            player.moveAll("x", X - (areas.get(currentArea).maxWidth - WINDOW_WIDTH));
        } else {
            paneHandler.hitboxPane.setTranslateX(-(X - (WINDOW_WIDTH - PIXEL_PER_TILE) / 2));
            areas.get(currentArea).backgroundPane.setTranslateX(-(X - (WINDOW_WIDTH - PIXEL_PER_TILE) / 2));
            player.moveAll("x", (WINDOW_WIDTH - PIXEL_PER_TILE) / 2);
        }
        if (Y <= (WINDOW_HEIGHT - PIXEL_PER_TILE) / 2) {
            paneHandler.hitboxPane.setTranslateY(0);
            areas.get(currentArea).backgroundPane.setTranslateY(0);
            player.moveAll("y", Y);
        } else if (Y >= areas.get(currentArea).maxHeight - (WINDOW_HEIGHT + PIXEL_PER_TILE) / 2) {
            paneHandler.hitboxPane.setTranslateY(-(areas.get(currentArea).maxHeight - WINDOW_HEIGHT));
            areas.get(currentArea).backgroundPane.setTranslateY(-(areas.get(currentArea).maxHeight - WINDOW_HEIGHT));
            player.moveAll("y", Y - (areas.get(currentArea).maxHeight - WINDOW_HEIGHT));
        } else {
            paneHandler.hitboxPane.setTranslateY(-(Y - (WINDOW_HEIGHT - PIXEL_PER_TILE) / 2));
            areas.get(currentArea).backgroundPane.setTranslateY(-(Y - (WINDOW_HEIGHT - PIXEL_PER_TILE) / 2));
            player.moveAll("y", (WINDOW_HEIGHT - PIXEL_PER_TILE) / 2);
        }
    }
}
