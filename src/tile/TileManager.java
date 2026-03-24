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

    int[][] layer1;
    int[][] layer2;
    int[][] layer3;
    int[][] layer4;

    private BufferedImage forestSheet;

    public TileManager(GamePanel gp) {
        this.gp = gp;

        layer1 = new int[gp.maxWorldRow][gp.maxWorldCol];
        layer2 = new int[gp.maxWorldRow][gp.maxWorldCol];
        layer3 = new int[gp.maxWorldRow][gp.maxWorldCol];
        layer4 = new int[gp.maxWorldRow][gp.maxWorldCol];

        loadSpritesheets();
        loadTiles();

        loadMap("/maps/map_Tile_Layer_1.csv", layer1);
        loadMap("/maps/map_Tile_Layer_2.csv", layer2);
        loadMap("/maps/map_Tile_Layer_3.csv", layer3);
        loadMap("/maps/map_Tile_Layer_4.csv", layer4);
    }

    private void loadSpritesheets() {
        try {
            forestSheet = ImageIO.read(getClass().getResourceAsStream("/tiles/forest_tiles.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    }

    private void drawLayer(Graphics2D g2, int[][] layer) {
        
        for (int worldRow = 0; worldRow < gp.maxWorldRow; ++worldRow) {
            for (int worldCol = 0; worldCol < gp.maxWorldCol; ++worldCol) {

                int tileNum = layer[worldRow][worldCol];

                // -1 means empty in Tiled CSV, skip it
                if (tileNum < 0) continue;

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;

                int camX = gp.player.worldX - gp.player.screenX;
                int camY = gp.player.worldY - gp.player.screenY;

                int screenX = worldX - camX;
                int screenY = worldY - camY;

                // Frustum culling
                if (worldX + gp.tileSize > camX &&
                    worldX < camX + gp.screenWidth &&
                    worldY + gp.tileSize > camY &&
                    worldY < camY + gp.screenHeight) {

                    // Safety check — don't crash if tile index not loaded
                    if (tileNum < tile.length && tile[tileNum] != null) {
                        g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                    }
                }
            }
        }
    }
}