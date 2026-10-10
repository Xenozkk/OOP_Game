package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
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
    
    // Animation
    private BufferedImage[][] sprites;
    private int spriteCounter = 0;
    private int spriteNum = 0;
    private int direction = 0; // 0=Down, 1=Left, 2=Right, 3=Up
    private boolean isMoving = false;
    
    // คลังอาวุธของผู้เล่น (Inventory)
    public ArrayList<Weapon> inventory = new ArrayList<>();

    public Player(GamePanel gp, float x, float y, PlayerStats stats, KeyHandler keyH) {
        super(x, y, 80, 80, stats.getMaxHp(), stats.getMoveSpeed()); // ปรับขนาดเป็น 80x80
        this.gp = gp;
        this.stats = stats;
        this.keyH = keyH;
        
        loadPlayerSprite();
    }
    
    private void loadPlayerSprite() {
        try {
            String sheetPath = gp.selectedChar == 1 
                ? "/Users/xenoz/Game_OOP/assets/characters/knight_sheet.png" 
                : "/Users/xenoz/Game_OOP/assets/characters/player_sheet.png";
                
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));
            sprites = new BufferedImage[4][4]; // 4 ทิศทาง, 4 เฟรม
            for (int dir = 0; dir < 4; dir++) {
                for (int frame = 0; frame < 4; frame++) {
                    sprites[dir][frame] = spriteSheet.getSubimage(frame * 32, dir * 32, 32, 32);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public PlayerStats getStats() {
        return stats;
    }
    
    public void syncStats() {
        this.maxHp = stats.getMaxHp();
        this.speed = stats.getMoveSpeed(); // เผื่อมีการเรียกใช้จาก Entity
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
    }
    
    // เพิ่มอาวุธเข้ากระเป๋า
    public void addWeapon(Weapon w) {
        inventory.add(w);
    }

    private int regenTimer = 0;

    @Override
    public void update() {
        float currentSpeed = stats.getMoveSpeed();
        float dx = 0;
        float dy = 0;
        
        isMoving = false;
        if (keyH.upPressed) { dy -= 1; direction = 3; isMoving = true; }
        if (keyH.downPressed) { dy += 1; direction = 0; isMoving = true; }
        if (keyH.leftPressed) { dx -= 1; direction = 1; isMoving = true; }
        if (keyH.rightPressed) { dx += 1; direction = 2; isMoving = true; }
        
        // Normalize speed for diagonal movement
        if (dx != 0 && dy != 0) {
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;
        }
        
        x += dx * currentSpeed;
        y += dy * currentSpeed;
        
        // Animation counter
        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 10) { // ทุกๆ 10 เฟรมเปลี่ยนรูป
                spriteNum++;
                if (spriteNum >= 4) {
                    spriteNum = 0;
                }
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0; // ยืนนิ่งๆ ใช้เฟรมที่ 0
        }
        
        if (x < 0) { x = 0; }
        if (y < 0) { y = 0; }
        if (x > gp.SCREEN_WIDTH - this.width) { x = gp.SCREEN_WIDTH - this.width; }
        if (y > gp.SCREEN_HEIGHT - this.height) { y = gp.SCREEN_HEIGHT - this.height; }
        
        if (invincibilityTimer > 0) { invincibilityTimer--; }
        
        // --- ระบบ HP Regen ---
        if (stats.getHpRegen() > 0) {
            regenTimer++;
            if (regenTimer >= 600) { // ทุกๆ 10 วินาที (600 เฟรม)
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
    public void render(Graphics g) {
        if (invincibilityTimer > 0 && invincibilityTimer % 10 < 5) return;
        
        if (sprites != null && sprites[direction][spriteNum] != null) {
            // ขยายขนาด 32x32 ให้เป็น 40x40 (หรือตามที่ปรับ) บนหน้าจอ
            g.drawImage(sprites[direction][spriteNum], (int)x, (int)y, width, height, null);
        } else {
            renderFallback(g);
        }
    }

    @Override
    protected void renderFallback(Graphics g) {
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

