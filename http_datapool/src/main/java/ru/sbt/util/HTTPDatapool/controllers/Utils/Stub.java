package ru.sbt.util.HTTPDatapool.controllers.Utils;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.controllers.dto.AddingStatus;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Stub {
    //    public static List<StubTable> tables = new ArrayList<>(100);

    static AtomicInteger ai = new AtomicInteger();

    public static List<Map<String, String>> generateList(int count) {
        return Stream.generate(Stub::generateMap).limit(count).collect(Collectors.toList());
    }

    public static String getStatus(List<Map<String, String>> list, String name) {
        String status = list.stream()
                .filter(map -> map.get("name").equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no such table"))
                .get("status");

        return status;
    }

    public static void startTimer(Map<String, String> map, List<Map<String, String>> listToUpdate, int indexOfMap) {
        ExecutorService service = Executors.newFixedThreadPool(1);
        log.error("startTimer executed");

        service.submit(() -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    map.put("status", AddingStatus.READY.name());
                    listToUpdate.set(indexOfMap, map);
                    log.error("list has been updated!");
                }
            }, 10_000);
        });
    }

    static Map<String, String> generateMap() {

        HashMap<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(ai.getAndIncrement()));
        map.put("name", UUID.randomUUID().toString().replace("-", "").toUpperCase());
        map.put("rowCount", String.valueOf(ThreadLocalRandom.current().nextInt(100, 400)));
        map.put("status", AddingStatus.UPDATING.name());
        return map;
    }

}
