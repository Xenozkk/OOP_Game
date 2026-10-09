package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import stats.PlayerStats;
import game.KeyHandler;
import game.GamePanel;
import game.GameState;
import weapons.Weapon;
import weapons.Pistol;

public class Player extends Entity {
    private PlayerStats stats;
    private KeyHandler keyH;
    private GamePanel gp;
    private int invincibilityTimer = 0;
    
    // คลังอาวุธของผู้เล่น (Inventory)
    public ArrayList<Weapon> inventory = new ArrayList<>();

    public Player(GamePanel gp, float x, float y, PlayerStats stats, KeyHandler keyH) {
        super(x, y, 40, 40, stats.getMaxHp(), stats.getMoveSpeed());
        this.gp = gp;
        this.stats = stats;
        this.keyH = keyH;
    }
    
    public PlayerStats getStats() {
        return stats;
    }
    
    // เพิ่มอาวุธเข้ากระเป๋า
    public void addWeapon(Weapon w) {
        inventory.add(w);
    }

    private int regenTimer = 0;

    @Override
    public void update() {
        float currentSpeed = stats.getMoveSpeed();
        if (keyH.upPressed) { y -= currentSpeed; }
        if (keyH.downPressed) { y += currentSpeed; }
        if (keyH.leftPressed) { x -= currentSpeed; }
        if (keyH.rightPressed) { x += currentSpeed; }
        
        if (x < 0) { x = 0; }
        if (y < 0) { y = 0; }
        if (x > gp.SCREEN_WIDTH - this.width) { x = gp.SCREEN_WIDTH - this.width; }
        if (y > gp.SCREEN_HEIGHT - this.height) { y = gp.SCREEN_HEIGHT - this.height; }
        
        if (invincibilityTimer > 0) { invincibilityTimer--; }
        
        // --- ระบบ HP Regen ---
        if (stats.getHpRegen() > 0) {
            regenTimer++;
            if (regenTimer >= 300) { // ทุกๆ 5 วินาที (300 เฟรม)
                heal(stats.getHpRegen());
                regenTimer = 0;
            }
        }
    }
    
    // อัปเดตอาวุธทุกชิ้น (เรียกจาก GamePanel พร้อมส่งลิสต์ศัตรูให้)
    public void updateWeapons(ArrayList<Enemy> enemies) {
        for (Weapon w : inventory) {
            w.update(this, enemies); // 👑 ส่งตัวเอง (Player) เข้าไปให้อาวุธดึงสเตตัสได้
            // ถ้าเป็นปืน ให้อัปเดตกระสุนด้วย
            if (w instanceof Pistol) {
                ((Pistol)w).updateBullets(gp.SCREEN_WIDTH, gp.SCREEN_HEIGHT, enemies);
            }
        }
    }

    @Override
    public void takeDamage(int damage) {
        if (invincibilityTimer == 0) {
            // หักลบด้วยเกราะ
            int finalDamage = damage - stats.getArmor();
            if (finalDamage < 1) finalDamage = 1; // เจ็บขั้นต่ำ 1 เสมอ
            
            super.takeDamage(finalDamage);
            invincibilityTimer = 60;
        }
    }

    @Override
    protected void renderFallback(Graphics g) {
        if (invincibilityTimer > 0 && invincibilityTimer % 10 < 5) return;
        g.setColor(Color.BLUE);
        g.fillRect((int)x, (int)y, width, height);
    }
    
    // วาดอาวุธด้วย Graphics2D (เพื่อรองรับการหมุน/Rotate)
    public void renderWeapons(Graphics2D g2d) {
        for (Weapon w : inventory) {
            w.render(g2d);
        }
    }

    @Override
    protected void die() {
        System.out.println("Player has died! Game Over.");
        gp.currentState = GameState.GAME_OVER;
    }
}

