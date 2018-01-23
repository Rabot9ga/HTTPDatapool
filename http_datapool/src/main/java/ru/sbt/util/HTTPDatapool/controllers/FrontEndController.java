package ru.sbt.util.HTTPDatapool.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sbt.util.HTTPDatapool.connectionInterface.DataRepository;
import ru.sbt.util.HTTPDatapool.controllers.dto.StatusContainer;
import ru.sbt.util.HTTPDatapool.datapool.Datapool;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/frontEnd/")
public class FrontEndController {

    @Autowired
    DataRepository dataRepository;

    @Autowired
    Datapool datapool;

    ExecutorService service = Executors.newFixedThreadPool(3);

//    {
//        HashSet<String> columnNames = new HashSet<>(Arrays.asList("*"));
//        List<Map<String, String>> result = dataRepository.getDataFromCache("FORTEST", columnNames);
//    }


    //    private List<Map<String, String>> list = Stub.generateList(3);
//    private List<Map<String, String>> list = dataRepository.getAllInfoAboutTablesInCache();
    private List<Map<String, String>> list = new ArrayList<>();

    /**
     * Receiving list of tables in cache
     *
     * @return all tables in cache
     */
    @GetMapping("/getTables")
    public ResponseEntity<List<Map<String, String>>> getTables() {

        list = dataRepository.getAllInfoAboutTablesInCache();
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

        dataRepository.clearCache(tableName);
        datapool.clearCache(tableName);

        list = dataRepository.getAllInfoAboutTablesInCache();
//        list.remove(indexToDelete.get());

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

//        int newTableSize = ThreadLocalRandom.current().nextInt(1000);
//        HashMap<String, String> map = new HashMap<>();
//        map.put("id", String.valueOf(list.size() + 1));
//        map.put("name", tableName);
//        map.put("rowCount", String.valueOf(newTableSize));
//        map.put("status", AddingStatus.UPDATING.name());
//        map.put("progress", "0.0");
//        list.add(map);
//        Stub.startTimer(map, list, list.indexOf(map));

        service.submit(() -> dataRepository.getDataFromCache(tableName, new HashSet<>(Collections.singletonList("*"))));

        list = dataRepository.getAllInfoAboutTablesInCache();

        return ResponseEntity.ok(list);
    }

    @PostMapping("/getStatus")
    public ResponseEntity<StatusContainer> getStatus(@RequestBody String tableName) {
//        StatusContainer status = Stub.getStatus(list, tableName);
        StatusContainer status;

        double loadedPercent = dataRepository.getLoadedPercent(tableName);

        if (loadedPercent == 100) {
            status = new StatusContainer("READY", Double.toString(loadedPercent));
        } else {
            status = new StatusContainer("UPDATING", Double.toString(loadedPercent));
        }

        log.debug("getStatus was executed for table {}. Status is {}, percent is {}", tableName, status.getStatus(), status.getPercent());
        return ResponseEntity.ok(status);
    }
}
