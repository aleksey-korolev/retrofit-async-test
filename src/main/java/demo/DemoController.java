package demo;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class DemoController {

    ForkJoinPool fjPoolP200 = new ForkJoinPool(200);
    ForkJoinPool fjPoolP400 = new ForkJoinPool(400);

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<String> emptyQuery() {
        return ResponseEntity.ok("Success");
    }

    @GetMapping(value = "/one", produces = "application/json")
    public ResponseEntity<String> oneSecQuery() throws Exception {

        Thread.currentThread().sleep(1000);
        return ResponseEntity.ok("Success");
    }

    @GetMapping(value = "/rf", produces = "application/json")
    public ResponseEntity<String> retrofitQuery() throws IOException {
        String resuslt = SimpleOneSecRfService.call.clone().execute().body();
        return ResponseEntity.ok(resuslt);
    }

    @GetMapping(value = "/rfasync", produces = "application/json")
    public ResponseEntity<String> retrofitAsyncQuery() throws IOException {
        SimpleOneSecRfService.call.clone().enqueue(
                new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        System.out.println("Response received");
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        System.err.println(t);
                    }
                }
        );
        return ResponseEntity.ok("Success");
    }


    @GetMapping(value = "/rfcfSync", produces = "application/json")
    public String retrofitCFQuerySync() throws Exception {
        CompletableFuture<String> completableFuture = SimpleOneSecRfServiceWithCF.oneSec.one();
        String result = completableFuture.get();
        return result;
    }

    @GetMapping(value = "/rfcfAsync", produces = "application/json")
    @Async
    public String retrofitCFAsync() throws Exception {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return SimpleOneSecRfService.call.clone().execute().body();
            } catch (IOException e) {
                return (e.getMessage());
            }
        }, fjPoolP200);

        String result = completableFuture.get();
        return result;
    }

    @GetMapping(value = "/rfcfAsync2", produces = "application/json")
    @Async
    public String retrofitCFQueryAsync2() throws Exception {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return SimpleOneSecRfService.call.clone().execute().body();
            } catch (IOException e) {
                return (e.getMessage());
            }
        }, fjPoolP200);

        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            try {
                return SimpleOneSecRfService.call.clone().execute().body();
            } catch (IOException e) {
                return (e.getMessage());
            }
        }, fjPoolP200);

        // ToDo: instead of waiting for completableFuture result, run both CFs in parallel and wait for completion of both
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(completableFuture, completableFuture2);

        combinedFuture.get();

        String combinedResult = Stream.of(completableFuture, completableFuture2)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));

        return combinedResult;
    }

    @GetMapping(value = "/rfcf3", produces = "application/json")
    public String retrofitCFQuery3() throws Exception {
        System.out.println("In Servlet: " + Thread.currentThread());
        CompletableFuture<Response<String>> completableFuture = new CompletableFuture<>();
        SimpleOneSecRfService.call.clone().enqueue(new RfCallbackToCompletableFuture<>(completableFuture));

        CompletableFuture<Response<String>> completableFuture2 = new CompletableFuture<>();
        SimpleOneSecRfService.call.clone().enqueue(new RfCallbackToCompletableFuture<>(completableFuture2));

        // ToDo: combine and wait for the slowest task to complete
//        String result = completableFuture.handle((response, failure) -> {return response.body();}).get();
//        String result2 = completableFuture2.handle((response, failure) -> {return response.body();}).get();
//        return result + result2;

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(completableFuture, completableFuture2);

        combinedFuture.get();

        return "Success2";
    }
}