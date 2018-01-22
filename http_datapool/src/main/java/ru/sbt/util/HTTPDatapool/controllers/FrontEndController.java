package ru.sbt.util.HTTPDatapool.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sbt.util.HTTPDatapool.controllers.Utils.Stub;
import ru.sbt.util.HTTPDatapool.controllers.dto.AddingStatus;
import ru.sbt.util.HTTPDatapool.controllers.dto.StatusContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/frontEnd/")
public class FrontEndController {

//    @Autowired
//    Datapool datapool;


    private List<Map<String, String>> list = Stub.generateList(3);

    /**
     * Receiving list of tables in cache
     *
     * @return all tables in cache
     */
    @GetMapping("/getTables")
    public ResponseEntity<List<Map<String, String>>> getTables() {

        log.info("getTables was executed!");
        log.info("list is {}", list);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
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

        list.remove(indexToDelete.get());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/addTable")
    public ResponseEntity<List<Map<String, String>>> addTable(@RequestBody String tableName) {
        log.info("addTable was executed for table {}", tableName);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);

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
            return new ResponseEntity("Table exists", httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        int newTableSize = ThreadLocalRandom.current().nextInt(1000);
        HashMap<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(list.size() + 1));
        map.put("name", tableName);
        map.put("rowCount", String.valueOf(newTableSize));
        map.put("status", AddingStatus.UPDATING.name());
        map.put("progress", "0.0");

        list.add(map);

        Stub.startTimer(map, list, list.indexOf(map));


        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/getStatus")
    public ResponseEntity<StatusContainer> getStatus(@RequestBody String tableName) {
        log.info("getStatus was executed for table {}", tableName);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);

        StatusContainer status = Stub.getStatus(list, tableName);

        log.info("Status is {}, percent is {}", status.getStatus(), status.getPercent());

        return new ResponseEntity(status, httpHeaders, HttpStatus.OK);
    }
}
