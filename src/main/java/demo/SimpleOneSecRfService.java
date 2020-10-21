package demo;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;
import java.util.concurrent.TimeUnit;

public final class SimpleOneSecRfService {
  public static final String API_URL = "http://localhost:8080";

  public interface OneSecService {
    @GET("/one")
    Call<String> one();
  }

  // OkHttpClient. Be conscious with the order
//  static OkHttpClient okHttpClient = new OkHttpClient()
//          .newBuilder()
//          .connectionPool(new ConnectionPool(200,5L, TimeUnit.MINUTES))
//          .build();

  static Retrofit retrofit =
          new Retrofit.Builder()
//                  .client(okHttpClient)
                  .baseUrl(API_URL)
//                  .addConverterFactory(GsonConverterFactory.create())
                  .addConverterFactory(ScalarsConverterFactory.create())
                  .build();

  // Create an instance of our GitHub API interface.
  static OneSecService oneSec = retrofit.create(OneSecService.class);

  // Create a call instance for looking up Retrofit contributors.
  static Call<String> call = oneSec.one();
}
