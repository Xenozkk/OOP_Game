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

    public void update(entities.Player player, ArrayList<Enemy> enemies) {
        // 1. อัปเดตตำแหน่งรูปอาวุธให้แปะอยู่รอบผู้เล่นก่อน
        updatePosition(player, enemies);

        // 2. นับถอยหลัง cooldown
        if (cooldownTimer > 0) {
            cooldownTimer--;
        } else {
            // ถ้าพร้อมโจมตีแล้ว ให้โจมตีเลย
            attack(player, enemies);
            
            // คำนวณคูลดาวน์รอบต่อไป โดยเอา Attack Speed มาคิด
            int bonus = player.getStats().getAttackSpeedBonus();
            float multiplier = (bonus >= 0) ? (100.0f / (100.0f + bonus)) : ((100.0f - bonus) / 100.0f);
            cooldownTimer = Math.max(5, (int)(cooldown * multiplier)); // ยิงไวสุดได้แค่ 5 เฟรม/นัด
        }
    }

    // ฟังก์ชันคำนวณสเตตัสสดๆ
    protected int getCalculatedDamage(entities.Player player) {
        return Math.max(1, damage + player.getStats().getDamageBonus());
    }

    protected int getCalculatedRange(entities.Player player) {
        return Math.max(20, range + player.getStats().getWeaponRangeBonus());
    }

    // บังคับให้อาวุธทุกตัวต้องมีวิธีโจมตีเป็นของตัวเอง (Polymorphism)
    protected abstract void attack(entities.Player player, ArrayList<Enemy> enemies);

    // บังคับให้อาวุธทุกตัวต้องกำหนดว่าจะวางอาวุธไว้ที่ไหน
    protected abstract void updatePosition(entities.Player player, ArrayList<Enemy> enemies);

    // บังคับให้อาวุธทุกตัวต้องวาดตัวเองได้
    public abstract void render(Graphics2D g2d);
}
