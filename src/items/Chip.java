package items;

import stats.PlayerStats;

public class Chip extends ShopItem {
    public Chip() {
        super("Chip", "Weapon Range +40, ATK Spd +5, Armor -1", 25);

        loadImage("assets/items/Icon14_32.png");
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseWeaponRangeBonus(40);
        stats.increaseAttackSpeedBonus(5);
        stats.increaseArmor(-1);
    }
}
