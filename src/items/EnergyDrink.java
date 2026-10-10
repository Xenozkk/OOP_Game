package items;

import stats.PlayerStats;

public class EnergyDrink extends ShopItem {
    public EnergyDrink() {
        super("Energy Drink", "ATK Spd +10, Speed +1, Max HP -2", 15);

        loadImage("./assets/items/soft_drink_blue.png");
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseAttackSpeedBonus(10);
        stats.setMoveSpeed(stats.getMoveSpeed() + 1.0f);
        stats.increaseMaxHp(-2); // หักเลือด
    }
}
