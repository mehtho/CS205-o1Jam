package cs205.a3.scorecalc;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import cs205.a3.game.BeatsTimer;
import cs205.a3.song.NoteTimer;

public class NoteHandler implements Runnable{
    private static final int OFFSET = 400;

    private final Queue<QueuedNote> noteQueue;
    private final Board board;
    private final NoteTimer noteTimer;
    private final ScoreHandler scoreHandler;
    private final BeatsTimer beatsTimer = new BeatsTimer();

    private final AtomicBoolean isActive;

    public NoteHandler(Queue<QueuedNote> noteQueue, Board board, ScoreHandler scoreHandler) {
        this.noteQueue = noteQueue;
        this.board = board;
        this.noteTimer = new NoteTimer();
        this.isActive = new AtomicBoolean(false);
        this.scoreHandler = scoreHandler;
    }

    public boolean isActive() {
        return isActive.get();
    }

    public void end() {
        isActive.set(false);
    }

    @Override
    public void run() {
        isActive.compareAndSet(false, true);
        noteTimer.start();
        beatsTimer.start();

        while(isActive.get()) {
            long millDelta = noteTimer.getDelta();

            while (!noteQueue.isEmpty() && millDelta > noteQueue.peek().getTime() - OFFSET) {
                board.addNote(noteQueue.remove().getLane());
            }

            for(int i = 0; i < beatsTimer.getBeats(); i++){
                //Register a miss if a note times out
                if (board.tick()) {
                    scoreHandler.enqueueScore(-1);
                }
            }

            if (noteQueue.isEmpty() && board.isEmpty() && !board.isEnding()) {
                board.startEnding();
            }
        }
    }
}
