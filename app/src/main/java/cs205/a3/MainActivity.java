package cs205.a3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import cs205.a3.song.SongServer;

public class MainActivity extends Activity {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SongServer server = SongServer.getInstance(getString(R.string.server));
        server.getAllSongs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doOnClick(View view) {
        InternalStorage.writeFileOnInternalStorage(getApplicationContext(), "Test", "Test string");
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("songName", "helloworld");
        startActivity(intent);
    }
}