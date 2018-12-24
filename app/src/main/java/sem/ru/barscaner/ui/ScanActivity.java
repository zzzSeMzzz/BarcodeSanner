package sem.ru.barscaner.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sem.ru.barscaner.R;

public class ScanActivity extends AppCompatActivity {

    private static final String TAG = "ScanActivity";

    private CaptureManager capture;
    private String barCode="";

    @BindView(R.id.zxing_barcode_scanner)
    DecoratedBarcodeView barcodeScannerView;
    //private Button switchFlashlightButton;
    @BindView(R.id.zxing_viewfinder_view)
    ViewfinderView viewfinderView;
    @BindView(R.id.zxing_status_view)
    TextView tvStatus;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                VibrationEffect effect =
                        VibrationEffect.createOneShot(700, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);
            }else{
                long[] mVibratePattern = new long[]{500, 500, 500, 500};
                vibrator.vibrate(mVibratePattern, -1);
            }

            barcodeScannerView.pause();
            barCode=result.getText();
            Log.d(TAG, "barcodeResult: " + barCode);
            tvStatus.setText(barCode);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        Collection<BarcodeFormat> formats =
                Arrays.asList(BarcodeFormat.CODE_39,
                        BarcodeFormat.EAN_8,
                        BarcodeFormat.CODE_128,
                        BarcodeFormat.EAN_13,
                        BarcodeFormat.CODE_93
                        );

        barcodeScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeScannerView.decodeContinuous(callback);

        //changeMaskColor(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
        outState.putString("barcode", barCode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        barCode=savedInstanceState.getString("barcode","");
    }


    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void changeMaskColor(View view) {
        /*Random rnd = new Random();
        int color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        viewfinderView.colo*/
    }

    @OnClick(R.id.btnDone)
    public void onClickDone(View v){
        Intent intent = new Intent();
        intent.putExtra("barcode", barCode);
        setResult(RESULT_OK, intent);
        finish();
    }
}
