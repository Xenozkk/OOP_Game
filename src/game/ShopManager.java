package game;

import items.*;
import stats.PlayerStats;
import java.util.ArrayList;
import java.util.Random;

/**
 * ผู้จัดการร้านค้า (Shop Manager)
 * คอยจัดการการสุ่มไอเทม การซื้อ และการรีเฟรชร้าน
 */
public class ShopManager {
    private ArrayList<ShopItem> allItems = new ArrayList<>();
    private ShopItem[] currentItems = new ShopItem[3]; // ร้านค้ามี 3 สล็อต
    private int rerollCost = 2; // ค่ารีเฟรชเริ่มต้น
    private Random rand = new Random();

    public ShopManager() {
        // ลงทะเบียนไอเทมทั้งหมดที่มีในเกมเข้าโกดัง
        allItems.add(new EnergyDrink());
        allItems.add(new CameraLens());
        allItems.add(new GiftBox());       // เดิมคือ HeavyBackpack
        allItems.add(new Burger());        // เดิมคือ LeftoverBurger
        allItems.add(new CreditCard());
        allItems.add(new Battery());      // เดิมคือ SmartWatch
        allItems.add(new EspressoShot());
        allItems.add(new RubberDuck());
        allItems.add(new Chip());
        allItems.add(new HelmetArmor());
    }

    // เรียกตอนจบเวฟ เพื่อรีเซ็ตราคารีเฟรชและสุ่มของใหม่ฟรี 1 รอบ
    public void startNewWaveShop() {
        rerollCost = 2;
        rollItems();
    }

    // สุ่มไอเทมลงสล็อตแบบไม่ให้ซ้ำกัน
    private void rollItems() {
        // สร้างลิตส์ชั่วคราวจากของทั้งหมด
        ArrayList<ShopItem> temp = new ArrayList<>(allItems);
        
        // สุ่มของในลิตส์ชั่วคราว
        java.util.Collections.shuffle(temp, rand);
        
        // หยิบ 3 ชิ้นแรกมาใส่ในร้าน
        for (int i = 0; i < currentItems.length; i++) {
            if (i < temp.size()) {
                currentItems[i] = temp.get(i);
            }
        }
    }

    public boolean isShopEmpty() {
        for (ShopItem item : currentItems) {
            if (item != null) return false;
        }
        return true;
    }

    // ผู้เล่นกดปุ่ม Reroll
    public boolean reroll(PlayerStats stats) {
        // ถ้าเหมาของหมดร้านแล้ว ให้รีฟรี 1 ครั้ง
        if (isShopEmpty()) {
            rollItems();
            return true;
        }
        
        if (stats.spendMaterials(rerollCost)) {
            rollItems();
            rerollCost += 2; // กดแล้วแพงขึ้นทีละ 2 บาท
            return true;
        }
        return false;
    }

    // ผู้เล่นกดซื้อของในช่องที่กำหนด (0, 1, 2)
    public boolean buyItem(int slotIndex, PlayerStats stats) {
        if (slotIndex >= 0 && slotIndex < currentItems.length) {
            ShopItem item = currentItems[slotIndex];
            if (item != null) {
                if (stats.spendMaterials(item.getPrice())) {
                    item.applyEffect(stats); // แสดงผล Polymorphism
                    currentItems[slotIndex] = null; // ซื้อแล้วของหายไป
                    return true;
                }
            }
        }
        return false;
    }

    public ShopItem[] getCurrentItems() {
        return currentItems;
    }

    public int getRerollCost() {
        return rerollCost;
    }
}
