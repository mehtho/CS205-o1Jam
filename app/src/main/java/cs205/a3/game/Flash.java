package cs205.a3.game;

import android.graphics.Paint;

public class Flash {
    private int age = 0;
    private int type = 0;
    private int lane = 0;

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
