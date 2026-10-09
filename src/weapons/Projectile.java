package weapons;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * กระสุนที่ปืนยิงออกมา
 * เดินทางตรงไปตามมุมที่คำนวณ และหายไปเมื่อออกนอกจอหรือชนศัตรู
 */
public class Projectile {
    private float x, y;
    private float velX, velY;   // ความเร็วในแกน X, Y
    private int damage;
    private boolean active = true;
    private static final float SPEED = 8f;
    
    // ขนาดกระสุน
    public static final int SIZE = 8;

    public Projectile(float startX, float startY, float targetX, float targetY, int damage) {
        this.x = startX;
        this.y = startY;
        this.damage = damage;
        
        // คำนวณทิศทางด้วย Math.atan2 -> แปลงเป็น Velocity X, Y
        double angle = Math.atan2(targetY - startY, targetX - startX);
        this.velX = (float)(Math.cos(angle) * SPEED);
        this.velY = (float)(Math.sin(angle) * SPEED);
    }

    public void update() {
        x += velX;
        y += velY;
    }

    public void render(Graphics2D g2d) {
        // วาดกระสุนเป็นวงกลมสีเหลือง
        g2d.setColor(new Color(255, 230, 50));
        g2d.fillOval((int)x - SIZE/2, (int)y - SIZE/2, SIZE, SIZE);
        // วาดแสงรัศมีรอบกระสุนนิดหน่อยให้ดูสวย
        g2d.setColor(new Color(255, 255, 200, 100));
        g2d.fillOval((int)x - SIZE, (int)y - SIZE, SIZE*2, SIZE*2);
    }
    
    public boolean isOutOfBounds(int screenW, int screenH) {
        return x < 0 || x > screenW || y < 0 || y > screenH;
    }

    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public int getDamage() { return damage; }
}
