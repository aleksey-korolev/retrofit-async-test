package demo;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import rx.Observable;

import java.util.concurrent.CompletableFuture;

public final class SimpleOneSecRfServiceWithObservable {
  public static final String API_URL = "http://localhost:8080";

  public interface OneSecService {
    @GET("/one")
    Observable<String> one();
  }

  static Retrofit retrofit =
          new Retrofit.Builder()
                  .baseUrl(API_URL)
                  .addConverterFactory(ScalarsConverterFactory.create())
                  .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                  .build();

  static SimpleOneSecRfServiceWithObservable.OneSecService oneSec = SimpleOneSecRfServiceWithObservable.retrofit.create(
          SimpleOneSecRfServiceWithObservable.OneSecService.class);

}
