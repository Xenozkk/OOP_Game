package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * คลาสสำหรับรับคำสั่งจากแป้นพิมพ์ (Keyboard)
 */
public class KeyHandler implements KeyListener {
    
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    private GamePanel gp;
    
    // รับ GamePanel เข้ามาเพื่อใช้เปลี่ยนสถานะเกมเวลาผู้เล่นกดปุ่ม
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); 
        
        // --- ปุ่มบังคับทิศทาง ---
        if(code == KeyEvent.VK_W) { upPressed = true; }
        if(code == KeyEvent.VK_S) { downPressed = true; }
        if(code == KeyEvent.VK_A) { leftPressed = true; }
        if(code == KeyEvent.VK_D) { rightPressed = true; }
        
        // --- ปุ่มเมนู ---
        if(code == KeyEvent.VK_ESCAPE) {
            // ถ้ากำลังเล่นอยู่ ให้สลับเป็น Pause
            if (gp.currentState == GameState.PLAYING) {
                gp.currentState = GameState.PAUSED;
            } 
            // ถ้า Pause อยู่ ให้สลับกลับมาเล่น
            else if (gp.currentState == GameState.PAUSED) {
                gp.currentState = GameState.PLAYING;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        if(code == KeyEvent.VK_W) { upPressed = false; }
        if(code == KeyEvent.VK_S) { downPressed = false; }
        if(code == KeyEvent.VK_A) { leftPressed = false; }
        if(code == KeyEvent.VK_D) { rightPressed = false; }
    }
}
