package sem.ru.barscaner.mvp.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arellomobile.mvp.InjectViewState;

import java.io.File;
import java.util.ArrayList;

import sem.ru.barscaner.di.App;
import sem.ru.barscaner.mvp.model.LocalPhoto;
import sem.ru.barscaner.mvp.view.ScanView;

@InjectViewState
public class ScanPresenter extends BasePresenter<ScanView> {

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
}
