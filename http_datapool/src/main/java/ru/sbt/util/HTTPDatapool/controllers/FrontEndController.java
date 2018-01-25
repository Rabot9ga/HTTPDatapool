package ru.sbt.util.HTTPDatapool.controllers;

import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;
import ru.sbt.util.HTTPDatapool.controllers.dto.MetricsContainer;
import ru.sbt.util.HTTPDatapool.controllers.dto.StatusContainer;
import ru.sbt.util.HTTPDatapool.datapool.Datapool;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/frontEnd/")
public class FrontEndController {

    @Autowired
    DBConnection dbConnection;

    @Autowired
    Datapool datapool;

    ExecutorService service = Executors.newFixedThreadPool(3);

    private List<Map<String, String>> list = new ArrayList<>();

    /**
     * Receiving list of tables in cache
     *
     * @return all tables in cache
     */
    @GetMapping("/getTables")
    public ResponseEntity<List<Map<String, String>>> getTables() {

        list = dbConnection.getAllInfoAboutTablesInCache();
        log.info("getTables was executed! list is {}", list);


        return ResponseEntity.ok(list);
    }

    /**
     * Clearing cache of specific table
     *
     * @param tableName - name of table which should be cleared
     * @return all tables in cache after delete
     */
    @PostMapping("/clearTable")
    public ResponseEntity<List<Map<String, String>>> clearCache(@RequestBody String tableName) {
        log.info("clearCache was executed for table {}", tableName);

        //AtomicInteger is used to be wrapper over int. Otherwise it is impossible to change local variable from lambda
        AtomicInteger indexToDelete = new AtomicInteger();

        list.forEach(stringStringMap -> {
            if (stringStringMap.get("name").equals(tableName)) {
                indexToDelete.set(list.indexOf(stringStringMap));
            }
        });

        dbConnection.clearCache(tableName);
        datapool.clearCache(tableName);

        list = dbConnection.getAllInfoAboutTablesInCache();

        return ResponseEntity.ok(list);
    }

    @PostMapping("/addTable")
    public ResponseEntity<List<Map<String, String>>> addTable(@RequestBody String tableName) {
        log.debug("addTable was executed for table {}", tableName);

        /*
            1) Looking for existing tables with the same name
            2) Adding table to cache
            3) Requesting its rowCount
         */

        boolean tableExists = list.stream()
                .map(map -> map.get("name"))
                .anyMatch(s -> s.equals(tableName));

        if (tableExists) {
            log.error("trying to save existing table with name {}", tableName);
            return new ResponseEntity("Table already exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        service.submit(() -> dbConnection.getDataFromCache(tableName, new HashSet<>(Collections.singletonList("*"))));

        list = dbConnection.getAllInfoAboutTablesInCache();

        return ResponseEntity.ok(list);
    }

    /**
     * Get status for specific table
     *
     * @param tableName - name of required table
     * @return rs object with StatusContainer storing required data
     */

    @PostMapping("/getStatus")
    public ResponseEntity<StatusContainer> getStatus(@RequestBody String tableName) {

        StatusContainer status;
        double loadedPercent = dbConnection.getLoadedPercent(tableName);

        if (loadedPercent == 100) {
            status = new StatusContainer("READY", Double.toString(loadedPercent));
        } else {
            status = new StatusContainer("UPDATING", Double.toString(loadedPercent));
        }

        log.debug("getStatus was executed for table {}. Status is {}, percent is {}", tableName, status.getStatus(), status.getPercent());
        return ResponseEntity.ok(status);
    }

    /**
     * Get status for specific table
     *
     * @return rs object with StatusContainer storing required data
     */

    @GetMapping("/getMetrics")
    public ResponseEntity<List<MetricsContainer>> getMetrics() {

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double processCpuLoad = new BigDecimal(osBean.getProcessCpuLoad() * 100).setScale(2, RoundingMode.UP).doubleValue();
        double systemCpuLoad = new BigDecimal(osBean.getSystemCpuLoad() * 100).setScale(2, RoundingMode.UP).doubleValue();
        double systemLoadAverage = new BigDecimal(osBean.getSystemLoadAverage()).setScale(2, RoundingMode.UP).doubleValue();

        double totalPhysicalMemorySize = osBean.getTotalPhysicalMemorySize() / 1024 / 1024;
        double freePhysicalMemorySize = osBean.getFreePhysicalMemorySize() / 1024 / 1024;

        int availableProcessors = osBean.getAvailableProcessors();
        String name = osBean.getName();
        String arch = osBean.getArch();

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        List<MetricsContainer> list = new ArrayList<>();
        if (processCpuLoad != -1)
            list.add(new MetricsContainer("processCpuLoad", processCpuLoad + " %", "Утилизация CPU JVM"));
        if (systemCpuLoad != -1)
            list.add(new MetricsContainer("systemCpuLoad", systemCpuLoad + " %", "Утилизация CPU системой"));

        if (systemLoadAverage != -1)
            list.add(new MetricsContainer("systemLoadAverage", systemLoadAverage + " %", "-1 если не поддерживается"));

        list.add(new MetricsContainer("totalPhysicalMemorySize", totalPhysicalMemorySize + " MB", "Общее количество физической памяти"));
        list.add(new MetricsContainer("freePhysicalMemorySize", freePhysicalMemorySize + " MB", "Свободно физической памяти"));
        list.add(new MetricsContainer("Xmx", Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB", "СЮДА НЕ СМОТРИМ"));
        list.add(new MetricsContainer("freeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB", "Доступно памяти для JVM"));

        list.add(new MetricsContainer("", "" , ""));
        list.add(new MetricsContainer("Heap Max", memoryMXBean.getHeapMemoryUsage().getMax() / 1024 / 1024 + " MB" , ""));
        list.add(new MetricsContainer("Heap Committed", memoryMXBean.getHeapMemoryUsage().getCommitted() / 1024 / 1024 + " MB" , ""));
        list.add(new MetricsContainer("Heap Used", memoryMXBean.getHeapMemoryUsage().getUsed() / 1024 / 1024 + " MB" , ""));
        list.add(new MetricsContainer("nonHeap Committed", memoryMXBean.getNonHeapMemoryUsage().getCommitted() / 1024 / 1024 + " MB" , ""));
        list.add(new MetricsContainer("nonHeap Used", memoryMXBean.getNonHeapMemoryUsage().getUsed() / 1024 / 1024 + " MB" , ""));
        list.add(new MetricsContainer("", "" , ""));

        list.add(new MetricsContainer("ОС", "" + name, ""));
        list.add(new MetricsContainer("Архитектура", "" + arch, ""));
        list.add(new MetricsContainer("availableProcessors", "" + availableProcessors, "Количество CPU"));



        log.debug("getMetrics was executed");
        return ResponseEntity.ok(list);
    }
}
