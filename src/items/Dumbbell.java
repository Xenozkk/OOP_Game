package items;

import stats.PlayerStats;

public class Dumbbell extends ShopItem {
    public Dumbbell() {
        super("Dumbbell", "Damage +3, Max HP +5, Move Speed -1.5", 22);
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseDamageBonus(3);
        stats.increaseMaxHp(5);
        stats.setMoveSpeed(stats.getMoveSpeed() - 1.5f);
    }
}
