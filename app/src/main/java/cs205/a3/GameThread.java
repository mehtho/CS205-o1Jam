package cs205.a3;

public class GameThread extends Thread {

    private final Game game;

    public GameThread(final Game game) {
        this.game = game;
    }

    public void startLoop() {
        game.startRunning();
        game.playSong();
        start();
    }

    public void stopLoop() {
        game.stopRunning();
    }

    @Override
    public void run() {
        super.run();
        while (game.isRunning()) {
            game.draw();
            game_sleep();
            game.update();
        }
    }

    private void game_sleep() {
        long sleepTime = game.getSleepTime();
        if (sleepTime > 0) {
            try {
                sleep(sleepTime);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
