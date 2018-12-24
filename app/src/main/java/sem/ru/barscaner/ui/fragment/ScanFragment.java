package sem.ru.barscaner.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sem.ru.barscaner.R;
import sem.ru.barscaner.mvp.presenter.ScanPresenter;
import sem.ru.barscaner.mvp.view.ScanView;
import sem.ru.barscaner.ui.ScanActivity;
import sem.ru.barscaner.utils.PermissionHelper;

public class ScanFragment extends MvpAppCompatFragment implements
        PermissionHelper.OnPermissionResultImpl, PermissionHelper.OnShowRationaleAlert,
        ScanView {

    private static final String TAG = "ScanFragment";

    @BindView(R.id.edBarCode)
    EditText edBarCode;

    @InjectPresenter
    ScanPresenter presenter;


    private final String[] permAll = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERM_ALL = 1;
    private static final int REQUEST_BARCODE=1;
    private PermissionHelper permissionHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scan, null);
        ButterKnife.bind(this, v);

        permissionHelper = new PermissionHelper(this,
                this, this);
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper
                .onPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onShowRationalePermissionAlert(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info, null);
        dialogBuilder.setView(dialogView);
        TextView tvTitle = dialogView.findViewById(R.id.dlg_title);
        TextView tvInfo = dialogView.findViewById(R.id.dlg_info);
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        tvTitle.setText("Разрешения");
        tvInfo.setText(getResources().getString(R.string.error_camera_perm));
        AlertDialog alertDialog = dialogBuilder.create();
        btnOk.setOnClickListener(view -> {
            alertDialog.dismiss();
            permissionHelper.againRequestPermission(permAll, PERM_ALL);
        });
        alertDialog.show();
    }

    @Override
    public void onPermissionsResult(int requestCode, boolean isGranted) {
        switch (requestCode) {
            case PERM_ALL:
                if (isGranted) {
                    showScanner();
                } else {
                    showError(getResources().getString(R.string.error_camera_perm));
                }
                break;
        }
    }

    @Override
    public void showError(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setBarcode(String barcode) {
        edBarCode.setText(barcode);
    }

    @OnClick(R.id.btnSnan)
    public void onClickScan(View v){
        permissionHelper.requestPermissions(permAll, PERM_ALL,
                getResources().getString(R.string.error_camera_perm));
    }

    private void showScanner(){
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        startActivityForResult(intent, REQUEST_BARCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BARCODE) {
            if (resultCode == Activity.RESULT_OK) {
                if(data.getStringExtra("barcode")!=null){
                    presenter.setBarCode(data.getStringExtra("barcode"));
                }
            }
        }
    }
}
