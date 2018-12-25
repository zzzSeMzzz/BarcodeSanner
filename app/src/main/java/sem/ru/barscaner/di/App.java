package sem.ru.barscaner.di;

import android.app.Application;

import sem.ru.barscaner.di.modules.ContextModule;
import sem.ru.barscaner.di.modules.DbModule;


public class App extends Application {

    private static AppComponent sAppComponent;
    private static final String TAG = "Application";
    public static final int THUMBNAIL_WIDTH = 200;
    public static final int THUMBNAIL_HEIGHT = 200;


    @Override
    public void onCreate() {
        super.onCreate();

        sAppComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .dbModule(new DbModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
