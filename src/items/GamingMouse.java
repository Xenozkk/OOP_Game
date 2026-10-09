package items;

import stats.PlayerStats;

public class GamingMouse extends ShopItem {
    public GamingMouse() {
        super("Gaming Mouse", "Weapon Range +40, ATK Spd +5, Armor -1", 25);
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseWeaponRangeBonus(40);
        stats.increaseAttackSpeedBonus(5);
        stats.increaseArmor(-1);
    }
}
