package cs205.a3.scorecalc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Object storing information on the game's board
 */
public class Board {
    private final Object mutex = new Object();
    private final List<LinkedList<Note>> board;

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

    /**
     * Updates the position of notes and removes them if they have expired (Moved through
     * the screen)
     *
     * @return If a miss has been triggered by an expired note
     */
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

    /**
     * Responds a tap on a given lane
     *
     * @param laneNo Lane number that was tapped
     * @return The score as a result of tapping the given lane
     */
    public int tapLane(int laneNo) {
        synchronized (mutex) {
            LinkedList<Note> lane = board.get(laneNo);

            if (!lane.isEmpty()) {
                return lane.pop().getScore();
            }
        }
        return -2;
    }

    public String toString() {
        return board.get(0).toString() + board.get(1).toString() + board.get(2).toString() + board.get(3).toString();
    }
}
