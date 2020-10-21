package demo;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

import java.util.concurrent.CompletableFuture;

public final class SimpleOneSecRfServiceNoCall {
  public static final String API_URL = "http://localhost:8080";

  public interface OneSecService {
    @GET("/one")
    String one();
  }

  static Retrofit retrofit =
          new Retrofit.Builder()
                  .baseUrl(API_URL)
                  .addConverterFactory(ScalarsConverterFactory.create())
                  .build();

  static SimpleOneSecRfServiceNoCall.OneSecService oneSec = retrofit.create(SimpleOneSecRfServiceNoCall.OneSecService.class);
}
