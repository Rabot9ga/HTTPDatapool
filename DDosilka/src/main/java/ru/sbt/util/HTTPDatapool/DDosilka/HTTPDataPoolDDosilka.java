package ru.sbt.util.HTTPDatapool.DDosilka;

import ru.sbt.util.HTTPDatapool.httpdto.RequestType;
import ru.sbt.util.HTTPDatapool.httpparameter.HttpParameter;
import ru.sbt.util.HTTPDatapool.httpparameter.ParameterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Script for loading HTTPDataPool application with specified number of threads and requests located on specified host
 *
 * Usage:
 *
 * 1) Build jar
 * 2) java -jar DDosilka-<version> <hostname> <number of Threads> <number of requests for every Thread>
 *
 */

public class HTTPDataPoolDDosilka {

    static AtomicInteger ai = new AtomicInteger();
    static AtomicInteger aiPrev = new AtomicInteger(ai.get());

    private static List<Integer> TPSes = new ArrayList<>(1000);



    public static void main(String[] args) throws InterruptedException {

        System.out.println("Requesting host: " + args[0] + " with " + args[1] + " threads. Tasks per Thread: " + args[2]);
        System.out.println("Requesting Table: " + args[3] + ". Requested column is " + args[4]);

        int taskCount = Integer.parseInt(args[1]);

        Callable<ParameterList> callable = () -> HttpParameter.getInstance("http://" + args[0] + ":8080")
                .addRequest(args[3], RequestType.RANDOM, "testScript123", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
//                .addRequest("TABLE2", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
//                .addRequest("TABLE3", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
                .getParameters();


        ThreadPoolExecutor LoaderService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        ScheduledExecutorService TPSCounterService = Executors.newSingleThreadScheduledExecutor();

        int tasks = Integer.parseInt(args[2]);

        for (int i = 0; i < taskCount; i++) {
            LoaderService.submit(() -> {
                for (int j = 0; j < tasks; j++) {
                    try {
                        callable.call().getRequestValue(0).get(args[4]);
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
