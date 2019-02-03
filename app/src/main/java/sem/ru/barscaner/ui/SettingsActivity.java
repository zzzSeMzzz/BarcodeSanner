package sem.ru.barscaner.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sem.ru.barscaner.R;
import sem.ru.barscaner.di.modules.RetrofitModule;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    public static final String DEFAULT_PHOTO_DIR = "BarcodeScanner";
    public static final int DEFAULT_MAX_PHOTO = 10;

    public static final String SD_CARD =
            Environment.getExternalStorageDirectory().getAbsolutePath()+"/";

    @BindView(R.id.edFolder)
    EditText edFolder;
    @BindView(R.id.edMaxPhoto)
    EditText edMaxPhoto;
    @BindView(R.id.swSendServer)
    Switch swSendServer;
    @BindView(R.id.edToken)
    EditText edToken;
    @BindView(R.id.edUrl)
    EditText edUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        edFolder.setKeyListener(null);
        setTitle("Настройки");
        SharedPreferences preferences = getSharedPreferences("conf", MODE_PRIVATE);
        String folder = preferences.getString("folder", DEFAULT_PHOTO_DIR);
        int maxPhoto = preferences.getInt("max", DEFAULT_MAX_PHOTO);
        boolean sendServer = preferences.getBoolean("sendServer", false);
        String token = preferences.getString("token",
                "Bearer a7f2c75dd6074825c305b8dc6f0038c6095882e6");
        String baseUrl=preferences.getString("base_url", RetrofitModule.API_BASE_URL);
        String urlPost=preferences.getString("url_post", "restservice/setimages/");
        swSendServer.setChecked(sendServer);
        edFolder.setText(SD_CARD+folder);
        edMaxPhoto.setText(String.valueOf(maxPhoto));
        edToken.setText(token);
        edUrl.setText(baseUrl+urlPost);
    }

    @OnClick(R.id.btnSave)
    public void onClickSave(View v){
        if(edMaxPhoto.getText().toString().isEmpty()
                ||Integer.valueOf(edMaxPhoto.getText().toString())==0){
            Toast.makeText(this, "Максимум не может быть 0", Toast.LENGTH_SHORT).show();
            return;
        }
        if(edToken.getText().toString().isEmpty()||edUrl.getText().toString().isEmpty()){
            Toast.makeText(this,
                    "Токен и API URL не могут быть пустыми", Toast.LENGTH_SHORT).show();
            return;
        }
        URL url = null;
        try {
            url = new URL(edUrl.getText().toString());
        } catch (MalformedURLException e) {
            Toast.makeText(this, "Не верный формат url", Toast.LENGTH_SHORT).show();
            return;
        }
        /*Log.d(TAG, "onClickSave: "+url.getProtocol());
        Log.d(TAG, "onClickSave: "+url.getHost());
        Log.d(TAG, "onClickSave: "+url.getPort());
        Log.d(TAG, "onClickSave: "+url.getPath());*/
        String baseUrl = url.getProtocol()+"://"+url.getHost();
        if(url.getPort()!=-1){
            baseUrl=baseUrl+":"+url.getPort()+"/";
        }else{
            baseUrl=baseUrl+"/";
        }
        Log.d(TAG, "onClickSave: "+baseUrl);
        String folder = edFolder.getText().toString();
        int iS = folder.lastIndexOf("/")+1;
        int iE = folder.length();
        Log.d(TAG, "onClickSave: "+folder.substring(iS, iE));
        getSharedPreferences("conf", MODE_PRIVATE)
                .edit()
                .putString("folder", folder.substring(iS, iE))
                .putInt("max", Integer.valueOf(edMaxPhoto.getText().toString()))
                .putBoolean("sendServer", swSendServer.isChecked())
                .putString("token", edToken.getText().toString())
                .putString("base_url", baseUrl)
                .putString("url_post", url.getPath().substring(1))
                .apply();
        Toast.makeText(this, "Изменения вступят в силу после перезагрузки",
                Toast.LENGTH_SHORT).show();
        finish();

    }

    @OnClick(R.id.btnFolder)
    public void onClickChangeFolder(View v){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dlg_input, null);
        dialogBuilder.setView(dialogView);
        EditText edMain = dialogView.findViewById(R.id.edMain);
        edMain.setText(getSharedPreferences("conf", MODE_PRIVATE).getString("folder", DEFAULT_PHOTO_DIR));
        dialogBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
            if(!edMain.getText().toString().isEmpty()){
                getSharedPreferences("conf", MODE_PRIVATE)
                        .edit()
                        .putString("folder", edMain.getText().toString())
                        .apply();
                edFolder.setText(SD_CARD+edMain.getText().toString());
            }
        })
                .setTitle("Название папки")
                .create().show();

    }
}
