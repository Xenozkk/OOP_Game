package entities;

/**
 * Base class for all living things (Player, Enemies).
 * Demonstrates Inheritance from GameObject.
 */
public abstract class Entity extends GameObject {
    protected int hp;
    protected int maxHp;
    protected float speed;

    public Entity(float x, float y, int width, int height, int maxHp, float speed) {
        super(x, y, width, height);
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.speed = speed;
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
        System.out.println("Entity took " + damage + " damage. HP left: " + this.hp);
        if (this.hp <= 0) {
            die();
        }
    }

    // บังคับให้คลาสลูกเขียนวิธีตายของตัวเอง (Polymorphism)
    protected abstract void die();

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    
    public void heal(int amount) {
        this.hp += amount;
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
    }
    
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }
}
