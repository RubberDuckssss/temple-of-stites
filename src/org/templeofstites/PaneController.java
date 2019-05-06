package org.templeofstites;

import static org.templeofstites.GameDriver.*;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PaneController
{
    // Wish this was a pain controller too, because my brain needs some oxycodone now.
    Pane masterPane, hitboxPane, hudPane;
    FadeTransition doorFade = new FadeTransition();
    private Rectangle doorFadeRectangle;
    Scene scene;

    public PaneController() {
        masterPane = new Pane();
        hitboxPane = new Pane();
        hudPane = new Pane();
        scene = new Scene(masterPane, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);
        masterPane.getChildren().addAll(hitboxPane, hudPane);

        doorFadeRectangle = new Rectangle(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        doorFade.setNode(doorFadeRectangle);
        doorFade.setDuration(new Duration(2000));
        doorFade.setFromValue(1.0);
        doorFade.setToValue(0);
        doorFade.setCycleCount(1);
        doorFade.play();
    }

    void loadHud(Player player) {
        for (int i = 0; i < player.maxHearts.size(); i++) {
            hudPane.getChildren().add((player.maxHearts).get(i));
        }
        for (int i = 0; i < player.hearts.size(); i++) {
            hudPane.getChildren().add((player.hearts).get(i));
        }
        hudPane.getChildren().add(doorFadeRectangle);
        Text version = new Text("v" + getVersion());
        version.setFont(new Font("Comic Sans MS", 32));
        version.setFill(Color.WHITE);
        version.setStroke(Color.BLACK);
        version.setStrokeWidth(1.4);
        version.setX(0);
        version.setY(25);
        hudPane.getChildren().add(version);
    }

    void changePane(Area area)
    {
        masterPane.getChildren().clear();
        hitboxPane.getChildren().clear();
        for (int i = 0; i < area.hitBoxList.size(); i++) {
            hitboxPane.getChildren().add(area.hitBoxList.get(i).shape);
        }
        hitboxPane.getChildren().add(player.hitBox);
        for (int i = 0; i < player.spriteArray.length; i++) {
            area.entityPane.getChildren().add(player.spriteArray[i]);
        }
        Hitbox.setVisible(false);
        masterPane.getChildren().addAll(area.backgroundPane, area.entityPane, hitboxPane, hudPane);
    }
}
