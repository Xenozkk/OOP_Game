package items;

import stats.PlayerStats;

public class HeavyBackpack extends ShopItem {
    public HeavyBackpack() {
        super("Heavy Backpack", "Armor +2, Move Speed -1", 15);
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseArmor(2);
        stats.setMoveSpeed(stats.getMoveSpeed() - 1.0f);
    }
}
