package weapons;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import entities.Enemy;

/**
 * อาวุธดาบ (Melee Weapon)
 * Animation: ดาบอยู่กับที่ข้างผู้เล่น พอเจอศัตรูในระยะ จะพุ่ง (Lunge) เข้าหาแล้วสลับกลับ
 */
public class Sword extends Weapon {

    private BufferedImage swordImage;

    // ตำแหน่ง "บ้าน" (ที่วางดาบเมื่อไม่โจมตี) สัมพัทธ์กับผู้เล่น
    private static final float HOME_OFFSET_X = 30f;
    private static final float HOME_OFFSET_Y = -10f;
    private static final int SWORD_W = 44;
    private static final int SWORD_H = 44;

    // --- ระบบ Lunge (พุ่งเข้าหาศัตรู) ---
    private enum SwordState { IDLE, LUNGING, RETURNING }
    private SwordState state = SwordState.IDLE;

    private float lungeTargetX, lungeTargetY; // จุดหมายที่พุ่งไป
    private float lungeStartX, lungeStartY;   // จุดเริ่มต้นก่อนพุ่ง
    private float lungeProgress = 0f;         // 0.0 = เริ่ม, 1.0 = ถึงเป้า
    private static final float LUNGE_SPEED = 0.18f;
    private static final float RETURN_SPEED = 0.12f;

    public Sword() {
        super(3, 40, 180); // ดาเมจ 3, cooldown 40 เฟรม, ระยะ 180px (넓어졌음)
        loadImage();
    }

    private void loadImage() {
        // ลอง path ต่างๆ เผื่อ working directory ต่างกัน
        String[] paths = {
            "assets/weapons/sword.png",
            "../../assets/weapons/sword.png",
            "/Users/xenoz/Game_OOP/assets/weapons/sword.png"
        };
        for (String path : paths) {
            try {
                File f = new File(path);
                if (f.exists()) {
                    swordImage = ImageIO.read(f);
                    System.out.println("✅ โหลดรูปดาบสำเร็จจาก: " + path);
                    return;
                }
            } catch (IOException e) { /* ลองต่อ */ }
        }
        System.out.println("⚠️ ไม่พบรูปดาบ ใช้ Fallback แทน");
    }

    @Override
    protected void attack(entities.Player player, ArrayList<Enemy> enemies) {
        if (state != SwordState.IDLE) return; // ถ้ากำลังพุ่งอยู่ ข้ามไป

        // หาศัตรูที่ใกล้ที่สุดในระยะ
        Enemy target = null;
        float currentRange = getCalculatedRange(player); // 👑 ใช้โบนัสระยะฟัน
        float minDist = currentRange;
        for (Enemy e : enemies) {
            float dx = e.getX() - player.getX();
            float dy = e.getY() - player.getY();
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < minDist) { minDist = dist; target = e; }
        }

        if (target != null) {
            // เริ่ม Lunge ไปหาศัตรู
            lungeStartX = weaponX;
            lungeStartY = weaponY;
            lungeTargetX = target.getX();
            lungeTargetY = target.getY();
            lungeProgress = 0f;
            state = SwordState.LUNGING;
            target.takeDamage(getCalculatedDamage(player)); // 👑 ใช้โบนัสดาเมจ
        }
    }

    @Override
    protected void updatePosition(entities.Player player, ArrayList<Enemy> enemies) {
        float homeX = player.getX() + HOME_OFFSET_X;
        float homeY = player.getY() + HOME_OFFSET_Y;

        switch (state) {
            case IDLE:
                // อยู่นิ่งข้างผู้เล่น
                weaponX = homeX;
                weaponY = homeY;
                break;

            case LUNGING:
                // พุ่งไปหาเป้า
                lungeProgress = Math.min(1f, lungeProgress + LUNGE_SPEED);
                weaponX = lerp(lungeStartX, lungeTargetX, easeOut(lungeProgress));
                weaponY = lerp(lungeStartY, lungeTargetY, easeOut(lungeProgress));
                if (lungeProgress >= 1f) {
                    state = SwordState.RETURNING;
                    lungeProgress = 0f;
                }
                break;

            case RETURNING:
                // ดึงกลับบ้าน
                lungeProgress = Math.min(1f, lungeProgress + RETURN_SPEED);
                float fromX = lungeTargetX, fromY = lungeTargetY;
                weaponX = lerp(fromX, homeX, easeOut(lungeProgress));
                weaponY = lerp(fromY, homeY, easeOut(lungeProgress));
                if (lungeProgress >= 1f) {
                    state = SwordState.IDLE;
                    lungeProgress = 0f;
                }
                break;
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        java.awt.geom.AffineTransform original = g2d.getTransform();

        // หมุนดาบทำมุม 45 องศาแบบตายตัว (แนวเฉียง เหมือนในรูป)
        g2d.rotate(Math.toRadians(-45), weaponX + SWORD_W / 2.0, weaponY + SWORD_H / 2.0);

        if (swordImage != null) {
            g2d.drawImage(swordImage, (int)weaponX, (int)weaponY, SWORD_W, SWORD_H, null);
        } else {
            g2d.setColor(new java.awt.Color(180, 200, 220));
            g2d.fillRect((int)weaponX, (int)weaponY, SWORD_W, SWORD_H);
        }

        g2d.setTransform(original);
    }

    // --- Helper Math ---
    private float lerp(float a, float b, float t) { return a + (b - a) * t; }
    private float easeOut(float t) { return 1 - (1 - t) * (1 - t); }
}

