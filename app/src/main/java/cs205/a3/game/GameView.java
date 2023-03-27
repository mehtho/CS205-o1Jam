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

import cs205.a3.NotificationPublisher;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private final Game game = new Game(this::useCanvas);

    private GameThread gameThread;

    @SuppressLint("ClickableViewAccessibility")
    public GameView(Context context, AttributeSet attributeSet) {
        super(context);
        game.setContext(context);
        getHolder().addCallback(this);
        setKeepScreenOn(true);
    }

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

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if ((gameThread == null) || (gameThread.getState() == Thread.State.TERMINATED)) {
            gameThread = new GameThread(game);
        }
        final Rect rect = getHolder().getSurfaceFrame();
        game.resize(rect.width(), rect.height());
        gameThread.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        game.resize(i1, i2);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        gameThread.stopLoop();
        gameThread = null;
    }

    @Override
    public void draw(final Canvas canvas) {
        super.draw(canvas);
//        game.draw();
    }
}
