package items;

import stats.PlayerStats;

public class EspressoShot extends ShopItem {
    public EspressoShot() {
        super("Espresso Shot", "ATK Spd +15, Weapon Range -20", 18);
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseAttackSpeedBonus(15);
        stats.increaseWeaponRangeBonus(-20);
    }
}
