package cs205.a3.game;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class LeaderboardUtils {
    private LeaderboardUtils() {

    }

    public static void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("name.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readNameFile(Context context) {
        try {
            Scanner sc = new Scanner(new File(context.getFilesDir() + "/name.txt"));
            if (sc.hasNextLine()) {
                return sc.nextLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
