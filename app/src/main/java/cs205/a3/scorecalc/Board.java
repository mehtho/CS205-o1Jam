package cs205.a3.scorecalc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Board {
    private final Object mutex = new Object();
    private final List<LinkedList<Note>> board;

    private AtomicBoolean endFlag = new AtomicBoolean(false);

    public Board() {
        board = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            board.add(new LinkedList<>());
        }
    }

    public List<LinkedList<Note>> getBoard() {
        synchronized (mutex) {
            return board;
        }
    }

    public void addNote(int lane) {
        synchronized (mutex) {
            board.get(lane).add(new Note());
        }
    }

    public boolean isEnding() {
        return endFlag.get();
    }

    public void startEnding() {
        endFlag.compareAndSet(false, true);
    }

    public boolean tick() {
        AtomicBoolean triggerMiss = new AtomicBoolean(false);
        synchronized (mutex) {
            board.forEach(lane -> {
                Iterator<Note> iter = lane.iterator();

                while (iter.hasNext()) {
                    if (iter.next().incAge()) {
                        iter.remove();
                        triggerMiss.compareAndSet(false, true);
                    }
                }
            });

            return triggerMiss.get();
        }
    }

    public int tapLane(int laneNo) {
        synchronized (mutex) {
            LinkedList<Note> lane = board.get(laneNo);

            if (!lane.isEmpty()) {
                return lane.pop().getScore();
            }
        }
        return -2;
    }

    public boolean isEmpty() {
        boolean empty = true;
        synchronized (mutex) {
            for (List<Note> lane : board) {
                empty = empty && lane.isEmpty();
            }
        }

        return empty;
    }

    public String toString() {
        return board.get(0).toString() + board.get(1).toString() + board.get(2).toString() + board.get(3).toString();
    }
}
