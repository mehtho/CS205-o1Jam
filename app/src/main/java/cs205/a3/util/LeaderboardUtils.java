package cs205.a3.util;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Utilities for leaderboard operations
 */
public class LeaderboardUtils {
    private LeaderboardUtils() {

    }

    /**
     * Saves the user's selected name
     *
     * @param data    Data to write
     * @param context Android context
     */
    public static void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("name.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the user's name
     *
     * @param context Android context
     * @return The user's name
     */
    public static String readNameFile(Context context) {
        try {
            Scanner sc = new Scanner(new File(context.getFilesDir() + "/name.txt"));
            if (sc.hasNextLine()) {
                return sc.nextLine();
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}
