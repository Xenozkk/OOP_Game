package items;

import stats.PlayerStats;

public class HardHat extends ShopItem {
    public HardHat() {
        super("Hard Hat", "Armor +2, Max HP +5, Pickup Range -20", 20);
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseArmor(2);
        stats.increaseMaxHp(5);
        stats.setPickupRange(stats.getPickupRange() - 20.0f);
    }
}
