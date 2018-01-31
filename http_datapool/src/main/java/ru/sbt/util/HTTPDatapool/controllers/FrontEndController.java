package ru.sbt.util.HTTPDatapool.controllers;

import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.bind.annotation.*;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;
import ru.sbt.util.HTTPDatapool.controllers.dto.MetricsContainer;
import ru.sbt.util.HTTPDatapool.controllers.dto.StatusContainer;
import ru.sbt.util.HTTPDatapool.datapool.Datapool;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/frontEnd/")
public class FrontEndController {

    @Autowired
    DBConnection dbConnection;

    @Autowired
    Datapool datapool;

    ThreadPoolExecutor service;

    @PostConstruct
    private void init(){
        service = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new CustomizableThreadFactory("FrontUpdateThread"));
        service.allowCoreThreadTimeOut(true);
    }

    private List<Map<String, String>> list = new ArrayList<>();

    /**
     * Receiving list of tables in cache
     *
     * @return all tables in cache
     */
    @GetMapping("/getTables")
    public ResponseEntity<List<Map<String, String>>> getTables() {

        list = dbConnection.getAllInfoAboutTablesInCache();
        log.debug("getTables was executed! list is {}", list);


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
        log.debug("clearCache was executed for table {}", tableName);

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
     * Get list of tables in DB available for downloading to cache
     *
     * @return rs object with list of tables in database
     */

    @GetMapping("/getTableListInDB")
    public ResponseEntity<List<String>> getTableListInDB() {
        log.debug("getTableListInDB was executed. Returned list is {}", dbConnection.getAllTableNamesInDB());
        return ResponseEntity.ok(dbConnection.getAllTableNamesInDB());
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

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        List<MetricsContainer> list = new ArrayList<>();
        if (processCpuLoad != -1)
            list.add(new MetricsContainer("General", "processCpuLoad", processCpuLoad + " %", "Утилизация CPU JVM"));
        if (systemCpuLoad != -1)
            list.add(new MetricsContainer("General", "systemCpuLoad", systemCpuLoad + " %", "Утилизация CPU системой"));
        if (systemLoadAverage != -1)
            list.add(new MetricsContainer("General", "systemLoadAverage", systemLoadAverage + " %", "-1 если не поддерживается"));
        list.add(new MetricsContainer("General", "ОС", "" + osBean.getName(), "Операционная система"));
        list.add(new MetricsContainer("General", "Архитектура", "" + osBean.getArch(), "Архитектура ОС"));
        list.add(new MetricsContainer("General", "availableProcessors", "" + osBean.getAvailableProcessors(), "Количество CPU"));

        list.add(new MetricsContainer("Heap_nonHeap", "totalPhysicalMemorySize", totalPhysicalMemorySize + " MB", "Общее количество физической памяти"));
        list.add(new MetricsContainer("Heap_nonHeap", "freePhysicalMemorySize", freePhysicalMemorySize + " MB", "Свободно физической памяти"));
        list.add(new MetricsContainer("Heap_nonHeap", "freeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB", "Доступно памяти для JVM"));

        list.add(new MetricsContainer("Heap_nonHeap", "Heap Max", memoryMXBean.getHeapMemoryUsage().getMax() / 1024 / 1024 + " MB", "Heap Max (Xmx)"));
        list.add(new MetricsContainer("Heap_nonHeap", "Heap Committed", memoryMXBean.getHeapMemoryUsage().getCommitted() / 1024 / 1024 + " MB", "Heap Committed"));
        list.add(new MetricsContainer("Heap_nonHeap", "Heap Used", memoryMXBean.getHeapMemoryUsage().getUsed() / 1024 / 1024 + " MB", "Heap Used"));
        list.add(new MetricsContainer("Heap_nonHeap", "nonHeap Committed", memoryMXBean.getNonHeapMemoryUsage().getCommitted() / 1024 / 1024 + " MB", "nonHeap Committed"));
        list.add(new MetricsContainer("Heap_nonHeap", "nonHeap Used", memoryMXBean.getNonHeapMemoryUsage().getUsed() / 1024 / 1024 + " MB", "nonHeap Used"));

        log.debug("getMetrics was executed");
        return ResponseEntity.ok(list);
    }
}
