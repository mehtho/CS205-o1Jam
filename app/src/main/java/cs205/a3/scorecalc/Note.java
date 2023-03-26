package cs205.a3.scorecalc;

public class Note {
    public static final int MAX_AGE = 40;
    private int age;

    public int getAge() {
        return age;
    }

    public boolean incAge() {
        return this.age++ >= MAX_AGE;
    }

    public String toString() {
        return "" + age;
    }

    public int getScore() {
        if (this.age < 10) {
            return -1;
        } else if (this.age < 15) {
            return 50;
        } else if (this.age < 25) {
            return 100;
        } else if (this.age < 35) {
            return 300;
        } else {
            return 100;
        }
    }
}
