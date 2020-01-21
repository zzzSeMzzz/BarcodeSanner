package sem.ru.barscaner.ui;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import sem.ru.barscaner.R;
import sem.ru.barscaner.ui.fragment.ScanFragment;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_SETTINGS=3030;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            default:
                return super.onOptionsItemSelected(item);
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_settings:
                startActivityForResult(
                        new Intent(MainActivity.this,
                                SettingsActivity.class), REQUEST_SETTINGS);
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SETTINGS && resultCode==RESULT_OK){
            Log.d(TAG, "onActivityResult: change settings");
            try {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.my_nav_host_fragment);
                ScanFragment fragment = (ScanFragment)
                        navHostFragment.getChildFragmentManager().getFragments().get(0);
                fragment.changeSettings();
            }catch (Exception e){}
        }
    }
}
