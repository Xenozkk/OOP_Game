package items;

import stats.PlayerStats;

public class CameraLens extends ShopItem {
    public CameraLens() {
        super("Camera Lens", "Weapon Range +50, Pickup Range +30, ATK Spd -5", 20);
    }

    @Override
    public void applyEffect(PlayerStats stats) {
        stats.increaseWeaponRangeBonus(50);
        stats.setPickupRange(stats.getPickupRange() + 30.0f);
        stats.increaseAttackSpeedBonus(-5);
    }
}
