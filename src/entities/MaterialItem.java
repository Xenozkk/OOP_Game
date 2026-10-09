package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * ไอเทมเงิน (Material) ที่ดรอปจากศัตรู
 * สามารถถูกดูดเข้าหาผู้เล่นได้ถ้าระยะใกล้พอ
 */
public class MaterialItem extends GameObject {
    
    // ใช้ตัวแปร static เพื่อโหลดรูปแค่ครั้งเดียว (ประหยัดหน่วยความจำและ CPU)
    private static BufferedImage moneySprite = null;
    private static boolean imageLoaded = false;
    
    private boolean isMagnetized = false;
    private Player targetPlayer = null;
    private static final float MAGNET_SPEED = 8.0f;
    private int value;

    public MaterialItem(float x, float y, int value) {
        // ขยายขนาดปึกเงินให้ใหญ่และเห็นชัดขึ้น
        super(x, y, 32, 24); 
        this.value = value;
        
        // โหลดรูปถ้ายังไม่เคยโหลด
        if (!imageLoaded) {
            loadMoneySprite();
            imageLoaded = true;
        }
        
        // เซ็ตรูปภาพให้กับ GameObject พ่อ เพื่อให้มันวาดรูปให้อัตโนมัติใน render()
        if (moneySprite != null) {
            this.setSprite(moneySprite);
        }
    }

    private static void loadMoneySprite() {
        String[] paths = {
            "assets/items/money.png",
            "../../assets/items/money.png",
            "/Users/xenoz/Game_OOP/assets/items/money.png"
        };
        for (String path : paths) {
            try {
                File f = new File(path);
                if (f.exists()) {
                    moneySprite = ImageIO.read(f);
                    System.out.println("✅ โหลดรูปเงินสำเร็จ: " + path);
                    return;
                }
            } catch (IOException e) { /* ลองต่อ */ }
        }
        System.out.println("⚠️ ไม่พบรูปเงิน ใช้ Fallback แทน");
    }

    public void magnetize(Player p) {
        this.isMagnetized = true;
        this.targetPlayer = p;
    }
    
    public boolean isMagnetized() {
        return isMagnetized;
    }
    
    public int getValue() {
        return value;
    }

    @Override
    public void update() {
        if (isMagnetized && targetPlayer != null) {
            float px = targetPlayer.getX() + targetPlayer.getWidth() / 2;
            float py = targetPlayer.getY() + targetPlayer.getHeight() / 2;
            
            float cx = x + width / 2;
            float cy = y + height / 2;
            
            float dx = px - cx;
            float dy = py - cy;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (dist > 0) {
                x += (dx / dist) * MAGNET_SPEED;
                y += (dy / dist) * MAGNET_SPEED;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (sprite != null) {
            java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
            java.awt.geom.AffineTransform original = g2d.getTransform();
            
            // หมุน -90 องศา รอบจุดกึ่งกลางของปึกเงิน
            g2d.rotate(Math.toRadians(-90), x + width / 2.0, y + height / 2.0);
            
            g2d.drawImage(sprite, (int)x, (int)y, width, height, null);
            
            g2d.setTransform(original);
        } else {
            renderFallback(g);
        }
    }

    @Override
    protected void renderFallback(Graphics g) {
        g.setColor(new Color(50, 220, 80));
        g.fillOval((int)x, (int)y, width, height);
        g.setColor(new Color(20, 150, 40));
        g.drawOval((int)x, (int)y, width, height);
    }
}

