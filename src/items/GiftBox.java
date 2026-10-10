package items;

import stats.PlayerStats;

public class GiftBox extends ShopItem {
    public GiftBox() {
        super("Gift Box", "Armor +2, Move Speed -1", 15);
        loadImage("assets/items/gift_01e.png");
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseArmor(2);
        stats.setMoveSpeed(stats.getMoveSpeed() - 1.0f);
    }
}
