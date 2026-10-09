package stats;

/**
 * Class to hold player statistics.
 * Demonstrates Encapsulation.
 */
public class PlayerStats {
    private int maxHp = 10;
    private int damageBonus = 0; // % bonus damage
    private float moveSpeed = 3.0f;
    private int materials = 0; // เงินในเกม
    private float pickupRange = 100.0f; // ระยะดูดของ

    public PlayerStats() {
        // Default stats
    }

    public float getPickupRange() {
        return pickupRange;
    }

    public void setPickupRange(float range) {
        this.pickupRange = range;
    }

    // --- Getters & Setters (Encapsulation) ---

    public void addMaterials(int amount) {
        if (amount > 0) {
            this.materials += amount;
        }
    }

    public boolean spendMaterials(int cost) {
        if (this.materials >= cost) {
            this.materials -= cost;
            return true; // จ่ายสำเร็จ
        }
        return false; // เงินไม่พอ
    }

    public int getMaterials() {
        return materials;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void increaseMaxHp(int amount) {
        this.maxHp += amount;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public void increaseDamageBonus(int amount) {
        this.damageBonus += amount;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }
    
    public void setMoveSpeed(float speed) {
        this.moveSpeed = speed;
    }
}
