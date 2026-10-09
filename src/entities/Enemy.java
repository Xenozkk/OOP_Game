package entities;

import java.awt.Color;
import java.awt.Graphics;

public class Enemy extends Entity {
    private final Player player; // ต้องรู้พิกัดผู้เล่นเพื่อเดินตาม

    public Enemy(float x, float y, Player player) {
        // ให้ศัตรูขนาด 30x30 เลือด 10 ความเร็ว 1.5
        super(x, y, 30, 30, 2, 1.5f);
        this.player = player;
    }

    @Override
    public void update() {
        // --- AI พื้นฐาน: เดินเข้าหาผู้เล่น ---
        // 1. หาระยะห่างระหว่างเรากับผู้เล่น
        float diffX = player.getX() - this.x;
        float diffY = player.getY() - this.y;
        
        // 2. ใช้สูตรพีทาโกรัสหาระยะทางรวม
        float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        // 3. ขยับพิกัดเข้าหาผู้เล่น
        if (distance > 0) {
            this.x += (diffX / distance) * speed;
            this.y += (diffY / distance) * speed;
        }
    }

    @Override
    protected void renderFallback(Graphics g) {
        // ถ้ารูป Art ยังไม่มา ให้ศัตรูเป็นสี่เหลี่ยมสีแดง
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y, width, height);
    }

    @Override
    protected void die() {
        System.out.println("Enemy died!");
    }
}
