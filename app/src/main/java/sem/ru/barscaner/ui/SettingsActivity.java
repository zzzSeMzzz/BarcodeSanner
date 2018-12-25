package sem.ru.barscaner.ui;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sem.ru.barscaner.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String DEFAULT_PHOTO_DIR = "BarcodeScanner";
    public static final int DEFAULT_MAX_PHOTO = 10;

    public static final String SD_CARD =
            Environment.getExternalStorageDirectory().getAbsolutePath()+"/";

    @BindView(R.id.edFolder)
    EditText edFolder;
    @BindView(R.id.edMaxPhoto)
    EditText edMaxPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setTitle("Настройки");
        String folder = getSharedPreferences("conf", MODE_PRIVATE).getString("folder", DEFAULT_PHOTO_DIR);
        int maxPhoto = getSharedPreferences("conf", MODE_PRIVATE).getInt("max", DEFAULT_MAX_PHOTO);
        edFolder.setText(SD_CARD+folder);
        edMaxPhoto.setText(String.valueOf(maxPhoto));
    }

    @OnClick(R.id.btnSave)
    public void onClickSave(View v){
        Toast.makeText(this, "В демо версии сохранение недоступно", Toast.LENGTH_SHORT).show();
        return;
        /*String folder = edFolder.getText().toString();
        int iS = folder.lastIndexOf("/");
        int iE = folder.length()-iS;
        getSharedPreferences("conf", MODE_PRIVATE)
                .edit()
                .putString("folder", folder.substring(iS, iE))
                .putInt("max", Integer.valueOf(edMaxPhoto.getText().toString()))
                .apply();
        finish();*/

    }
}
