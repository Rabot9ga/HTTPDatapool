package ru.sbt.util.HTTPDatapool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sbt.util.HTTPDatapool.datapool.Datapool;
import ru.sbt.util.HTTPDatapool.httpapi.DatapoolRequest;
import ru.sbt.util.HTTPDatapool.httpapi.DatapoolResponse;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    Datapool datapool;


    @PostMapping(value = "/getParameter")
    public ResponseEntity<DatapoolResponse> getParameter(@RequestBody DatapoolRequest datapoolRequest) {

        DatapoolResponse parameters = datapool.getParameters(datapoolRequest);

        return ResponseEntity.ok(parameters);
    }

    @GetMapping("/clearCache")
    public ResponseEntity<Void> clearCache() {

        datapool.clearCache();
        return ResponseEntity.ok(null);
    }



}
