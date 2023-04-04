package cs205.a3.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import cs205.a3.scorecalc.Board;
import cs205.a3.scorecalc.Note;
import cs205.a3.scorecalc.QueuedNote;
import cs205.a3.scorecalc.ScoreHandler;
import cs205.a3.song.MillDeltaTimer;

public class Game {
    private static final int OFFSET = 600;
    private final static int targetFps = 50;
    private final static long intervalFps = 1000L;
    private final Object flashMutex = new Object();
    private final Predicate<Consumer<Canvas>> useCanvas;
    private final ScoreHandler scoreHandler = new ScoreHandler();
    private final MillDeltaTimer noteTimer = new MillDeltaTimer();
    private final Counter frameCounter = new Counter();
    private final ElapsedTimer elapsedTimer = new ElapsedTimer();
    private final MediaPlayer songPlayer = new MediaPlayer();
    private final Paint fpsText = new Paint();
    private final Paint comboText = new Paint();
    private final Paint scoreText = new Paint();
    private final Paint noteColorOdd = new Paint();
    private final Paint noteColorEven = new Paint();
    private final Board board = new Board();
    private final Queue<QueuedNote> noteQueue = new LinkedList<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final MillDeltaTimer clockTimer = new MillDeltaTimer();
    private int canvasHeight;
    private int canvasWidth;
    private double avgFps = 0.0;
    private final DeltaStepper fpsUpdater = new DeltaStepper(intervalFps, this::fpsUpdate);
    private String songPath;
    private String songName;
    private String songId;
    private boolean isEnding = false;
    private final Flash[] flashes = new Flash[4];
    private int maxTime = 1;
    private final RectF spinningTimer = new RectF(1210F, 0F, 1410F, 200F);
    private String timerText = "00:00";
    private final DeltaStepper clockUpdater = new DeltaStepper(intervalFps, this::clockUpdate);
    private Context context;

    private final Thread transition = new Thread(() -> {
        try {
            Thread.sleep(5000);

            Intent intent = new Intent(context, EndScreen.class);
            intent.putExtra("songName", songName);
            intent.putExtra("songId", songId);
            intent.putExtra("score", scoreHandler.getScore());

            context.startActivity(intent);
            ((Activity) context).finish();
        } catch (InterruptedException e) {
            // Do nothing
        }
    });

    public Game(final Predicate<Consumer<Canvas>> useCanvas) {
        this.useCanvas = useCanvas;
        new Thread(scoreHandler).start();

        {
            fpsText.setColor(Color.rgb(200, 200, 200));
            fpsText.setTextSize(40.0f);
            fpsText.setStyle(Paint.Style.FILL);
        }

        {
            comboText.setColor(Color.rgb(200, 200, 200));
            comboText.setTextSize(160.0f);
        }

        {
            scoreText.setColor(Color.rgb(200, 200, 200));
            scoreText.setTextSize(120.0f);
        }

        {
            noteColorOdd.setColor(Color.rgb(41, 66, 153));
            noteColorOdd.setStyle(Paint.Style.STROKE);
            noteColorOdd.setAntiAlias(true);
            noteColorOdd.setStrokeWidth(1);
            noteColorOdd.setStyle(Paint.Style.FILL);
        }

        {
            noteColorEven.setColor(Color.rgb(230, 191, 85));
            noteColorEven.setStyle(Paint.Style.STROKE);
            noteColorEven.setAntiAlias(true);
            noteColorEven.setStrokeWidth(1);
            noteColorEven.setStyle(Paint.Style.FILL);
        }
    }

    public void initSong(String songId, String songName) {
        this.songId = songId;
        this.songName = songName;

        try {
            songPlayer.setDataSource(songPath + songId + ".mp3");
            songPlayer.prepare();

            File myObj = new File(songPath + songId + ".osu");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(",");
                noteQueue.add(new QueuedNote(Integer.parseInt(data[0]), Integer.parseInt(data[1])));
                if (!myReader.hasNextLine()) {
                    maxTime = Integer.parseInt(data[0]);
                }
            }

            myReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playSong() {
        songPlayer.start();
        noteTimer.start();
        clockTimer.start();
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
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
        /**
         * Null checks
         */
        if (canvas == null) {
            return;
        }
        if (songName == null) {
            return;
        }

        // Wipe canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        //Register a miss if a note times out
        if (board.tick()) {
            scoreHandler.enqueueScore(-1);
        }

        /**
         * Update the state of notes, draw new ones
         */
        long millDelta = noteTimer.getDelta();

        while (!noteQueue.isEmpty() && millDelta > noteQueue.peek().getTime() - OFFSET) {
            board.addNote(noteQueue.remove().getLane());
        }

        for (int lane = 0; lane < 4; lane++) {
            for (Note note : board.getBoard().get(lane)) {
                int l = lane * (canvasWidth / 4);
                int t = note.getAge() * (canvasHeight / 50);
                if (lane == 1 || lane == 2) {
                    canvas.drawRect(l, t, l + (canvasWidth / 4), t + 80, noteColorOdd);
                } else {
                    canvas.drawRect(l, t, l + (canvasWidth / 4), t + 80, noteColorEven);
                }
            }
        }

        /**
         * Draw UI components
         */
        canvas.drawText(
                String.format("%.2f", avgFps),
                1000.0f, 30.0f,
                fpsText
        );

        canvas.drawText(
                timerText,
                1230.0f, 300.0f,
                fpsText
        );

        canvas.drawText(
                String.format("%d", scoreHandler.getScore()),
                0.0f, 100.0f,
                scoreText
        );

        canvas.drawText(
                String.format("x%d", scoreHandler.getCombo()),
                0.0f, 300.0f,
                comboText
        );

        canvas.drawLine(
                0,
                30 * (canvasHeight / 50),
                canvasWidth,
                30 * (canvasHeight / 50),
                fpsText);

        canvas.drawArc(
                spinningTimer,
                0F,
                (float) (Math.min(1.0, (float) millDelta / maxTime) * 360),
                true,
                fpsText);

        //Draw tap effects
        drawFlashes(canvas);

        // Init the end if empty
        if (noteQueue.isEmpty() && !isEnding) {
            isEnding = true;
            transition.start();
        }
    }

    public void update() {
        final long deltaTime = elapsedTimer.progress();
        if (deltaTime <= 0) {
            return;
        }
        // Step updates.
        fpsUpdater.update(deltaTime);
        clockUpdater.update(deltaTime);
        // Immediate updates.
    }

    private boolean fpsUpdate(long deltaTime) {
        final double fractionTime = intervalFps / (double) deltaTime;
        avgFps = frameCounter.getValue() * fractionTime;
        return false;
    }

    private boolean clockUpdate(long deltaTime) {
        long totalSeconds = clockTimer.getDelta() / 1000;
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60;

        timerText = String.format("%02d:%02d", minutes, seconds);
        return false;
    }

    public void resize(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    /**
     * Process a tap in a given lane.
     *
     * @param lane Lane number 0-3
     */
    public void tapLane(int lane) {
        int point = board.tapLane(lane);

        synchronized (flashMutex) {
            flashes[lane] = new Flash(point, lane);
        }

        if (point != -2) {
            scoreHandler.enqueueScore(point);
        }
    }

    /**
     * Draw tap effects.
     *
     * @param canvas
     */
    private void drawFlashes(Canvas canvas) {
        for (int i = 0; i < 4; i++) {
            if (flashes[i] != null) {
                int l = i * (canvasWidth / 4);
                int t = flashes[i].getAge();

                Shader shader;
                if (flashes[i].getType() == 50) {
                    shader = new LinearGradient(0, canvasHeight / 3, 0, 0, Color.BLUE, Color.BLACK, Shader.TileMode.CLAMP);
                } else if (flashes[i].getType() == 100) {
                    shader = new LinearGradient(0, canvasHeight / 3, 0, 0, Color.GREEN, Color.BLACK, Shader.TileMode.CLAMP);
                } else if (flashes[i].getType() == 300) {
                    shader = new LinearGradient(0, canvasHeight / 3, 0, 0, Color.YELLOW, Color.BLACK, Shader.TileMode.CLAMP);
                } else if (flashes[i].getType() == -1) {
                    shader = new LinearGradient(0, canvasHeight / 3, 0, 0, Color.RED, Color.BLACK, Shader.TileMode.CLAMP);
                } else {
                    shader = new LinearGradient(0, canvasHeight / 3, 0, 0, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
                }

                Paint paint = new Paint();
                paint.setShader(shader);

                canvas.drawRect(new RectF(l, canvasHeight, l + (canvasWidth / 4), ((canvasHeight * 2) / 3) + (75 * t)), paint);

                flashes[i].incAge();
                if (flashes[i].getAge() > 5) {
                    flashes[i] = null;
                }
            }
        }
    }

    public void startRunning() {
        isRunning.set(true);
    }

    public void stopRunning() {
        isRunning.set(false);
        songPlayer.stop();
        transition.interrupt();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
