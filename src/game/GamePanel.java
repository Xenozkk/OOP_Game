package game;

import entities.Enemy;
import entities.EnemyProjectile;
import entities.MaterialItem;
import entities.Player;
import items.ShopItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JPanel;
import stats.PlayerStats;

public class GamePanel extends JPanel implements Runnable {

    public final int SCREEN_WIDTH = 800;
    public final int SCREEN_HEIGHT = 600;
    final int FPS = 60;

    Thread gameThread;

    public GameState currentState = GameState.TITLE;

    KeyHandler keyH;
    MouseHandler mouseH;

    Player player;

    private final Background background;

    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<MaterialItem> materials = new ArrayList<>();

    int frameCount = 0;
    Random random = new Random();

    // Wave system
    public static final int MAX_WAVE = 10;
    public int currentWave = 1;
    public int waveTimer = 30 * FPS;

    public ShopManager shop = new ShopManager();

    public int selectedChar = 0;
    public int selectedWeapon = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.DARK_GRAY);
        setDoubleBuffered(true);

        background = new Background();

        keyH = new KeyHandler(this);
        addKeyListener(keyH);

        mouseH = new MouseHandler(this);
        addMouseListener(mouseH);

        setFocusable(true);
    }

    // --------------------------------------------------
    // Setup
    // --------------------------------------------------

    public void setupGame() {
        background.generateRandomMap(random);

        PlayerStats initialStats = new PlayerStats();

        if (selectedChar == 1) {
            initialStats.setMaxHp(20);
            initialStats.setMoveSpeed(2.0f);
        } else if (selectedChar == 2) {
            initialStats.setMaxHp(5);
            initialStats.setMoveSpeed(5.0f);
        }

        player = new Player(
            this,
            SCREEN_WIDTH / 2 - 20,
            SCREEN_HEIGHT / 2 - 20,
            initialStats,
            keyH
        );

        // เลือกอาวุธตามที่ผู้เล่นเลือก
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
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;

        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    // --------------------------------------------------
    // Update game
    // --------------------------------------------------

    public void update() {
        if (currentState != GameState.PLAYING) {
            return;
        }

        if (player == null) {
            return;
        }

        // ตรวจสอบว่าเวลาของ Wave หมดหรือยัง
        waveTimer--;

        if (waveTimer <= 0) {
            waveTimer = 0;
            finishWave();
            return;
        }

        // อัปเดตผู้เล่นและอาวุธ
        player.update();
        player.updateWeapons(enemies);

        // --------------------------------------------------
        // Spawn enemies using WaveManager
        // --------------------------------------------------

        frameCount++;

        int spawnRate = WaveManager.getSpawnInterval(currentWave);

        if (frameCount >= spawnRate) {
            Enemy.Type type = WaveManager.getRandomAvailableType(
                currentWave,
                random,
                enemies
            );
            if (type != null) {

                int spawnX = random.nextBoolean()
                    ? -40
                    : SCREEN_WIDTH + 40;
                
                int spawnY = random.nextInt(SCREEN_HEIGHT);

                Enemy newEnemy = new Enemy(
                    spawnX,
                    spawnY,
                    player,
                    type,
                    currentWave
                );

                enemies.add(newEnemy);

                frameCount = 0;
            }
        }

        // --------------------------------------------------
        // Update enemies and check collisions
        // --------------------------------------------------

        Iterator<Enemy> enemyIterator = enemies.iterator();

        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            enemy.update();

            enemy.updateProjectiles(
                SCREEN_WIDTH,
                SCREEN_HEIGHT
            );

            // ตรวจสอบการชนระหว่างผู้เล่นกับศัตรู
            boolean isColliding =
                player.getX() < enemy.getX() + enemy.getWidth()
                && player.getX() + player.getWidth() > enemy.getX()
                && player.getY() < enemy.getY() + enemy.getHeight()
                && player.getY() + player.getHeight() > enemy.getY();

            if (isColliding) {
                player.takeDamage(enemy.getContactDamage());
            }

            // ตรวจสอบกระสุนของ Shooter ที่ชนผู้เล่น
            Iterator<EnemyProjectile> bulletIterator =
                enemy.getProjectiles().iterator();

            while (bulletIterator.hasNext()) {
                EnemyProjectile bullet = bulletIterator.next();

                boolean hitPlayer =
                    bullet.getX() >= player.getX()
                    && bullet.getX() <= player.getX() + player.getWidth()
                    && bullet.getY() >= player.getY()
                    && bullet.getY() <= player.getY() + player.getHeight();

                if (hitPlayer) {
                    player.takeDamage(bullet.getDamage());
                    bulletIterator.remove();
                }
            }

            // ศัตรูตายแล้วดรอป Materials
            if (enemy.getHp() <= 0) {
                materials.add(
                    new MaterialItem(
                        enemy.getX() + enemy.getWidth() / 2,
                        enemy.getY() + enemy.getHeight() / 2,
                        1
                    )
                );

                enemyIterator.remove();
            }
        }

        // --------------------------------------------------
        // Materials system
        // --------------------------------------------------

        Iterator<MaterialItem> materialIterator =
            materials.iterator();

        while (materialIterator.hasNext()) {
            MaterialItem material = materialIterator.next();

            material.update();

            float playerX =
                player.getX() + player.getWidth() / 2.0f;

            float playerY =
                player.getY() + player.getHeight() / 2.0f;

            float materialX =
                material.getX() + material.getWidth() / 2.0f;

            float materialY =
                material.getY() + material.getHeight() / 2.0f;

            float dx = playerX - materialX;
            float dy = playerY - materialY;

            float distance =
                (float) Math.sqrt(dx * dx + dy * dy);

            // ดูด Materials เมื่ออยู่ในระยะเก็บ
            if (distance < player.getStats().getPickupRange()) {
                material.magnetize(player);
            }

            // เก็บ Materials
            if (distance < 15) {
                player.getStats().addMaterials(material.getValue());
                materialIterator.remove();
            }
        }
    }

    // --------------------------------------------------
    // Finish Wave
    // --------------------------------------------------

    private void finishWave() {
        currentState = GameState.SHOP;

        enemies.clear();

        // รับ Materials ที่ยังเหลืออยู่เป็นรางวัล
        for (MaterialItem material : materials) {
            player.getStats().addMaterials(material.getValue());
        }

        materials.clear();

        // สุ่มไอเทมร้านค้าสำหรับรอบใหม่
        shop.startNewWaveShop();
    }

    // เรียกจาก MouseHandler เมื่อกด Next Wave
    public void nextWave() {
        if (currentWave >= MAX_WAVE) {
            currentState = GameState.GAME_OVER;
            return;
        }

        currentWave++;
        waveTimer = 30 * FPS;
        frameCount = 0;

        currentState = GameState.PLAYING;
    }

    // --------------------------------------------------
    // Render
    // --------------------------------------------------

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        if (currentState == GameState.TITLE) {
            drawTitleScreen(g2d);

        } else if (currentState == GameState.CHAR_SELECT) {
            drawCharSelectScreen(g2d);

        } else if (currentState == GameState.WEAPON_SELECT) {
            drawWeaponSelectScreen(g2d);

        } else if (currentState == GameState.SHOP) {
            drawShopScreen(g2d);

        } else {
            // วาดพื้นหลังก่อนวาดวัตถุอื่น
            background.draw(
                g2d,
                SCREEN_WIDTH,
                SCREEN_HEIGHT,
                0,
                0
            );

            // วาดศัตรูและกระสุนของ Shooter
            for (Enemy enemy : enemies) {
                enemy.render(g2d);

                for (EnemyProjectile bullet : enemy.getProjectiles()) {
                    bullet.render(g2d);
                }
            }

            // วาด Materials
            for (MaterialItem material : materials) {
                material.render(g2d);
            }

            // วาดผู้เล่นและอาวุธ
            if (player != null) {
                player.render(g2d);
                player.renderWeapons(g2d);
            }

            drawHUD(g2d);

            if (currentState == GameState.PAUSED) {
                drawPauseScreen(g2d);

            } else if (currentState == GameState.GAME_OVER) {
                drawGameOverScreen(g2d);
            }
        }

        g2d.dispose();
    }

    // --------------------------------------------------
    // Menu screens
    // --------------------------------------------------

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

        g.setColor(Color.GRAY);
        g.fillRect(300, 400, 200, 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("START", 350, 435);
    }

    private void drawCharSelectScreen(Graphics g) {
        drawBackButton(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Select Character", 240, 150);

        // Tank
        g.setColor(Color.GRAY);
        g.fillRect(150, 250, 200, 200);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("TANK", 210, 290);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("HP: 20", 220, 340);
        g.drawString("Speed: Slow", 200, 370);

        // Speedy
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
        drawBackButton(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Select Weapon", 260, 150);

        // Sword
        g.setColor(Color.DARK_GRAY);
        g.fillRect(150, 250, 200, 200);

        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("SWORD", 200, 290);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Type: Melee", 205, 340);

        // Pistol
        g.setColor(Color.DARK_GRAY);
        g.fillRect(450, 250, 200, 200);

        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("PISTOL", 505, 290);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Type: Ranged", 500, 340);
    }

    // --------------------------------------------------
    // HUD
    // --------------------------------------------------

    private void drawHUD(Graphics g) {
        if (player == null) {
            return;
        }

        int barX = 20;
        int barY = 20;
        int barWidth = 200;
        int barHeight = 25;

        // HP bar background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        // HP bar
        g.setColor(new Color(200, 50, 50));

        double hpRatio =
            (double) player.getHp() / player.getMaxHp();

        hpRatio = Math.max(0, Math.min(1, hpRatio));

        int hpWidth = (int) (barWidth * hpRatio);

        g.fillRect(barX, barY, hpWidth, barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(
            "HP: " + player.getHp() + " / " + player.getMaxHp(),
            barX + 60,
            barY + 18
        );

        // Materials
        g.setColor(new Color(50, 220, 80));
        g.drawString(
            "Materials: " + player.getStats().getMaterials(),
            barX,
            barY + 45
        );

        // Wave and timer
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));

        g.drawString(
            "Wave " + currentWave + " / " + MAX_WAVE,
            SCREEN_WIDTH / 2 - 75,
            30
        );

        int timeSeconds = waveTimer / FPS;

        g.drawString(
            "Time: " + timeSeconds,
            SCREEN_WIDTH / 2 - 45,
            60
        );
    }

    // --------------------------------------------------
    // Pause screen
    // --------------------------------------------------

    private void drawPauseScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("PAUSED", SCREEN_WIDTH / 2 - 90, 200);

        // Resume
        g.setColor(Color.GRAY);
        g.fillRect(300, 300, 200, 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Resume", 355, 335);

        // Restart
        g.setColor(Color.GRAY);
        g.fillRect(300, 370, 200, 50);

        g.setColor(Color.WHITE);
        g.drawString("Restart", 360, 405);

        // Main Menu
        g.setColor(Color.GRAY);
        g.fillRect(300, 440, 200, 50);

        g.setColor(Color.WHITE);
        g.drawString("Main Menu", 335, 475);
    }

    // --------------------------------------------------
    // Game Over / Victory screen
    // --------------------------------------------------

    private void drawGameOverScreen(Graphics g) {
        boolean won =
            currentWave >= MAX_WAVE
            && player != null
            && player.getHp() > 0;

        if (won) {
            g.setColor(new Color(0, 100, 0, 180));
        } else {
            g.setColor(new Color(150, 0, 0, 150));
        }

        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));

        if (won) {
            g.drawString(
                "YOU WIN!",
                SCREEN_WIDTH / 2 - 110,
                250
            );
        } else {
            g.drawString(
                "GAME OVER",
                SCREEN_WIDTH / 2 - 130,
                250
            );
        }

        // Restart
        g.setColor(Color.GRAY);
        g.fillRect(180, 400, 200, 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Restart", 240, 435);

        // Main Menu
        g.setColor(Color.GRAY);
        g.fillRect(420, 400, 200, 50);

        g.setColor(Color.WHITE);
        g.drawString("Main Menu", 455, 435);
    }

    // --------------------------------------------------
    // Shop screen
    // --------------------------------------------------

    private void drawShopScreen(Graphics g) {
        g.setColor(new Color(30, 40, 50));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString(
            "SHOP - Wave " + currentWave + " Cleared!",
            150,
            80
        );

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(new Color(50, 220, 80));

        g.drawString(
            "Your Materials: " + player.getStats().getMaterials(),
            300,
            130
        );

        ShopItem[] items = shop.getCurrentItems();

        int startX = 70;
        int gap = 30;
        int width = 200;

        for (int i = 0; i < items.length; i++) {
            ShopItem item = items[i];

            int x = startX + (i * (width + gap));
            int y = 180;

            if (item != null) {
                // Item box
                g.setColor(Color.DARK_GRAY);
                g.fillRect(x, y, width, 240); // 📦 เพิ่มความสูงกล่องเป็น 240 กันข้อความทับกัน

                // Item name
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.drawString(item.getName(), x + 15, y + 30);
                
                // 🎨 วาดรูปภาพไอเทมถ้ามี
                if (item.getImage() != null) {
                    g.drawImage(item.getImage(), x + 70, y + 40, 60, 60, null);
                }

                // Item description
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.setColor(Color.LIGHT_GRAY);

                String[] descParts =
                    item.getDescription().split(", ");

                for (int j = 0; j < descParts.length; j++) {
                    g.drawString(
                        descParts[j],
                        x + 15,
                        y + 120 + (j * 20) // ขยับลงมาที่ 120
                    );
                }

                // Price
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 18));

                g.drawString(
                    "Cost: " + item.getPrice(),
                    x + 60,
                    y + 190 // ขยับราคาลงมาหลบข้อความ 3 บรรทัด
                );

                // Buy button
                g.setColor(Color.GRAY);
                g.fillRect(x + 30, y + 200, 140, 30); // ขยับปุ่มลงมา

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                g.drawString("BUY", x + 80, y + 222);

            } else {
                // Sold out
                g.setColor(new Color(40, 50, 60));
                g.fillRect(x, y, width, 240); // เพิ่มความสูงให้เท่ากัน

                g.setColor(Color.GRAY);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.drawString("SOLD OUT", x + 50, y + 120);
            }
        }

        // Reroll button
        g.setColor(Color.ORANGE);
        g.fillRect(100, 450, 200, 60);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));

        g.drawString(
            "REROLL (" + shop.getRerollCost() + ")",
            120,
            490
        );

        // Next Wave / Finish button
        g.setColor(new Color(200, 50, 50));
        g.fillRect(500, 450, 200, 60);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 26));

        if (currentWave >= MAX_WAVE) {
            g.drawString("FINISH", 550, 490);
        } else {
            g.drawString("NEXT WAVE", 510, 490);
        }
    }
}
