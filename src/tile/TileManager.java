package tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    int tileSize = 16;

    public int[][] layer1;
    public int[][] layer2;
    public int[][] layer3;
    public int[][] layer4;
    public int[][] layer5;

    private BufferedImage forestSheet;

    public TileManager(GamePanel gp) {
        this.gp = gp;

        layer1 = new int[gp.maxWorldRow][gp.maxWorldCol];
        layer2 = new int[gp.maxWorldRow][gp.maxWorldCol];
        layer3 = new int[gp.maxWorldRow][gp.maxWorldCol];
        layer4 = new int[gp.maxWorldRow][gp.maxWorldCol];
        layer5 = new int[gp.maxWorldRow][gp.maxWorldCol];

        loadSpritesheets();
        loadTiles();

        loadMap("/maps/map_Base.csv", layer1);
        loadMap("/maps/map_Layer2.csv", layer2);
        loadMap("/maps/map_Layer3.csv", layer3);
        loadMap("/maps/map_Layer4.csv", layer4);
        loadMap("/maps/map_Object_layer.csv", layer5);

    }

    private void loadSpritesheets() {
        try {
            forestSheet = ImageIO.read(getClass().getResourceAsStream("/tiles/forest_tiles.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isSolidTile(int id) {

    // All base ranges you gave
    int[][] baseRanges = {
        {512, 515}, {544, 547}, {576, 579}, {608, 611},
        {640, 643}, {672, 675}, {704, 707}, {736, 739},

        {384, 391}, {416, 423}, {448, 455}, {480, 487},

        {456, 467}, {488, 499}, {520, 537}
    };

    // Extend pattern every +32 until reaching ~744–761
    for (int offset = 0; offset <= (744 - 384); offset += 32) {

        for (int[] range : baseRanges) {

            int start = range[0] + offset;
            int end   = range[1] + offset;

            // stop if we go beyond your final limit
            if (start > 761) continue;

            if (id >= start && id <= end) {
                return true;
            }
        }
    }

        return false;
    }

    private void loadTiles() {
        
        int sheetCols = forestSheet.getWidth() / tileSize; // spritesheet is 32 tiles wide (512px / 16px)
    
        // Max ID in CSV is 755, size 800 to be safe
        tile = new Tile[800]; 
    
        for (int i = 0; i < tile.length; ++i) {
            int col = i % sheetCols;
            int row = i / sheetCols;
        
            int px = col * tileSize;
            int py = row * tileSize;
        
            if (px + tileSize <= forestSheet.getWidth() && 
                py + tileSize <= forestSheet.getHeight()) {
                tile[i] = new Tile();
                tile[i].image = forestSheet.getSubimage(px, py, tileSize, tileSize);

                if (isSolidTile(i)) {
                    tile[i].collision = true;
                }
            }
        }
    }

    public void loadMap(String filePath, int[][] layer) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // CSV from Tiled sometimes has a header line, skip it if needed
            // br.readLine(); // uncomment if first line is not numbers

            for (int row = 0; row < gp.maxWorldRow; ++row) {
                String line = br.readLine();
                if (line == null) break;

                String[] numbers = line.split(",");

                for (int col = 0; col < gp.maxWorldCol; ++col) {
                    // Tiled uses -1 for empty tiles, we keep that
                    layer[row][col] = Integer.parseInt(numbers[col].trim());
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        drawLayer(g2, layer1);
        drawLayer(g2, layer2);
        drawLayer(g2, layer3);
        drawLayer(g2, layer4);
        drawLayer(g2, layer5);

    }

    private void drawLayer(Graphics2D g2, int[][] layer) {
        
        // Clamp camera
        int camX = gp.player.worldX - gp.player.screenX;
        int camY = gp.player.worldY - gp.player.screenY;

        int maxCamX = gp.maxWorldCol * gp.tileSize - gp.screenWidth;
        int maxCamY = gp.maxWorldRow * gp.tileSize - gp.screenHeight;

        if (camX < 0) camX = 0;
        if (camY < 0) camY = 0;
        if (camX > maxCamX) camX = maxCamX;
        if (camY > maxCamY) camY = maxCamY;

        for (int worldRow = 0; worldRow < gp.maxWorldRow; ++worldRow) {
            for (int worldCol = 0; worldCol < gp.maxWorldCol; ++worldCol) {

                int tileNum = layer[worldRow][worldCol];
                if (tileNum < 0) continue;

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;

                // Use clamped camera
                int screenX = worldX - camX;
                int screenY = worldY - camY;

                if (worldX + gp.tileSize > camX &&
                    worldX < camX + gp.screenWidth &&
                    worldY + gp.tileSize > camY &&
                    worldY < camY + gp.screenHeight) {

                    if (tileNum < tile.length && tile[tileNum] != null) {
                        g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                    }
                }
            }
        }
    }

    public void loadObjects(object.SuperObject[] objects) {
        int index = 0;
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                int id = layer5[row][col];
                if (id < 0) continue; // skip empty tiles

                // Everything in this layer is special grass
                objects[index] = new object.ObjectGrass();
                objects[index].worldX = col * gp.tileSize;
                objects[index].worldY = row * gp.tileSize;
                index++;

                if (index >= objects.length) return; // safety, don't overflow array
            }
        }
    }


}