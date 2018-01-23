package ru.sbt.util.HTTPDatapool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
