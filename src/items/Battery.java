package items;

import stats.PlayerStats;

public class Battery extends ShopItem {
    public Battery() {
        super("Battery", "HP Regen +2, Move Speed +1, Damage -1", 20);
        loadImage("assets/items/batteries.png");
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseHpRegen(2);
        stats.setMoveSpeed(stats.getMoveSpeed() + 1.0f);
        stats.increaseDamageBonus(-1);
    }
}
