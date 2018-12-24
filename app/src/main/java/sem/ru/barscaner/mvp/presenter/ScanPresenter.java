package sem.ru.barscaner.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;

import sem.ru.barscaner.mvp.view.ScanView;

@InjectViewState
public class ScanPresenter extends BasePresenter<ScanView> {

    private String barCode;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
        getViewState().setBarcode(barCode);
    }
}
