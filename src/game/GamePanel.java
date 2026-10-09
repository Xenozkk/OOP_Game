package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JPanel;
import entities.Player;
import entities.Enemy;
import entities.MaterialItem;
import stats.PlayerStats;

public class GamePanel extends JPanel implements Runnable {
    
    public final int SCREEN_WIDTH = 800;
    public final int SCREEN_HEIGHT = 600;
    int FPS = 60;
    Thread gameThread;
    
    // ตั้งให้เกมเริ่มที่หน้า TITLE แทน PLAYING
    public GameState currentState = GameState.TITLE; 
    
    // ระบบรับ input
    KeyHandler keyH;
    MouseHandler mouseH;
    
    // สิ่งต่างๆ ในเกม
    Player player;
    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<MaterialItem> materials = new ArrayList<>(); // ลิสต์เก็บเงินบนพื้น
    int frameCount = 0;
    Random random = new Random();
    
    // ระบบ Wave
    public int currentWave = 1;
    public int waveTimer = 30 * FPS; // 30 วินาที
    
    // ตัวแปรเก็บค่าการเลือกจากหน้าต่างเมนู
    public int selectedChar = 0;
    public int selectedWeapon = 0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.DARK_GRAY); 
        this.setDoubleBuffered(true);
        
        keyH = new KeyHandler(this);
        this.addKeyListener(keyH);
        
        mouseH = new MouseHandler(this);
        this.addMouseListener(mouseH);
        
        this.setFocusable(true); 
    }
    
    // ฟังก์ชันสร้างตัวละครหลังจากเลือกเสร็จ
    public void setupGame() {
        PlayerStats initialStats = new PlayerStats();
        
        if (selectedChar == 1) {
            initialStats.setMaxHp(20);
            initialStats.setMoveSpeed(2.0f);
        } else if (selectedChar == 2) {
            initialStats.setMaxHp(5);
            initialStats.setMoveSpeed(5.0f);
        }
        
        player = new Player(this, SCREEN_WIDTH/2 - 20, SCREEN_HEIGHT/2 - 20, initialStats, keyH);
        
        // ติดอาวุธตามที่เลือก
        if (selectedWeapon == 1) {
            player.addWeapon(new weapons.Sword());
        } else if (selectedWeapon == 2) {
            player.addWeapon(new weapons.Pistol());
        }
        
        enemies.clear();
        materials.clear();
        frameCount = 0;
        currentWave = 1;
        waveTimer = 30 * FPS;
    }
    
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS; 
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint(); 
                delta--;
            }
        }
    }
    
    public void update() {
        if (currentState == GameState.PLAYING) {
            
            // --- นับเวลา Wave ---
            waveTimer--;
            if (waveTimer <= 0) {
                // จบเวฟ
                currentState = GameState.SHOP;
                enemies.clear();
                
                // ดูดเงินทั้งหมดบนพื้นเข้ากระเป๋าทันทีเป็นรางวัลจบเวฟ
                for (MaterialItem m : materials) {
                    player.getStats().addMaterials(m.getValue());
                }
                materials.clear();
                return;
            }

            player.update();
            player.updateWeapons(enemies); // อัปเดตอาวุธและกระสุนทุกเฟรม
            
            frameCount++;
            // ศัตรูเกิดไวขึ้นเมื่อเวฟสูงขึ้น
            int spawnRate = Math.max(10, 60 - (currentWave * 5)); 
            if (frameCount >= spawnRate) {
                int spawnX = random.nextBoolean() ? -40 : SCREEN_WIDTH + 40; // เกิดนอกจอ
                int spawnY = random.nextInt(SCREEN_HEIGHT);
                
                // สร้างศัตรู เลือดเยอะขึ้นตามเวฟ
                Enemy newEnemy = new Enemy(spawnX, spawnY, player);
                // สมมติ: ถ้ามี setter เลือด ก็ใช้เพิ่มเลือดศัตรูได้ (เดี๋ยวทำทีหลังถ้าต้องการ)
                
                enemies.add(newEnemy);
                frameCount = 0;
            }
            
            Iterator<Enemy> it = enemies.iterator();
            while (it.hasNext()) {
                Enemy e = it.next();
                e.update();
                boolean isColliding = player.getX() < e.getX() + e.getWidth() &&
                                      player.getX() + player.getWidth() > e.getX() &&
                                      player.getY() < e.getY() + e.getHeight() &&
                                      player.getY() + player.getHeight() > e.getY();
                if (isColliding) {
                    player.takeDamage(1); 
                }
                if (e.getHp() <= 0) {
                    // ดรอปเงินเมื่อศัตรูตาย
                    materials.add(new MaterialItem(e.getX() + e.getWidth()/2, e.getY() + e.getHeight()/2, 1));
                    it.remove();
                }
            }
            
            // --- ระบบจัดการเงิน (Materials) ---
            Iterator<MaterialItem> matIt = materials.iterator();
            while (matIt.hasNext()) {
                MaterialItem mat = matIt.next();
                mat.update();
                
                // หาพิกัดตรงกลาง
                float px = player.getX() + player.getWidth() / 2;
                float py = player.getY() + player.getHeight() / 2;
                float mx = mat.getX() + mat.getWidth() / 2;
                float my = mat.getY() + mat.getHeight() / 2;
                
                float dx = px - mx;
                float dy = py - my;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                
                // ถ้าระยะห่างน้อยกว่า Pickup Range ให้เริ่มดูด
                if (dist < player.getStats().getPickupRange()) {
                    mat.magnetize(player);
                }
                
                // ถ้าใกล้มาก (ชนแล้ว) ให้เก็บเงิน
                if (dist < 15) {
                    player.getStats().addMaterials(mat.getValue());
                    matIt.remove();
                }
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); 
        Graphics2D g2d = (Graphics2D) g; // แปลงเป็น Graphics2D เพื่อให้รองรับการ Rotate
        
        if (currentState == GameState.TITLE) {
            drawTitleScreen(g);
        } else if (currentState == GameState.CHAR_SELECT) {
            drawCharSelectScreen(g);
        } else if (currentState == GameState.WEAPON_SELECT) {
            drawWeaponSelectScreen(g);
        } else if (currentState == GameState.SHOP) {
            drawShopScreen(g);
        } else {
            for (Enemy e : enemies) { e.render(g); }
            for (MaterialItem m : materials) { m.render(g); } // วาดเงิน
            if (player != null) { 
                player.render(g);          // วาดตัวผู้เล่น
                player.renderWeapons(g2d); // วาดอาวุธรอบๆ ผู้เล่น (พร้อม Animation หมุน/Recoil)
            }
            
            drawHUD(g);
            
            if (currentState == GameState.PAUSED) {
                drawPauseScreen(g);
            } else if (currentState == GameState.GAME_OVER) {
                drawGameOverScreen(g);
            }
        }
        g.dispose(); 
    }
    
    // --- ฟังก์ชันวาดเมนูก่อนเล่น (Mock up) ---
    private void drawBackButton(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(20, 20, 80, 40);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("BACK", 35, 45);
    }

    private void drawTitleScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("BROTATO CLONE", 140, 250);
        
        // ปุ่ม Start
        g.setColor(Color.GRAY);
        g.fillRect(300, 400, 200, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("START", 350, 435);
    }
    
    private void drawCharSelectScreen(Graphics g) {
        drawBackButton(g); // วาดปุ่ม Back
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Select Character", 240, 150);
        
        // กล่อง 1: Tank
        g.setColor(Color.GRAY);
        g.fillRect(150, 250, 200, 200);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("TANK", 210, 290);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("HP: 20", 220, 340);
        g.drawString("Speed: Slow", 200, 370);
        
        // กล่อง 2: Speedy
        g.setColor(Color.GRAY);
        g.fillRect(450, 250, 200, 200);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("SPEEDY", 500, 290);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("HP: 5", 530, 340);
        g.drawString("Speed: Fast", 510, 370);
    }

    private void drawWeaponSelectScreen(Graphics g) {
        drawBackButton(g); // วาดปุ่ม Back
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Select Weapon", 260, 150);
        
        // กล่อง 1: Melee
        g.setColor(Color.DARK_GRAY);
        g.fillRect(150, 250, 200, 200);
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("SWORD", 200, 290);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Type: Melee", 205, 340);
        
        // กล่อง 2: Ranged
        g.setColor(Color.DARK_GRAY);
        g.fillRect(450, 250, 200, 200);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("PISTOL", 505, 290);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Type: Ranged", 500, 340);
    }
    
    // --- ฟังก์ชันวาด UI ระหว่างเล่น ---
    private void drawHUD(Graphics g) {
        if(player == null) return;
        int barX = 20, barY = 20, barWidth = 200, barHeight = 25;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);
        
        g.setColor(new Color(200, 50, 50));
        double hpRatio = (double) player.getHp() / player.getMaxHp();
        if (hpRatio < 0) hpRatio = 0;
        int hpWidth = (int)(barWidth * hpRatio);
        g.fillRect(barX, barY, hpWidth, barHeight);
        
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
        
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("HP: " + player.getHp() + " / " + player.getMaxHp(), barX + 60, barY + 18);
        
        // วาดจำนวนเงิน
        g.setColor(new Color(50, 220, 80));
        g.drawString("Materials: " + player.getStats().getMaterials(), barX, barY + 45);
        
        // วาด Wave และ Timer ตรงกลางบน
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String waveText = "Wave " + currentWave;
        int timeSeconds = waveTimer / FPS;
        String timeText = "Time: " + timeSeconds;
        g.drawString(waveText, SCREEN_WIDTH/2 - 40, 30);
        g.drawString(timeText, SCREEN_WIDTH/2 - 45, 60);
    }
    
    private void drawPauseScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150)); 
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("PAUSED", SCREEN_WIDTH/2 - 90, 200);
        
        // ปุ่ม Resume
        g.setColor(Color.GRAY); g.fillRect(300, 300, 200, 50);
        g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Resume", 355, 335);
        
        // ปุ่ม Restart
        g.setColor(Color.GRAY); g.fillRect(300, 370, 200, 50);
        g.setColor(Color.WHITE); g.drawString("Restart", 360, 405);
        
        // ปุ่ม Main Menu
        g.setColor(Color.GRAY); g.fillRect(300, 440, 200, 50);
        g.setColor(Color.WHITE); g.drawString("Main Menu", 335, 475);
    }
    
    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(150, 0, 0, 150)); 
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("GAME OVER", SCREEN_WIDTH/2 - 130, 250);
        
        // ปุ่ม Restart (เล่นใหม่ตัวเดิม)
        g.setColor(Color.GRAY);
        g.fillRect(180, 400, 200, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Restart", 240, 435);
        
        // ปุ่ม Main Menu
        g.setColor(Color.GRAY);
        g.fillRect(420, 400, 200, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Main Menu", 455, 435);
    }
    
    private void drawShopScreen(Graphics g) {
        // พื้นหลังร้านค้า
        g.setColor(new Color(30, 40, 50));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("SHOP - Wave " + currentWave + " Cleared!", 150, 80);
        
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(new Color(50, 220, 80));
        g.drawString("Your Materials: " + player.getStats().getMaterials(), 300, 130);
        
        // --- กล่องอัปเกรด 1: Max HP ---
        g.setColor(Color.DARK_GRAY);
        g.fillRect(100, 200, 180, 150);
        g.setColor(Color.WHITE);
        g.drawString("+5 Max HP", 125, 250);
        g.setColor(Color.YELLOW);
        g.drawString("Cost: 10", 145, 290);
        // ปุ่มซื้อ HP
        g.setColor(Color.GRAY);
        g.fillRect(120, 310, 140, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("BUY", 170, 332);
        
        // --- กล่องอัปเกรด 2: Move Speed ---
        g.setColor(Color.DARK_GRAY);
        g.fillRect(310, 200, 180, 150);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("+1 Speed", 345, 250);
        g.setColor(Color.YELLOW);
        g.drawString("Cost: 15", 355, 290);
        // ปุ่มซื้อ Speed
        g.setColor(Color.GRAY);
        g.fillRect(330, 310, 140, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("BUY", 380, 332);
        
        // --- กล่องอัปเกรด 3: Pickup Range ---
        g.setColor(Color.DARK_GRAY);
        g.fillRect(520, 200, 180, 150);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("+Range", 565, 250);
        g.setColor(Color.YELLOW);
        g.drawString("Cost: 10", 565, 290);
        // ปุ่มซื้อ Range
        g.setColor(Color.GRAY);
        g.fillRect(540, 310, 140, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("BUY", 590, 332);
        
        // --- ปุ่ม Next Wave ---
        g.setColor(new Color(200, 50, 50));
        g.fillRect(300, 450, 200, 60);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("NEXT WAVE", 310, 492);
    }
}
