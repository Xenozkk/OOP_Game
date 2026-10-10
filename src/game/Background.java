package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class Background {

    private BufferedImage groundTile;
    private static final int TILE_SIZE = 64;
    private String currentMapName;

    public Background() {
    }

    public void generateRandomMap(Random random) {
        int mapType = random.nextInt(3);

        switch (mapType) {
            case 0:
                currentMapName = "CAVERN";
                break;
            case 1:
                currentMapName = "RUINS";
                break;
            default:
                currentMapName = "CAVE";
                break;
        }

        loadMapImages(currentMapName);
    }

    private void loadMapImages(String mapType) {
        String imagePath;
        
        // เลือกไฟล์ภาพตามธีม
        if (mapType.equals("CAVERN")) {
            imagePath = "assets/backgrounds/Cavern_03.png";
        } else if (mapType.equals("RUINS")) {
            imagePath = "assets/backgrounds/Ruins_04.png";
        } else {
            imagePath = "assets/backgrounds/Cave_04.png";
        }

        try {
            BufferedImage fullImage = ImageIO.read(new File(imagePath));
            
            // ตัดภาพพื้นเป็น Tile ขนาด 64x64 พิกเซล
            groundTile = fullImage.getSubimage(
                0, TILE_SIZE, TILE_SIZE, TILE_SIZE
            );

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    // เมธอดสำหรับวาดฉากลงจอ 
    public void draw(Graphics g, int screenWidth, int screenHeight, int cameraX, int cameraY) {
        if (groundTile == null) {
            return;
        }

        int startX = (cameraX % TILE_SIZE) - TILE_SIZE;
        int startY = (cameraY % TILE_SIZE) - TILE_SIZE;

        for (int y = startY; y < screenHeight + TILE_SIZE; y += TILE_SIZE) {
            for (int x = startX; x < screenWidth + TILE_SIZE; x += TILE_SIZE) {
                g.drawImage(groundTile, x, y, TILE_SIZE, TILE_SIZE, null);
            }
        }
    }
}
