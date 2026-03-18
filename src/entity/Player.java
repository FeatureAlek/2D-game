package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues(){
        
        x = 100;
        y = 100;
        speed = 4;
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
        if(keyH.upPressed == true){
            direction = "up";
            angle = Math.toRadians(-90);
            y -= speed;
        }
        else if(keyH.downPressed == true){
            direction = "down";
            angle = Math.toRadians(90);
            y += speed;
        }
        else if(keyH.leftPressed == true){
            direction = "left";
            angle = 0;
            x -= speed;
        }
        else if(keyH.rightPressed == true){
            direction = "right";
            angle = 0;
            x += speed;
        }
    }

    public void draw(Graphics2D g2){

        BufferedImage image = right;

        if(direction.equals("left")) image = left;
        if(direction.equals("up")) image = up;
        if(direction.equals("down")) image = down;

        g2.rotate(angle, x + gp.tileSize/2, y + gp.tileSize/2);

        if(direction.equals("left")){
            g2.drawImage(image, x, y, -gp.tileSize, gp.tileSize, null);
        }
        else{
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }


    }
}