package items;

import stats.PlayerStats;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * คลาสแม่ของไอเทมทุกชิ้นในร้านค้า (Abstract Class)
 * โชว์เรื่อง Inheritance และ Polymorphism
 */
public abstract class ShopItem {
    protected String name;
    protected String description;
    protected int price;
    protected BufferedImage image;

    public ShopItem(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    protected void loadImage(String imagePath) {
        String clean = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
        try (InputStream resource = ShopItem.class.getResourceAsStream("/" + clean)) {
            if (resource != null) {
                this.image = ImageIO.read(resource);
                return;
            }
        } catch (Exception ignored) {}

        String[] paths = {
            clean,
            "/Users/xenoz/Game_OOP/" + clean,
            System.getProperty("user.dir") + "/" + clean
        };
        for (String path : paths) {
            try {
                File f = new File(path);
                if (f.exists()) {
                    this.image = ImageIO.read(f);
                    return;
                }
            } catch (IOException e) { }
        }
        System.out.println("Error loading image (Not Found): " + imagePath);
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public BufferedImage getImage() { return image; }

    // คลาสลูกทุกตัว "ต้อง" เขียนทับฟังก์ชันนี้ เพื่อบอกว่าบวก/ลบ Stat อะไรบ้าง
    public abstract void applyEffect(PlayerStats stats);
}
