package items;

import stats.PlayerStats;

public class HelmetArmor extends ShopItem {
    public HelmetArmor() {
        super("Helmet Armor", "Armor +2, Max HP +5, Pickup Range -20", 20);

        loadImage("assets/items/helmet_01b.png");
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseArmor(2);
        stats.increaseMaxHp(5);
        stats.setPickupRange(stats.getPickupRange() - 20.0f);
    }
}
