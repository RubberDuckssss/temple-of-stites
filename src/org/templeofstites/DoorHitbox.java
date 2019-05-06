package org.templeofstites;

public class DoorHitbox extends Hitbox {

    int areaNumber, correspondingDoor,height, width;
    private String side;

    public DoorHitbox(boolean isPassable, String name, int damage, int areaNumber, int correspondingDoor, String side) {
        super(isPassable, name, damage);
        this.areaNumber = areaNumber;
        this.correspondingDoor = correspondingDoor;
        this.side = side;
    }
    
    void updatePosition(int width, int height){
        this.height = height;
        this.width = width;
    }
}
