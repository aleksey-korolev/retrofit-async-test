package demo;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class DemoController {

    private int counter = 0;

    ForkJoinPool fjPoolP200 = new ForkJoinPool(200);
    ForkJoinPool fjPoolP400 = new ForkJoinPool(400);

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<String> emptyQuery() {
        return ResponseEntity.ok("Success");
    }

    @GetMapping(value = "/one", produces = "application/json")
    public ResponseEntity<String> oneSecQuery() throws Exception {

        Thread.currentThread().sleep(1000);
        return ResponseEntity.ok(String.valueOf(++counter));
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
        }, fjPoolP400);

        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            try {
                return SimpleOneSecRfService.call.clone().execute().body();
            } catch (IOException e) {
                return (e.getMessage());
            }
        }, fjPoolP400);

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(completableFuture, completableFuture2);

        // ToDo: wait for either of CFs and process response, then wait for the remaining response and process it too
        combinedFuture.get();

        String combinedResult = Stream.of(completableFuture, completableFuture2)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));

        return combinedResult;
    }


    @GetMapping(value = "/rfcfAsync2Either", produces = "application/json")
    @Async
    public String retrofitCFQueryAsync2Either() throws Exception {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return "1: " + SimpleOneSecRfService.call.clone().execute().body();
            } catch (IOException e) {
                return (e.getMessage());
            }
        }, fjPoolP400);

        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            try {
                return "2: " + SimpleOneSecRfService.call.clone().execute().body();
            } catch (IOException e) {
                return (e.getMessage());
            }
        }, fjPoolP400);

//        CompletableFuture<Void> combinedFuture = completableFuture.acceptEither(completableFuture2, r -> {System.out.println(r);});
        CompletableFuture<Void> combinedFuture = completableFuture.thenAcceptAsync(r -> {System.out.println(r);});
        CompletableFuture<Void> combinedFuture2 = completableFuture2.thenAcceptAsync(r -> {System.out.println(r);});

        String result = "";
        return result;
    }

    @GetMapping(value = "/rfcf3", produces = "application/json")
    public String retrofitCFQuery3() throws Exception {
        CompletableFuture<Response<String>> completableFuture = new CompletableFuture<>();
        SimpleOneSecRfService.call.clone().enqueue(new RfCallbackToCompletableFuture<>(completableFuture));

        CompletableFuture<Response<String>> completableFuture2 = new CompletableFuture<>();
        SimpleOneSecRfService.call.clone().enqueue(new RfCallbackToCompletableFuture<>(completableFuture2));

//        String result = completableFuture.handle((response, failure) -> {return response.body();}).get();
//        String result2 = completableFuture2.handle((response, failure) -> {return response.body();}).get();
//        return result + result2;



        System.out.println(SimpleOneSecRfService.okHttpClient.connectionPool().connectionCount());

        return "Success";
    }

    @GetMapping(value = "/rfrx", produces = "application/json")
    public String retrofitRxJava() throws Exception {
        PublishSubject publishSubject = PublishSubject.create();
        publishSubject.subscribe(System.out::println);
        Observable<String> observable = SimpleOneSecRfServiceWithObservable.oneSec.one();
        Observable<String> observable2 = SimpleOneSecRfServiceWithObservable.oneSec.one();
        observable.subscribe(publishSubject);
        observable2.subscribe(publishSubject);
        return "Success";
    }

    @GetMapping(value = "/rfArmeria", produces = "application/json")
    public String retrofitArmeria() throws Exception {
        CompletableFuture<String> completableFuture = SimpleOneSecRfArmeriaServiceWithCF.oneSec.one();
//        String result = completableFuture.get();
//        return result;
        return "Armeria";
    }
}