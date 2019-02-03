package sem.ru.barscaner.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;
import sem.ru.barscaner.api.MainService;
import sem.ru.barscaner.db.SQLiteDB;
import sem.ru.barscaner.di.modules.ApiModule;
import sem.ru.barscaner.di.modules.BaseUrlHolderModule;
import sem.ru.barscaner.di.modules.ContextModule;
import sem.ru.barscaner.di.modules.DbModule;
import sem.ru.barscaner.utils.BaseUrlHolder;


@Singleton
@Component(modules = {ContextModule.class, DbModule.class, ApiModule.class,
		BaseUrlHolderModule.class})
public interface AppComponent {
	Context getContext();
	Retrofit getRetrofit();
	BaseUrlHolder getBaseUrlHolder();
	MainService getMainService();

	SQLiteDB getSqLiteDB();
	//void inject(ScanPresenter presenter);
}
