package items;

import stats.PlayerStats;

public class SmartWatch extends ShopItem {
    public SmartWatch() {
        super("Smart Watch", "HP Regen +2, Move Speed +1, Damage -1", 20);
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseHpRegen(2);
        stats.setMoveSpeed(stats.getMoveSpeed() + 1.0f);
        stats.increaseDamageBonus(-1);
    }
}
