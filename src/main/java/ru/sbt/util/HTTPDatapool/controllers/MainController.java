package ru.sbt.util.HTTPDatapool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbt.util.HTTPDatapool.httpapi.HTTPRequestParam;
import ru.sbt.util.HTTPDatapool.httpapi.ResponseParam;
import ru.sbt.util.HTTPDatapool.datapool.Datapool;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    Datapool datapool;


    @PostMapping("/init")
    public ResponseEntity<ResponseParam> getParameter(@RequestBody HTTPRequestParam requestParam) {

        ResponseParam responseParam;
        if (requestParam.getToken() == null) {
            responseParam = datapool.initParameter1(requestParam);
        } else {
            responseParam = datapool.getParameters1(requestParam);
        }

        return ResponseEntity.ok(responseParam);
    }
}
