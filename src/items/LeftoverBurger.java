package items;

import stats.PlayerStats;

public class LeftoverBurger extends ShopItem {
    public LeftoverBurger() {
        super("Leftover Burger", "Max HP +8, HP Regen -1", 12);
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseMaxHp(8);
        stats.increaseHpRegen(-1);
    }
}
