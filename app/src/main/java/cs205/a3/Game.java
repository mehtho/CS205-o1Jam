package cs205.a3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Game {
    private final Runnable runnable;

    private final Predicate<Consumer<Canvas>> useCanvas;

    public Game(final Runnable runnable, final Predicate<Consumer<Canvas>> useCanvas) {
        this.runnable = runnable;
        this.useCanvas = useCanvas;
    }

}
