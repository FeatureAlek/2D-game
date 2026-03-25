package object;

import java.awt.image.BufferedImage;

public class SuperObject {
    public BufferedImage image;
    public String name;
    public int worldX, worldY;
    public boolean collision = false;

    // Override this in subclasses for interaction logic
    public void onPlayerEnter(main.GamePanel gp) {}
}