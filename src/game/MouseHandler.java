package game;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    private GamePanel gp;

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        // ================================================
        // 1. Main Menu
        // ================================================
        if (gp.currentState == GameState.TITLE) {

            // Start Game
            if (mx >= 300 && mx <= 500
                    && my >= 400 && my <= 450) {

                gp.currentState = GameState.CHAR_SELECT;
            }
        }

        // ================================================
        // 2. Character Selection
        // ================================================
        else if (gp.currentState == GameState.CHAR_SELECT) {

            // Back
            if (mx >= 20 && mx <= 100
                    && my >= 20 && my <= 60) {

                gp.currentState = GameState.TITLE;
                return;
            }

            // Tank
            if (mx >= 150 && mx <= 350
                    && my >= 250 && my <= 450) {

                gp.selectedChar = 1;
                gp.currentState = GameState.WEAPON_SELECT;
            }

            // Speedy
            else if (mx >= 450 && mx <= 650
                    && my >= 250 && my <= 450) {

                gp.selectedChar = 2;
                gp.currentState = GameState.WEAPON_SELECT;
            }
        }

        // ================================================
        // 3. Weapon Selection
        // ================================================
        else if (gp.currentState == GameState.WEAPON_SELECT) {

            // Back
            if (mx >= 20 && mx <= 100
                    && my >= 20 && my <= 60) {

                gp.currentState = GameState.CHAR_SELECT;
                return;
            }

            // Sword
            if (mx >= 150 && mx <= 350
                    && my >= 250 && my <= 450) {

                gp.selectedWeapon = 1;

                gp.setupGame();
                gp.currentState = GameState.PLAYING;
            }

            // Pistol
            else if (mx >= 450 && mx <= 650
                    && my >= 250 && my <= 450) {

                gp.selectedWeapon = 2;

                gp.setupGame();
                gp.currentState = GameState.PLAYING;
            }
        }

        // ================================================
        // 4. Pause Menu
        // ================================================
        else if (gp.currentState == GameState.PAUSED) {

            // Resume
            if (mx >= 300 && mx <= 500
                    && my >= 300 && my <= 350) {

                gp.currentState = GameState.PLAYING;
            }

            // Restart
            else if (mx >= 300 && mx <= 500
                    && my >= 370 && my <= 420) {

                gp.setupGame();
                gp.currentState = GameState.PLAYING;
            }

            // Main Menu
            else if (mx >= 300 && mx <= 500
                    && my >= 440 && my <= 490) {

                gp.currentState = GameState.TITLE;
            }
        }

        // ================================================
        // 5. Shop
        // ================================================
        else if (gp.currentState == GameState.SHOP) {

            int startX = 70;
            int gap = 30;
            int width = 200;

            // Buy item
            for (int i = 0; i < 3; i++) {

                int x = startX + (i * (width + gap));

                int btnX = x + 30;
                int btnY = 180 + 200;

                if (mx >= btnX && mx <= btnX + 140
                        && my >= btnY && my <= btnY + 30) {

                    gp.shop.buyItem(i, gp.player.getStats());

                    // อัปเดต Max HP หลังซื้อไอเทม
                    gp.player.setMaxHp(
                        gp.player.getStats().getMaxHp()
                    );

                    return;
                }
            }

            // Reroll
            if (mx >= 100 && mx <= 300
                    && my >= 450 && my <= 510) {

                gp.shop.reroll(gp.player.getStats());
            }

            // Next Wave / Finish
            else if (mx >= 500 && mx <= 700
                    && my >= 450 && my <= 510) {

                // ถ้าผ่าน Wave 10 แล้ว ให้จบเกม
                if (gp.currentWave >= GamePanel.MAX_WAVE) {

                    gp.currentState = GameState.GAME_OVER;
                } else {

                    // เริ่ม Wave ถัดไป
                    gp.nextWave();
                }
            }
        }

        // ================================================
        // 6. Game Over / Victory
        // ================================================
        else if (gp.currentState == GameState.GAME_OVER) {

            // Restart
            if (mx >= 180 && mx <= 380
                    && my >= 400 && my <= 450) {

                gp.setupGame();
                gp.currentState = GameState.PLAYING;
            }

            // Main Menu
            else if (mx >= 420 && mx <= 620
                    && my >= 400 && my <= 450) {

                gp.currentState = GameState.TITLE;
            }
        }
    }
}