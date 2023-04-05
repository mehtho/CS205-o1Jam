package cs205.a3.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

/**
 * Game view that holds the canvas on which notes flow down.
 * <p>
 * Rendered below activity elements such as the keys to press
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    public static Game game;

    private final Game gameInstance = new Game(this::useCanvas);

    private GameThread gameThread;

    /**
     * Initialised the game object and keeps the screen on while the game activity is running
     *
     * @param context      Android context
     * @param attributeSet unused
     */
    @SuppressLint("ClickableViewAccessibility")
    public GameView(Context context, AttributeSet attributeSet) {
        super(context);
        game = gameInstance;
        gameInstance.setContext(context);
        getHolder().addCallback(this);
        setKeepScreenOn(true);
    }

    /**
     * Controls the synchronous use of the canvas
     *
     * @param onDraw Game drawing cycle
     * @return Whether access is granted to the canvas
     */
    private boolean useCanvas(final Consumer<Canvas> onDraw) {
        boolean result = false;
        try {
            final SurfaceHolder holder = getHolder();
            final Canvas canvas = holder.lockCanvas();
            try {
                onDraw.accept(canvas);
            } finally {
                try {
                    holder.unlockCanvasAndPost(canvas);
                    result = true;
                } catch (final IllegalStateException e) {
                    // Do nothing
                }
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Actions to perform when created, such as initialising the game thread
     *
     * @param surfaceHolder
     */
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if ((gameThread == null) || (gameThread.getState() == Thread.State.TERMINATED)) {
            gameThread = new GameThread(gameInstance);
        }
        final Rect rect = getHolder().getSurfaceFrame();
        gameInstance.resize(rect.width(), rect.height());
        gameThread.startLoop();
    }

    /**
     * Actions to perform when the game view is resized
     *
     * @param surfaceHolder Surface holder
     * @param i             Format
     * @param i1            Width
     * @param i2            Height
     */
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        gameInstance.resize(i1, i2);
    }

    /**
     * Actions to perform when the game view is destroyed, such as stopping the game thread and
     * setting it to null to facilitate garbage collection.
     * <p>
     * Disables the keep screen on wakelock
     *
     * @param surfaceHolder
     */
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        gameThread.stopLoop();
        gameThread = null;
        setKeepScreenOn(false);
    }
}
