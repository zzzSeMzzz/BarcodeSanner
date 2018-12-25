package sem.ru.barscaner.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import sem.ru.barscaner.db.SQLiteDB;


@Module
public class DbModule {
	private SQLiteDB sqLiteDB;

	public DbModule(Context context) {
		sqLiteDB = new SQLiteDB(context);
	}

	@Provides
	@Singleton
	public SQLiteDB provideSQLiteDB() {
		return sqLiteDB;
	}
}
