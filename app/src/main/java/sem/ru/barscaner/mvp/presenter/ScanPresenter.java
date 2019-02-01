package sem.ru.barscaner.mvp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import sem.ru.barscaner.api.MainService;
import sem.ru.barscaner.di.App;
import sem.ru.barscaner.mvp.model.LocalPhoto;
import sem.ru.barscaner.mvp.view.ScanView;
import sem.ru.barscaner.utils.ApiUtil;

@InjectViewState
public class ScanPresenter extends BasePresenter<ScanView> {

    @Inject
    MainService service;
    private static final String token = "Bearer a7f2c75dd6074825c305b8dc6f0038c6095882e6";

    private static final String TAG = "ScanPresenter";

    private String barCode;
    //private List<LocalPhoto> items;
    private boolean scanClick;

    public boolean isScanClick() {
        return scanClick;
    }

    public void setScanClick(boolean scanClick) {
        this.scanClick = scanClick;
    }

    public ScanPresenter() {
        //items = new ArrayList<>();
        App.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initRecycle(App.getAppComponent().getSqLiteDB().getAllPhotos());
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
        getViewState().setBarcode(barCode);
    }

    public void addLocalPhoto(String fileName){
        LocalPhoto localPhoto = new LocalPhoto();
        localPhoto.setPhoto(new File(fileName));
        localPhoto.setFileName(localPhoto.getPhoto().getName());
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeFile(localPhoto.getPhoto().getAbsolutePath());
            int origWidth = b.getWidth();
            int origHeight = b.getHeight();
            int destHeight = origHeight/(origWidth / App.THUMBNAIL_WIDTH);
            localPhoto.setScaleWidth(App.THUMBNAIL_WIDTH);
            localPhoto.setSclaeHeight(destHeight);
        } catch (Exception e) {
            e.printStackTrace();
            localPhoto.setScaleWidth(App.THUMBNAIL_WIDTH);
            localPhoto.setSclaeHeight(App.THUMBNAIL_HEIGHT);
        }
        App.getAppComponent().getSqLiteDB().addPhoto(localPhoto);
        getViewState().addPhoto(localPhoto);
    }

    private void copyFile(File source, File dest) throws IOException {
        FileInputStream is = new FileInputStream(source);
        if(!dest.exists()) {
            if (!dest.createNewFile()) {
                dest.delete();
                dest.createNewFile();
            }
        }
        try {
            FileOutputStream os = new FileOutputStream(dest);
            try {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } finally {
                os.close();
            }
        } finally {
            is.close();
        }
    }

    public void saveAllPhoto(List<LocalPhoto> photos, String folder, String barCode){
        Log.d(TAG, "saveAllPhoto: try create folder "+folder);
        getViewState().onStartCopyPhoto();
        new File(folder).mkdirs();
        List<String> fileNames = new ArrayList<>();
        for(int i=0; i<photos.size(); i++){
            try {
                String newFileName;
                newFileName = i==0 ? folder + barCode + ".jpg" : folder + barCode + "_" + i + ".jpg";
                fileNames.add(newFileName);
                copyFile(photos.get(i).getPhoto(), new File(newFileName));
                photos.get(i).getPhoto().delete();
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "saveAllPhoto: error copy file");
            }
        }
        App.getAppComponent().getSqLiteDB().clearPhotos();
        boolean sendServer = App.getAppComponent().getContext()
                .getSharedPreferences("conf", Context.MODE_PRIVATE)
                .getBoolean("sendServer", true);
        if(sendServer) {
            sendFiles(fileNames, barCode);
        }else {
            getViewState().onStartStopPhoto(true);
        }
    }

    /*public void sendFiles222(List<String> fileNames){
        if(fileNames.size()==0){
            getViewState().onStartStopPhoto();
            return;
        }
        sendFile(fileNames.get(0));
    }*/

    private MultipartBody.Part[] createMultiParts(List<String> filenames){
        MultipartBody.Part[] parts = new MultipartBody.Part[filenames.size()];
        for (int i = 0; i < filenames.size(); i++) {

            File file = new File(filenames.get(i));
            RequestBody fileReqBody =
                    RequestBody.create(MediaType.parse("image/jpeg"), file);

            MultipartBody.Part part =
                    MultipartBody.Part.createFormData("image[]",
                            file.getName(), fileReqBody);
            parts[i]=part;
        }
        return parts;
    }

    public void sendFiles(List<String> fileNames, String barCode){
        //storage/emulated/0/BarcodeScanner/12121.jpg
        /*File file = new File(fileName);
        RequestBody fileReqBody =
                RequestBody.create(MediaType.parse("image/jpeg"), file);

        MultipartBody.Part part =
                MultipartBody.Part.createFormData("image[]",
                        file.getName(), fileReqBody);*/

        getViewState().showProgress(true);
        RequestBody description =
                RequestBody.create(MediaType.parse("text/plain"), barCode);

        Disposable d = service.sendFile(token, createMultiParts(fileNames), description)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            Log.d(TAG, "send file success");
                            Log.d(TAG, "sendFile: "+response.toString());
                            if(response.getMessage()!=null){
                                getViewState().showError(response.getMessage());
                            }
                            getViewState().showProgress(false);
                            getViewState().onStartStopPhoto(false);
                        },
                        error -> {
                            Log.e(TAG, "send file failed");
                            getViewState().showProgress(false);
                            getViewState().showError("Ошибка загурзки файла "+
                                    ApiUtil.getMessage(error));
                        }
                );
        unsubscribeOnDestroy(d);
    }
}
