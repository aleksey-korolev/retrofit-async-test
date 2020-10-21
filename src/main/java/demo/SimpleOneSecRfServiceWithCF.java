package demo;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

import java.util.concurrent.CompletableFuture;

public final class SimpleOneSecRfServiceWithCF {
  public static final String API_URL = "http://localhost:8080";

  public interface OneSecService {
    @GET("/one")
    CompletableFuture<String> one();
  }

  // OkHttpClient. Be conscious with the order
  static OkHttpClient okHttpClient = new OkHttpClient()
          .newBuilder()
          .build();

  static Retrofit retrofit =
          new Retrofit.Builder()
//                  .client(okHttpClient)
                  .baseUrl(API_URL)
                  .addConverterFactory(ScalarsConverterFactory.create())
                  .build();

  static SimpleOneSecRfServiceWithCF.OneSecService oneSec = SimpleOneSecRfServiceWithCF.retrofit.create(
          SimpleOneSecRfServiceWithCF.OneSecService.class);

}
