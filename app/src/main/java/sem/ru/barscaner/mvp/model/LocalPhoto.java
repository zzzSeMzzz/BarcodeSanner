package sem.ru.barscaner.mvp.model;

import java.io.File;

public class LocalPhoto {

    private long id;

    private String fileName, barcode;
    private File photo;
    private int scaleWidth, sclaeHeight;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getScaleWidth() {
        return scaleWidth;
    }

    public void setScaleWidth(int scaleWidth) {
        this.scaleWidth = scaleWidth;
    }

    public int getSclaeHeight() {
        return sclaeHeight;
    }

    public void setSclaeHeight(int sclaeHeight) {
        this.sclaeHeight = sclaeHeight;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
