package ru.sbt.util.HTTPDatapool;

import ru.sbt.util.HTTPDatapool.httpapi.RequestType;
import ru.sbt.util.HTTPDatapool.httpparameter.HttpParameter;
import ru.sbt.util.HTTPDatapool.httpparameter.ParameterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HTTPDataPoolDDosilka {

    static AtomicInteger ai = new AtomicInteger();
    static AtomicInteger aiPrev = new AtomicInteger(ai.get());

    private static List<Integer> TPSes = new ArrayList<>(100_000);

    static int taskCount = 200;

    public static void main(String[] args) throws InterruptedException {

//        Callable<ParameterList> callable = () -> HttpParameter.getInstance("http://localhost:8080")
        Callable<ParameterList> callable = () -> HttpParameter.getInstance("http://10.247.97.189:8080")
                .addRequest("TABLE1", RequestType.RANDOM, "testScript123", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
//                .addRequest("TABLE2", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
//                .addRequest("TABLE3", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
                .getParameters();


        ThreadPoolExecutor LoaderService = (ThreadPoolExecutor) Executors.newFixedThreadPool(200);
        ScheduledExecutorService TPSCounterService = Executors.newSingleThreadScheduledExecutor();

        for (int i = 0; i < taskCount; i++) {
            LoaderService.submit(() -> {
                for (int j = 0; j < 10_000; j++) {
                    try {
                        callable.call().getRequestValue(0).get("COLUMN1");
                        ai.incrementAndGet();
//                        callable.call().getRequestValue(0).get("COLUMN2");
//                        callable.call().getRequestValue(0).get("COLUMN3");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        TPSCounterService.scheduleAtFixedRate(() -> {

            int i = ai.get();
            int TPS = i - aiPrev.get();
            TPSes.add(TPS);

            System.out.println(TPS + " TPS");
            aiPrev.set(i);

        }, 0, 1, TimeUnit.SECONDS);


        while (!LoaderService.isTerminated()) {
            if (LoaderService.getCompletedTaskCount() == taskCount) {
                LoaderService.shutdown();
                TPSCounterService.shutdown();
                CalculateStats(TPSes);
            }
//            System.out.println("LoaderService.getCompletedTaskCount() = " + LoaderService.getCompletedTaskCount());
            TimeUnit.SECONDS.sleep(2);
        }

    }

    private static void CalculateStats(List<Integer> tpSes) {
        Collections.sort(tpSes);
        double median = tpSes.get(tpSes.size() / 2);
        if (tpSes.size() % 2 == 0)
            median = (median + tpSes.get(tpSes.size() / 2 - 1)) / 2;

        System.out.println("=========== STATISTICS ============");

        System.out.println("Max TPS:    " + tpSes.stream().reduce(Integer::max).get());
        System.out.println("Min TPS:    " + tpSes.stream().reduce(Integer::min).get());
        System.out.println("Avg TPS:    " + tpSes.stream().mapToInt(i -> i).average().getAsDouble());
        System.out.println("Median TPS: " + median);
    }
}
