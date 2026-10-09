package game;

import java.util.Random;
import entities.Enemy;

public class WaveManager {

    public static int getSpawnInterval(int wave) {
        // ยิ่ง Wave สูง ศัตรูยิ่งเกิดถี่ขึ้น
        return Math.max(12, 60 - (wave * 4));
    }

    public static Enemy.Type getRandomType(int wave, Random random) {
        Enemy.Type[] types;

        if (wave <= 1) {
            types = new Enemy.Type[] {
                Enemy.Type.CHASER
            };
        } else if (wave == 2) {
            types = new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.CHASER
            };
        } else if (wave == 3) {
            types = new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.CHASER,
                Enemy.Type.RUNNER
            };
        } else if (wave == 4) {
            types = new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.CHASER,
                Enemy.Type.SHOOTER
            };
        } else if (wave == 5) {
            types = new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.SHOOTER,
                Enemy.Type.TANK
            };
        } else if (wave == 6) {
            types = new Enemy.Type[] {
                Enemy.Type.RUNNER,
                Enemy.Type.RUNNER,
                Enemy.Type.SHOOTER,
                Enemy.Type.TANK
            };
        } else if (wave == 7) {
            types = new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.RUNNER,
                Enemy.Type.SHOOTER,
                Enemy.Type.TANK,
                Enemy.Type.BRUTE
            };
        } else if (wave == 8) {
            types = new Enemy.Type[] {
                Enemy.Type.TANK,
                Enemy.Type.TANK,
                Enemy.Type.BRUTE,
                Enemy.Type.SHOOTER
            };
        } else if (wave == 9) {
            types = new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.RUNNER,
                Enemy.Type.RUNNER,
                Enemy.Type.SHOOTER,
                Enemy.Type.TANK,
                Enemy.Type.BRUTE,
                Enemy.Type.BRUTE
            };
        } else {
            // Wave 10 เป็นต้นไป: ศัตรูทุกชนิด
            // เพิ่มโอกาสเกิด Runner และ Brute
            types = new Enemy.Type[] {
                Enemy.Type.CHASER,
                Enemy.Type.RUNNER,
                Enemy.Type.RUNNER,
                Enemy.Type.SHOOTER,
                Enemy.Type.TANK,
                Enemy.Type.BRUTE,
                Enemy.Type.BRUTE
            };
        }

        return types[random.nextInt(types.length)];
    }
}