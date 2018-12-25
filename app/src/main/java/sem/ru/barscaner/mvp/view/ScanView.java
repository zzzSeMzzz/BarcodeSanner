package sem.ru.barscaner.mvp.view;

import java.util.List;

import sem.ru.barscaner.mvp.model.LocalPhoto;

public interface ScanView extends BaseView {

    void setBarcode(String barcode);

    void initRecycle(List<LocalPhoto> items);

    void addPhoto(LocalPhoto localPhoto);

    void onStartCopyPhoto();

    void onStartStopPhoto();
}
