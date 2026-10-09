package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Base abstract class for all objects in the game.
 */
public abstract class GameObject {
    protected float x, y;
    protected int width, height;
    
    // ตัวแปรสำหรับเก็บรูปภาพจากฝ่าย Art
    protected BufferedImage sprite = null;

    public GameObject(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update();

    // ระบบวาดภาพอัตโนมัติ (ถ้ารูปยังไม่มา วาดสี่เหลี่ยมแทน / ถ้ารูปมาแล้ว วาดรูป)
    public void render(Graphics g) {
        if (sprite != null) {
            // ถ้ารูปมี วาดรูปลงในขนาด Hitbox เป๊ะๆ
            g.drawImage(sprite, (int)x, (int)y, width, height, null);
        } else {
            // ถ้ายังไม่มีรูปภาพ ให้ลูกๆ กำหนดสีและวาดสี่เหลี่ยมเองผ่านฟังก์ชัน fallback
            renderFallback(g);
        }
    }

    // บังคับให้ลูกๆ กำหนดการวาดชั่วคราว (ตอนที่ยังไม่มีรูปภาพ)
    protected abstract void renderFallback(Graphics g);

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    // Setter สำหรับใส่รูปตอน Art พร้อม
    public void setSprite(BufferedImage image) {
        this.sprite = image;
    }
}
