package entities;

import java.awt.Color;
import java.awt.Graphics;

public class EnemyProjectile {
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private int damage;
    private boolean active = true;

    private static final float SPEED = 4.0f;
    private static final int SIZE = 8;

    public EnemyProjectile(float startX, float startY,
                           float targetX, float targetY, int damage) {
        this.x = startX;
        this.y = startY;
        this.damage = damage;

        float dx = targetX - startX;
        float dy = targetY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            velocityX = (dx / distance) * SPEED;
            velocityY = (dy / distance) * SPEED;
        }
    }

    public void update() {
        x += velocityX;
        y += velocityY;
    }

    public void render(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillOval((int) x - SIZE / 2, (int) y - SIZE / 2, SIZE, SIZE);
    }

    public boolean isOutOfBounds(int screenWidth, int screenHeight) {
        return x < 0 || x > screenWidth || y < 0 || y > screenHeight;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getDamage() {
        return damage;
    }
}