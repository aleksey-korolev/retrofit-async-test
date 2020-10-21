package demo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.concurrent.CompletableFuture;

public class RfCallbackToCompletableFuture<T> implements Callback<T> {

    private CompletableFuture<Response<T>> cf;

    public RfCallbackToCompletableFuture(CompletableFuture<Response<T>> cf) {
        this.cf = cf;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        cf.complete(response);
    }


    @Override
    public void onFailure(Call<T> call, Throwable t) {
        cf.completeExceptionally(t);
    }
}
