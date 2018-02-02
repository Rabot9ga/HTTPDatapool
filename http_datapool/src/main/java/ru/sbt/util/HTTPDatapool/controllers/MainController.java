package ru.sbt.util.HTTPDatapool.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbt.util.HTTPDatapool.datapool.Datapool;
import ru.sbt.util.HTTPDatapool.httpdto.DatapoolRequest;
import ru.sbt.util.HTTPDatapool.httpdto.DatapoolResponse;

@RestController
@RequestMapping("/api")
@Slf4j
public class MainController {

    @Autowired
    Datapool datapool;


    @PostMapping(value = "/getParameter")
    public ResponseEntity<DatapoolResponse> getParameter(@RequestBody DatapoolRequest datapoolRequest) {
        log.debug("Call /api/getParameter: {}", datapoolRequest);

        DatapoolResponse parameters = datapool.getParameters(datapoolRequest);

        log.debug("return parameters: {}", parameters);

        return ResponseEntity.ok(parameters);
    }
}
