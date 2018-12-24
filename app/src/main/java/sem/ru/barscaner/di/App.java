package sem.ru.barscaner.di;

import android.app.Application;

import sem.ru.barscaner.di.modules.ContextModule;


public class App extends Application {

    private static AppComponent sAppComponent;
    private static final String TAG = "Application";


    @Override
    public void onCreate() {
        super.onCreate();

        sAppComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
