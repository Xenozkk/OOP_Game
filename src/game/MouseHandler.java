package game;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * คลาสสำหรับรับคำสั่งการคลิกเมาส์ในหน้า UI ต่างๆ
 */
public class MouseHandler extends MouseAdapter {
    private GamePanel gp;

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        // 1. หน้าต่าง Main Menu (TITLE)
        if (gp.currentState == GameState.TITLE) {
            // ปุ่ม Start Game
            if (mx >= 300 && mx <= 500 && my >= 400 && my <= 450) {
                gp.currentState = GameState.CHAR_SELECT;
            }
        } 
        // 2. หน้าต่าง เลือกตัวละคร (CHAR_SELECT)
        else if (gp.currentState == GameState.CHAR_SELECT) {
            // ปุ่ม Back
            if (mx >= 20 && mx <= 100 && my >= 20 && my <= 60) {
                gp.currentState = GameState.TITLE;
                return;
            }
            // กล่อง 1 (ตัวถึก)
            if (mx >= 150 && mx <= 350 && my >= 250 && my <= 450) {
                gp.selectedChar = 1;
                gp.currentState = GameState.WEAPON_SELECT;
            }
            // กล่อง 2 (ตัววิ่งไว)
            else if (mx >= 450 && mx <= 650 && my >= 250 && my <= 450) {
                gp.selectedChar = 2;
                gp.currentState = GameState.WEAPON_SELECT;
            }
        } 
        // 3. หน้าต่าง เลือกอาวุธ (WEAPON_SELECT)
        else if (gp.currentState == GameState.WEAPON_SELECT) {
            // ปุ่ม Back
            if (mx >= 20 && mx <= 100 && my >= 20 && my <= 60) {
                gp.currentState = GameState.CHAR_SELECT;
                return;
            }
            // กล่อง 1 (ดาบ)
            if (mx >= 150 && mx <= 350 && my >= 250 && my <= 450) {
                gp.selectedWeapon = 1;
                gp.setupGame(); 
                gp.currentState = GameState.PLAYING;
            }
            // กล่อง 2 (ปืน)
            else if (mx >= 450 && mx <= 650 && my >= 250 && my <= 450) {
                gp.selectedWeapon = 2;
                gp.setupGame(); 
                gp.currentState = GameState.PLAYING;
            }
        }
        // 4. หน้าต่าง Pause
        else if (gp.currentState == GameState.PAUSED) {
            // ปุ่ม Resume
            if (mx >= 300 && mx <= 500 && my >= 300 && my <= 350) {
                gp.currentState = GameState.PLAYING;
            }
            // ปุ่ม Restart
            else if (mx >= 300 && mx <= 500 && my >= 370 && my <= 420) {
                gp.setupGame();
                gp.currentState = GameState.PLAYING;
            }
            // ปุ่ม Main Menu
            else if (mx >= 300 && mx <= 500 && my >= 440 && my <= 490) {
                gp.currentState = GameState.TITLE;
            }
        }
        // 5. หน้าต่าง Shop
        else if (gp.currentState == GameState.SHOP) {
            // ปุ่มซื้อ HP (120, 310, 140x30)
            if (mx >= 120 && mx <= 260 && my >= 310 && my <= 340) {
                if (gp.player.getStats().spendMaterials(10)) {
                    gp.player.getStats().increaseMaxHp(5);
                    gp.player.setMaxHp(gp.player.getStats().getMaxHp());
                    gp.player.heal(5); // แถมฮีลให้ด้วย
                }
            }
            // ปุ่มซื้อ Speed (330, 310, 140x30)
            else if (mx >= 330 && mx <= 470 && my >= 310 && my <= 340) {
                if (gp.player.getStats().spendMaterials(15)) {
                    gp.player.getStats().setMoveSpeed(gp.player.getStats().getMoveSpeed() + 1.0f);
                }
            }
            // ปุ่มซื้อ Range (540, 310, 140x30)
            else if (mx >= 540 && mx <= 680 && my >= 310 && my <= 340) {
                if (gp.player.getStats().spendMaterials(10)) {
                    gp.player.getStats().setPickupRange(gp.player.getStats().getPickupRange() + 40.0f);
                }
            }
            // ปุ่ม Next Wave (300, 450, 200x60)
            else if (mx >= 300 && mx <= 500 && my >= 450 && my <= 510) {
                gp.currentWave++;
                gp.waveTimer = 30 * gp.FPS; // เริ่มจับเวลาใหม่
                gp.currentState = GameState.PLAYING;
            }
        }
        // 6. หน้าต่าง Game Over
        else if (gp.currentState == GameState.GAME_OVER) {
            // ปุ่ม Restart (เล่นตัวเดิม)
            if (mx >= 180 && mx <= 380 && my >= 400 && my <= 450) {
                gp.setupGame();
                gp.currentState = GameState.PLAYING;
            }
            // ปุ่ม Main Menu
            else if (mx >= 420 && mx <= 620 && my >= 400 && my <= 450) {
                gp.currentState = GameState.TITLE;
            }
        }
    }
}
