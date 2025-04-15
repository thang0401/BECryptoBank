package com.cryptobank.backend.controller;

import com.cryptobank.backend.utils.Xid;
import java.sql.Timestamp;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/id")
public class IdGeneratorController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getId() {
        Xid xid = new Xid();
        return Map.of("id", xid.toString(), "date", Timestamp.from(xid.getDate().toInstant()).toString());
    }

}
