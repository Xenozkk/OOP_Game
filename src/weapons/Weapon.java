package weapons;

import java.awt.Graphics2D;
import java.util.ArrayList;
import entities.Enemy;

/**
 * Abstract class สำหรับอาวุธทุกชนิด (Polymorphism หลัก)
 * ทุกอาวุธต้องมีวิธี attack() ของตัวเอง
 */
public abstract class Weapon {
    protected int damage;       // ดาเมจต่อครั้ง
    protected int cooldown;     // ความถี่โจมตี (เฟรม)
    protected int cooldownTimer = 0; // ตัวนับถอยหลังก่อนยิงได้
    protected int range;        // ระยะโจมตี (pixels)
    
    // ตำแหน่งของอาวุธ (แปะไว้รอบๆ ตัวผู้เล่น)
    protected float weaponX, weaponY;

    public Weapon(int damage, int cooldown, int range) {
        this.damage = damage;
        this.cooldown = cooldown;
        this.range = range;
    }

    public void update(float playerX, float playerY, ArrayList<Enemy> enemies) {
        // 1. อัปเดตตำแหน่งรูปอาวุธให้แปะอยู่รอบผู้เล่นก่อน
        updatePosition(playerX, playerY, enemies);

        // 2. นับถอยหลัง cooldown
        if (cooldownTimer > 0) {
            cooldownTimer--;
        } else {
            // ถ้าพร้อมโจมตีแล้ว ให้โจมตีเลย
            attack(playerX, playerY, enemies);
            cooldownTimer = cooldown;
        }
    }

    // บังคับให้อาวุธทุกตัวต้องมีวิธีโจมตีเป็นของตัวเอง (Polymorphism)
    protected abstract void attack(float playerX, float playerY, ArrayList<Enemy> enemies);

    // บังคับให้อาวุธทุกตัวต้องกำหนดว่าจะวางอาวุธไว้ที่ไหน
    protected abstract void updatePosition(float playerX, float playerY, ArrayList<Enemy> enemies);

    // บังคับให้อาวุธทุกตัวต้องวาดตัวเองได้
    public abstract void render(Graphics2D g2d);
}
