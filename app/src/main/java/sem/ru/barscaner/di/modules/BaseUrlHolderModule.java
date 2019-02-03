package sem.ru.barscaner.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import sem.ru.barscaner.utils.BaseUrlHolder;

@Module
public class BaseUrlHolderModule {

    private BaseUrlHolder baseUrlHolder;

    @Provides
    @Singleton
    public BaseUrlHolder provideBaseUrlHolder() {
        return new BaseUrlHolder();
    }
}
