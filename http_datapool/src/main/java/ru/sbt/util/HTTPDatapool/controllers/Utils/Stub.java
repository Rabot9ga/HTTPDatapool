package ru.sbt.util.HTTPDatapool.controllers.Utils;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.controllers.dto.AddingStatus;
import ru.sbt.util.HTTPDatapool.controllers.dto.StatusContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Stub {
    static AtomicInteger ai = new AtomicInteger();

    public static List<Map<String, String>> generateList(int count) {
        return Stream.generate(Stub::generateMap).limit(count).collect(Collectors.toList());
    }

    public static StatusContainer getStatus(List<Map<String, String>> list, String name) {
        StatusContainer status = list.stream()
                .filter(map -> map.get("name").equals(name))
                .findFirst()
                .map(Stub::wrapWithStatusContainer)
                .orElseThrow(() -> new RuntimeException("no such table"));
        return status;
    }

    private static StatusContainer wrapWithStatusContainer(Map<String, String> map) {
        return new StatusContainer(map.get("status"), map.get("progress"));
    }

    public static void startTimer(Map<String, String> map, List<Map<String, String>> listToUpdate, int indexOfMap) {
//        ExecutorService service = Executors.newFixedThreadPool(1);
        ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(10);
        ScheduledFuture<?> future = null;
        log.error("startTimer executed");

//        service.submit(() -> {
//            int counter = 0;
//
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    map.put("status", AddingStatus.READY.name());
//                    listToUpdate.set(indexOfMap, map);
//                    log.error("list has been updated!");
//                }
//            }, 5_000);
//        });
//
        ScheduledFuture<?> finalFuture = future;
        class StubTask implements Runnable {
            int delay;
            int counter;

            public StubTask(int delay) {
                this.delay = delay;
            }

            @Override
            public void run() {
                if (counter == delay) {
                    map.put("status", AddingStatus.READY.name());
                    map.put("progress", "100");
                    finalFuture.cancel(true);
                } else {
                    map.put("progress", String.valueOf(counter / 100));
                }

                counter += 1_000;

                listToUpdate.set(indexOfMap, map);
//                log.error("list has been updated!");
                log.error("List is {}" + listToUpdate);
            }
        }

        future = service.scheduleAtFixedRate(new StubTask(10_000), 0, 1, TimeUnit.SECONDS);

//        service.submit(() -> {
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    map.put("status", AddingStatus.READY.name());
//                    map.put("progress", "100");
//                    listToUpdate.set(indexOfMap, map);
//                    log.error("list has been updated!");
//                    log.error("List is {}" + listToUpdate);
//                }
//            }, 5_000);
//        });
    }

    static Map<String, String> generateMap() {

        HashMap<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(ai.getAndIncrement()));
        map.put("name", UUID.randomUUID().toString().replace("-", "").toUpperCase());
        map.put("rowCount", String.valueOf(ThreadLocalRandom.current().nextInt(100, 100_000_000)));
        map.put("status", AddingStatus.READY.name());
        map.put("progress", "100");
        return map;
    }

}
