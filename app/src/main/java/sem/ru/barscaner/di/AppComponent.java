package sem.ru.barscaner.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import sem.ru.barscaner.db.SQLiteDB;
import sem.ru.barscaner.di.modules.ContextModule;
import sem.ru.barscaner.di.modules.DbModule;


@Singleton
@Component(modules = {ContextModule.class, DbModule.class})
public interface AppComponent {
	Context getContext();

	SQLiteDB getSqLiteDB();
	//void inject(LoginPresenter presenter);
}
