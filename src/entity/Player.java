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
    
    boolean moving = false;
    int pixelCounter = 0;


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
        speed = 10;
        direction = "right";
        angle = 0;
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

    public void update(){

        collisionOn = false;
        gp.cChecker.checkTile(this);

        // TODO implement tile based walking
            if(keyH.upPressed == true){
                direction = "up";
                angle = Math.toRadians(-90);
                if (!collisionOn) worldY -= speed;
            }
            else if(keyH.downPressed == true){
                direction = "down";
                angle = Math.toRadians(90);
                if (!collisionOn) worldY += speed;
            }
            else if(keyH.leftPressed == true){
                direction = "left";
                angle = 0;
                if (!collisionOn) worldX -= speed;
            }
            else if(keyH.rightPressed == true){
                direction = "right";
                angle = 0;
                if (!collisionOn) worldX += speed;
            }


        // World boundaries
        int maxX = gp.tileSize * gp.maxWorldCol - gp.tileSize;
        int maxY = gp.tileSize * gp.maxWorldRow - gp.tileSize;

        if(worldX < 0) worldX = 0;
        if(worldY < 0) worldY = 0;
        if(worldX > maxX) worldX = maxX;
        if(worldY > maxY) worldY = maxY;


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