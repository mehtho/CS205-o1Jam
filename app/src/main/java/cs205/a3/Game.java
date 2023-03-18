package cs205.a3;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import cs205.a3.scorecalc.Board;
import cs205.a3.scorecalc.Note;
import cs205.a3.scorecalc.ScoreHandler;

public class Game {
    public static Game game;

    private final static int targetFps = 50;

    private final static long intervalFps = 1000L;
    private final Runnable runnable;

    private final Predicate<Consumer<Canvas>> useCanvas;

    private double avgFps = 0.0;

    private final Counter frameCounter = new Counter();

    private final ElapsedTimer elapsedTimer = new ElapsedTimer();

    private final DeltaStepper fpsUpdater = new DeltaStepper(intervalFps, this::fpsUpdate);

    private final Paint fpsText = new Paint();

    private final Board board = new Board();

    private final Paint noteColor = new Paint();

    private int canvasHeight;

    private int canvasWidth;

    private String songName;

    private ScoreHandler scoreHandler;

    public Game(final Runnable runnable, final Predicate<Consumer<Canvas>> useCanvas) {
        this.runnable = runnable;
        this.useCanvas = useCanvas;

        this.scoreHandler = new ScoreHandler();
        new Thread(scoreHandler).start();

        {
            fpsText.setColor(Color.rgb(200, 200, 200));
            fpsText.setTextSize(40.0f);
        }

        {
            noteColor.setColor(Color.rgb(255, 0, 255));
            noteColor.setStyle(Paint.Style.STROKE);
            noteColor.setAntiAlias(true);
            noteColor.setStrokeWidth(1);
            noteColor.setStyle(Paint.Style.FILL);
        }

        Game.game = this;
    }

    public void setSongName(String songName) {
        this.songName = songName;
        System.out.println(songName + "SONG NAME");
    }

    public long getSleepTime() {
        final double targetFrameTime = (1000.0 / targetFps);
        final long updateTime = System.currentTimeMillis() - elapsedTimer.getUpdateStartTime();
        return Math.round(targetFrameTime - updateTime);
    }

    public void draw() {
        if (useCanvas.test(this::draw)) {
            frameCounter.increment();
        }
    }

    @SuppressLint("DefaultLocale")
    private void draw(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        if(songName == null) {
            System.out.println("No song name");
            return;
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if(board.tick()) {
            scoreHandler.enqueueScore(-1);
        }

        // TODO: Replace with proper spawning
        if(Math.random()<0.01) {
            board.addNote(0);
        }

        for(int lane = 0; lane < 4; lane++) {
            for(Note note:board.getBoard().get(lane)) {
                int l = lane * (canvasWidth/4);
                int t = note.getAge()*(canvasHeight/50);
                canvas.drawRect(l, t, l + (canvasWidth/4), t + 80, noteColor);
            }
        }

        canvas.drawText(
                String.format("%.2f", avgFps),
                10.0f, 30.0f,
                fpsText
        );

        canvas.drawText(
                String.format("%d", scoreHandler.getScore()),
                200.0f, 30.0f,
                fpsText
        );

        canvas.drawText(
                String.format("x%d", scoreHandler.getCombo()),
                400.0f, 30.0f,
                fpsText
        );
    }

    public void update() {
        final long deltaTime = elapsedTimer.progress();
        if (deltaTime <= 0) {
            return;
        }
        // Step updates.
        fpsUpdater.update(deltaTime);
        // Immediate updates.
    }

    private boolean fpsUpdate(long deltaTime) {
        final double fractionTime = intervalFps / (double)deltaTime;
        avgFps = frameCounter.getValue() * fractionTime;
        return false;
    }

    public void resize(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public void tapLane(int lane) {
        int point = board.tapLane(lane);
        if (point != -2 ){
            scoreHandler.enqueueScore(point);
        }
    }
}
