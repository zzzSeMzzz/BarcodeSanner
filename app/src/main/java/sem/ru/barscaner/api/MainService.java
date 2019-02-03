package sem.ru.barscaner.api;


import javax.inject.Singleton;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;
import sem.ru.barscaner.mvp.model.BaseRespone;

/**
 * Created by SeM on 02.10.2017.
 */

@Singleton
public interface MainService {


    @Multipart
    @POST()
    Observable<BaseRespone> sendFile(
            @Header("Authorization") String token,
            @Part MultipartBody.Part[] files,
            @Part("code") RequestBody requestBody,
            @Url String url
    );

}
