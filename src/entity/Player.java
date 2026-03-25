package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    
    boolean isMoving = false;
    private int targetWorldX, targetWorldY;
    private int moveSpeed = 4; // pixels per frame during animation

    private int speedBoostTimer = 0;
    private int boostedMoveSpeed = 8;


    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight/2 - (gp.tileSize / 2);

        solidArea = new Rectangle(1, 1, 46, 46); // collision of player character

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues(){
        
        worldX = gp.tileSize * 23; // starting position of player
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "right";
        angle = 0;

        // Snap to tile grid on start
        targetWorldX = worldX;
        targetWorldY = worldY;
    }

    public void getPlayerImage(){

        try{
            right = ImageIO.read(getClass().getResourceAsStream("/player/tractor.png"));
            left = ImageIO.read(getClass().getResourceAsStream("/player/tractor.png"));
            down = ImageIO.read(getClass().getResourceAsStream("/player/tractor.png"));
            up = ImageIO.read(getClass().getResourceAsStream("/player/tractor.png"));

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void activateSpeedBoost(int duration) {
        speedBoostTimer = duration;
        moveSpeed = boostedMoveSpeed;
    }

    public void update() {

        if (speedBoostTimer > 0) {
                speedBoostTimer--;
            if (speedBoostTimer == 0) {
                moveSpeed = speed; // reset speed
            }
        }

        if (isMoving) {
            // Slide toward target
            int dx = targetWorldX - worldX;
            int dy = targetWorldY - worldY;

            if (Math.abs(dx) <= moveSpeed && Math.abs(dy) <= moveSpeed) {
                // Snap exactly to target when close enough
                worldX = targetWorldX;
                worldY = targetWorldY;
                isMoving = false;
                gp.cChecker.checkObject(this);
            } else {
                // Move toward target
                if (dx != 0) worldX += (dx > 0) ? moveSpeed : -moveSpeed;
                if (dy != 0) worldY += (dy > 0) ? moveSpeed : -moveSpeed;
            }

        } else {
            int nextX = targetWorldX;
            int nextY = targetWorldY;

            if (keyH.upPressed) {
                direction = "up";
                angle = Math.toRadians(-90);
                nextY -= gp.tileSize;
            } else if (keyH.downPressed) {
                direction = "down";
                angle = Math.toRadians(90);
                nextY += gp.tileSize;
            } else if (keyH.leftPressed) {
                direction = "left";
                angle = 0;
                nextX -= gp.tileSize;
            } else if (keyH.rightPressed) {
                direction = "right";
                angle = 0;
                nextX += gp.tileSize;
            } else {
                return;
            }

            // World boundary check
            int maxX = gp.tileSize * (gp.maxWorldCol - 1);
            int maxY = gp.tileSize * (gp.maxWorldRow - 1);
            nextX = Math.max(0, Math.min(nextX, maxX));
            nextY = Math.max(0, Math.min(nextY, maxY));

            // Collision check against the target tile
            collisionOn = false;
            gp.cChecker.checkTileAt(this, nextX, nextY);

            if (!collisionOn) {
                targetWorldX = nextX;
                targetWorldY = nextY;
                isMoving = true;
            }
        }
    }

    public void draw(Graphics2D g2) {

        int camX = worldX - screenX;
        int camY = worldY - screenY;

        int maxCamX = gp.maxWorldCol * gp.tileSize - gp.screenWidth;
        int maxCamY = gp.maxWorldRow * gp.tileSize - gp.screenHeight;

        if (camX < 0) camX = 0;
        if (camY < 0) camY = 0;
        if (camX > maxCamX) camX = maxCamX;
        if (camY > maxCamY) camY = maxCamY;

        // Player screen position relative to clamped camera
        int drawX = worldX - camX;
        int drawY = worldY - camY;

        BufferedImage image = right;
        if (direction.equals("left")) image = left;
        if (direction.equals("up")) image = up;
        if (direction.equals("down")) image = down;

        g2.rotate(angle, drawX + gp.tileSize / 2, drawY + gp.tileSize / 2);

        if (direction.equals("left")) {
            g2.drawImage(image, drawX, drawY, -gp.tileSize, gp.tileSize, null);
        } else {
            g2.drawImage(image, drawX, drawY, gp.tileSize, gp.tileSize, null);
        }
    }
}