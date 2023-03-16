package cs205.a3.scorecalc;

public class Note {
    public static final int MAX_AGE = 60;
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
}
