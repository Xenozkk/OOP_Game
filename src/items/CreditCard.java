package items;

import stats.PlayerStats;

public class CreditCard extends ShopItem {
    public CreditCard() {
        super("Credit Card", "Damage +2, ATK Spd +5, Armor -2", 25);
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseDamageBonus(2);
        stats.increaseAttackSpeedBonus(5);
        stats.increaseArmor(-2);
    }
}
