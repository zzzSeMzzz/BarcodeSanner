package sem.ru.barscaner.mvp.presenter;

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

import sem.ru.barscaner.di.App;
import sem.ru.barscaner.mvp.model.LocalPhoto;
import sem.ru.barscaner.mvp.view.ScanView;

@InjectViewState
public class ScanPresenter extends BasePresenter<ScanView> {

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
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initRecycle(new ArrayList<>());
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
        for(int i=0; i<photos.size(); i++){
            try {
                String newFileName = folder+"demo_"+i+".jpg";
                copyFile(photos.get(i).getPhoto(), new File(newFileName));
                photos.get(i).getPhoto().delete();
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "saveAllPhoto: error copy file");
            }
        }
        getViewState().onStartStopPhoto();
    }
}
