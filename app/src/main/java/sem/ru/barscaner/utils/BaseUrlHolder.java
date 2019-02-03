package sem.ru.barscaner.utils;

public class BaseUrlHolder {

    public String baseUrl;

    public BaseUrlHolder() {
        this.baseUrl="https://pezhon.ru/";
    }

    public BaseUrlHolder(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
