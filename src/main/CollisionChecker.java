package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp){
        this.gp = gp;
    }

    public void checkTile(Entity entity) {

        int entityLeftWorldX   = entity.worldX + entity.solidArea.x;
        int entityRightWorldX  = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY    = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol   = entityLeftWorldX  / gp.tileSize;
        int entityRightCol  = entityRightWorldX / gp.tileSize;
        int entityTopRow    = entityTopWorldY   / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                tileNum1 = getTileAt(entityLeftCol,  entityTopRow);
                tileNum2 = getTileAt(entityRightCol, entityTopRow);
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = getTileAt(entityLeftCol,  entityBottomRow);
                tileNum2 = getTileAt(entityRightCol, entityBottomRow);
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = getTileAt(entityLeftCol, entityTopRow);
                tileNum2 = getTileAt(entityLeftCol, entityBottomRow);
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = getTileAt(entityRightCol, entityTopRow);
                tileNum2 = getTileAt(entityRightCol, entityBottomRow);
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
        }
    }

    // Check all layers for a solid tile at given col/row
    private int getTileAt(int col, int row) {
        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow) return -1;
        
        // Check each layer, return first solid tile found
        int[][] layers[] = {gp.tileM.layer1, gp.tileM.layer2, gp.tileM.layer3, gp.tileM.layer4};
        for (int[][] layer : layers) {
            int id = layer[row][col];
            if (id >= 0 && id < gp.tileM.tile.length && gp.tileM.tile[id] != null) {
                if (gp.tileM.tile[id].collision) return id;
            }
        }
        return -1;
    }

    public void checkTileAt(Entity entity, int nextX, int nextY) {

        int left   = (nextX + entity.solidArea.x) / gp.tileSize;
        int right  = (nextX + entity.solidArea.x + entity.solidArea.width) / gp.tileSize;
        int top    = (nextY + entity.solidArea.y) / gp.tileSize;
        int bottom = (nextY + entity.solidArea.y + entity.solidArea.height) / gp.tileSize;

        int[][] layersArr[] = {gp.tileM.layer1, gp.tileM.layer2, gp.tileM.layer3, gp.tileM.layer4};

        int[] cols = {left, right};
        int[] rows = {top, bottom};

        for (int[][] layer : layersArr) {
            for (int r : rows) {
                for (int c : cols) {
                    if (r < 0 || c < 0 || r >= gp.maxWorldRow || c >= gp.maxWorldCol) {
                        entity.collisionOn = true;
                        return;
                    }
                    int id = layer[r][c];
                    if (id >= 0 && id < gp.tileM.tile.length
                            && gp.tileM.tile[id] != null
                            && gp.tileM.tile[id].collision) {
                        entity.collisionOn = true;
                        return;
                    }
                }
            }
        }
    }

    private boolean isSolid(int tileNum) {
        if (tileNum < 0) return false;
        return gp.tileM.tile[tileNum] != null && gp.tileM.tile[tileNum].collision;
    }

    public void checkObject(entity.Entity entity) {

    for (object.SuperObject obj : gp.objects) {
        if (obj == null) continue;

        // Check if entity is on the same tile as the object
        int entityCol = entity.worldX / gp.tileSize;
        int entityRow = entity.worldY / gp.tileSize;
        int objCol    = obj.worldX    / gp.tileSize;
        int objRow    = obj.worldY    / gp.tileSize;

        if (entityCol == objCol && entityRow == objRow) {
            obj.onPlayerEnter(gp);
        }
    }
}

}