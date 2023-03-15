package cs205.a3.scorecalc;

public class Note {
    public static final int MAX_AGE = 100;
    private int age;

    public int getAge() {
        return age;
    }

    public boolean incAge() {
        return this.age++ >= 100;
    }

    public String toString() {
        return "" + age;
    }
}
