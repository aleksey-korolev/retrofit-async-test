package demo;

import com.linecorp.armeria.client.retrofit2.ArmeriaRetrofit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

import java.util.concurrent.CompletableFuture;

public final class SimpleOneSecRfArmeriaServiceWithCF {
  public static final String API_URL = "http://127.0.0.1:8080/";

  public interface OneSecService {
    @GET("/one")
    CompletableFuture<String> one();
  }

  static Retrofit retrofit = ArmeriaRetrofit.builder(API_URL)
          .addConverterFactory(ScalarsConverterFactory.create())
          .build();

  static SimpleOneSecRfArmeriaServiceWithCF.OneSecService oneSec = SimpleOneSecRfArmeriaServiceWithCF.retrofit.create(
          SimpleOneSecRfArmeriaServiceWithCF.OneSecService.class);

}
