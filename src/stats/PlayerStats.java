package stats;

/**
 * Class to hold player statistics.
 * Demonstrates Encapsulation.
 */
public class PlayerStats {
    private int maxHp = 10;
    private int damageBonus = 0; // หน่วยดาเมจที่บวกเพิ่ม
    private float moveSpeed = 3.0f;
    private int materials = 0; // เงินในเกม
    private float pickupRange = 100.0f; // ระยะดูดของ
    
    // --- สเตตัสใหม่ที่เพิ่มเข้ามา ---
    private int armor = 0; // เกราะ (ลดความเสียหาย)
    private int attackSpeedBonus = 0; // % ความเร็วโจมตี (ลดคูลดาวน์)
    private int hpRegen = 0; // เด้งเลือดทุกๆ 5 วินาที
    private int weaponRangeBonus = 0; // เพิ่มระยะยิง/ฟัน

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

    // --- Getters & Setters สำหรับสเตตัสใหม่ ---
    public int getArmor() { return armor; }
    public void increaseArmor(int amount) { this.armor += amount; }
    public void setArmor(int armor) { this.armor = armor; }

    public int getAttackSpeedBonus() { return attackSpeedBonus; }
    public void increaseAttackSpeedBonus(int amount) { this.attackSpeedBonus += amount; }
    public void setAttackSpeedBonus(int amount) { this.attackSpeedBonus = amount; }

    public int getHpRegen() { return hpRegen; }
    public void increaseHpRegen(int amount) { this.hpRegen += amount; }
    public void setHpRegen(int hpRegen) { this.hpRegen = hpRegen; }
    
    public int getWeaponRangeBonus() { return weaponRangeBonus; }
    public void increaseWeaponRangeBonus(int amount) { this.weaponRangeBonus += amount; }
    public void setWeaponRangeBonus(int amount) { this.weaponRangeBonus = amount; }
}
