package cs205.a3.game;

public class Flash {
    private int age;
    private final int type;
    private final int lane;

    public Flash(int type, int lane) {
        this.type = type;
        this.lane = lane;
    }

    public void incAge() {
        this.age++;
    }

    public int getAge() {
        return age;
    }

    public int getType() {
        return type;
    }

    public int getLane() {
        return lane;
    }
}
