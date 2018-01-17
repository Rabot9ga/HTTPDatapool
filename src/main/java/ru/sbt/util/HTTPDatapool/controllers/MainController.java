package ru.sbt.util.HTTPDatapool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbt.util.HTTPDatapool.datapool.Datapool;
import ru.sbt.util.HTTPDatapool.httpapi.HTTPRequestParam;
import ru.sbt.util.HTTPDatapool.httpapi.HTTPResponseParam;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    Datapool datapool;


    @PostMapping("/getParameter")
    public ResponseEntity<HTTPResponseParam> getParameter(@RequestBody HTTPRequestParam requestParam) {

        HTTPResponseParam parameters = datapool.getParameters(requestParam);

        return ResponseEntity.ok(parameters);
    }

    @PostMapping("/clearCache")
    public ResponseEntity<Void> clearCache(@RequestBody HTTPRequestParam requestParam) {



        return ResponseEntity.ok(null);
    }

}
