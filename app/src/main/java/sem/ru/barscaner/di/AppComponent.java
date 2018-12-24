package sem.ru.barscaner.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import sem.ru.barscaner.di.modules.ContextModule;


@Singleton
@Component(modules = {ContextModule.class})
public interface AppComponent {
	Context getContext();

	//void inject(LoginPresenter presenter);

}
