package sem.ru.barscaner.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;
import sem.ru.barscaner.db.SQLiteDB;
import sem.ru.barscaner.di.modules.ApiModule;
import sem.ru.barscaner.di.modules.ContextModule;
import sem.ru.barscaner.di.modules.DbModule;
import sem.ru.barscaner.mvp.presenter.ScanPresenter;


@Singleton
@Component(modules = {ContextModule.class, DbModule.class, ApiModule.class})
public interface AppComponent {
	Context getContext();
	Retrofit getRetrofit();

	SQLiteDB getSqLiteDB();
	void inject(ScanPresenter presenter);
}
