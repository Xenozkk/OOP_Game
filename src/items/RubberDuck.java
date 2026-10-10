package items;

import stats.PlayerStats;

public class RubberDuck extends ShopItem {
    public RubberDuck() {
        super("RubberDuck", "Damage +3, Max HP +5, Move Speed -1.5", 22);

        loadImage("assets/items/rubber_duck.png");
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseDamageBonus(3);
        stats.increaseMaxHp(5);
        stats.setMoveSpeed(stats.getMoveSpeed() - 1.5f);
    }
}
