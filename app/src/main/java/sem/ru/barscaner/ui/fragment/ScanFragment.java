package sem.ru.barscaner.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sem.ru.barscaner.R;
import sem.ru.barscaner.mvp.model.LocalPhoto;
import sem.ru.barscaner.mvp.presenter.ScanPresenter;
import sem.ru.barscaner.mvp.view.ScanView;
import sem.ru.barscaner.ui.ImageViewerActivity;
import sem.ru.barscaner.ui.ScanActivity;
import sem.ru.barscaner.ui.SettingsActivity;
import sem.ru.barscaner.ui.adapter.LocalPhotoAdapter;
import sem.ru.barscaner.utils.PermissionHelper;

public class ScanFragment extends MvpAppCompatFragment implements
        PermissionHelper.OnPermissionResultImpl, PermissionHelper.OnShowRationaleAlert,
        ScanView, LocalPhotoAdapter.OnRvItemClickListener {

    private static final String TAG = "ScanFragment";

    @BindView(R.id.edBarCode)
    EditText edBarCode;
    @BindView(R.id.rvPhoto)
    RecyclerView rvPhoto;
    @BindView(R.id.btnPhoto)
    Button btnPhoto;
    @BindView(R.id.btnSave)
    FloatingActionButton btnSave;
    @BindView(R.id.cardView)
    CardView cardViewCode;
    @BindView(R.id.cardView2)
    CardView cardViewPhoto;
    @BindView(R.id.progressBar)
    ProgressBar progress;
    @BindView(R.id.llProgress)
    LinearLayout llProgress;

    @InjectPresenter
    ScanPresenter presenter;
    private LocalPhotoAdapter adapter;


    private final String[] permAll = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERM_ALL = 1;
    private static final int REQUEST_BARCODE=1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private PermissionHelper permissionHelper;
    private String currentPhotoFileName="";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentPhotoFileName !=null){
            outState.putString("currentPhotoFile", currentPhotoFileName);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            currentPhotoFileName = savedInstanceState.getString("currentPhotoFile");
        }catch (Exception ignored){}
    }

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
                    if(presenter.isScanClick()) {
                        showScanner();
                    }else {
                        showCamera();
                    }
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
    public void showProgress(boolean show) {
        llProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        cardViewCode.setVisibility(!show ? View.VISIBLE : View.GONE);
        cardViewPhoto.setVisibility(!show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
    }

    @Override
    public void setBarcode(String barcode) {
        edBarCode.setText(barcode);
    }

    @Override
    public void initRecycle(List<LocalPhoto> items) {
        LinearLayoutManager horizontalLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvPhoto.setLayoutManager(horizontalLayoutManager);
        adapter = new LocalPhotoAdapter(this, items);
        rvPhoto.setAdapter(adapter);
    }

    @Override
    public void onPhotoItemClick(LocalPhoto localPhoto) {
        Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
        intent.putExtra("image", localPhoto.getPhoto().getAbsolutePath());
        startActivity(intent);
    }

    @Override
    public void addPhoto(LocalPhoto localPhoto) {
        adapter.addItem(localPhoto);

        onClickPhoto(btnPhoto);
    }

    @Override
    public void onStartCopyPhoto() {
        btnSave.setEnabled(false);
    }

    @Override
    public void onStartStopPhoto(boolean showMessage) {
        if(adapter!=null) adapter.clearItems();
        btnSave.setEnabled(true);
        edBarCode.setText("");
        String folder =SettingsActivity.SD_CARD + getActivity().getSharedPreferences("conf", Context.MODE_PRIVATE)
                .getString("folder", SettingsActivity.DEFAULT_PHOTO_DIR)+"/";
        if(showMessage) {
            showError("Сохранено в " + folder);
        }
    }

    @OnClick(R.id.btnSnan)
    public void onClickScan(View v){
        presenter.setScanClick(true);
        permissionHelper.requestPermissions(permAll, PERM_ALL,
                getResources().getString(R.string.error_camera_perm));
    }

    @OnClick(R.id.btnPhoto)
    public void onClickPhoto(View v){
        int maxPhoto = getActivity().getSharedPreferences("conf", Context.MODE_PRIVATE)
                .getInt("max", SettingsActivity.DEFAULT_MAX_PHOTO);
        if(adapter!=null&&adapter.getItemCount()>=maxPhoto){
            showError("Максимум фотографий "+maxPhoto);
            return;
        }
        presenter.setScanClick(false);
        permissionHelper.requestPermissions(permAll, PERM_ALL,
                getResources().getString(R.string.error_camera_perm));
    }

    @OnClick(R.id.btnSave)
    public void onClickSave(View v){
        if(edBarCode.getText().toString().isEmpty()){
            showError("Заполниет поле штрих код");
            return;
        }
        if(adapter==null||adapter.getItemCount()==0) return;
        String folder =SettingsActivity.SD_CARD + getActivity().getSharedPreferences("conf", Context.MODE_PRIVATE)
                .getString("folder", SettingsActivity.DEFAULT_PHOTO_DIR)+"/";
        presenter.saveAllPhoto(adapter.getItems(), folder, edBarCode.getText().toString());
        //presenter.sendFile("/storage/emulated/0/BarcodeScanner/12121.jpg");
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
                    onClickPhoto(btnPhoto);
                }
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: camera " + currentPhotoFileName);

            //File dest = null;//getCopiedFile(new File(currentPhotoFileName));
            try {
                /*dest = presenter
                        .resizeImage(new File(currentPhotoFileName), App.MAX_WIDTH_IMAGE);*/
                //dest = new File(currentPhotoFileName);
                presenter.addLocalPhoto(currentPhotoFileName);
            } catch (Exception e) {
                e.printStackTrace();
                showError("Ошибка обработки файла");
                return;
            }
        }
    }

    private void grantUriPerm(Intent intent, Uri uri){
        List<ResolveInfo> resInfoList = getActivity().getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            Log.d(TAG, "grantUriPerm: "+packageName);
            getActivity().grantUriPermission(packageName, uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "img"; //+ timeStamp* + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoFileName = image.getAbsolutePath();
        Log.d(TAG, "createImageFile: "+image.getAbsolutePath());
        return image;
    }

    private void showCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "ru.sem.barscaner.fileprovider",
                        photoFile);

                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                    grantUriPerm(takePictureIntent, photoURI);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
}
