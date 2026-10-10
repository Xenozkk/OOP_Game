package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class Enemy extends Entity {

    public enum Type {
        CHASER,
        RUNNER,
        SHOOTER
    }

    private static BufferedImage chaserImg;
    private static BufferedImage runnerImg;
    private static BufferedImage shooterImg;

    private final Player player;
    private final Type type;
    private final int contactDamage;
    private final ArrayList<EnemyProjectile> projectiles = new ArrayList<>();

    private int shootCooldown = 0;

    public Enemy(float x, float y, Player player) {
        this(x, y, player, Type.CHASER, 1);
    }

    public Enemy(float x, float y, Player player, Type type, int wave) {
        super(
            x,
            y,
            getSize(type),
            getSize(type),
            getScaledHp(type, wave),
            getScaledSpeed(type, wave)
        );

        this.player = player;
        this.type = type;
        this.contactDamage = getDamage(type);
    }

    private static int getSize(Type type) {
        switch (type) {
            case RUNNER:
                return 40; // จาก 24 เป็น 40
            case SHOOTER:
                return 48; // จาก 28 เป็น 48
            default:
                return 50; // จาก 30 เป็น 50
        }
    }

    private static int getBaseHp(Type type) {
        switch (type) {
            case RUNNER:
                return 2;
            case SHOOTER:
                return 3;
            default:
                return 2;
        }
    }

    private static float getBaseSpeed(Type type) {
        switch (type) {
            case RUNNER:
                return 2.5f;
            case SHOOTER:
                return 0.8f;
            default:
                return 1.5f;
        }
    }

    private static int getScaledHp(Type type, int wave) {
        int extraHp = Math.max(0, wave - 1) / 3;
        return getBaseHp(type) + extraHp;
    }

    private static float getScaledSpeed(Type type, int wave) {
        float multiplier = 1.0f + Math.max(0, wave - 1) * 0.025f;
        multiplier = Math.min(multiplier, 1.25f);
        return getBaseSpeed(type) * multiplier;
    }

    private static int getDamage(Type type) {
        return 1;
    }

    @Override
    public void update() {
        if (player == null) {
            return;
        }

        float playerX = player.getX() + player.getWidth() / 2.0f;
        float playerY = player.getY() + player.getHeight() / 2.0f;
        float enemyX = x + width / 2.0f;
        float enemyY = y + height / 2.0f;

        float dx = playerX - enemyX;
        float dy = playerY - enemyY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (type == Type.SHOOTER) {
            updateShooter(dx, dy, distance, enemyX, enemyY, playerX, playerY);
        } else if (distance > 0) {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        }
    }

    private void updateShooter(float dx, float dy, float distance,
                               float enemyX, float enemyY,
                               float playerX, float playerY) {
        // รักษาระยะจากผู้เล่น ให้มากกว่าระยะยิงเริ่มต้นของผู้เล่น (500)
        float preferredDistance = 550.0f;

        if (distance > preferredDistance && distance > 0) {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        } else if (distance < 450.0f && distance > 0) {
            x -= (dx / distance) * speed;
            y -= (dy / distance) * speed;
        }

        if (shootCooldown > 0) {
            shootCooldown--;
        }

        if (distance <= 800.0f && shootCooldown <= 0) {
            projectiles.add(new EnemyProjectile(
                enemyX, enemyY, playerX, playerY, 1
            ));
            shootCooldown = 75;
        }
    }

    public void updateProjectiles(int screenWidth, int screenHeight) {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            EnemyProjectile projectile = projectiles.get(i);
            projectile.update();

            if (!projectile.isActive()
                    || projectile.isOutOfBounds(screenWidth, screenHeight)) {
                projectiles.remove(i);
            }
        }
    }

    public ArrayList<EnemyProjectile> getProjectiles() {
        return projectiles;
    }

    public Type getType() {
        return type;
    }

    public int getContactDamage() {
        return contactDamage;
    }

    @Override
    public void render(Graphics g) {
        BufferedImage img = null;
        try {
            if (type == Type.CHASER) {
                if (chaserImg == null) chaserImg = ImageIO.read(new File("/Users/xenoz/Game_OOP/assets/characters/enemy_chaser.png"));
                img = chaserImg;
            } else if (type == Type.RUNNER) {
                if (runnerImg == null) runnerImg = ImageIO.read(new File("/Users/xenoz/Game_OOP/assets/characters/enemy_runner.png"));
                img = runnerImg;
            } else if (type == Type.SHOOTER) {
                if (shooterImg == null) shooterImg = ImageIO.read(new File("/Users/xenoz/Game_OOP/assets/characters/enemy_shooter.png"));
                img = shooterImg;
            }
        } catch (Exception e) {}

        if (img != null && player != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            boolean faceLeft = player.getX() < this.x; // ถ้าเป้าหมายอยู่ซ้ายมือ
            
            if (faceLeft) {
                // หันหน้าซ้าย (Flip แนวนอน)
                g2d.translate((int)x + width, (int)y);
                g2d.scale(-1, 1);
                g2d.drawImage(img, 0, 0, width, height, null);
            } else {
                // หันหน้าขวา (ปกติ)
                g2d.drawImage(img, (int)x, (int)y, width, height, null);
            }
            g2d.dispose();
            
            // วาดหลอดเลือด
            g.setColor(Color.BLACK);
            g.fillRect((int) x, (int) y - 7, width, 4);
            g.setColor(Color.GREEN);
            int hpWidth = (int) (width * Math.max(0.0, Math.min(1.0, (double) hp / maxHp)));
            g.fillRect((int) x, (int) y - 7, hpWidth, 4);
            
        } else {
            renderFallback(g);
        }
    }

    @Override
    protected void renderFallback(Graphics g) {
        switch (type) {
            case RUNNER:
                g.setColor(Color.YELLOW);
                break;
            case SHOOTER:
                g.setColor(Color.MAGENTA);
                break;
            default:
                g.setColor(Color.RED);
                break;
        }

        g.fillRect((int) x, (int) y, width, height);

        // แถบเลือดเหนือศัตรู
        g.setColor(Color.BLACK);
        g.fillRect((int) x, (int) y - 7, width, 4);

        g.setColor(Color.GREEN);
        int hpWidth = (int) (width * Math.max(0.0,
                Math.min(1.0, (double) hp / maxHp)));
        g.fillRect((int) x, (int) y - 7, hpWidth, 4);
    }

    @Override
    protected void die() {
        System.out.println(type + " died!");
    }
}