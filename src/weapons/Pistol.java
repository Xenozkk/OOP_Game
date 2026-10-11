package weapons;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import entities.Enemy;
import game.SoundManager;

/**
 * อาวุธปืน (Ranged Weapon)
 * - อยู่ตำแหน่งคงที่ข้างผู้เล่น (ซ้ายหรือขวา ขึ้นอยู่กับทิศศัตรู)
 * - Animation: Recoil (สะบัดถอยหลัง) ตอนยิง
 */
public class Pistol extends Weapon {

    private BufferedImage pistolImage;
    private BufferedImage shootAnimation;
    private int animationFrame;
    private int animationTimer;
    private static final int ANIMATION_FRAME_WIDTH = 48;
    private static final int ANIMATION_FRAME_HEIGHT = 48;
    private static final int ANIMATION_FRAME_DELAY = 2;
    private float recoilOffset = 0f;
    private static final float MAX_RECOIL = 8f;
    private static final float RECOIL_SPEED = 1.5f;

    private static final int GUN_W = 40;
    private static final int GUN_H = 24;

    public ArrayList<Projectile> bullets = new ArrayList<>();

    public Pistol() {
        super(2, 40, 500); // ดาเมจ 2, cooldown 40 เฟรม, ระยะ 500px (ลดลงตามความเหมาะสม)
        loadImage();
    }

    private void loadImage() {
        loadShootAnimation();

        String[] paths = {
            "assets/weapons/pistol.png",
            "../../assets/weapons/pistol.png",
            "/Users/xenoz/Game_OOP/assets/weapons/pistol.png"
        };
        for (String path : paths) {
            try {
                File f = new File(path);
                if (f.exists()) {
                    pistolImage = ImageIO.read(f);
                    System.out.println("✅ โหลดรูปปืนสำเร็จ: " + path);
                    return;
                }
            } catch (IOException e) { /* ลองต่อ */ }
        }
        System.out.println("⚠️ ไม่พบรูปปืน ใช้ Fallback แทน");
    }

    private void loadShootAnimation() {
        String relativePath = "assets/weapons/pistol-animatiom/[SHOOT WITH CASING AND MUZZLE FLASH] Glock - P80.png";
        String[] fallbackPaths = {
            relativePath,
            "/Users/xenoz/Game_OOP/" + relativePath,
            System.getProperty("user.dir") + "/" + relativePath
        };

        for (String p : fallbackPaths) {
            try {
                File file = new File(p);
                if (file.exists()) {
                    shootAnimation = ImageIO.read(file);
                    return;
                }
            } catch (Exception ignored) {}
        }
        System.err.println("Unable to load pistol shooting animation: " + relativePath);
    }

    @Override
    protected void attack(entities.Player player, ArrayList<Enemy> enemies) {
        Enemy nearest = findNearest(player, enemies);
        if (nearest != null) {
            // ปากกระบอกปืนอยู่ที่ weaponX + GUN_W ถ้าหันขวา หรือ weaponX ถ้าหันซ้าย
            float muzzleX = facingRight ? weaponX + GUN_W : weaponX;
            float muzzleY = weaponY + 8; // เลื่อนลงมาให้ตรงกับรูลำกล้องปืนพอดี
            
            // เล็งไปที่กลางตัวศัตรู
            float targetX = nearest.getX() + 20;
            float targetY = nearest.getY() + 20;
            
            // 👑 ส่งดาเมจที่บวกโบนัสแล้วให้กระสุน
            bullets.add(new Projectile(muzzleX, muzzleY, targetX, targetY, getCalculatedDamage(player)));
            SoundManager.play(SoundManager.Effect.SHOOT);
            recoilOffset = MAX_RECOIL;
            animationFrame = 0;
            animationTimer = getAnimationFrameCount() * ANIMATION_FRAME_DELAY;
        }
    }

    private Enemy findNearest(entities.Player player, ArrayList<Enemy> enemies) {
        Enemy nearest = null;
        float currentRange = getCalculatedRange(player); // 👑 ใช้ระยะที่บวกโบนัส
        float minDist = currentRange;
        for (Enemy e : enemies) {
            float dx = e.getX() - player.getX();
            float dy = e.getY() - player.getY();
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < minDist) { minDist = dist; nearest = e; }
        }
        return nearest;
    }

    @Override
    protected void updatePosition(entities.Player player, ArrayList<Enemy> enemies) {
        if (animationTimer > 0) {
            animationTimer--;
            if (animationTimer % ANIMATION_FRAME_DELAY == 0) {
                animationFrame++;
            }
        } else {
            animationFrame = 0;
        }

        // Recoil ฟื้นตัว
        if (recoilOffset > 0) {
            recoilOffset -= RECOIL_SPEED;
            if (recoilOffset < 0) recoilOffset = 0;
        }

        // ตรงกลางตัวผู้เล่น
        float playerCenterX = player.getX() + player.getWidth() / 2.0f; 
        
        // วางปืนซ้ายหรือขวาให้ระยะห่างเท่ากัน
        if (facingRight) {
            // ปืนอยู่ด้านขวาของผู้เล่น
            weaponX = playerCenterX + 10 - recoilOffset;
        } else {
            // ปืนอยู่ด้านซ้ายของผู้เล่น (ต้องลบด้วยความกว้างปืนเพื่อให้สมมาตร)
            weaponX = playerCenterX - 10 - GUN_W + recoilOffset;
        }
        
        // ให้ปืนอยู่ประมาณมือของผู้เล่น
        weaponY = player.getY() + player.getHeight() / 2.0f - GUN_H / 2.0f + 10;
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        java.awt.geom.AffineTransform original = g2d.getTransform();

        if (!facingRight) {
            // Flip รูปในแนวนอน
            g2d.translate(weaponX + GUN_W, weaponY);
            g2d.scale(-1, 1);
            if (hasActiveAnimation()) {
                drawAnimationFrame(g2d);
            } else if (pistolImage != null) {
                g2d.drawImage(pistolImage, 0, 0, GUN_W, GUN_H, null);
            } else {
                drawFallback(g2d);
            }
        } else {
            if (hasActiveAnimation()) {
                drawAnimationFrame(g2d);
            } else if (pistolImage != null) {
                g2d.drawImage(pistolImage, (int)weaponX, (int)weaponY, GUN_W, GUN_H, null);
            } else {
                g2d.translate(weaponX, weaponY);
                drawFallback(g2d);
            }
        }

        g2d.setTransform(original);

        // วาดกระสุน
        for (Projectile p : bullets) {
            p.render(g2d);
        }
    }

    private boolean hasActiveAnimation() {
        return shootAnimation != null && animationTimer > 0;
    }

    private int getAnimationFrameCount() {
        return shootAnimation == null
            ? 0
            : shootAnimation.getWidth() / ANIMATION_FRAME_WIDTH;
    }

    private void drawAnimationFrame(Graphics2D g2d) {
        int frameCount = getAnimationFrameCount();
        if (frameCount == 0) {
            return;
        }

        int frame = Math.min(animationFrame, frameCount - 1);
        int sourceX = frame * ANIMATION_FRAME_WIDTH;
        g2d.drawImage(
            shootAnimation,
            0,
            0,
            GUN_W,
            GUN_H,
            sourceX,
            0,
            sourceX + ANIMATION_FRAME_WIDTH,
            ANIMATION_FRAME_HEIGHT,
            null
        );
    }

    private void drawFallback(Graphics2D g2d) {
        g2d.setColor(new java.awt.Color(60, 60, 80));
        g2d.fillRect(0, 5, 40, 15);
        g2d.fillRect(20, 15, 10, 20);
    }

    public void updateBullets(int screenW, int screenH, ArrayList<Enemy> enemies) {
        ArrayList<Projectile> toRemove = new ArrayList<>();
        for (Projectile p : bullets) {
            p.update();
            for (Enemy e : enemies) {
                // เช็คการชนแบบสี่เหลี่ยม (AABB Collision) โดยดูว่าจุดศูนย์กลางกระสุนอยู่ในกรอบศัตรูหรือไม่
                boolean hit = p.getX() >= e.getX() &&
                              p.getX() <= e.getX() + e.getWidth() &&
                              p.getY() >= e.getY() &&
                              p.getY() <= e.getY() + e.getHeight();
                              
                if (hit) {
                    e.takeDamage(p.getDamage());
                    SoundManager.play(SoundManager.Effect.HIT);
                    toRemove.add(p);
                    break;
                }
            }
            if (p.isOutOfBounds(screenW, screenH)) toRemove.add(p);
        }
        bullets.removeAll(toRemove);
    }
}
