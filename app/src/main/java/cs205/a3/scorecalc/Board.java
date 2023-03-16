package cs205.a3.scorecalc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Board {
    private final List<LinkedList<Note>> board;

    public Board() {
        board = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            board.add(new LinkedList<>());
        }
    }

    public List<LinkedList<Note>> getBoard() {
        return board;
    }

    public void addNote(int lane) {
        board.get(lane).add(new Note());
    }

    public boolean tick() {
        AtomicBoolean triggerMiss = new AtomicBoolean(false);
        board.forEach(lane -> {
            Iterator<Note> iter = lane.iterator();
            while(iter.hasNext()) {
                if(iter.next().incAge()){
                    iter.remove();
                    triggerMiss.set(true);
                }
            }
        });

        return triggerMiss.get();
    }

    public String toString() {
        return board.get(0).toString() + board.get(1).toString() + board.get(2).toString() + board.get(3).toString();
    }
}
