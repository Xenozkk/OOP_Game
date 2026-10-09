package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Enemy extends Entity {

    public enum Type {
        CHASER,
        RUNNER,
        SHOOTER,
        TANK,
        BRUTE
    }

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
                return 24;
            case TANK:
                return 38;
            case BRUTE:
                return 44;
            case SHOOTER:
                return 28;
            default:
                return 30;
        }
    }

    private static int getBaseHp(Type type) {
        switch (type) {
            case RUNNER:
                return 2;
            case SHOOTER:
                return 3;
            case TANK:
                return 10;
            case BRUTE:
                return 20;
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
            case TANK:
                return 0.6f;
            case BRUTE:
                return 0.4f;
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
        switch (type) {
            case TANK:
                return 2;
            case BRUTE:
                return 3;
            default:
                return 1;
        }
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
        // รักษาระยะจากผู้เล่น ไม่วิ่งชนเหมือนศัตรูทั่วไป
        float preferredDistance = 220.0f;

        if (distance > preferredDistance && distance > 0) {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        } else if (distance < 130.0f && distance > 0) {
            x -= (dx / distance) * speed;
            y -= (dy / distance) * speed;
        }

        if (shootCooldown > 0) {
            shootCooldown--;
        }

        if (distance <= 320.0f && shootCooldown <= 0) {
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
    protected void renderFallback(Graphics g) {
        switch (type) {
            case RUNNER:
                g.setColor(Color.YELLOW);
                break;
            case SHOOTER:
                g.setColor(Color.MAGENTA);
                break;
            case TANK:
                g.setColor(Color.GRAY);
                break;
            case BRUTE:
                g.setColor(new Color(120, 30, 30));
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