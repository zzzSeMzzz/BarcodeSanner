package sem.ru.barscaner.mvp.view;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import sem.ru.barscaner.mvp.model.LocalPhoto;

public interface ScanView extends BaseView {

    void setBarcode(String barcode);

    void initRecycle(List<LocalPhoto> items);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void addPhoto(LocalPhoto localPhoto);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void onStartCopyPhoto();

    @StateStrategyType(OneExecutionStateStrategy.class)
    void onStartStopPhoto(boolean showMessage);

    void showProgress(boolean show);

    void showServerInfo(boolean show);
}
