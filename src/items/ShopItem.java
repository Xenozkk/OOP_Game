package items;

import stats.PlayerStats;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.out.println("Error loading image: " + imagePath);
        }
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public BufferedImage getImage() { return image; }

    // คลาสลูกทุกตัว "ต้อง" เขียนทับฟังก์ชันนี้ เพื่อบอกว่าบวก/ลบ Stat อะไรบ้าง
    public abstract void applyEffect(PlayerStats stats);
}
