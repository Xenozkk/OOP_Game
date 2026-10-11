
package game;

import java.util.ArrayList;
import java.util.Random;

import entities.Enemy;

public class WaveManager {

    private static final Enemy.Type[] ALL_TYPES = Enemy.Type.values();

    // เวลารอระหว่างการเกิดศัตรู หน่วยเป็น frame ที่ 60 FPS
    public static int getSpawnInterval(int wave) {
        if (wave <= 1) return 60;
        if (wave == 2) return 54;
        if (wave == 3) return 51;
        if (wave == 4) return 48;
        if (wave == 5) return 45;
        if (wave == 6) return 42;
        if (wave == 7) return 39;
        if (wave == 8) return 36;
        if (wave == 9) return 33;

        return 30; // Wave 10 เป็นต้นไป
    }

    // จำนวนศัตรูแต่ละประเภทที่มีชีวิตพร้อมกันได้สูงสุด
    // คืนค่า 0 หมายถึงยังไม่ปลดล็อกใน Wave นั้น
    public static int getMaxAlive(Enemy.Type type, int wave) {
        switch (type) {
            case CHASER:
                return 8;

            case RUNNER:
                return wave >= 3 ? 4 : 0;

            case SHOOTER:
                return wave >= 3 ? 3 : 0;

            default:
                return 0;
        }
    }

    // นับจำนวนศัตรูที่ยังมีชีวิตแยกตามประเภท
    private static int countAlive(
            Enemy.Type type,
            ArrayList<Enemy> enemies) {

        int count = 0;

        for (Enemy enemy : enemies) {
            if (enemy.getHp() > 0 && enemy.getType() == type) {
                count++;
            }
        }

        return count;
    }

    // เลือกศัตรูที่ยังมีช่องว่างให้เกิดได้
    // ถ้าประเภทใน pool เต็ม จะลองเลือกประเภทอื่น
    // ถ้าทุกประเภทเต็ม จะคืนค่า null เพื่อให้ GamePanel รอ
    public static Enemy.Type getRandomAvailableType(
            int wave,
            Random random,
            ArrayList<Enemy> enemies) {

        ArrayList<Enemy.Type> weightedPool = new ArrayList<>();

        // คงโอกาสสุ่มของประเภทศัตรูตามระบบเดิม
        Enemy.Type[] originalPool = getSpawnPool(wave);

        for (Enemy.Type type : originalPool) {
            int maxAlive = getMaxAlive(type, wave);
            int alive = countAlive(type, enemies);

            if (maxAlive > 0 && alive < maxAlive) {
                weightedPool.add(type);
            }
        }

        // ถ้ายังมีประเภทใน pool ที่เกิดได้ ให้สุ่มจาก pool นั้น
        if (!weightedPool.isEmpty()) {
            return weightedPool.get(random.nextInt(weightedPool.size()));
        }

        // ถ้าประเภทใน pool เต็มหมด ให้ลองประเภทอื่นที่ปลดล็อกแล้ว
        ArrayList<Enemy.Type> fallbackPool = new ArrayList<>();

        for (Enemy.Type type : ALL_TYPES) {
            int maxAlive = getMaxAlive(type, wave);
            int alive = countAlive(type, enemies);

            if (maxAlive > 0 && alive < maxAlive) {
                fallbackPool.add(type);
            }
        }

        if (fallbackPool.isEmpty()) {
            return null;
        }

        return fallbackPool.get(random.nextInt(fallbackPool.size()));
    }

    // รายชื่อประเภทและน้ำหนักการสุ่มตามระบบเดิม
    private static Enemy.Type[] getSpawnPool(int wave) {
        if (wave <= 1) {
            return new Enemy.Type[] {
                Enemy.Type.CHASER
            };
        } else if (wave == 2) {
            return new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.CHASER
            };
        } else if (wave == 3) {
            return new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.CHASER,
                Enemy.Type.RUNNER
            };
        } else if (wave == 4) {
            return new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.CHASER,
                Enemy.Type.SHOOTER
            };
        } else if (wave == 5) {
            return new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.SHOOTER,
                Enemy.Type.RUNNER
            };
        } else if (wave == 6) {
            return new Enemy.Type[] {
                Enemy.Type.RUNNER,
                Enemy.Type.RUNNER,
                Enemy.Type.SHOOTER,
                Enemy.Type.CHASER
            };
        } else if (wave == 7) {
            return new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.RUNNER,
                Enemy.Type.SHOOTER,
                Enemy.Type.RUNNER,
                Enemy.Type.CHASER
            };
        } else if (wave == 8) {
            return new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.RUNNER,
                Enemy.Type.CHASER,
                Enemy.Type.SHOOTER
            };
        }

        return new Enemy.Type[] {
            Enemy.Type.CHASER,
            Enemy.Type.RUNNER,
            Enemy.Type.RUNNER,
            Enemy.Type.SHOOTER,
            Enemy.Type.CHASER,
            Enemy.Type.RUNNER,
            Enemy.Type.SHOOTER
        };
    }
}