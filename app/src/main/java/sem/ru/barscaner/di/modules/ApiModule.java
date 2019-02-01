package sem.ru.barscaner.di.modules;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import sem.ru.barscaner.api.MainService;


@Module (includes = {RetrofitModule.class})
public class ApiModule {


    @Provides
    @Singleton
    public MainService provideMainService(Retrofit retrofit){
        return retrofit.create(MainService.class);
    }
}
