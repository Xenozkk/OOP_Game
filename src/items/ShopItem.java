package items;

import stats.PlayerStats;

/**
 * คลาสแม่ของไอเทมทุกชิ้นในร้านค้า (Abstract Class)
 * โชว์เรื่อง Inheritance และ Polymorphism
 */
public abstract class ShopItem {
    protected String name;
    protected String description;
    protected int price;

    public ShopItem(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }

    // คลาสลูกทุกตัว "ต้อง" เขียนทับฟังก์ชันนี้ เพื่อบอกว่าบวก/ลบ Stat อะไรบ้าง
    public abstract void applyEffect(PlayerStats stats);
}
