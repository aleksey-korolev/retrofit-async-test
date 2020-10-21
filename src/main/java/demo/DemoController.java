package demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@RestController
public class DemoController {

    ForkJoinPool fjPool = new ForkJoinPool(200);

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<String> emptyQuery() {
//    ForkJoinPool<String> emptyQuery() {

        System.out.println("Test");
        return ResponseEntity.ok("Success");
    }

    @GetMapping(value = "/one", produces = "application/json")
    public ResponseEntity<String> oneSecQuery() throws Exception {

        Thread.currentThread().sleep(1000);
//        return ResponseEntity.ok("{\"Success\": \"yes\"}");
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

    @GetMapping(value = "/rfcf", produces = "application/json")
    public String retrofitCFQuery() throws Exception {
//        System.out.println("In Servlet: " + Thread.currentThread());
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
//                System.out.println("In CompletableFuture: " + Thread.currentThread());
                return SimpleOneSecRfService.call.clone().execute().body();
            } catch (IOException e) {
                return (e.getMessage());
            }
        });
//        }, fjPool);

        while(!completableFuture.isDone()) {

        }
//        String result = completableFuture.get();
//        return result;
        return "fake result";
    }

    @GetMapping(value = "/rfcf2", produces = "application/json")
    public String retrofitCFQuery2() throws Exception {
        CompletableFuture<String> completableFuture = SimpleOneSecRfServiceWithCF.oneSec.one();
        String result = completableFuture.get();
        return result;
    }

    @GetMapping(value = "/rfcf3", produces = "application/json")
    public String retrofitCFQuery3() throws Exception {
        System.out.println("In Servlet: " + Thread.currentThread());
        CompletableFuture<Response<String>> completableFuture = new CompletableFuture<>();

        SimpleOneSecRfService.call.enqueue(new RfCallbackToCompletableFuture<>(completableFuture));
        String result = completableFuture.handle((response, failure) -> {return null;});
        return result;
    }
}