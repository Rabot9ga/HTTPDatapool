package ru.sbt.util.HTTPDatapool.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/api/frontEnd/")
public class FrontEndController {

//    @Autowired
//    Datapool datapool;


    private List<Map<String, String>> list = Stub.generateList(4);

    /**
     * Receiving list of tables in cache
     *
     * @return all tables in cache
     */
    @GetMapping("/getTables")
    public ResponseEntity<List<Map<String, String>>> getTables() {

        log.info("getTables was executed!");

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


        /*
            1) Adding table to cache
            2) Requesting its rowCount
         */

        int newTableSize = ThreadLocalRandom.current().nextInt(1000);

        HashMap<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(list.size()+1));
        map.put("name", tableName);
        map.put("RowCount", String.valueOf(newTableSize));

        list.add(map);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    static class Stub {
        static AtomicInteger ai = new AtomicInteger();

        public static List<Map<String, String>> generateList(int count) {
            return Stream.generate(Stub::generateMap).limit(count).collect(Collectors.toList());
        }

        public static Map<String, String> generateMap() {

            HashMap<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(ai.getAndIncrement()));
            map.put("name", UUID.randomUUID().toString().replace("-", "").toUpperCase());
            map.put("rowCount", String.valueOf(ThreadLocalRandom.current().nextInt(100, 400)));
            return map;
        }
    }
}
